package com.Synctec.Synctec.repository;


import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<VerificationToken,String> {
    Optional<VerificationToken> findByConfirmationToken(String confirmationToken);
//    Optional<VerificationToken> findTokenByEmail(String userEmail);
    Optional<VerificationToken> findByBaseUser(BaseUser baseUser);
    Optional<VerificationToken> findTokenByConfirmationToken(String confirmationToken);
//    List<VerificationToken> findVerificationTokenByExpiry()
}
