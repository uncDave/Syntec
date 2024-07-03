package com.Synctec.Synctec.service.interfaces.JpaInterfaces;

import com.Synctec.Synctec.domains.BaseUser;

import java.util.Optional;

public interface UserJpaService {
    Optional<BaseUser> findByEmail(String identifier);

    Optional<BaseUser> findByGoogleId(String googleId);
    Optional<BaseUser> findById(String id);
     boolean existsBaseUserByUserName(String userName);
    Optional<BaseUser> findBaseUserByUserName(String userName);
    BaseUser findByUserName(String username);
    BaseUser saveUser(BaseUser user);
}
