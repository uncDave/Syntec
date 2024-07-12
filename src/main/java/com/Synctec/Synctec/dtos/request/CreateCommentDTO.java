package com.Synctec.Synctec.dtos.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;
import java.util.List;

public class CreateCommentDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String postId;
//        private String commentId;
        private String content;

    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateCommentResponse {
        private String postId;
        private String commentId;
        private String parentId;
        private String username;
        private int likeCount;
        private String userId;
        private String content;
        private Instant createdAt;
        private List<CreateReplyDto.CreateReplyResponse> replies;
    }


}
