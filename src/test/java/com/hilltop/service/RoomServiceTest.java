package com.hilltop.service;

import com.hilltop.domain.request.HotelIdRequestDto;
import com.hilltop.domain.request.RoomCreateRequestDto;
import com.hilltop.domain.request.RoomTypeCreateRequestDto;
import com.hilltop.domain.response.RoomCreateResponseDto;
import com.hilltop.exception.InvalidRoomException;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.Room;
import com.hilltop.model.RoomType;
import com.hilltop.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class RoomServiceTest {

    private static final String ROOM_ID = "rid-92be0c67-3810-47c2-9e28-615f81efad6a";
    private static final String HOTEL_ID = "hid-92be0c67-3810-47c2-9e28-615f81efad6a";

    private RoomService roomService;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomTypeService roomTypeService;

    private HotelIdRequestDto hotelIdRequestDto;


    @BeforeEach
    void setUp() {
        initMocks(this);
        roomService = new RoomService(roomRepository, roomTypeService);

        hotelIdRequestDto = new HotelIdRequestDto();
        hotelIdRequestDto.setHotelIds(Arrays.asList("hid-123", "hid-456"));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_SaveRoom() {
        var roomCreateRequestDto = getSampleRoomCreateResponseDto();
        when(roomTypeService.getRoomType(roomCreateRequestDto.getRoomTypeId())).thenReturn(getSampleRoomType());
        when(roomService.saveRoom(roomCreateRequestDto)).thenReturn(any(RoomCreateResponseDto.class));
        roomService.saveRoom(roomCreateRequestDto);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void Should_UpdateRoom_WhenValidDataIsGiven() {
        var room = getSampleRoom();
        RoomCreateRequestDto sampleRoomCreateResponseDto = getSampleRoomCreateResponseDto();
        when(roomTypeService.getRoomType(sampleRoomCreateResponseDto.getRoomTypeId())).thenReturn(getSampleRoomType());
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomService.saveRoom(sampleRoomCreateResponseDto)).thenReturn(any(RoomCreateResponseDto.class));
        roomService.updateRoom(ROOM_ID, sampleRoomCreateResponseDto);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void Should_ThrowException_When_UpdatingRoom() {
        var room = getSampleRoom();
        RoomCreateRequestDto sampleRoomCreateResponseDto = getSampleRoomCreateResponseDto();
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomTypeService.getRoomType(sampleRoomCreateResponseDto.getRoomTypeId())).thenReturn(getSampleRoomType());
        when(roomRepository.save(any(Room.class))).thenThrow(new DataAccessException("ERROR") {
        });
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.updateRoom(ROOM_ID, sampleRoomCreateResponseDto));
        assertEquals("Updating room by id rid-92be0c67-3810-47c2-9e28-615f81efad6a from database was failed.",
                roomServiceException.getMessage());
    }

    @Test
    void Should_ThrowException_When_SavingRoom() {
        var roomCreateRequestDto = getSampleRoomCreateResponseDto();
        when(roomTypeService.getRoomType(roomCreateRequestDto.getRoomTypeId())).thenReturn(getSampleRoomType());
        when(roomRepository.save(any(Room.class))).thenThrow(new DataAccessException("ERROR") {
        });
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.saveRoom(roomCreateRequestDto));
        assertEquals("Saving room info into database was failed.", roomServiceException.getMessage());
    }

    @Test
    void Should_ReturnRoom_When_RoomIdProvided() {
        var room = getSampleRoom();
        room.setId(ROOM_ID);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        assertEquals(room, roomService.getRoom(ROOM_ID));
    }

    @Test
    void Should_ThrowInvalidRoomException_When_InvalidRoomIdProvided() {
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());
        InvalidRoomException invalidRoomException = assertThrows(InvalidRoomException.class, () -> roomService.getRoom(ROOM_ID));
        assertEquals("No room found for id: " + ROOM_ID, invalidRoomException.getMessage());
    }


    @Test
    void Should_ThrowException_When_GettingRoomById() {
        when(roomRepository.findById(ROOM_ID)).thenThrow(new DataAccessException("ERROR") {
        });
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.getRoom(ROOM_ID));
        assertEquals("Getting room info into database was failed.", roomServiceException.getMessage());
    }

    @Test
    void Should_DeleteRoom() {
        Room sampleRoom = getSampleRoom();
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(sampleRoom));
        roomService.deleteRoom(ROOM_ID);
        verify(roomRepository, times(1)).delete(sampleRoom);
    }

    @Test
    void Should_ThrowRoomServiceException_When_DeletingARoomById() {
        Room sampleRoom = getSampleRoom();
        sampleRoom.setId(ROOM_ID);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(sampleRoom));
        doThrow(new DataAccessException("ERROR") {
        }).when(roomRepository).delete(sampleRoom);
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.deleteRoom(ROOM_ID));
        assertEquals("Deleting room by id " + ROOM_ID + " from database was failed.",
                roomServiceException.getMessage());
    }

    @Test
    void Should_ThrowException_When_DeletingARoomById() {
        Room sampleRoom = getSampleRoom();
        doThrow(new DataAccessException("ERROR") {
        }).when(roomRepository).delete(sampleRoom);
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.deleteRoom(ROOM_ID));
        assertEquals("No room found for id: " + ROOM_ID, roomServiceException.getMessage());
    }

    @Test
    void Should_ReturnRoomPageByHotelId_WhenProvidingRequiredFields() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("updatedAt").descending());
        roomService.getRoomPageByHotelId(pageable, HOTEL_ID);
        verify(roomRepository, times(1)).findByHotelId(pageable, HOTEL_ID);
    }

    @Test
    void Should_ThrowRoomServiceException_When_GettingRoomsByHotelIdAndPagebale() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("updatedAt").descending());
        doThrow(new DataAccessException("ERROR") {
        }).when(roomRepository).findByHotelId(pageable, HOTEL_ID);
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.getRoomPageByHotelId(pageable, HOTEL_ID));
        assertEquals("Getting room list by hotel id " + HOTEL_ID + " from database was failed.",
                roomServiceException.getMessage());
    }


    @Test
    void Should_Return_RoomsForPaxCountAndHotelIds() {
        List<Room> rooms1 = new ArrayList<>();
        Room room1 = getSampleRoom();
        room1.setId("rid-1");
        room1.setHotelId("hid-123");
        room1.setPaxCount(2);
        rooms1.add(room1);
        Mockito.when(roomRepository.findByHotelId("hid-123")).thenReturn(rooms1);
        List<Room> rooms2 = new ArrayList<>();
        Room room2 = getSampleRoom();
        room2.setId("rid-2");
        room2.setHotelId("hid-456");
        room2.setPaxCount(2);
        rooms2.add(room2);
        Mockito.when(roomRepository.findByHotelId("hid-456")).thenReturn(rooms2);
        Map<String, List<Room>> expected = new HashMap<>();
        expected.put("hid-123", rooms1);
        List<Room> searchList = new ArrayList<>();
        searchList.add(room2);
        expected.put("hid-456", searchList);
        Map<String, List<Room>> result = roomService.getRoomsForPaxCountAndHotelIds(2, hotelIdRequestDto.getHotelIds());
        assertEquals(expected, result);
    }

    @Test
    void Should_Return_RoomsForExtraPaxCount() {
        var roomSet = new HashSet<Room>();
        var room1 = getSampleRoom();
        room1.setId("r1");
        room1.setPaxCount(2);
        var room2 = getSampleRoom();
        room2.setId("r2");
        room2.setPaxCount(3);
        var room3 = getSampleRoom();
        room3.setId("r3");
        room3.setPaxCount(6);
        roomSet.add(room1);
        roomSet.add(room2);
        roomSet.add(room3);
        Room room4 = getSampleRoom();
        room4.setId("r4");
        room4.setPaxCount(3);
        roomSet.add(room4);
        List<Room> result = roomService.findRoomsForExtraPaxCount(roomSet, 2);
        assertEquals(2, result.size());
    }

    @Test
    void Should_Return_MultipleRoomsForPaxCount() {
        var room1 = getSampleRoom();
        room1.setId("1");
        room1.setPaxCount(2);
        var room2 = getSampleRoom();
        room2.setId("2");
        room2.setPaxCount(3);
        var room3 = getSampleRoom();
        room3.setId("3");
        room3.setPaxCount(4);
        var roomSet = new HashSet<>(Arrays.asList(room1, room2, room3));
        int paxCount = 6;
        Room room4 = getSampleRoom();
        room4.setId("4");
        room4.setPaxCount(5);
        roomSet.add(room4);
        List<Room> result = roomService.findMultipleRoomsForPaxCount(room3, roomSet, paxCount);
        List<Room> expected = Arrays.asList(room3, room1);
        assertEquals(expected, result);
    }

    private RoomCreateRequestDto getSampleRoomCreateResponseDto() {
        RoomCreateRequestDto roomCreateResponseDto = new RoomCreateRequestDto();
        roomCreateResponseDto.setHotelId(HOTEL_ID);
        roomCreateResponseDto.setImageUrls(new ArrayList<>());
        roomCreateResponseDto.setRoomTypeId("rtid-gea34-fge3");
        roomCreateResponseDto.setPaxCount(2);
        roomCreateResponseDto.setPricePerNight(BigDecimal.valueOf(20));
        return roomCreateResponseDto;
    }

    private Room getSampleRoom() {
        return new Room(getSampleRoomCreateResponseDto(), getSampleRoomType());
    }

    private RoomType getSampleRoomType() {
        return new RoomType(new RoomTypeCreateRequestDto("rtid-vagae", 5));
    }
}