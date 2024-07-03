package com.Synctec.Synctec.security;

import com.Synctec.Synctec.service.interfaces.JpaInterfaces.UserJpaService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {
    private final UserJpaService userJpaService;

    public CustomUserDetailsService(UserJpaService userJpaService) {
        this.userJpaService = userJpaService;
    }

    // Method to load user by ID
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        return userJpaService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
