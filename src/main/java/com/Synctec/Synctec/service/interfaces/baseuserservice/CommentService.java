package com.Synctec.Synctec.service.interfaces.baseuserservice;

import com.Synctec.Synctec.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface CommentService {

    ResponseEntity<ApiResponse<?>> commentOnPost(String postId, String userId, String content);
}
