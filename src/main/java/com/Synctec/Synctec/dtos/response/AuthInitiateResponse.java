package com.Synctec.Synctec.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthInitiateResponse {
    private String initiateUrl;
}
