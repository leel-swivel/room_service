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

import static org.junit.jupiter.api.Assertions.*;
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


    @BeforeEach
    void setUp() {
        initMocks(this);
        roomService = new RoomService(roomRepository, roomTypeService);
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
        doThrow(new DataAccessException("ERROR") {}).when(roomRepository).delete(sampleRoom);
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.deleteRoom(ROOM_ID));
        assertEquals("Deleting room by id " + ROOM_ID + " from database was failed.", roomServiceException.getMessage());
    }

    @Test
    void Should_ThrowException_When_DeletingARoomById() {
        Room sampleRoom = getSampleRoom();
        doThrow(new DataAccessException("ERROR") {}).when(roomRepository).delete(sampleRoom);
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.deleteRoom(ROOM_ID));
        assertEquals("No room found for id: " + ROOM_ID, roomServiceException.getMessage());
    }

    @Test
    void Should_ReturnRoomPageByHotelId_WhenProvidingRequiredFields(){
        Pageable pageable = PageRequest.of(0, 2, Sort.by("updatedAt").descending());
        roomService.getRoomPageByHotelId(pageable,HOTEL_ID);
        verify(roomRepository,times(1)).findByHotelId(pageable,HOTEL_ID);
    }

    @Test
    void Should_ThrowRoomServiceException_When_GettingRoomsByHotelIdAndPagebale() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("updatedAt").descending());
        doThrow(new DataAccessException("ERROR") {
        }).when(roomRepository).findByHotelId(pageable,HOTEL_ID);
        RoomServiceException roomServiceException = assertThrows(RoomServiceException.class, () ->
                roomService.getRoomPageByHotelId(pageable, HOTEL_ID));
        assertEquals("Getting room list by hotel id " + HOTEL_ID+ " from database was failed.", roomServiceException.getMessage());
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