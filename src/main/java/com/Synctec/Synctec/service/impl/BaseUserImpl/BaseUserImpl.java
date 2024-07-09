package com.Synctec.Synctec.service.impl.BaseUserImpl;

import com.Synctec.Synctec.domains.*;
import com.Synctec.Synctec.dtos.request.*;
import com.Synctec.Synctec.dtos.response.UserProfileDetailResponse;
import com.Synctec.Synctec.email.EmailService;
import com.Synctec.Synctec.enums.Roles;
import com.Synctec.Synctec.repository.BaseUserRepository;
import com.Synctec.Synctec.repository.TokenRepository;
import com.Synctec.Synctec.security.JwtService;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.*;
import com.Synctec.Synctec.service.interfaces.baseuserservice.BaseUserService;
import com.Synctec.Synctec.utils.ApiResponse;
import com.Synctec.Synctec.utils.Validators.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.Synctec.Synctec.utils.ResponseUtils.*;


@RequiredArgsConstructor
@Service
@Slf4j
public class BaseUserImpl implements BaseUserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BaseUserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserJpaService userJpaService;
    private final TokenJpaService tokenJpaService;
    private final CommunityJpaService communityJpaService;
    private final PostJpaService postJpaService;
    private final LikeJpaService likeJpaService;
    private final CommentJpaService commentJpaService;
    private final WaitListJpaInterface waitListJpaInterface;


    @Override
    public ResponseEntity<ApiResponse<?>> registerUser(String email,String password) {
        try {
            Optional<BaseUser> optionalUser = userJpaService.findByEmail(email);
            if (optionalUser.isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(createFailureResponse("User exist already", "User exist"));
            }
            BaseUser baseUser = BaseUser.builder().email(email)
                    .password(passwordEncoder.encode(password))
                    .isVerified(false)
                    .role(Roles.valueOf("user"))
                    .build();

            VerificationToken confirmationToken = new VerificationToken(baseUser,"SIGNUP");
            // Send the verification token
            sendVerificationToken(confirmationToken);
            BaseUser savedUser = userJpaService.saveUser(baseUser);

            tokenJpaService.saveToken(confirmationToken);
            CreateUserRequest.SignUpResponse  signUpResponse =  CreateUserRequest.SignUpResponse.builder().userId(savedUser.getId()).build();
//            SignUpResponse signUpResponse = new SignUpResponse();
//            signUpResponse.setUserId(savedUser.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse(signUpResponse,"User successfully created"));
        }catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Sign up failed", "Reason: "+ ex.getMessage()));
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Operation failed", "Reason: "+ ex.getMessage()));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> createUserName(CreateUserNameRequest request) {
        // Check if the username is already taken
        if (isUsernameTaken(request.getUserName())) {
            List<String> suggestions = generateUsernameSuggestions(request.getUserName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createFailureWithObject(suggestions, "Username already taken"));
        }
        Optional<BaseUser> optionalUser = userJpaService.findById(request.getUserId());
        if (optionalUser.isPresent()){
            BaseUser baseUser = optionalUser.get();
            if (!baseUser.isVerified() && !baseUser.isActive() ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("Account not verified", "Please verify your account first"));
            }else {
                baseUser.setUserName(request.getUserName());
                userJpaService.saveUser(baseUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessMessage("successful created"));
            }

        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createFailureResponse("User not found", "User not found"));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> loginUser(LoginDTO.Request request) {

        try {
            Optional<BaseUser> user = userJpaService.findBaseUserByUserName(request.getUserName());
            log.info("This is the user object " + user.get());

            if (user.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse("Invalid Credentials", "Kindly Signup"));
            }
            BaseUser baseUser = user.get();

            // Check if the user is verified
            if (!baseUser.isVerified() && !baseUser.isActive() ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("Account not verified", "Please verify your account first"));
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(),request.getPassword()));
            var jwtToken = jwtService.generateToken(baseUser);

//            // Create a custom token with user ID as the principal
//            CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(
//                    baseUser.getId(), // User ID as principal
//                    null,
//                    baseUser.getAuthorities()
//            );

            // Set the custom token in the security context
//            SecurityContextHolder.getContext().setAuthentication(customAuthenticationToken);

            LoginDTO.Response response = LoginDTO.Response.builder()
                    .jwtToken(jwtToken)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(createSuccessResponse(response,"Login successful"));
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("Invalid credentials", "Login Failed"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Login failed", ex.getMessage()));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> validateUserByOtp(OtpRequest otp) {
        log.info("inside validate otp method");
        Optional<VerificationToken> optionalToken = tokenJpaService.findByConfirmationToken(otp.getConfirmationToken());
        if (optionalToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("Invalid token", "Token does not exist"));
        }
        VerificationToken verificationToken = optionalToken.get();

        // Check if the token has already been marked as used (expired)
        if (verificationToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createFailureResponse("Token expired", "The provided OTP has already been used or expired"));
        }

        // Check if the token has expired based on time
        if (Instant.now().isAfter(verificationToken.getExpiry())) {
            verificationToken.setExpired(true);
            tokenJpaService.saveToken(verificationToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createFailureResponse("Token expired", "The provided OTP has expired"));
        }
        // Check if the provided OTP matches the stored OTP
        if (!verificationToken.getConfirmationToken().equals(otp.getConfirmationToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createFailureResponse("Invalid OTP", "The OTP provided is incorrect."));
        }
        BaseUser baseUser = verificationToken.getBaseUser();

// Handle the OTP based on its purpose
        switch (verificationToken.getPurpose()) {
            case "SIGNUP":
                // Verify the user account
                if (!baseUser.isVerified() && !baseUser.isActive()) {
                    baseUser.setVerified(true);
                    baseUser.setActive(true);
                    baseUser.setEnabledAt(Instant.now());
                    verificationToken.setExpired(true);
                    tokenJpaService.saveToken(verificationToken);
                    userJpaService.saveUser(baseUser);

                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createSuccessMessage( "Successfully verified your account"));

            case "PASSWORD_RESET":
                // For password reset, we don't change the verified status but may handle other logic here
                OtpRequest.Response response = OtpRequest.Response.builder().otpId(verificationToken.getId()).build();
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createSuccessResponse(response,"User successfully created"));

            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createFailureResponse("Invalid OTP purpose", "The OTP purpose is not recognized"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> regenerateVerificationTokenAndSendEmail(String identifier) {
        try {
            Optional<BaseUser> optionalUser = userJpaService.findByEmail(identifier);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("User not found", "Invalid identifier"));
            }
            BaseUser existingUser = optionalUser.get();
            // Check if the user is already verified
            if (existingUser.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("User already verified", "No need to regenerate OTP"));
            }

//            VerificationToken verificationToken = new VerificationToken(existingUser, "SIGNUP");
            Optional<VerificationToken> optionalToken = tokenJpaService.findByBaseUser(existingUser);

            if (optionalToken.isPresent()) {
                VerificationToken token = optionalToken.get();
                String newConfirmationToken = UserValidation.generateNumericOTP();
                token.setConfirmationToken(newConfirmationToken);
                sendVerificationToken(token);
                tokenJpaService.saveToken(token);
                return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("OTP successfully resent"));
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createFailureResponse("No verification token found", "Please try register again"));
            }
        }catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Token regeneration failed", "Reason: "+ ex.getMessage()));
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Operation failed", "Reason: "+ ex.getMessage()));
        }
    }


    @Override
    public ResponseEntity<ApiResponse<?>> initiateResetPassword(String identifier) {
        try {
            Optional<BaseUser> optionalUser = userJpaService.findByEmail(identifier);
            if (optionalUser.isPresent()) {
                BaseUser user = optionalUser.get();

                // Handle existing tokens
                Optional<VerificationToken> existingToken = tokenJpaService.findByBaseUser(user);
                if (existingToken.isPresent()) {
                    // If any token exists, delete it
                    tokenJpaService.deleteToken(existingToken.get());
                }

                // Generate and save a new OTP for password reset
                VerificationToken newToken = new VerificationToken(user, "PASSWORD_RESET");
                sendOtpToResetPassword(newToken);
                tokenJpaService.saveToken(newToken);

                return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("Password reset token sent to user"));
            }
        }catch (RuntimeException ex){

            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("Password change failed", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Password change failed", ex.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("User not found", "Invalid identifier"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> completeResetPassword(ResetPassword.ResetComplete resetComplete) {
        try {
            var user = userJpaService.findByEmail(resetComplete.getIdentifier());
            if (user.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createFailureResponse("User does not exist", "User does not exist"));
            }
            BaseUser baseUser = user.get();

            Optional<VerificationToken> otpEntity = tokenRepository.findByBaseUser(baseUser);
            if (otpEntity.isPresent()){
                VerificationToken verificationToken = otpEntity.get();
                if (verificationToken.isExpired() || Instant.now().isAfter(verificationToken.getExpiry())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(createFailureResponse("Expired OTP", "The OTP has expired. Please request a new one."));
                }

                // Check if the provided OTP matches the stored OTP
                if (!verificationToken.getConfirmationToken().equals(resetComplete.getOtp())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(createFailureResponse("Invalid OTP", "The OTP provided is incorrect."));
                }

                baseUser.setPassword(passwordEncoder.encode(resetComplete.getNewPassword()));
                userJpaService.saveUser(baseUser);

                // Optionally, mark the OTP as expired or delete it after use
                verificationToken.setExpired(true);
                tokenJpaService.saveToken(verificationToken);



                return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("Password Successfully reset"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Invalid credentials", "Invalid credentials"));
        }catch (RuntimeException ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createFailureResponse("Password change failed", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("Password change failed", ex.getMessage()));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateUsername(String userId,UpdateUsername request) {
        Optional<BaseUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            BaseUser baseUser = optionalUser.get();
            baseUser.setUserName(request.getUserName());
            userJpaService.saveUser(baseUser);
            return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("You have successfully changed your username "));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("User not found", "Invalid credentials"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> fetchUserDetails(String userId) {
        log.info("This is the userId in the service " + userId);
        Optional<BaseUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            BaseUser baseUser = optionalUser.get();
            UserProfileDetailResponse userProfileDetailResponse = UserProfileDetailResponse.builder()
                    .id(baseUser.getId())
                    .email(baseUser.getEmail())
                    .userName(baseUser.getUsername()).build();
            return ResponseEntity.status(HttpStatus.OK).body(createSuccessResponse(userProfileDetailResponse,"Successfully retrieved the users profile info "));
        }
        log.error("here cause the user is not found ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("User not found", "Invalid credentials"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> changePassword(String userId, ChangePasswordRequest changePasswordRequest) {
        log.info("This is the userId in the service " + userId);
        Optional<BaseUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            BaseUser baseUser = optionalUser.get();
            baseUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userJpaService.saveUser(baseUser);
            return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("You have successfully changed your password"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFailureResponse("User not found", "Invalid credentials"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> createCommunity(String userId, CreateCommunityRequest communityRequest) {
        Optional<BaseUser> optionalUser = userJpaService.findById(userId);
        if (optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse("Invalid Credentials", "Kindly Signup"));
        }
        BaseUser baseUser = optionalUser.get();
        Community community  = Community.builder()
                .communityName(communityRequest.getCommunityName())
                .createdBy(baseUser)
                .isActive(true)
                .build();

        // Save the community to the database
        Community savedCommunity = communityJpaService.create(community);

        // Add the creator to the community's user set
        baseUser.getCommunities().add(savedCommunity);
        userJpaService.saveUser(baseUser); // Update the user's communities

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSuccessResponse(savedCommunity, "Community successfully created"));

    }

    @Override
    public ResponseEntity<ApiResponse<?>> joinWaitList(WaitListDto.Request waitlist) {
        WaitList waitList =  WaitList.builder()
                .phoneNo(waitlist.getPhoneNo())
                .fullName(waitlist.getFullName())
                .email(waitlist.getEmail()).build();
        WaitList savedWaitList = waitListJpaInterface.createWaitList(waitList);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSuccessResponse(savedWaitList, "Congratulations!, you have successfully joined the waitlist"));
    }

    private void sendVerificationToken(VerificationToken token) {
        BaseUser user = token.getBaseUser();
            emailService.sendTokenToUsersEmail(user,token.getConfirmationToken());
    }

    private void sendOtpToResetPassword(VerificationToken token) {
        BaseUser user = token.getBaseUser();
            emailService.sendResetPasswordEmail(user,token.getConfirmationToken());
    }

    @Override
    public List<String> generateUsernameSuggestions(String baseUsername) {
        List<String> suggestions = new ArrayList<>();
        Random random = new Random();

        while (suggestions.size() < 3) {
            // Append a random number to the base username
            int randomNumber = random.nextInt(1000);  // Generates a number between 0 and 999
            String suggestion = baseUsername + randomNumber;

            // Ensure the suggestion is unique
            if (!isUsernameTaken(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }



    @Override
    public boolean isUsernameTaken(String username) {
        return userJpaService.existsBaseUserByUserName(username);
    }

    // Helper method to determine if the identifier is an email
    private boolean isEmail(String identifier) {
        return identifier.contains("@");
    }

}
