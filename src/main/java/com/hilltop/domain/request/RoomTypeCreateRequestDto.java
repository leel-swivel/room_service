package com.hilltop.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RoomTypeCreateRequestDto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeCreateRequestDto extends RequestDto {

    private String roomType;
    private double markupPercentage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
