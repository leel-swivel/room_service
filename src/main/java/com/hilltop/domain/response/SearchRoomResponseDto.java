package com.hilltop.domain.response;

import com.hilltop.model.Room;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SearchRoomResponseDto extends ResponseDto {

    private String id;
    private int roomNumber;
    private String hotelId;
    private String roomTypeName;
    private BigDecimal costPerNight;
    private int paxCount;
    private List<String> imageUrls;

    public SearchRoomResponseDto(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.hotelId = room.getHotelId();
        this.roomTypeName = room.getRoomType().getName();
        this.costPerNight = room.getCost();
        this.paxCount = room.getPaxCount();
        this.imageUrls = room.getImageUrls();
    }
}
