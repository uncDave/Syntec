package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Community;
import com.Synctec.Synctec.repository.CommunityRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.CommunityJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityJpaService {

    private  final CommunityRepository communityRepository;

    @Override
    public Community create(Community community) {
        Community savedCommunity = communityRepository.save(community);
        return savedCommunity;
    }
}
