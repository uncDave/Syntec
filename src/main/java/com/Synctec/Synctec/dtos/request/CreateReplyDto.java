package com.Synctec.Synctec.dtos.request;

import lombok.*;

import java.time.Instant;

public class CreateReplyDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String postId;
        private String commentId;
        private String replyId;
        private String content;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateReplyResponse {
        private String replyId;
        private String postId;
        private String userId;
        private String commentId;
        private String content;
        private int likeCount;
        private String username;
        private Instant createdAt;
    }
}
