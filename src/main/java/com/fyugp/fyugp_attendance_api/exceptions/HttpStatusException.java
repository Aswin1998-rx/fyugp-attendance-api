package com.fyugp.fyugp_attendance_api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * HttpStatus exception.
 */
@Getter
public class HttpStatusException extends AppException {
    private final HttpStatus status;

    public HttpStatusException(HttpStatus status, String message, Object... args) {
        this(status, message, null, args);
    }

    public HttpStatusException(HttpStatus status, String message, Throwable cause, Object... args) {
        super(message, cause, args);
        this.status = status;
    }
}
