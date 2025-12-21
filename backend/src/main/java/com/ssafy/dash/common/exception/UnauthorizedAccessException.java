package com.ssafy.dash.common.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String resource, Long resourceId, Long userId) {
        super(String.format("User %d is not authorized to modify %s with id %d", userId, resource, resourceId));
    }

}
