package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Post;
import com.Synctec.Synctec.repository.PostRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.PostJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostJpaImpl implements PostJpaService {
    private final PostRepository postRepository;


    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
}
