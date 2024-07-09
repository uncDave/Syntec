package com.Synctec.Synctec.controllers.waitlist;

import com.Synctec.Synctec.domains.WaitList;
import com.Synctec.Synctec.dtos.request.WaitListDto;
import com.Synctec.Synctec.service.interfaces.baseuserservice.BaseUserService;
import com.Synctec.Synctec.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/waitlist")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class WaitLIstController {

    private final BaseUserService baseUserService;

    @PostMapping("join-waitlist")
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody WaitListDto.Request request){
        return baseUserService.joinWaitList(request);
    }
}
