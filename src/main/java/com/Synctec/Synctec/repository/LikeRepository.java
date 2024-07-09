package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.Like;
import com.Synctec.Synctec.domains.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,String> {
    Optional<Like> findByPostAndAndUser(Post post, BaseUser user);
}
