package com.Synctec.Synctec.dtos.response;

import com.Synctec.Synctec.dtos.request.CreateCommentDTO;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostWithDetailsDTO {
    private String postId;
    private String title;
    private String slug;
    private String mediaUrl;
    private int likeCount;
    private Instant createdAt;
    private List<CreateCommentDTO.CreateCommentResponse> comments;
//    private List<ReplyResponseDTO> replies;
}
