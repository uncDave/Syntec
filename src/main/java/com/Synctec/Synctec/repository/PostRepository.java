package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.Like;
import com.Synctec.Synctec.domains.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,String> {
    Optional<Post> findBySlug(String slug);
}
