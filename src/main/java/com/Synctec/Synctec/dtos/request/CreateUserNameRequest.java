package com.Synctec.Synctec.dtos.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CreateUserNameRequest {
    private String userId;
    private String userName;
}
