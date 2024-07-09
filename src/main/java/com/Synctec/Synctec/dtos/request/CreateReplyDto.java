package com.Synctec.Synctec.dtos.request;

import lombok.*;

public class CreateReplyDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String postId;
        private String commentId;
        private String content;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateReplyResponse {
        private String postId;
        private String userId;
        private String commentId;
        private String content;
        private String username;
    }
}
