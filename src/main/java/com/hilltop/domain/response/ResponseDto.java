package com.hilltop.domain.response;

import com.hilltop.domain.BaseDto;

/**
 * ResponseDto
 */
public abstract class ResponseDto implements BaseDto {
    @Override
    public String toLogJson() {
        return toJson();
    }
}
