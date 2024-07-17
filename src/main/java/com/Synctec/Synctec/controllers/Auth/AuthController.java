package com.Synctec.Synctec.controllers.Auth;

import com.Synctec.Synctec.dtos.request.*;
import com.Synctec.Synctec.service.interfaces.baseuserservice.BaseUserService;
import com.Synctec.Synctec.service.interfaces.thirdpartyauth.AuthorizationService;
import com.Synctec.Synctec.utils.ApiResponse;
import com.Synctec.Synctec.utils.Validators.PasswordValidator;
import com.Synctec.Synctec.utils.Validators.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

import static com.Synctec.Synctec.utils.ResponseUtils.createFailureResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AuthController {
    private final BaseUserService baseUserService;
    private final AuthorizationService authorizationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody CreateUserRequest.Request request){
        String email = request.getEmail();
        String password = request.getPassword();
        // Register as email
           return baseUserService.registerUser(email.toLowerCase().trim(),password);

    }

    @PostMapping("/set-username")
    public ResponseEntity<?> setUsername(@RequestBody CreateUserNameRequest request) {
        log.info("creating username for user {}" + request.getUserName());
        return baseUserService.createUserName(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO.Request request) {
        log.info("Login user {}" + request.getUserName());
        return baseUserService.loginUser(request);
    }
    @PostMapping("/create-profile")
    public ResponseEntity<ApiResponse<?>> createPost(@RequestParam String userId,@RequestParam MultipartFile profilePicture,
                                                     @RequestParam MultipartFile backgroundPicture,@RequestParam String firstName,@RequestParam String lastName,@RequestParam String phoneNumber,@RequestParam String dob) throws IOException {
        log.info("This is the file:{}",profilePicture.getOriginalFilename());
        log.info("this is the request:{}",userId);
        return baseUserService.createprofile(userId,profilePicture,backgroundPicture,firstName,lastName,phoneNumber,dob);
    }

    @PostMapping("/reset-password-otp")
    public ResponseEntity<ApiResponse<?>> sendForgotPasswordOtp(@RequestBody ResetPassword.RequestInit request){
        String identifier = request.getIdentifier();
            return baseUserService.initiateResetPassword(identifier);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody ResetPassword.ResetComplete resetComplete){
        log.info("This are the passwords " + resetComplete.getNewPassword() + "and" + resetComplete.getNewPasswordConfirmation());
        boolean isValid = PasswordValidator.validatePassword(resetComplete.getNewPassword(), resetComplete.getNewPasswordConfirmation());
        if (isValid){
            return baseUserService.completeResetPassword(resetComplete);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Password do not match", "Password do not match"));
    }

    @PostMapping("/confirm-otp")
    public ResponseEntity<ApiResponse<?>> confirmUserAccount(@RequestBody OtpRequest tokenDTO) {
        log.info("here to verify the otp " + tokenDTO.getConfirmationToken());
        String confirmationToken = tokenDTO.getConfirmationToken();
        return baseUserService.validateUserByOtp(tokenDTO);
    }


    @PostMapping("/regenerate-otp")
    public ResponseEntity<ApiResponse<?>> regenerateTokenForRegisteredUser(@RequestBody AccountValidationRequest request){
        String identifier = request.getIdentifier();
        // Validate the identifier (email or phone)
        if (UserValidation.isValidEmail(identifier)) {
            // Register as email
            return baseUserService.regenerateVerificationTokenAndSendEmail(identifier);
        }else if(UserValidation.isValidPhoneNumber(identifier)){
            // Normalize and register as phone number
            String normalizedPhoneNumber = UserValidation.normalizePhoneNumber(identifier);
            if (normalizedPhoneNumber == null) {
                return ResponseEntity.status(HttpStatus.OK).body(createFailureResponse("Invalid phone number format","Invalid phone number format"));
            }
            return baseUserService.regenerateVerificationTokenAndSendEmail(identifier);
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(createFailureResponse("Invalid email or phone number","Invalid email or phone number"));
        }

    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(Principal principal, @RequestBody ChangePasswordRequest changePasswordRequest){
        log.info("This are the passwords " + changePasswordRequest.getNewPassword() + "and" + changePasswordRequest.getNewPasswordConfirmation());
        String userId = principal.getName();
        boolean isValid = PasswordValidator.validatePassword(changePasswordRequest.getNewPassword(), changePasswordRequest.getNewPasswordConfirmation());
        if (isValid){
            return baseUserService.changePassword(userId,changePasswordRequest);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Password do not match", "Password do not match"));
    }


    @GetMapping("/google")
    public ResponseEntity<ApiResponse<?>> initiateGoogleAuth(){
        return authorizationService.generateAuthorizationUrl();
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyGoogleAuth(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @RequestParam(value = "error", required = false) String error) {
        log.info("google calls this url " + code + "" + state + "" + error);

        // Delegate the handling to the service
        return authorizationService.verifyAuth(code, state, error);
    }

    @GetMapping("/initiate-twitter")
    public ResponseEntity<ApiResponse<?>> initiateTwitterAuth(){
        log.info("here to initiate the process:{}");
        return authorizationService.generateTwitterAuthorizationUrl();
    }
    @GetMapping("/verify-twitter")
    public ResponseEntity<ApiResponse<?>> verifyTwitterAuth(@RequestParam("code") String authorizationCode,
                                                            @RequestParam("state") String state){

        log.info("here to verify the user:{}", authorizationCode, state);
        return authorizationService.verifyTwitterAuth(authorizationCode);
    }




}
