package com.hilltop.contoller;

import com.hilltop.configuration.Translator;
import com.hilltop.domain.request.RoomTypeCreateRequestDto;
import com.hilltop.domain.response.RoomTypeCreateResponse;
import com.hilltop.enums.SuccessResponseStatusType;
import com.hilltop.exception.RoomServiceException;
import com.hilltop.service.RoomTypeService;
import com.hilltop.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RoomTypeController
 */
@RestController
@RequestMapping("/api/v1/room-type")
@Slf4j
public class RoomTypeController extends Controller {
    private final RoomTypeService roomTypeService;

    public RoomTypeController(Translator translator, RoomTypeService roomTypeService) {
        super(translator);
        this.roomTypeService = roomTypeService;
    }

    /**
     * This endpoint used to save room type.
     *
     * @param roomTypeCreateRequestDto roomTypeCreateRequestDto
     * @return roomTypeCreateResponse
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper> saveRoomType(@RequestBody RoomTypeCreateRequestDto roomTypeCreateRequestDto) {
        try {
            var roomTypeCreateResponseDto = roomTypeService.saveRoomType(roomTypeCreateRequestDto);
            var roomTypeCreateResponse = new RoomTypeCreateResponse(roomTypeCreateResponseDto);
            return getSuccessResponse(roomTypeCreateResponse, SuccessResponseStatusType.CREATE_ROOM_TYPE, HttpStatus.CREATED);
        } catch (RoomServiceException e) {
            log.error("Saving room type was failed.", e);
            return getInternalServerError();
        }
    }

}
