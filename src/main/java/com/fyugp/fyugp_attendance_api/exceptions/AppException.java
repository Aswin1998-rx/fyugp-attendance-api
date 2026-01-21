package com.fyugp.fyugp_attendance_api.exceptions;






import com.fyugp.fyugp_attendance_api.utils.BeanUtil;
import com.fyugp.fyugp_attendance_api.utils.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * App exception.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException {

    private static final Message MESSAGE = BeanUtil.getBean(Message.class);

    public AppException(String message) {
        this(message, (Object) null);
    }

    public AppException(String message, Object... args) {
        this(message, null, args);
    }

    public AppException(String message, Throwable cause) {
        this(message, cause, (Object) null);
    }

    public AppException(String message, Throwable cause, Object... args) {
        super(MESSAGE.getMessage(message, message, null, args), cause);
    }

}
