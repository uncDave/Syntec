package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.repository.BaseUserRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.UserJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserJpaServiceImple implements UserJpaService {
    private final BaseUserRepository userRepository;


    @Override
    public Optional<BaseUser> findByEmail(String email) {
        return userRepository.findBaseUserByEmail(email);
    }

    @Override
    public Optional<BaseUser> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
//    .orElseThrow(()-> new RuntimeException("User not found"));

    @Override
    public Optional<BaseUser> findById(String id) {
        return  userRepository.findById(id);
    }

    @Override
    public boolean existsBaseUserByUserName(String userName) {
        boolean val = userRepository.existsBaseUserByUserName(userName);
        return  val;
    }

    @Override
    public Optional<BaseUser> findBaseUserByUserName(String userName) {
        return  userRepository.findBaseUserByUserName(userName);
    }

    @Override
    public BaseUser findByUserName(String username) {
        return null;
    }

    @Override
    public BaseUser saveUser(BaseUser user) {
        BaseUser savedUser = userRepository.save(user);
        return savedUser;
    }
}
