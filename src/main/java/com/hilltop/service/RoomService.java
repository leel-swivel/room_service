package com.hilltop.service;

import com.hilltop.domain.request.HotelIdRequestDto;
import com.hilltop.domain.request.RoomCreateRequestDto;
import com.hilltop.domain.response.RoomCreateResponseDto;
import com.hilltop.exception.InvalidRoomException;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.Room;
import com.hilltop.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoomService {

    private static final String ERROR_MESSAGE = " from database was failed.";
    private final RoomRepository roomRepository;
    private final RoomTypeService roomTypeService;

    public RoomService(RoomRepository roomRepository, RoomTypeService roomTypeService) {
        this.roomRepository = roomRepository;
        this.roomTypeService = roomTypeService;
    }

    /**
     * This method used to save a room.
     *
     * @param roomCreateRequestDto roomCreateRequestDto
     * @return RoomCreateResponseDto
     */
    public RoomCreateResponseDto saveRoom(RoomCreateRequestDto roomCreateRequestDto) {
        var roomType = roomTypeService.getRoomType(roomCreateRequestDto.getRoomTypeId());
        var room = new Room(roomCreateRequestDto, roomType);
        try {
            roomRepository.save(room);
            log.info("Successfully save room by id: {}", room.getId());
            return new RoomCreateResponseDto(room);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Saving room info into database was failed.", e);
        }
    }

    /**
     * This method used to get room by id.
     *
     * @param roomId roomId
     * @return room
     */
    public Room getRoom(String roomId) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isPresent()) {
                return roomOptional.get();
            } else {
                log.error("Error getting room by id: {}.", roomId);
                throw new InvalidRoomException("No room found for id: " + roomId);
            }
        } catch (DataAccessException e) {
            throw new RoomServiceException("Getting room info into database was failed.", e);
        }
    }

    /**
     * This method used to get room page by hotel id.
     *
     * @param pageable pageable
     * @param hotelId  hotelId
     * @return RoomPage
     */
    public Page<Room> getRoomPageByHotelId(Pageable pageable, String hotelId) {
        try {
            return roomRepository.findByHotelId(pageable, hotelId);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Getting room list by hotel id " + hotelId + ERROR_MESSAGE, e);
        }
    }

    /**
     * This method used to delete a room.
     *
     * @param roomId roomId
     */
    public void deleteRoom(String roomId) {
        try {
            var room = getRoom(roomId);
            roomRepository.delete(room);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Deleting room by id " + roomId + ERROR_MESSAGE, e);
        }
    }

    /**
     * This method used to update a room.
     *
     * @param id                   id
     * @param roomCreateRequestDto roomCreateRequestDto
     * @return Room
     */
    public void updateRoom(String id, RoomCreateRequestDto roomCreateRequestDto) {
        try {
            Room room = getRoom(id);
            var roomType = roomTypeService.getRoomType(roomCreateRequestDto.getRoomTypeId());
            room.update(roomCreateRequestDto, roomType);
            roomRepository.save(room);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Updating room by id " + id + ERROR_MESSAGE, e);
        }
    }

    /**
     * This method used to get rooms by pax count and hotel ids.
     * This method returns room list for exact pax count by iterating all hotelId list.
     *
     * @param paxCount          paxCount
     * @param hotelIds hotelIds
     * @return hotel id vs room list map
     */
    public Map<String, List<Room>> getRoomsForPaxCountAndHotelIds(int paxCount, List<String> hotelIds) {
        try {
            Map<String, List<Room>> hotelAndRoomsMap = new HashMap<>();
            for (String id : hotelIds) {
                List<Room> rooms = getRoomsByHotelId(id);
                List<Room> searchList = rooms
                        .stream().filter(room -> room.getPaxCount() == paxCount).collect(Collectors.toList());
                if (searchList.isEmpty()) {
                    searchList = findRoomsForExtraPaxCount(new HashSet<>(rooms), paxCount);
                }
                if (!searchList.isEmpty()) {
                    hotelAndRoomsMap.put(id, searchList);
                }
            }
            return hotelAndRoomsMap;
        } catch (DataAccessException e) {
            throw new RoomServiceException("Failed to get room list by hotel ids and pax count from database.", e);
        }
    }

    /**
     * This method used to get room list by extra pax count.
     * This method returns room that can occupy extra pax count : Eg: Pax count is 5 then
     * this method will return rooms for that can occupy pax count: 6
     *
     * @param roomSet  roomSet
     * @param paxCount paxCount
     * @return Room List
     */
    public List<Room> findRoomsForExtraPaxCount(Set<Room> roomSet, int paxCount) {
        List<Room> searchList = roomSet.stream()
                .filter(room -> room.getPaxCount() >
                        paxCount && room.getPaxCount() <= paxCount + 1)
                .collect(Collectors.toList());
        if (searchList.isEmpty()) {
            Optional<Room> optionalRoom = roomSet
                    .stream().filter(room -> room.getPaxCount() < paxCount).max(Comparator.comparing(Room::getPaxCount));
            if (optionalRoom.isPresent()) {
                searchList = findMultipleRoomsForPaxCount(optionalRoom.get(), roomSet, paxCount);
            }
        }
        return searchList;
    }

    /**
     * This method return the combination of rooms:
     * Eg: PaxCount = 3 ; Then room will return pax count 1 and 2 rooms.
     *
     * @param maximumPaxRoom maximumPaxRoom
     * @param roomSet        roomSet
     * @param paxCount       paxCount
     * @return Room List
     */
    public List<Room> findMultipleRoomsForPaxCount(Room maximumPaxRoom, Set<Room> roomSet, int paxCount) {
        List<Room> searchRoomList = new ArrayList<>();
        searchRoomList.add(maximumPaxRoom);
        int totalPaxCount = maximumPaxRoom.getPaxCount();
        List<Room> sortedRoomList = roomSet.stream()
                .filter(room -> !room.equals(maximumPaxRoom))
                .sorted(Comparator.comparing(Room::getPaxCount).reversed())
                .collect(Collectors.toList());
        for (Room room : sortedRoomList) {
            int roomPaxCount = room.getPaxCount();
            if (totalPaxCount + roomPaxCount <= paxCount) {
                searchRoomList.add(room);
                totalPaxCount += roomPaxCount;
                if (totalPaxCount == paxCount) {
                    break;
                }
            }
        }
        return totalPaxCount == paxCount ? searchRoomList : Collections.emptyList();
    }

    /**
     * This method finds rooms by hotel id.
     *
     * @param hotelId hotelId
     * @return Room List
     */
    public List<Room> getRoomsByHotelId(String hotelId) {
        try {
            return roomRepository.findByHotelId(hotelId);
        } catch (DataAccessException e) {
            log.error("Failed to get room by hotel id: {}", hotelId);
            throw new RoomServiceException("Failed to get rooms by hotel id.", e);
        }
    }
}
