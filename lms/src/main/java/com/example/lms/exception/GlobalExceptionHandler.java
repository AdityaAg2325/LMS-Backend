package com.example.lms.exception;

import com.example.lms.dto.ResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });

        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGlobalException(Exception exception,
                                                             WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                            WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.NOT_FOUND.toString(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception,
                                                                            WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.CONFLICT.toString(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDTO> handleDataIntegrityViolationException(DataIntegrityViolationException exception,
                                                                            WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.BAD_REQUEST.toString(),
//                exception.getMessage()
                "Operation failed"
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException exception,
                                                                            WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.NOT_FOUND.toString(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO> handleBadCredentialsException(BadCredentialsException exception,
                                                                       WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.UNAUTHORIZED.toString(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDTO> handleIllegalStateException(IllegalStateException exception, WebRequest webRequest) {
        ResponseDTO errorResponseDto = new ResponseDTO(
                HttpStatus.METHOD_NOT_ALLOWED.toString(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.METHOD_NOT_ALLOWED);
    }

}
