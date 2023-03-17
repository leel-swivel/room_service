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

import java.util.List;
import java.util.Optional;

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
    public Room updateRoom(String id, RoomCreateRequestDto roomCreateRequestDto) {
        try {
            Room room = getRoom(id);
            var roomType = roomTypeService.getRoomType(roomCreateRequestDto.getRoomTypeId());
            room.update(roomCreateRequestDto, roomType);
            return roomRepository.save(room);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Updating room by id " + id + ERROR_MESSAGE, e);
        }
    }

    /**
     * This method used to get room for the search.
     *
     * @param paxCount          paxCount
     * @param hotelIdRequestDto hotelIdRequestDto
     * @return Room List
     */
    public List<Room> getRooms(int paxCount, HotelIdRequestDto hotelIdRequestDto) {
        try {
            return roomRepository
                    .getAllByHotelIdInAndPaxCountGreaterThanEqual(hotelIdRequestDto.getHotelIds(), paxCount);
        } catch (DataAccessException e) {
            throw new RoomServiceException("Getting room list by hotel ids and pax count from database was failed.", e);
        }
    }
}
