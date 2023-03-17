package com.hilltop.exception;

/**
 * InvalidRoomException
 */
public class InvalidRoomException extends RoomServiceException {

    public InvalidRoomException(String errorMessage) {
        super(errorMessage);
    }
}
