package com.hilltop.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * RoomCreateRequestDto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateRequestDto extends RequestDto {

    private int roomNumber;
    private String hotelId;
    private int paxCount;
    private String roomTypeId;
    private List<String> imageUrls;
    private BigDecimal pricePerNight;
    private String city;

    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return roomNumber > 0 && isNonEmpty(hotelId) && paxCount > 0 && isNonEmpty(roomTypeId);
    }
}
