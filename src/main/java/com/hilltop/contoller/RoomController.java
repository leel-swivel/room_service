package com.hilltop.contoller;

import com.hilltop.configuration.Translator;
import com.hilltop.domain.request.HotelIdRequestDto;
import com.hilltop.domain.request.RoomCreateRequestDto;
import com.hilltop.domain.response.RoomListPageResponseDto;
import com.hilltop.domain.response.RoomListResponseDto;
import com.hilltop.domain.response.RoomResponseDto;
import com.hilltop.enums.ErrorResponseStatusType;
import com.hilltop.enums.SuccessResponseStatusType;
import com.hilltop.exception.InvalidRoomException;
import com.hilltop.exception.InvalidRoomTypeException;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.model.Room;
import com.hilltop.service.RoomService;
import com.hilltop.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * RoomController
 */
@RestController
@RequestMapping("/api/v1/room")
@Slf4j
public class RoomController extends Controller {

    private final RoomService roomService;

    public RoomController(Translator translator, RoomService roomService) {
        super(translator);
        this.roomService = roomService;
    }

    /**
     * This endpoint used to save a room.
     *
     * @param roomCreateRequestDto roomCreateRequestDto
     * @return roomCreateResponseDto
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper> saveRoom(@RequestBody RoomCreateRequestDto roomCreateRequestDto) {
        try {
            if (!roomCreateRequestDto.isRequiredAvailable()) {
                log.error("Missing required filed to save a room.");
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            var roomCreateResponseDto = roomService.saveRoom(roomCreateRequestDto);
            return getSuccessResponse(roomCreateResponseDto, SuccessResponseStatusType.CREATE_ROOM, HttpStatus.CREATED);
        } catch (RoomServiceException e) {
            log.error("Saving room was failed for hotel id: {}", roomCreateRequestDto.getHotelId(), e);
            return getInternalServerError();
        }
    }

    /**
     * This endpoint used to get room by id.
     *
     * @param id hotelId
     * @return roomResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getRoom(@PathVariable String id) {
        try {
            var room = roomService.getRoom(id);
            var roomResponseDto = new RoomResponseDto(room);
            return getSuccessResponse(roomResponseDto, SuccessResponseStatusType.GET_ROOM, HttpStatus.OK);
        } catch (InvalidRoomException e) {
            log.error("Invalid room id to get room details.");
            return getErrorResponse(ErrorResponseStatusType.INVALID_ROOM_ID);
        } catch (RoomServiceException e) {
            log.error("Getting room by id was failed.", e);
            return getInternalServerError();
        }
    }

    /**
     * This endpoint used to get room list for a hotel.
     *
     * @param hotelId hoteId
     * @param page    page
     * @param size    size
     * @return roomListPageResponseDto
     */
    @GetMapping("/hotel/{hotelId}/page/{page}/size/{size}")
    public ResponseEntity<ResponseWrapper> getRoomListForHotel(@PathVariable String hotelId,
                                                               @Min(DEFAULT_PAGE) @PathVariable int page,
                                                               @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            Page<Room> roomPageByHotelId = roomService.getRoomPageByHotelId(pageable, hotelId);
            var roomListPageResponseDto = new RoomListPageResponseDto(roomPageByHotelId);
            return getSuccessResponse(roomListPageResponseDto, SuccessResponseStatusType.ROOM_BY_HOTEL_ID, HttpStatus.OK);
        } catch (RoomServiceException e) {
            log.error("Getting room list by hotel id was failed.", e);
            return getInternalServerError();
        }
    }

    /**
     * This endpoint used to delete a room.
     *
     * @param roomId roomId
     * @return Success/Unsuccessful response
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ResponseWrapper> deleteRoom(@PathVariable String roomId) {
        try {
            roomService.deleteRoom(roomId);
            return getSuccessResponse(null, SuccessResponseStatusType.DELETE_ROOM, HttpStatus.OK);
        } catch (RoomServiceException e) {
            log.error("Deleting room by id was failed.", e);
            return getInternalServerError();
        }
    }

    /**
     * This endpoint used to update a room.
     *
     * @param roomId               roomId
     * @param roomCreateRequestDto roomCreateRequestDto
     * @return roomResponseDto
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<ResponseWrapper> updateRoom(@PathVariable String roomId,
                                                      @RequestBody RoomCreateRequestDto roomCreateRequestDto) {
        try {
            if (!roomCreateRequestDto.isRequiredAvailable()) {
                log.error("Missing required filed to save a room.");
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            Room room = roomService.updateRoom(roomId, roomCreateRequestDto);
            RoomResponseDto roomResponseDto = new RoomResponseDto(room);
            return getSuccessResponse(roomResponseDto, SuccessResponseStatusType.UPDATE_ROOM, HttpStatus.OK);
        } catch (InvalidRoomException e) {
            log.error("Invalid room id to update room details.");
            return getErrorResponse(ErrorResponseStatusType.INVALID_ROOM_ID);
        } catch (InvalidRoomTypeException e) {
            log.error("Invalid room type id to update room details.");
            return getErrorResponse(ErrorResponseStatusType.INVALID_ROOM_TYPE);
        } catch (RoomServiceException e) {
            log.error("Updating room by id was failed.", e);
            return getInternalServerError();
        }
    }

    /**
     * This endpoint used to get hotel room for the search.
     *
     * @param count             count
     * @param days              days
     * @param hotelIdRequestDto hotelIdRequestDto
     * @return searchRoomListResponseDto
     */
    @PostMapping("/list-hotel-room-by")
    public ResponseEntity<ResponseWrapper> getHotelRooms(@RequestParam int count,
                                                         @RequestParam int days,
                                                         @RequestBody HotelIdRequestDto hotelIdRequestDto) {
        try {
            var rooms = roomService.getRoomsForPaxCountAndHotelIds(count, hotelIdRequestDto);
            RoomListResponseDto roomListResponseDto = new RoomListResponseDto(rooms, days);
            log.info("Successfully returned the hotel rooms for pax count :{} and day count: {} ", count, days);
            return getSuccessResponse(roomListResponseDto, SuccessResponseStatusType.SEARCH_ROOMS, HttpStatus.OK);
        } catch (RoomServiceException e) {
            log.error("Getting rooms by id and hotel ids was failed.", e);
            return getInternalServerError();
        }
    }
}
