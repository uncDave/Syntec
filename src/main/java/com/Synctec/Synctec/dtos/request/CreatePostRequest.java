package com.Synctec.Synctec.dtos.request;

import jakarta.mail.Multipart;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostRequest {
    private String title;
    private MultipartFile multipartFile;
}
