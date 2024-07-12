package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.Like;
import com.Synctec.Synctec.domains.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,String> {
    Optional<Post> findBySlug(String slug);
//    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments c LEFT JOIN FETCH c.replies")
//    List<Post> findAllPostsWithComments();
}
