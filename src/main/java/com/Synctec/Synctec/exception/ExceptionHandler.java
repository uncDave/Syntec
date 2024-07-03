package com.Synctec.Synctec.exception;

import com.Synctec.Synctec.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class ExceptionHandler {
//    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
//    protected ResponseEntity<ApiResponse> handleGlobalExceptions(MethodArgumentNotValidException ex) {
//        String[] errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
//        ApiResponse response =new ApiResponse();
////        response.setStatus("Success");
//        response.setMessage(Arrays.toString(errors));
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<?> exceptionHandler(Exception exception){
        var response = ApiResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .requestTime(LocalDateTime.now())
                .requestType("Outbound")
                .message("Validation failed")
                .status(false)
                .error(exception.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {

        var response = ApiResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .requestTime(LocalDateTime.now())
                .requestType("Outbound")
                .message("Operation not successful")
                .status(false)
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("| "));

        var response = ApiResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .requestTime(LocalDateTime.now())
                .requestType("Outbound")
                .message("Validation failed")
                .status(false)
                .error(errorMessage)
                .build();

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException ex) {

        var response = ApiResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .requestTime(LocalDateTime.now())
                .requestType("Outbound")
                .message("Operation not successful")
                .status(false)
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

//    @org.springframework.web.bind.annotation.ExceptionHandler(value = BadCredentialsException.class)
//    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
//
//        var response = ApiResponse.builder()
//                .referenceId(UUID.randomUUID().toString())
//                .requestTime(LocalDateTime.now())
//                .requestType("Outbound")
//                .message("Operation not successful")
//                .status(false)
//                .error(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response, BAD_REQUEST);
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {

        var response = ApiResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .requestTime(LocalDateTime.now())
                .requestType("Outbound")
                .message("Operation not successful")
                .status(false)
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }


}
