package com.hilltop.service;

import com.hilltop.domain.request.RoomTypeCreateRequestDto;
import com.hilltop.exception.InvalidRoomTypeException;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.RoomType;
import com.hilltop.repository.RoomTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class RoomTypeServiceTest {
    private static final String ROOM_TYPE_ID = "rtid-92be0c67-3810-47c2-9e28-615f81efad6a";

    private RoomTypeService roomTypeService;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @BeforeEach
    void setUp() {
        initMocks(this);
        roomTypeService = new RoomTypeService(roomTypeRepository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_SaveHotelType() {
        var sampleRoomTypeCreateRequestDto = getSampleRoomTypeCreateRequestDto();
        roomTypeService.saveRoomType(sampleRoomTypeCreateRequestDto);
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void Should_ThrowException_When_savingRoomType() {
        var sampleRoomTypeCreateRequestDto = getSampleRoomTypeCreateRequestDto();
        when(roomTypeRepository.save(any(RoomType.class))).thenThrow(new DataAccessException("ERRPR") {
        });
        var roomServiceException = assertThrows(RoomServiceException.class,
                () -> roomTypeService.saveRoomType(sampleRoomTypeCreateRequestDto));
        assertEquals("Saving room type info into database was failed.", roomServiceException.getMessage());

    }

    @Test
    void Should_ReturnRoomType_When_RoomTypeIdProvided() {
        RoomType sampleRoomType = getSampleRoomType();
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.of(sampleRoomType));
        assertEquals(sampleRoomType, roomTypeService.getRoomType(ROOM_TYPE_ID));
    }

    @Test
    void Should_ThrowInvalidRoomTypeException_When_InvalidRoomTypeIdProvided() {
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.empty());
        InvalidRoomTypeException invalidRoomTypeException = assertThrows(InvalidRoomTypeException.class,
                () -> roomTypeService.getRoomType(ROOM_TYPE_ID));
        assertEquals("Invalid room type id: " + ROOM_TYPE_ID, invalidRoomTypeException.getMessage());
    }


    private RoomTypeCreateRequestDto getSampleRoomTypeCreateRequestDto() {
        RoomTypeCreateRequestDto roomTypeCreateRequestDto = new RoomTypeCreateRequestDto();
        roomTypeCreateRequestDto.setRoomType("ONLY_BED");
        roomTypeCreateRequestDto.setPricePerNight(BigDecimal.valueOf(5000.00));
        return roomTypeCreateRequestDto;
    }

    private RoomType getSampleRoomType() {
        RoomTypeCreateRequestDto sampleRoomTypeCreateRequestDto = getSampleRoomTypeCreateRequestDto();
        return new RoomType(sampleRoomTypeCreateRequestDto);
    }
}