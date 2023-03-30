package com.hilltop.contoller;

import com.hilltop.configuration.Translator;
import com.hilltop.domain.request.RoomTypeCreateRequestDto;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.RoomType;
import com.hilltop.service.RoomTypeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoomTypeControllerTest {
    private final static String CREATE_ROOM_TYPE_URL = "/api/v1/room-type";
    @Mock
    private RoomTypeService roomTypeService;
    @Mock
    private Translator translator;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        initMocks(this);
        RoomTypeController roomTypeController = new RoomTypeController(translator, roomTypeService);
        mockMvc = MockMvcBuilders.standaloneSetup(roomTypeController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_ReturnOk_When_CreatingARoomType() throws Exception {
        RoomType sampleRoomType = getSampleRoomType();
        RoomTypeCreateRequestDto sampleRoomTypeCreateRequestDto = getSampleRoomTypeCreateRequestDto();
        when(roomTypeService.saveRoomType(any())).thenReturn(sampleRoomType);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_ROOM_TYPE_URL)
                        .content(sampleRoomTypeCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingARoomType() throws Exception {
        RoomTypeCreateRequestDto sampleRoomTypeCreateRequestDto = getSampleRoomTypeCreateRequestDto();
        doThrow(new RoomServiceException("ERROR")).when(roomTypeService).saveRoomType(any());
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_ROOM_TYPE_URL)
                        .content(sampleRoomTypeCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }


    private RoomTypeCreateRequestDto getSampleRoomTypeCreateRequestDto() {
        RoomTypeCreateRequestDto roomTypeCreateRequestDto = new RoomTypeCreateRequestDto();
        roomTypeCreateRequestDto.setRoomType("ONLY_BED");
        return roomTypeCreateRequestDto;
    }

    private RoomType getSampleRoomType(){
        return new RoomType("rtid-ge35","Room_Only",20);
    }
}