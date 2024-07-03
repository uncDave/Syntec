package com.Synctec.Synctec.service.impl.thirdpartyauth;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.dtos.request.LoginDTO;
import com.Synctec.Synctec.dtos.response.AuthInitiateResponse;
import com.Synctec.Synctec.dtos.response.GoogleTokenResponse;
import com.Synctec.Synctec.dtos.response.TwitterTokenResponse;
import com.Synctec.Synctec.enums.Roles;
import com.Synctec.Synctec.security.JwtService;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.UserJpaService;
import com.Synctec.Synctec.service.interfaces.thirdpartyauth.AuthorizationService;
import com.Synctec.Synctec.utils.ApiResponse;
import com.Synctec.Synctec.utils.PkceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static com.Synctec.Synctec.utils.ResponseUtils.createFailureResponse;
import static com.Synctec.Synctec.utils.ResponseUtils.createSuccessResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyImpl implements AuthorizationService {

    private final PropertiesConfiguration configuration;
    private final UserJpaService userService;
    private final JwtService jwtService;
    private final UserJpaService userJpaService;
    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
    private String storedCodeVerifier;


    @Override
    public ResponseEntity<ApiResponse<?>> generateAuthorizationUrl() {
        String googleAuthInitiateUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id",configuration.getString("google.client-id"))
                .queryParam("redirect_uri", configuration.getString("google.redirect-uri"))
                .queryParam("scope", "openid email profile")
                .queryParam("state", "custom_state") // Optional: Custom state parameter for CSRF protection
                .toUriString();

        log.info("This is the initiation url " + googleAuthInitiateUrl);

        AuthInitiateResponse authInitiateResponse = AuthInitiateResponse.builder().initiateUrl(googleAuthInitiateUrl).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse(authInitiateResponse,"Google authentication successfully initiated"));

    }

    @Override
    public ResponseEntity<ApiResponse<?>> generateTwitterAuthorizationUrl() {
        try {
            String codeVerifier = PkceUtil.generateCodeVerifier();
            log.info("This is the verifier:{}", codeVerifier);
            String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);
            storedCodeVerifier = codeVerifier; // Store codeVerifier for later use
            log.info("This is the verifier:{}",storedCodeVerifier);

            String twitterAuthInitiateUrl =UriComponentsBuilder.fromHttpUrl("https://twitter.com/i/oauth2/authorize")
                    .queryParam("response_type", "code")
                    .queryParam("client_id", "UnN2LTBzbXhobXZXcW1TNkg2ZUY6MTpjaQ")
                    .queryParam("redirect_uri","http://localhost:8085/api/v1/auth/verify-twitter" )
                    .queryParam("scope", "tweet.read users.read offline.access") // Add required scopes
                    .queryParam("state", "custom_state") // Optional: CSRF protection
                    .queryParam("code_challenge", codeChallenge)
                    .queryParam("code_challenge_method", "S256")
                    .toUriString();

            AuthInitiateResponse authInitiateResponse = AuthInitiateResponse.builder().initiateUrl(twitterAuthInitiateUrl).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse(authInitiateResponse,"Twitter authentication successfully initiated"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createFailureResponse("Error initiating Twitter authentication", "Error initiating Twitter authentication"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> verifyTwitterAuth(String authorizationCode) {
        log.info("this is the authorization code:{}", authorizationCode);
        log.info("This is the verifier:{}",storedCodeVerifier);

        try {
            if (storedCodeVerifier==null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createFailureResponse("Error initiating Twitter authentication", "Error initiating Twitter authentication"));
            }

            String accessTokenResponse = exchangeAuthorizationCodeForToken(authorizationCode, storedCodeVerifier);
            log.info("This is the access token: {}",accessTokenResponse);

            // Ensure access token is valid and not JSON string
            if (accessTokenResponse == null || accessTokenResponse.isEmpty()) {
                log.error("Access token is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createFailureResponse("Failed to retrieve access token", "Access token is null or empty"));
            }

//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(accessTokenResponse);
//            String accessToken = jsonNode.get("access_token").asText();


            // Use the access token to get user information from Twitter API
            String userInfoUrl = "https://api.twitter.com/2/users/me";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessTokenResponse);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("Authorization header: {}", headers.get("Authorization"));

            ResponseEntity<JsonNode> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, JsonNode.class);
            log.info("Raw response from Twitter user info endpoint: {}", userInfoResponse.getBody());

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode userInfo = userInfoResponse.getBody();
                log.info("this is the raw  body:{}",userInfo);
                // Extract user information from the "data" object

                JsonNode dataNode = userInfo.path("data");
                log.info("This is the dataNode:{}",dataNode);
                String twitterId = dataNode.path("id").asText();
                String name = dataNode.path("name").asText();
                String username = dataNode.path("username").asText(); // Assuming this is how Twitter provides user ID

                log.info("This is the username: {}", username);
                log.info("This is the name: {}", name);
                log.info("This is the twitterId: {}", twitterId);

                // Check if the user already exists in the system
                Optional<BaseUser> existingUser = userService.findBaseUserByUserName(username);
                BaseUser baseUser = null;

                if (existingUser.isPresent()) {
                    // User exists, update user details if necessary
                    baseUser = existingUser.get();
                    if (baseUser.getTwitterId() == null) {
                        baseUser.setTwitterId(twitterId);
                    }
                }else {
                    baseUser = BaseUser.builder()
//                            .email(email)
                            .userName(name)
                            .role(Roles.valueOf("user")) // Adjust as necessary
                            .lastLogin(Instant.now())
                            .enabledAt(Instant.now())
                            .twitterId(twitterId)
                            .isVerified(true)
                            .isActive(true)
                            .build();
                }

                userService.saveUser(baseUser);

                String jwtToken = jwtService.generateToken(baseUser);

                LoginDTO.Response response = LoginDTO.Response.builder()
                        .jwtToken(jwtToken)
                        .build();

                return ResponseEntity.status(HttpStatus.OK).body(createSuccessResponse(response, "Twitter authentication successful"));

            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createFailureResponse("Failed to retrieve user info from Twitter", "Failed to retrieve user info from Twitter"));
            }
        } catch (RestClientException e) {
            log.error("Error during Twitter authentication: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createFailureResponse("Error during Twitter authentication", e.getMessage()));
        }
    }



    @Override
    public ResponseEntity<ApiResponse<?>> verifyGoogleAuth(String code, String state, String error) {

        try {
            if (error != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Authorization failed", error));
            }

            if (!isValidState(state)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid state parameter", null));
            }

            // Log the configuration values to verify correctness
            log.info("Google Token URL: {}", configuration.getString("google.token.uri"));
            log.info("Google Client ID: {}", configuration.getString("google.client-id"));
            log.info("Google Redirect URI: {}", configuration.getString("google.redirect-uri"));

            // Construct the token exchange URL
            String requestUrl = configuration.getString("google.token.uri");

            log.info("URL for token exchange: {}", requestUrl);

            // Prepare the request body as form data
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", configuration.getString("google.client-id"));
            params.add("client_secret", configuration.getString("google.client-secret"));
            params.add("redirect_uri", configuration.getString("google.redirect-uri"));
            params.add("grant_type", "authorization_code");

            // Set the content type header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create the HttpEntity with the request body and headers
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();

            // Post the request to exchange the code for an access token
            ResponseEntity<GoogleTokenResponse> tokenResponse = restTemplate.postForEntity(
                    requestUrl, entity, GoogleTokenResponse.class);

            log.info("Token exchange response: {}", tokenResponse);
            log.info("Token exchange body: {}", tokenResponse.getBody());

            if (tokenResponse.getStatusCode() == HttpStatus.OK) {
                GoogleTokenResponse tokens = tokenResponse.getBody();

                if (tokens != null) {
                    // Use the access token to get user information from Google
                    String accessToken = tokens.getAccessToken();
                    log.info("access token: {}",accessToken);
                    log.info("access expiresIn: {}",tokens.getExpiresIn());
                    log.info("access tokenType: {}",tokens.getTokenType());
                    log.info("access refreshToken: {}",tokens.getRefreshToken());

                    String userInfoUrl = UriComponentsBuilder.fromHttpUrl(configuration.getString("google.info.uri"))
                            .queryParam("access_token", accessToken)
                            .toUriString();

                    log.info("This is the info url : {}", userInfoUrl);

                    ResponseEntity<JsonNode> userInfoResponse = restTemplate.getForEntity(userInfoUrl, JsonNode.class);

                    if (userInfoResponse.getStatusCode() == HttpStatus.OK) {

                        JsonNode userInfo = userInfoResponse.getBody();

                        log.info("Complete user info: {}", userInfo.toPrettyString());

                        // Extract user information from the response
                        String email = userInfo.path("email").asText();
                        String name = userInfo.path("name").asText();
                        String googleId = userInfo.path("sub").asText();

                        log.info("Extracted email: {}", email);
                        log.info("Extracted name: {}", name);

                        // Check if the user already exists in the system
                        Optional<BaseUser> existingUser = userService.findByEmail(email);
                        BaseUser baseUser = null;

                        if (existingUser.isPresent()) {
                            // User exists, update user details if necessary
                            log.info("gets here cause the user has been authenticated with google before");
                            baseUser = existingUser.get();
                            if (baseUser.getGoogleId() == null){
                                baseUser.setGoogleId(googleId);
                            }
                        } else {
                            log.info("gets here cause the user uses google to be authenticated for the first time");
                            // User does not exist, create a new user
                            baseUser = BaseUser.builder()
                                    .email(email)
                                    .userName(name)
                                    .role(Roles.valueOf("user"))
                                    .lastLogin(Instant.now())
                                    .enabledAt(Instant.now())
                                    .googleId(googleId)
                                    .isVerified(true)
                                    .isActive(true)
                                    .build();
                        }
                        log.info("this is the identifier: {}", baseUser.getEmail());
                        log.info("this is the email: {}", baseUser.getUsername());
                        userJpaService.saveUser(baseUser);

                        var jwtToken = jwtService.generateToken(baseUser);
                        log.info("This is the jwt token for a google authenticated user " + jwtToken);

                        LoginDTO.Response response = LoginDTO.Response.builder()
                                .jwtToken(jwtToken)
                                .build();

                        return ResponseEntity.status(HttpStatus.OK).body(createSuccessResponse(response, "Login successful"));
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(false, "Failed to retrieve user info", null));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(false, "Token response body is null", null));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Failed to exchange authorization code for tokens", null));
            }
        } catch (RestClientException e) {
            log.error("Error during token exchange or user info retrieval: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Internal server error", e.getMessage()));
        }
    }

    private String exchangeAuthorizationCodeForToken(String authorizationCode, String codeVerifier) {
        try {
            log.info("here to exchange the auth token for access token");
            log.info("This is the stored verifier:{}",storedCodeVerifier);
            log.info("This is the verifier:{}",codeVerifier);
            String tokenUrl = "https://api.twitter.com/2/oauth2/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth("UnN2LTBzbXhobXZXcW1TNkg2ZUY6MTpjaQ", "rvcrD_BXT8mKvQfqWTO8l7oc13nZfa_ZCC0w3qtveHVFVVXGJ_");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("code", authorizationCode);
            body.add("redirect_uri", "http://localhost:8085/api/v1/auth/verify-twitter");
            body.add("code_verifier", codeVerifier);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            log.info("This is the header sent to twitter + {}", body);
            log.info("This is the requestEntity sent to twitter + {}", requestEntity);


            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            String responseBody = responseEntity.getBody();
            log.info("Raw response from Twitter: {}", responseBody); // Log the raw response

            ObjectMapper mapper = new ObjectMapper();
            TwitterTokenResponse tokenResponse = mapper.readValue(responseBody, TwitterTokenResponse.class);
            log.info("This is the token response:{}",tokenResponse);




//            String responseBody = responseEntity.getBody();
//            log.info("Raw response from Twitter: {}", responseBody); // Log the raw response
//
//            // Use ObjectMapper to parse the JSON response into a Map
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> responseMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
//            TwitterTokenResponse tokenResponse = mapper.readValue(responseBody, TwitterTokenResponse.class);
//
//            // Extract individual fields
//            String tokenType = (String) responseMap.get("token_type");
//            int expiresIn = (Integer) responseMap.get("expires_in");
//            String accessToken = (String) responseMap.get("access_token");
//            String scope = (String) responseMap.get("scope");
//            String refreshToken = (String) responseMap.get("refresh_token");

//            log.info("Token Type: {}", tokenType);
//            log.info("Expires In: {}", expiresIn);
//            log.info("Access Token: {}", accessToken);
//            log.info("Scope: {}", scope);
//            log.info("Refresh Token: {}", refreshToken);

            return tokenResponse.getAccess_token();
        } catch (RestClientException e) {
            log.error("Error exchanging authorization code for token: {}", e.getMessage());
            throw new RuntimeException("Error exchanging authorization code for token", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }





    private boolean isValidState(String state) {
        // Replace with your actual validation logic
        return "custom_state".equals(state);
    }


}
