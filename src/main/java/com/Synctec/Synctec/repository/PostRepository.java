package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.Like;
import com.Synctec.Synctec.domains.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,String> {
}
