package com.hilltop.exception;

/**
 * RoomServiceException
 */
public class RoomServiceException extends RuntimeException {

    /**
     * BaseComponentException Exception with error message.
     *
     * @param errorMessage error message
     */
    public RoomServiceException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Authentication Exception with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public RoomServiceException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
