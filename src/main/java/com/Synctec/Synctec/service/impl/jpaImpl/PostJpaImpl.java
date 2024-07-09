package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Post;
import com.Synctec.Synctec.repository.PostRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.PostJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostJpaImpl implements PostJpaService {
    private final PostRepository postRepository;


    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    @Override
    public Page<Post> findAllPost(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
}
