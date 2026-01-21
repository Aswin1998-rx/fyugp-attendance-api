package com.fyugp.fyugp_attendance_api.controller.advice;


import com.fyugp.fyugp_attendance_api.Constants;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.exceptions.HttpStatusException;
import com.fyugp.fyugp_attendance_api.utils.Message;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fyugp.fyugp_attendance_api.server.model.ApiError;

/**
 * Api exception handler.
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final DispatcherServlet dispatcherServlet;
    private final Message message;

    @PostConstruct()
    private void configureDispatcherServlet() {
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }

    /**
     * This method handles unhandled exceptions.
     *
     * @param e the unhandled exception.
     * @return the custom error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> apiExceptionHandler(Exception e) {
        log.error("", e);
        return ResponseEntity.internalServerError().body(ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(false)
                .message(e.getMessage())
                .build());
    }

    /**
     * Not found exception handling.
     *
     * @param e exception.
     * @return response.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> notFoundHandler(NoHandlerFoundException e) {
        log.error("{} {} not found : {}", e.getHttpMethod(), e.getRequestURL(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(com.fyugp.fyugp_attendance_api.server.model.ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.NOT_FOUND.value())
                .status(false)
                .message(e.getMessage())
                .build());
    }

    /**
     * handles {@link HttpRequestMethodNotSupportedException}.
     *
     * @param request the request that caused the exception
     * @param e       the occurred exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> methodNotSupported(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.error("{} {} not found : {}", e.getMethod(), request.getRequestURI(), e.getMessage());
        String allowedMethods = e.getSupportedHttpMethods() == null
                ? null
                : e.getSupportedHttpMethods().stream().map(HttpMethod::toString).collect(Collectors.joining(Constants.COMMA));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Allow", allowedMethods)
                .body(ApiError.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .status(false)
                        .message(message.getMessage("errors.requestMethodNotSupported", e.getMethod(), request.getRequestURI()))
                        .build());
    }

    /**
     * Bind exception handler.
     *
     * @param e exception.
     * @return response with message.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> bindExceptionHandler(BindException e) {
        String message = e.getBindingResult()
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(Constants.COMMA));
        log.error("", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .status(false)
                .message(message)
                .build());
    }

    /**
     * This method handles {@link HttpStatusException}.
     *
     * @param e the exception to be handled
     * @return the custom error response
     */
    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<ApiError> httpRequestExceptionHandler(HttpStatusException e) {
        log.error("", e);
        return ResponseEntity.status(e.getStatus()).body(ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .code(e.getStatus().value())
                .status(false)
                .message(e.getMessage())
                .build());
    }

    /**
     * AccessDenied exception handling.
     *
     * @param e       exception
     * @param request servlet request
     * @return respose.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accessDeniedExceptionHandler(AccessDeniedException e, HttpServletRequest request) {
        log.error("access denied to {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.FORBIDDEN.value())
                .status(false)
                .message(e.getMessage())
                .build());
    }

    /**
     * This method handles bad credentials error.
     *
     * @param e the exception
     * @return 401 response.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> badCredentialsHandler(BadCredentialsException e) {
        final var msg = message.getMessage(e.getMessage(), message.getMessage("errors.badCredentials"), null);
        log.error("invalid credentials - {}", e.getMessage());
        if (e.getCause() != null) {
            log.error("cause", e.getCause());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiError.builder()
                                .timestamp(OffsetDateTime.now())
                                .code(HttpStatus.UNAUTHORIZED.value())
                                .status(false)
                                .message(msg)
                                .build()
                );
    }

    /**
     * This method handles errors that occurs when a user account is locked.
     *
     * @param e the exception
     * @return 401 response
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> lockedExceptionHandler(LockedException e) {
        final var msg = message.getMessage(e.getMessage(), message.getMessage("errors.accountIsLocked"), null);
        log.error("locked account accessed {}", msg);
        if (e.getCause() != null) {
            log.error("cause", e.getCause());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiError.builder()
                                .timestamp(OffsetDateTime.now())
                                .code(HttpStatus.UNAUTHORIZED.value())
                                .status(false)
                                .message(msg)
                                .build()
                );
    }

    /**
     * This method handles the error that occurs when a user is disabled.
     *
     * @param e the exception
     * @return 401 response
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledError(DisabledException e) {
        final var msg = message.getMessage(e.getMessage(), message.getMessage("errors.accountIsDisabled"), null);
        log.error("user account is disabled - {}", e.getMessage());
        if (e.getCause() != null) {
            log.error("cause", e.getCause());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiError.builder()
                                .timestamp(OffsetDateTime.now())
                                .code(HttpStatus.UNAUTHORIZED.value())
                                .status(false)
                                .message(msg)
                                .build()
                );
    }

    /**
     * Http message Not readable exception handler.
     *
     * @param e the exception to be handled
     * @return the custom error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> invalidRequestHandler(HttpMessageNotReadableException e) {
        log.error("Invalid request body", e);
        var rootCause = getCause(e.getCause(), AppException.class);
        if (rootCause.isPresent()) {
            return apiExceptionHandler(rootCause.get());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .status(false)
                .message(e.getMessage())
                .build());
    }

    private <T extends Throwable> Optional<T> getCause(Throwable exception, Class<T> clzz) {
        if (exception == null) {
            return Optional.empty();
        }
        if (clzz.isAssignableFrom(exception.getClass())) {
            return Optional.of(clzz.cast(exception));
        }
        return getCause(exception.getCause(), clzz);
    }
}
