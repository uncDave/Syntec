package com.Synctec.Synctec.service.interfaces.thirdpartyauth;

import com.Synctec.Synctec.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AuthorizationService {
    ResponseEntity<ApiResponse<?>> generateAuthorizationUrl();

    ResponseEntity<ApiResponse<?>> generateTwitterAuthorizationUrl();

    ResponseEntity<ApiResponse<?>> verifyAuth(String code, String state, String error);

    ResponseEntity<ApiResponse<?>> verifyTwitterAuth(String authorizationCode);
}
