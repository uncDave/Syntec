package com.Synctec.Synctec.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            final String requestURI = request.getRequestURI();

            if (isExemptedUrl(requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid token");
                sendErrorResponse(response, "Please provide a valid JWToken");
                return;
            }

            final String jwt;
            final String userId;
            jwt = authHeader.substring(7);
            log.info("This is the jwt token " + jwt);
            userId = jwtService.extractUserId(jwt);
            log.info("This is the userId " + userId);

            if (!userId.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    CustomAuthenticationToken token = new CustomAuthenticationToken(
                            userId, // User ID as principal
                            null,
                            userDetails.getAuthorities()
                    );

//                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    securityContext.setAuthentication(token);

                    SecurityContextHolder.setContext(securityContext);
                }
                filterChain.doFilter(request, response);

            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("Application/JSON");
            String errorMessage = "{" +
                    "\"error\":\"Request failed\"," +
//                    "\"message\": \"Please provide a valid JsonWebToken\"}";
                    "\"message\": "+e.getMessage();
            response.getWriter().write(errorMessage);
            return;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String errorMessage = "{\"error\": \"Request failed\", \"message\": \"" + message + "\"}";
        response.getWriter().write(errorMessage);
    }

    private boolean isExemptedUrl(String requestURI) {
        if (requestURI == null) {
            return false;
        }

        return requestURI.startsWith("/api/v1/auth/register") ||
                requestURI.startsWith("/api/v1/auth/login") ||
                requestURI.startsWith("/api/v1/auth/set-username")||
                requestURI.startsWith("/api/v1/auth/confirm-otp")||
                requestURI.startsWith("/api/v1/auth/reset-password-otp")||
                requestURI.startsWith("/api/v1/auth/reset-password")||
                requestURI.startsWith("/api/v1/auth/regenerate-otp")||
                requestURI.startsWith("/api/v1/auth/google")||
                requestURI.startsWith("/api/v1/auth/verify")||
                requestURI.startsWith("/api/v1/auth/initiate-twitter")||
                requestURI.startsWith("/api/v1/auth/verify-twitter")||
                requestURI.startsWith("/api/v1/waitlist/join-waitlist");
    }

}
