package com.Synctec.Synctec.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    String extractUserId(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
