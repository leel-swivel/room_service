package com.hilltop.wrapper;

import com.hilltop.domain.response.ResponseDto;
import com.hilltop.enums.ResponseStatusType;
import com.hilltop.enums.SuccessResponseStatusType;
import lombok.Getter;

/**
 * SuccessResponseWrapper
 */
@Getter
public class SuccessResponseWrapper extends ResponseWrapper {

    private final int statusCode;

    public SuccessResponseWrapper(ResponseStatusType status, SuccessResponseStatusType successResponseStatusType,
                                  ResponseDto responseDto, String displayMessage) {
        super(status, successResponseStatusType.getMessage(), responseDto, displayMessage);
        this.statusCode = successResponseStatusType.getCode();
    }
}