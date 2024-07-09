package com.Synctec.Synctec.security;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.dtos.request.LoginDTO;
import com.Synctec.Synctec.enums.Roles;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.UserJpaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Optional;

@Component
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final UserJpaService userJpaService;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer:: disable)
                .authorizeHttpRequests(request-> request.requestMatchers("/api/v1/auth/**","/api/v1/waitlist/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
//                .oauth2Login(oauth2 -> oauth2 // Configure OAuth2 login
//                        .successHandler(this::handleOAuth2LoginSuccess) // Custom success handler
//                        .failureUrl("/loginFailure") // URL to redirect to on login failure
//                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


//    private void handleOAuth2LoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        try {
//            // Extract user details from the authentication principal (OAuth2User)
//            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//            String googleUserId = oAuth2User.getAttribute("sub");
//            String email = oAuth2User.getAttribute("email");
//            String name = oAuth2User.getAttribute("name");
//
//            // Check if the user already exists in your system based on Google ID
//            Optional<BaseUser> optionalUser = userJpaService.findByGoogleId(googleUserId);
//            BaseUser baseUser;
//
//            if (optionalUser.isPresent()) {
//                // If the user exists, retrieve the user
//                baseUser = optionalUser.get();
//            } else {
//                // If the user does not exist, create a new user
//                baseUser = BaseUser.builder()
//                        .uniqueIdentifier(email)
//                        .isVerified(true)
//                        .role(Roles.valueOf("user"))
//                        .isEmail(true)
//                        .googleId(googleUserId)
//                        .userName(name)
//                        .build();
//
//                userJpaService.saveUser(baseUser);
//            }
//
//            // Generate a JWT token for the user (new or existing)
//            String jwtToken = jwtService.generateToken(baseUser);
//
//            LoginDTO.Response loginResponse = new LoginDTO.Response(jwtToken);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonResponse = objectMapper.writeValueAsString(loginResponse);
//
//            // Set response headers
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//
//            // Write the JSON response
//            response.getWriter().write(jsonResponse);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during login processing.");
//        }
//    }
//





    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}
