package com.hilltop.contoller;

import com.hilltop.configuration.Translator;
import com.hilltop.domain.request.RoomTypeCreateRequestDto;
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

//    @Test
    void Should_ReturnOk_When_CreatingARoomType() throws Exception {
        RoomTypeCreateRequestDto sampleRoomTypeCreateRequestDto = getSampleRoomTypeCreateRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_ROOM_TYPE_URL)
                        .content(sampleRoomTypeCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    private RoomTypeCreateRequestDto getSampleRoomTypeCreateRequestDto() {
        RoomTypeCreateRequestDto roomTypeCreateRequestDto = new RoomTypeCreateRequestDto();
        roomTypeCreateRequestDto.setRoomType("ONLY_BED");
        roomTypeCreateRequestDto.setPricePerNight(BigDecimal.valueOf(5000.00));
        return roomTypeCreateRequestDto;
    }
}