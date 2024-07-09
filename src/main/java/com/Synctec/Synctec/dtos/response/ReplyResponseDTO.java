package com.Synctec.Synctec.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
@Data
@Builder
public class ReplyResponseDTO {
    private String replyId;
    private String userId;
    private String username;
    private String content;
    private int likeCount;
    private Instant createdAt;
}
