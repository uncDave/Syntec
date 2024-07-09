package com.Synctec.Synctec.service.impl.BaseUserImpl;

import com.Synctec.Synctec.domains.*;
import com.Synctec.Synctec.dtos.request.*;
import com.Synctec.Synctec.dtos.response.CommentResponseDTO;
import com.Synctec.Synctec.dtos.response.CreatePostResponse;
import com.Synctec.Synctec.dtos.response.PostWithDetailsDTO;
import com.Synctec.Synctec.dtos.response.ReplyResponseDTO;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.*;
import com.Synctec.Synctec.service.interfaces.baseuserservice.PostService;
import com.Synctec.Synctec.utils.ApiResponse;
import com.Synctec.Synctec.utils.SlugUtils;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.Synctec.Synctec.utils.ResponseUtils.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostJpaService postJpaService;
    private final UserJpaService userJpaService;
    private final CommentJpaService commentJpaService;
    private final LikeJpaService likeJpaService;
    private final ReplyJpaInterface replyJpaInterface;
    private final Cloudinary cloudinary;
    private final SlugUtils slugUtils;

    @Override
    public ResponseEntity<ApiResponse<?>> createPost(String userId,MultipartFile multipartFile, String title) {
        try {
            Optional<BaseUser> userOptional = userJpaService.findById(userId);
            if (userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse(" User not found", "User not found"));
            }
            BaseUser baseUser = userOptional.get();
            String slug = SlugUtils.toSlug(title);
            String uniqueSlug = slugUtils.findUniqueSlug(slug);
            String imageUrl = null;

            if (multipartFile != null && !multipartFile.isEmpty()){
                Map uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
                        ObjectUtils.asMap(
                                "public_id",uniqueSlug,
                                "folder", "images",
                                "overwrite", true,
                                "resource_type", "auto"
                        ));
                imageUrl = uploadResult.get("secure_url").toString();
            }
            Post newPost = Post.builder()
                    .title(title)
                    .user(baseUser)
                    .NameOfPoster(baseUser.getUsername())
                    .slug(uniqueSlug)
                    .mediaUrl(imageUrl)
                    .build();
            Post post = postJpaService.createPost(newPost);
            CreatePostResponse createPostResponse = CreatePostResponse.builder()
                    .postId(newPost.getId())
                    .userId(post.getUser().getId())
                    .NameOfPoster(newPost.getNameOfPoster())
                    .title(newPost.getTitle())
                    .mediaUrl(newPost.getMediaUrl())
                    .slug(newPost.getSlug()).build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(createPostResponse, "post successfully created"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllpostWithDetails(int page, int size, String sortDirection, String sortBy) {
        // Determine the sort direction
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Create pageable request
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Post> posts = postJpaService.findAllPost(pageable);

        // Transform posts into DTOs
        List<PostWithDetailsDTO> allPostWithDetails = posts.stream()
                .map(post -> PostWithDetailsDTO.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .mediaUrl(post.getMediaUrl())
                        .slug(post.getSlug())
                        .likeCount(post.getLikeCount())
                        .createdAt(post.getCreatedDate())
                        .comments(post.getComments().stream()
                                .map(comment -> CommentResponseDTO.builder()
                                        .commentId(comment.getId())
                                        .userId(comment.getUser().getId())
                                        .likeCount(comment.getLikeCount())
                                        .username(comment.getUser().getUsername())
                                        .content(comment.getContent())
                                        .createdAt(comment.getCreatedDate())
                                        .replies(comment.getReplies().stream()
                                                .map(reply -> ReplyResponseDTO.builder()
                                                        .replyId(reply.getId())
                                                        .userId(reply.getUser().getId())
                                                        .username(reply.getUser().getUsername())
                                                        .likeCount(reply.getLikeCount())
                                                        .content(reply.getContent())
                                                        .createdAt(reply.getCreatedDate())
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // Return the response
        return ResponseEntity.status(HttpStatus.OK).body(createSuccessResponse(allPostWithDetails, "Successfully retrieved"));

    }

    @Override
    public ResponseEntity<ApiResponse<?>> commentOnPost(String userId,CreateCommentDTO.Request request) {
        String postId = request.getPostId();
        String userIdentifier  = userId;
        String content = request.getContent();
        log.info("This is the userId:{}", userId);
        log.info("This is the postId:{}", postId);

        Optional<Post> postOptional = postJpaService.findById(postId);
        Optional<BaseUser> userOptional = userJpaService.findById(userIdentifier);
        if (postOptional.isPresent() && userOptional.isPresent()) {
            Post post = postOptional.get();
            BaseUser user = userOptional.get();

            Comment comment = Comment.builder()
                    .post(post)
                    .user(user)
                    .content(content)
                    .build();

            Comment savedComment = commentJpaService.create(comment);

            // Maintain the in-memory state by adding the comment to the post's set
            post.getComments().add(savedComment);


            CreateCommentDTO.CreateCommentResponse response = CreateCommentDTO.CreateCommentResponse.builder()
                    .postId(post.getId())
                    .commentId(savedComment.getId())
                    .content(comment.getContent())
                    .username(user.getUsername())
                    .userId(comment.getUser().getId()).build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(response, "Congratulations!, you have successfully commented on a post " + post.getSlug()));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse("Post or User not found", "Post or User not found"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> replyComment(String userId,CreateReplyDto.Request request) {
        String commentId = request.getCommentId();
        String postId = request.getPostId();
        String userIdentifier = userId;
        String content = request.getContent();

        log.info("This is the userId:{}", userId);
        log.info("This is the postId:{}", postId);

        Optional<Comment> commentOptional = commentJpaService.findById(commentId);
        Optional<Post> postOptional =postJpaService.findById(postId);
        Optional<BaseUser> userOptional =userJpaService.findById(userIdentifier);
        if (commentOptional.isPresent() && postOptional.isPresent() && userOptional.isPresent()) {
            Comment comment = commentOptional.get();
            Post post = postOptional.get();
            BaseUser user = userOptional.get();

            // Build the Reply entity
            Reply reply = Reply.builder()
                    .comment(comment)
                    .post(post)
                    .user(user)
                    .content(content)
                    .build();


            Reply savedReply = replyJpaInterface.createReply(reply);
            comment.getReplies().add(savedReply);

            CreateReplyDto.CreateReplyResponse response = CreateReplyDto.CreateReplyResponse.builder()
                    .postId(savedReply.getPost().getId())
                    .commentId(savedReply.getComment().getId())
                    .userId(savedReply.getUser().getId())
                    .username(savedReply.getUser().getUsername())
                    .content(savedReply.getContent()).build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(response, "Congratulations!, you have successfully replied a comment"));


        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse("Post or User or comment not found", "Post or User or comment not found"));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> toggleLike(String userId,PostLikeRequest postLikeRequest) {
        String postId = postLikeRequest.getPostId();
        String userIdentifier = userId;
        Optional<Post> postOptional =postJpaService.findById(postId);
        Optional<BaseUser> userOptional =userJpaService.findById(userId);
        // Check if both post and user exist
        if (postOptional.isPresent() && userOptional.isPresent()) {
            Post post = postOptional.get();
            BaseUser user = userOptional.get();
            // Check if a like entry exists for the post and user
            Optional<Like> existingLike = likeJpaService.findByPostAndUser(post, user);

            if (existingLike.isPresent()) {
                // Like entry exists
                Like like = existingLike.get();
                if (like.isLiked()) {
                    // User currently likes the post, so we unlike it
                    post.setLikeCount(post.getLikeCount() - 1);
                    like.setLiked(false);
                } else {
                    // User currently does not like the post, so we like it
                    post.setLikeCount(post.getLikeCount() + 1);
                    like.setLiked(true);
                }
                likeJpaService.saveLikes(like);
            }else {
                // No like entry exists, create a new like entry
                Like newLike = Like.builder()
                        .post(post)
                        .user(user)
                        .liked(true)
                        .build();
                post.setLikeCount(post.getLikeCount() + 1);
                likeJpaService.saveLikes(newLike);
            }
            // Save the updated post entity
            postJpaService.createPost(post);

        }
        return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("successful"));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> toggleCommentLike(String userId, CommentLikeRequest commentLikeRequest) {
        String commentId = commentLikeRequest.getCommentId();

        Optional<Comment> commentOptional = commentJpaService.findById(commentId);
        Optional<BaseUser> userOptional = userJpaService.findById(userId);

        if (commentOptional.isPresent() && userOptional.isPresent()) {
            Comment comment = commentOptional.get();
            BaseUser user = userOptional.get();
            Optional<Like> existingLike = likeJpaService.findByCommentAndUser(comment, user);
            if (existingLike.isPresent()) {
                Like like = existingLike.get();
                if (like.isLiked()) {
                    comment.setLikeCount(comment.getLikeCount() - 1);
                    like.setLiked(false);
                } else {
                    comment.setLikeCount(comment.getLikeCount() + 1);
                    like.setLiked(true);
                }
                likeJpaService.saveLikes(like);
        }
            else {
                Like newLike = Like.builder()
                        .comment(comment)
                        .user(user)
                        .liked(true)
                        .build();
                comment.setLikeCount(comment.getLikeCount() + 1);
                likeJpaService.saveLikes(newLike);
            }
            commentJpaService.create(comment);
            return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("Like toggled successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse("Comment or user not found","Comment or user not found"));
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> toggleReplyLike(String userId, ReplyLikeRequest replyLikeRequest) {
        String replyId = replyLikeRequest.getReplyId();
        Optional<Reply> replyOptional = replyJpaInterface.findById(replyId);
        Optional<BaseUser> userOptional = userJpaService.findById(userId);
        if (replyOptional.isPresent() && userOptional.isPresent()) {
            Reply reply = replyOptional.get();
            BaseUser user = userOptional.get();
            Optional<Like> existingLike = likeJpaService.findByReplyAndUser(reply, user);

            if (existingLike.isPresent()) {
                Like like = existingLike.get();
                if (like.isLiked()) {
                    reply.setLikeCount(reply.getLikeCount() - 1);
                    like.setLiked(false);
                } else {
                    reply.setLikeCount(reply.getLikeCount() + 1);
                    like.setLiked(true);
                }
                likeJpaService.saveLikes(like);
            } else {
                Like newLike = Like.builder()
                        .reply(reply)
                        .user(user)
                        .liked(true)
                        .build();
                reply.setLikeCount(reply.getLikeCount() + 1);
                likeJpaService.saveLikes(newLike);
            }
            return ResponseEntity.status(HttpStatus.OK).body(createSuccessMessage("Like toggled successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailureResponse("Comment or user not found","Comment or user not found"));
        }
    }


}
