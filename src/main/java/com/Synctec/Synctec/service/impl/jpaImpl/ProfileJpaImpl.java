package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Profile;
import com.Synctec.Synctec.repository.ProfileRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.ProfileJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileJpaImpl implements ProfileJpaService {
    private final ProfileRepository profileRepository;

    @Override
    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }
}
