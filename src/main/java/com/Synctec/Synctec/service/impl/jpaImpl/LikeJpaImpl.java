package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.Like;
import com.Synctec.Synctec.domains.Post;
import com.Synctec.Synctec.repository.LikeRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.LikeJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class LikeJpaImpl implements LikeJpaService {
    private final LikeRepository likeRepository;

    @Override
    public void saveLikes(Like like) {
        likeRepository.save(like);
    }

    @Override
    public Optional<Like> findByPostAndUser(Post post, BaseUser baseUser) {
        Optional<Like> optionalLike = likeRepository.findByPostAndAndUser(post, baseUser);
        return optionalLike;
    }
}
