package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Comment;
import com.Synctec.Synctec.domains.Community;
import com.Synctec.Synctec.repository.CommentRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.CommentJpaService;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.CommunityJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentJpaImpl implements CommentJpaService {
    private final CommentRepository commentRepository;


    @Override
    public Comment createComment(Comment comment) {
       return commentRepository.save(comment);
    }
}
