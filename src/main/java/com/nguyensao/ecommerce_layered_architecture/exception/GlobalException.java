package com.nguyensao.ecommerce_layered_architecture.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nguyensao.ecommerce_layered_architecture.dto.response.DataResponse;
import com.nguyensao.ecommerce_layered_architecture.constant.ExceptionMessageConstant;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<DataResponse<Object>> handleAppException(AppException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(ExceptionMessageConstant.APP_EXCEPTION);
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(exception.getBody().getDetail());

        BindingResult bindingResult = exception.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errors = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        response.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<DataResponse<Object>> handleException(RuntimeException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(ExceptionMessageConstant.RUNTIME_EXCEPTION);
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<DataResponse<Object>> handleNoResourceFoundException(NoResourceFoundException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError(ExceptionMessageConstant.NOT_FOUND_PATH);
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = MissingRequestCookieException.class)
    public ResponseEntity<DataResponse<Object>> handleMissingRequestCookieException(
            MissingRequestCookieException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(ExceptionMessageConstant.MISSING_COOKIE);
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<DataResponse<Object>> handleNullPointerException(NullPointerException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(ExceptionMessageConstant.NULL_POINTER);
        response.setMessage(ExceptionMessageConstant.INVALID_INPUT);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = JsonProcessingException.class)
    public ResponseEntity<DataResponse<Object>> handleJsonProcessingException(JsonProcessingException exception) {
        DataResponse<Object> response = new DataResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(ExceptionMessageConstant.JSON_PARSE_ERROR);
        response.setMessage(ExceptionMessageConstant.ENUM_CONVERT_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
