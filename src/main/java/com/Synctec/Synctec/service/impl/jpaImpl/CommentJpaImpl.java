package com.Synctec.Synctec.service.impl.jpaImpl;

import com.Synctec.Synctec.domains.Comment;
import com.Synctec.Synctec.domains.Community;
import com.Synctec.Synctec.repository.CommentRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.CommentJpaService;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.CommunityJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentJpaImpl implements CommentJpaService {
    private final CommentRepository commentRepository;


    @Override
    public Comment create(Comment comment) {
       return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(String commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public Optional<Comment> findByParentId(String parentId) {
        return commentRepository.findByParentId(parentId);
    }
}
