package com.hilltop.contoller;

import com.hilltop.configuration.Translator;
import com.hilltop.domain.request.HotelIdRequestDto;
import com.hilltop.domain.request.RoomCreateRequestDto;
import com.hilltop.exception.InvalidRoomException;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.Room;
import com.hilltop.model.RoomType;
import com.hilltop.service.RoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoomControllerTest {

    private static final String CREATE_ROOM_URL = "/api/v1/room";
    private static final String ROOM_ID = "rid-1235-1458-1785";
    private static final String GET_ROOM_BY_ID_URL = "/api/v1/room/{id}";
    private static final String UPDATE_ROOM_BY_ID_URL = "/api/v1/room/{id}";
    private static final String DELETE_ROOM_BY_ID_URL = "/api/v1/room/{id}";
    private static final String GET_ROOM_LIST_FOR_HOTEL = "/api/v1/room/hotel/{hotelId}/page/{page}/size/{size}";
    private static final int PAGE_NO = 0;
    private static final int SIZE = 1;
    private static final int DAYS = 2;

    @Mock
    private RoomService roomService;
    @Mock
    private Translator translator;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        initMocks(this);
        RoomController hotelController = new RoomController(translator, roomService);
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_ReturnOk_When_CreatingARoom() throws Exception {
        RoomCreateRequestDto sampleRoomCreateRequestDto = getSampleRoomCreateRequestDto();
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_ROOM_URL)
                        .content(sampleRoomCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingARoomWithoutRequiredFields() throws Exception {
        RoomCreateRequestDto sampleRoomCreateRequestDto = getSampleRoomCreateRequestDto();
        sampleRoomCreateRequestDto.setRoomNumber(0);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_ROOM_URL)
                        .content(sampleRoomCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Should_ReturnInternalServerError_When_CreatingARoom() throws Exception {
        RoomCreateRequestDto sampleRoomCreateRequestDto = getSampleRoomCreateRequestDto();
        doThrow(new RoomServiceException("ERROR")).when(roomService).saveRoom(any());
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_ROOM_URL)
                        .content(sampleRoomCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void Should_ReturnOk_When_ValidRoomIdIsProvided() throws Exception {
        String url = GET_ROOM_BY_ID_URL.replace("{id}", ROOM_ID);
        Room room = generateRoom();
        when(roomService.getRoom(ROOM_ID)).thenReturn(room);
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk());
    }

    @Test
    void Should_ReturnBadRequest_WhenProvidingInvalidRoomId() throws Exception {
        String url = GET_ROOM_BY_ID_URL.replace("{id}", ROOM_ID);
        doThrow(new InvalidRoomException("ERROR")).when(roomService).getRoom(ROOM_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Should_ReturnInternalServerError_WhenProvidingInvalidRoomId() throws Exception {
        String url = GET_ROOM_BY_ID_URL.replace("{id}", ROOM_ID);
        doThrow(new RoomServiceException("ERROR")).when(roomService).getRoom(ROOM_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void Should_ReturnOk_When_DeletingHotel() throws Exception {
        String url = DELETE_ROOM_BY_ID_URL.replace("{id}", ROOM_ID);
        Room room = generateRoom();
        when(roomService.getRoom(ROOM_ID)).thenReturn(room);
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isOk());
    }

    @Test
    void Should_ReturnBadRequest_When_DeletingARoom() throws Exception {
        String url = DELETE_ROOM_BY_ID_URL.replace("{id}", ROOM_ID);
        doThrow(new RoomServiceException("ERROR")).when(roomService).deleteRoom(ROOM_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void Should_ReturnOk_When_ValidPageAndSizeProvidedForGetAllRoomList() throws Exception {
        String url = GET_ROOM_LIST_FOR_HOTEL.replace("{hotelId}", "hid-gega3-23feg").replace("{page}",
                String.valueOf(PAGE_NO)).replace("{size}", String.valueOf(SIZE));
        Page<Room> roomPage = getRoomPage();
        when(roomService.getRoomPageByHotelId(
                PageRequest.of(PAGE_NO, SIZE, Sort.by("updatedAt").descending())
                , "hid-gega3-23feg")).thenReturn(roomPage);
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void Should_ReturnInternalServerError_When_ValidPageAndSizeProvidedForGetAllRoomList() throws Exception {
        String url = GET_ROOM_LIST_FOR_HOTEL.replace("{hotelId}", "hid-gega3-23feg").replace("{page}",
                String.valueOf(PAGE_NO)).replace("{size}", String.valueOf(SIZE));
        doThrow(new RoomServiceException("ERROR")).when(roomService).getRoomPageByHotelId(
                PageRequest.of(PAGE_NO, SIZE, Sort.by("updatedAt").descending()), "hid-gega3-23feg");
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void Should_ReturnBadRequest_When_UpdatingARoomWithoutRequiredFields() throws Exception {
        String url = UPDATE_ROOM_BY_ID_URL.replace("{id}", ROOM_ID);
        RoomCreateRequestDto sampleRoomCreateRequestDto = getSampleRoomCreateRequestDto();

        sampleRoomCreateRequestDto.setRoomNumber(0);
        mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .content(sampleRoomCreateRequestDto.toLogJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private RoomCreateRequestDto getSampleRoomCreateRequestDto() {
        RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto();
        roomCreateRequestDto.setRoomTypeId("rtid-gegeg-gse4gvs");
        roomCreateRequestDto.setPaxCount(5);
        roomCreateRequestDto.setRoomNumber(4);
        roomCreateRequestDto.setPricePerNight(BigDecimal.valueOf(5000.00));
        roomCreateRequestDto.setHotelId("hid-gega3-23feg");
        roomCreateRequestDto.setImageUrls(new ArrayList<>());
        return roomCreateRequestDto;
    }

    private Room generateRoom() {
        RoomCreateRequestDto sampleRoomCreateRequestDto = getSampleRoomCreateRequestDto();
        return new Room(sampleRoomCreateRequestDto, new RoomType("id-gejakjg", "SINGLE", 52.0));
    }

    private Page<Room> getRoomPage() {
        List<Room> rooms = new ArrayList<>();
        Room room = generateRoom();
        rooms.add(room);
        return new PageImpl<>(rooms);

    }

    private HotelIdRequestDto getSampleHotelIdRequestDto() {
        List<String> hotelIds = new ArrayList<>();
        hotelIds.add("hid-gega3-23feg");
        HotelIdRequestDto hotelIdRequestDto = new HotelIdRequestDto();
        hotelIdRequestDto.setHotelIds(hotelIds);
        return hotelIdRequestDto;
    }


//    private HotelIdRequestDto getSampleHotelIdRequestDto(){
//        HotelIdRequestDto hotelIdRequestDto = new HotelIdRequestDto();
//        hotelIdRequestDto.getHotelIds().add("aghejgh");
//        hotelIdRequestDto.getHotelIds().add("aghejgh");
//        hotelIdRequestDto.getHotelIds().add("aghejgh");
//        return hotelIdRequestDto;
//    }
}