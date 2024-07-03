package com.Synctec.Synctec.service.interfaces.JpaInterfaces;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.VerificationToken;

import java.util.Optional;

public interface TokenJpaService {
     void saveToken(VerificationToken token);
     void deleteToken(VerificationToken token);
     Optional<VerificationToken> findByConfirmationToken(String token);
     Optional<VerificationToken> findByBaseUser( BaseUser existingUser);

}
