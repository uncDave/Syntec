package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,String> {
    Optional<Like> findByPostAndAndUser(Post post, BaseUser user);
    Optional<Like> findByCommentAndAndUser(Comment comment, BaseUser user);
    Optional<Like> findByReplyAndAndUser(Reply reply, BaseUser user);
}
