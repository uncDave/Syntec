package com.Synctec.Synctec.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String userId;

    public CustomAuthenticationToken(String userId, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(userId, credentials, authorities);
        this.userId = userId;
    }

    @Override
    public Object getPrincipal() {
        return userId; // Principal is now the user ID
    }

    public String getUserId() {
        return userId;
    }
}
