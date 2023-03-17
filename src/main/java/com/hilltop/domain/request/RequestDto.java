package com.hilltop.domain.request;

import com.hilltop.domain.BaseDto;

/**
 * RequestDto
 */
public abstract class RequestDto implements BaseDto {
    /**
     * This method checks all required fields are available for a request.
     *
     * @return true/ false
     */
    public boolean isRequiredAvailable() {
        return true;
    }

    /**
     * This method checks the given field is non empty.
     *
     * @param field field
     * @return true/ false
     */
    protected boolean isNonEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }
}
