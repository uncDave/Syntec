package com.Synctec.Synctec.service.interfaces.baseuserservice;


import com.Synctec.Synctec.dtos.request.*;
import com.Synctec.Synctec.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseUserService {
    ResponseEntity<ApiResponse<?>> registerUser(String email,String password);
    ResponseEntity<ApiResponse<?>> createUserName(CreateUserNameRequest request);
    List<String> generateUsernameSuggestions(String baseIdentifier);

    ResponseEntity<ApiResponse<?>> completeResetPassword(ResetPassword.ResetComplete resetComplete);
    boolean isUsernameTaken(String username);
    ResponseEntity<ApiResponse<?>> loginUser(LoginDTO.Request login);
    ResponseEntity<ApiResponse<?>> validateUserByOtp(OtpRequest otp);
    ResponseEntity<ApiResponse<?>> regenerateVerificationTokenAndSendEmail(String email);
    ResponseEntity<ApiResponse<?>> initiateResetPassword(String email);

    ResponseEntity<ApiResponse<?>> updateUsername(String username,UpdateUsername request);
    ResponseEntity<ApiResponse<?>> fetchUserDetails(String username);
    ResponseEntity<ApiResponse<?>> changePassword(String userId,ChangePasswordRequest changePasswordRequest);
    ResponseEntity<ApiResponse<?>> createCommunity(String userId, CreateCommunityRequest communityRequest);
    ResponseEntity<ApiResponse<?>>joinWaitList(WaitListDto.Request waitlist);






//    Optional<BaseUser> findUserByEmail(String email);




}
