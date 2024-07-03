package com.Synctec.Synctec.utils;

import org.slf4j.MDC;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class ResponseUtils {
    public static <T> ApiResponse<T> createSuccessResponse(T data, String message) {
        var requestId = UUID.randomUUID().toString();
        var time  = LocalDateTime.now();

        MDC.put("requestId",requestId );
        MDC.put("requestTime", time.toString());
        MDC.put("data", data.toString());
        MDC.put("message", message);
        return ApiResponse.<T>builder()
                .requestTime(time)
                .requestType("Outbound")
                .referenceId(requestId)
                .status(true)
                .message(message)
                .data(data)
                .build();
    }


    public static <T> ApiResponse<T> createFailureResponse(String error, String message) {
        var requestId = UUID.randomUUID().toString();
        var time  = LocalDateTime.now();

        MDC.put("requestId",requestId );
        MDC.put("requestTime", time.toString());
        MDC.put("error", error);
        MDC.put("message", message);
        return ApiResponse.<T>builder()
                .requestTime(time)
                .requestType("Outbound")
                .referenceId(requestId)
                .status(false)
                .message(message)
                .error(error)
                .build();
    }

    public static <T> ApiResponse<T> createFailureWithObject(T data, String message) {
        var requestId = UUID.randomUUID().toString();
        var time  = LocalDateTime.now();

        MDC.put("requestId",requestId );
        MDC.put("requestTime", time.toString());
        MDC.put("data", data.toString());
        MDC.put("message", message);
        return ApiResponse.<T>builder()
                .requestTime(time)
                .requestType("Outbound")
                .referenceId(requestId)
                .status(false)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> createSuccessMessage( String message) {
        var requestId = UUID.randomUUID().toString();
        var time  = LocalDateTime.now();

        MDC.put("requestId",requestId );
        MDC.put("requestTime", time.toString());
        MDC.put("message", message);
        return ApiResponse.<T>builder()
                .requestTime(time)
                .requestType("Outbound")
                .referenceId(requestId)
                .status(true)
                .message(message)
                .build();
    }

    public static String generateSystemPassword(int length){
        String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@#$%^&+=!";
        Random RANDOM = new SecureRandom();
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

}
