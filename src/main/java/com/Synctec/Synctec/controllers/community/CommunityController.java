package com.Synctec.Synctec.controllers.community;

import com.Synctec.Synctec.dtos.request.CreateCommunityRequest;
import com.Synctec.Synctec.dtos.request.UpdateUsername;
import com.Synctec.Synctec.service.interfaces.baseuserservice.BaseUserService;
import com.Synctec.Synctec.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Slf4j
public class CommunityController {

    private final BaseUserService baseUserService;

    @PatchMapping("/create-community")
    public ResponseEntity<ApiResponse<?>> createCommunity(Principal principal, @RequestBody CreateCommunityRequest request){
        String userId = principal.getName();
        return baseUserService.createCommunity(userId,request);
    }
}
