package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.Comment;
import com.Synctec.Synctec.domains.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,String> {
    Optional<Comment> findByParentId(String commentId);
}
