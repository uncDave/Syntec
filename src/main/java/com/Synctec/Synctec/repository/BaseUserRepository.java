package com.Synctec.Synctec.repository;


import com.Synctec.Synctec.domains.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser,String> {
    Optional<BaseUser> findBaseUserByEmail(String email);
    Optional<BaseUser> findBaseUserByUserName(String userName);
    Optional<BaseUser> findByGoogleId(String googleId);

    boolean existsBaseUserByUserName(String userName);

//    Optional<BaseUser> findByEmail(String email);

}
