package com.Synctec.Synctec.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TwitterTokenResponse {
    @JsonProperty("token_type")
    private String token_type;
    @JsonProperty("expires_in")
    private int expires_in;
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("refresh_token")
    private String refresh_token;
}
