package com.Synctec.Synctec.security;

import com.Synctec.Synctec.domains.BaseUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements  JwtService{

    private final PropertiesConfiguration configuration;

    private final String SECRET_KEY = "6e5bc43a4f8a6562703ecc8f18fc348fdde509eab48fce875af0d03f6317bcee";
    @Override
    public String generateToken(UserDetails userDetails) {
        BaseUser user = (BaseUser) userDetails;
        return Jwts.builder()
                .subject(((BaseUser) userDetails).getId())
                .claim("Active", user.isActive())
                .claim("role", user.getAuthorities())
                .claim("userName", user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 36000000))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("here to check if the jwt is valid " + token);
        String userIdFromToken = extractUserId(token); // Extracts the user ID from the token
        log.info("the user id " + userIdFromToken);
        log.info("the user id from the base user " + ((BaseUser) userDetails).getId());
//        String username = extractUsername(token);
        return (userIdFromToken.equals(((BaseUser) userDetails).getId())) && !isTokenExpired(token); // Compares user IDs
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims,T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return  Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    @Override
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject); // Extracts the subject (user ID) from the token
    }
}
