package com.Synctec.Synctec.service.interfaces.JpaInterfaces;


import com.Synctec.Synctec.domains.Comment;
import com.Synctec.Synctec.domains.Post;

import java.util.Optional;

public interface CommentJpaService {

    Comment create(Comment comment);
    Optional<Comment> findById(String commentId);
    Optional<Comment> findByParentId(String commentId);

}
