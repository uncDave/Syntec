package com.Synctec.Synctec.controllers.profile;

import com.Synctec.Synctec.dtos.request.UpdateUsername;
import com.Synctec.Synctec.service.interfaces.baseuserservice.BaseUserService;
import com.Synctec.Synctec.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final BaseUserService baseUserService;

    @GetMapping("/profile")
    public  ResponseEntity<ApiResponse<?>> getUserDetails(Principal principal){
        String userId = principal.getName();
        log.info("This is the principal " + userId );
        return baseUserService.fetchUserDetails(userId);

    }

    @PatchMapping("/update-username")
    public  ResponseEntity<ApiResponse<?>>  updateUsername(Principal principal, @RequestBody UpdateUsername request){
        String userName = principal.getName();
        return baseUserService.updateUsername(userName,request);

    }


}
