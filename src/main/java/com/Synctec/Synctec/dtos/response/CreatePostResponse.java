package com.Synctec.Synctec.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostResponse {
    private String userId;
    private String NameOfPoster;
    private String postId;
    private String title;
    private String slug;
    private String mediaUrl;

}
