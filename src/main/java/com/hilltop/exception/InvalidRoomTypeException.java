package com.hilltop.exception;

/**
 * InvalidRoomTypeException
 */
public class InvalidRoomTypeException extends RoomServiceException {

    public InvalidRoomTypeException(String errorMessage) {
        super(errorMessage);
    }
}
