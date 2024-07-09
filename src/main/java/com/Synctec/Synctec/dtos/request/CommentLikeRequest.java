package com.Synctec.Synctec.dtos.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentLikeRequest {
    private String commentId;
}
