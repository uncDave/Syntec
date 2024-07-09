package com.Synctec.Synctec.controllers.Posts;

import com.Synctec.Synctec.dtos.request.*;
import com.Synctec.Synctec.service.interfaces.baseuserservice.PostService;
import com.Synctec.Synctec.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class PostController {
    private final PostService postService;


    @PostMapping("/create-post")
    public ResponseEntity<ApiResponse<?>> createPost(Principal principal, @RequestParam String title,
                           @RequestParam MultipartFile file) throws IOException {
        log.info("This is the title:{}",title);
        log.info("This is the file:{}",file.getOriginalFilename());
        String userId = principal.getName();
        log.info("this is the request:{}",userId);
        return postService.createPost(userId,file,title);
    }

    @GetMapping("/get-allPosts")
    public ResponseEntity<ApiResponse<?>> getAllPost(AllPostDTO request) {
        if (request == null) {
            request = new AllPostDTO();
        }

        // Extract and apply defaults or provided values
        int page = request.getPage();
        int size = request.getSize();
        String sortBy = request.getSortBy();
        String sortDirection = request.getSortDirection();

        return postService.getAllpostWithDetails(
                page,
                size,
                sortDirection,
                sortBy);

    }

    @PostMapping ("/comment")
    public ResponseEntity<ApiResponse<?>> commentOnPost(Principal principal, @RequestBody CreateCommentDTO.Request request){
        String userId = principal.getName();
        log.info("this is the request:{}",request);
        return postService.commentOnPost(userId,request);
    }
    @PostMapping ("/reply-comment")
    public ResponseEntity<ApiResponse<?>> replyComment(Principal principal,@RequestBody CreateReplyDto.Request request){
        String userId = principal.getName();
        log.info("this is the request:{}",request);
       return postService.replyComment(userId,request);
    }

    @PostMapping ("/like-post")
    public ResponseEntity<ApiResponse<?>> likePost(Principal principal,@RequestBody PostLikeRequest request){
        String userId = principal.getName();
        log.info("this is the request to like a post:{}",request);
        return postService.toggleLike(userId,request);
    }

    @PostMapping ("/like-comment")
    public ResponseEntity<ApiResponse<?>> likeComment(Principal principal, @RequestBody CommentLikeRequest request){
        String userId = principal.getName();
        log.info("this is the request to like a post:{}",request);
        return postService.toggleCommentLike(userId,request);
    }

    @PostMapping("/like-reply")
    public ResponseEntity<ApiResponse<?>> toggleReplyLike(Principal principal, @RequestBody ReplyLikeRequest request) {
        String userId = principal.getName();
        return postService.toggleReplyLike(userId, request);
    }



}
