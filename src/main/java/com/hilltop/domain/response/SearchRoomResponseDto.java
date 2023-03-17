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
    private BigDecimal pricePerNight;
    private int paxCount;
    private List<String> imageUrls;
    private BigDecimal totalCost;

    public SearchRoomResponseDto(Room room, int days) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.hotelId = room.getHotelId();
        this.roomTypeName = room.getRoomType().getName();
        this.pricePerNight = room.getRoomType().getPricePerNight();
        this.paxCount = room.getPaxCount();
        this.imageUrls = room.getImageUrls();
        this.totalCost = calculateRoomCost(pricePerNight, days);
    }

    private BigDecimal calculateRoomCost(BigDecimal pricePerNight, int days) {
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }
}
