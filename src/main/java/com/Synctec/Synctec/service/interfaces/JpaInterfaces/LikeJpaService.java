package com.Synctec.Synctec.service.interfaces.JpaInterfaces;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.Like;
import com.Synctec.Synctec.domains.Post;

import java.util.Optional;

public interface LikeJpaService {
    void saveLikes(Like like);
    Optional<Like> findByPostAndUser(Post post, BaseUser baseUser);
}
