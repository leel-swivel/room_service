package com.hilltop.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelIdRequestDto extends RequestDto {

    private List<String> hotelIds;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
