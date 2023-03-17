package com.hilltop.enums;

import lombok.Getter;

/**
 * SuccessResponseStatusType
 */
@Getter
public enum SuccessResponseStatusType {

    CREATE_ROOM(2000, "Successfully created the room."),
    GET_ROOM(2001, "Successfully returned the room."),
    CREATE_ROOM_TYPE(2002, "Successfully created the room type."),
    ROOM_BY_HOTEL_ID(2003, "Successfully returned the room list by hotel id."),
    DELETE_ROOM(2004,"Successfully deleted the room."),
    UPDATE_ROOM(2005,"Successfully update the room."),
    SEARCH_ROOMS(2006,"Successfully returned the search room list.");

    private final int code;
    private final String message;

    SuccessResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Success code covert into string to read display message from success property file
     *
     * @param successCode successCode
     * @return string code
     */
    public String getCodeString(int successCode) {
        return Integer.toString(successCode);
    }
}
