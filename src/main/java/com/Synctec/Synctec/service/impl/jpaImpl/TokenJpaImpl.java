package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.VerificationToken;
import com.Synctec.Synctec.repository.TokenRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.TokenJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenJpaImpl implements TokenJpaService {
    private final TokenRepository tokenRepository;

    @Override
    public void saveToken(VerificationToken token) {
        tokenRepository.save(token);
    }

    @Override
    public void deleteToken(VerificationToken token) {
        tokenRepository.delete(token);
    }

    @Override
    public Optional<VerificationToken> findByConfirmationToken(String token) {
        return tokenRepository.findTokenByConfirmationToken(token);
    }

    @Override
    public Optional<VerificationToken> findByBaseUser(BaseUser existingUser) {
        return tokenRepository.findByBaseUser(existingUser);
    }
}
