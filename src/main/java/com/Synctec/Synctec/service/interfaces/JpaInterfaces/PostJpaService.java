package com.Synctec.Synctec.service.interfaces.JpaInterfaces;

import com.Synctec.Synctec.domains.Post;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PostJpaService {
    Post createPost(Post post);
    Optional<Post> findById(String postId);

    Optional<Post> findBySlug(String slug);

     Page<Post> findAllPost(Pageable pageable);
}
