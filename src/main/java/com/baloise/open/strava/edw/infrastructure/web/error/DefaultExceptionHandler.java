package com.baloise.open.strava.edw.infrastructure.web.error;

import com.baloise.open.edw.domain.dto.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.StringWriter;
import java.net.URI;

/**
 * Base class for exception handlers we can use by default in springboot applications. The concrete implementation
 * must be located somewhere in the package hierarchy of teh application, otherwise it will not be injected.
 */
@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Provides a problem the client can interpret based on a requested resource that was not found.
     *
     * @param exception the exception to convert to a problem.
     * @param request   the web request carrying several additional information we want to add to the problem.
     * @return the response entity
     */
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Provides a problem the client can interpret based on an invalid parameter having been specified.
     *
     * @param exception the exception to convert to a problem.
     * @param request   the web request carrying several additional information we want to add to the problem.
     * @return the response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<Object> handleNotFoundException(IllegalArgumentException exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentTypeMismatchException.class})
    public final ResponseEntity<Object> handleBadRequestExceptions(Exception exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception, WebRequest request) {
        //handle all invalid fields
        StringWriter messageWriter = new StringWriter();
        for (ConstraintViolation<?> currentError : exception.getConstraintViolations()) {
            messageWriter.append(currentError.getPropertyPath().toString());
            messageWriter.append(": ");
            messageWriter.append(currentError.getMessage());
        }

        return createProblemDetails(exception, request, HttpStatus.BAD_REQUEST, messageWriter.toString());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public final ResponseEntity<Object> handleResponseStatusException(ResponseStatusException exception, WebRequest request) {
        return createProblemDetails(exception, request, exception.getStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Object> handleAllExceptions(RuntimeException exception, WebRequest request) {
        return createProblemDetails(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return createProblemDetails(exception, request, status);
    }

    /**
     * Internal method that should be used by sub-classes as well to render an exception base class, if no other handler processed the exception.
     *
     * @param exception  the detailed exception we are covering, needed to provide a stacktrace on the console.
     * @param request    the web request carrying detailed information about user, instance, etc.
     * @param httpStatus the precise status code we intend to return.
     * @return the response entity carrying HTTP status code and teh problem DTO.
     */
    protected final ResponseEntity<Object> createProblemDetails(Exception exception, WebRequest request, HttpStatus httpStatus) {
        return createProblemDetails(exception, request, httpStatus, exception.getMessage());
    }

    /**
     * Internal method that should be used by sub-classes as well to render a new standard problem.
     *
     * @param exception     the detailed exception we are covering, needed to provide a stacktrace on the console.
     * @param request       the web request carrying detailed information about user, instance, etc.
     * @param httpStatus    the precise status code we intend to return.
     * @param detailMessage the detailed message (already resolved with optional parameters).
     * @return the response entity carrying HTTP status code and teh problem DTO.
     */
    protected final ResponseEntity<Object> createProblemDetails(Exception exception, WebRequest request, HttpStatus httpStatus, String detailMessage) {
        if (!(request instanceof ServletWebRequest)) {
            throw new IllegalStateException("Could not cast to ServletWebRequest. This should not happen");
        }

        String requestUrl = ((ServletWebRequest) request).getRequest().getRequestURI();
        logErrorMessage(exception, httpStatus, requestUrl);

        Problem problemDetailsDto = Problem.builder()
                // SHOULD be the same as the recommended HTTP status phrase for that code (e.g., "Not Found" for 404).
                .title(httpStatus.getReasonPhrase())
                .detail(detailMessage)
                .status(httpStatus.value())
                .errorCode(0)
                .cid("tbd")
                .build();

        return ResponseEntity.status(problemDetailsDto.getStatus()).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problemDetailsDto);
    }

    /**
     * Internal method that should be used by sub-classes as well to provide the stack trace in teh log.
     *
     * @param exception  the detailed exception we are covering, needed to provide a stacktrace on the console.
     * @param httpStatus the precise status code we intend to return.
     * @param requestUrl the path in teh URL (not teh complete one) that is leading to the source of the problem, required for debugging purposes.
     */
    protected final void logErrorMessage(Exception exception, HttpStatus httpStatus, String requestUrl) {
        final String errorMessage = String.format("Handle error for request %s", requestUrl);
        if (httpStatus.is5xxServerError()) {
            logger.error(errorMessage, exception);
            return;
        }
        logger.info(errorMessage, exception);
    }

    /**
     * Provides the instance URL based on the given HTTP request.
     *
     * @param httpRequest the request that should be evaluated.
     * @return the URI used as instance URI in a problem detail.
     */
    protected URI createInstanceUri(HttpServletRequest httpRequest) {
        return ServletUriComponentsBuilder.fromRequestUri(httpRequest)
                .build()
                .toUri();
    }
}
