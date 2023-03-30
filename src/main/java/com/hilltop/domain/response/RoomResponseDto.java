package com.hilltop.domain.response;

import com.hilltop.model.Room;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * RoomResponseDto
 */
@Getter
@Setter
public class RoomResponseDto extends ResponseDto {

    private String id;
    private int roomNumber;
    private String hotelId;
    private String roomTypeName;
    private int paxCount;
    private List<String> imageUrls;
    private BigDecimal cost;

    public RoomResponseDto(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.hotelId = room.getHotelId();
        this.roomTypeName = room.getRoomType().getName();
        this.paxCount = room.getPaxCount();
        this.imageUrls = room.getImageUrls();
        cost = room.getCost();
    }
}
