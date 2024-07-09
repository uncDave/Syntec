package com.Synctec.Synctec.service.interfaces.JpaInterfaces;

import com.Synctec.Synctec.domains.*;

import java.util.Optional;

public interface LikeJpaService {
    void saveLikes(Like like);
    Optional<Like> findByPostAndUser(Post post, BaseUser baseUser);
    Optional<Like> findByCommentAndUser(Comment comment, BaseUser baseUser);
    Optional<Like> findByReplyAndUser(Reply reply, BaseUser baseUser);
}
