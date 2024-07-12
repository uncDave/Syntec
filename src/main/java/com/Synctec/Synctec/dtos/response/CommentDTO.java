package com.Synctec.Synctec.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CommentDTO {
    private String commentId;
    private String userId;
    private String username;
    private String content;
    private String parentId;
    private int likeCount;
    private Instant createdAt;
    private List<CommentDTO> replies;
}
