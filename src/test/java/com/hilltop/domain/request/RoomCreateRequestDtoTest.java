package com.hilltop.domain.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoomCreateRequestDtoTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_ReturnTrue_When_ValidFieldsAreProvided() {
        RoomCreateRequestDto sampleHotelCreateRequest = getSampleHotelCreateRequest();
        assertTrue(sampleHotelCreateRequest.isRequiredAvailable());
    }
    @Test
    void Should_ReturnTrue_When_ToStringMethodReturnsValue(){
        RoomCreateRequestDto sampleHotelCreateRequest = getSampleHotelCreateRequest();
        String sampleHotelCreateRequestToString = sampleHotelCreateRequestToString();
        assertEquals(sampleHotelCreateRequest.toLogJson(), sampleHotelCreateRequestToString);
    }

    private String sampleHotelCreateRequestToString() {
        return "{\"roomNumber\":1,\"hotelId\":\"hid-gejgka\",\"paxCount\":2,\"roomTypeId\":\"rtid-gejkge\"," +
                "\"imageUrls\":[],\"requiredAvailable\":true}";
    }

    private RoomCreateRequestDto getSampleHotelCreateRequest() {
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto();
        roomCreateRequestDto.setRoomNumber(1);
        roomCreateRequestDto.setHotelId("hid-gejgka");
        roomCreateRequestDto.setRoomTypeId("rtid-gejkge");
        roomCreateRequestDto.setPaxCount(2);
        roomCreateRequestDto.setImageUrls(new ArrayList<>());
        return roomCreateRequestDto;
    }


}