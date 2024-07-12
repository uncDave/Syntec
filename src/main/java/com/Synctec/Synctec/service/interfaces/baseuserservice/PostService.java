package com.Synctec.Synctec.service.interfaces.baseuserservice;

import com.Synctec.Synctec.dtos.request.*;
import com.Synctec.Synctec.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    ResponseEntity<ApiResponse<?>> createPost(String userId,MultipartFile multipartFile,String title);
    ResponseEntity<ApiResponse<?>> getAllpostWithDetails(int page, int size,String sortDirection,String sortBy);
    ResponseEntity<ApiResponse<?>> commentOnPost(String userId,CreateCommentDTO.Request request);
    ResponseEntity<ApiResponse<?>> replyComment(String userId,CreateReplyDto.Request request);
    ResponseEntity<ApiResponse<?>> toggleLike(String userId,PostLikeRequest postLikeRequest);
    ResponseEntity<ApiResponse<?>> toggleCommentLike(String userId, CommentLikeRequest commentLikeRequest);
    ResponseEntity<ApiResponse<?>> toggleReplyLike(String userId, ReplyLikeRequest replyLikeRequest);

    ResponseEntity<ApiResponse<?>> getAllpostWithDetails2(int page, int size,String sortDirection,String sortBy);

}
