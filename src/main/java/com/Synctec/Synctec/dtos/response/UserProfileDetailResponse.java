package com.Synctec.Synctec.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDetailResponse {
    private String id;
    private String email;
    private String userName;
}
