package com.Synctec.Synctec.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class WaitListDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
//        @NotNull(message = "All fields required")
//        @NotBlank(message = "All fields required")
        private String fullName;
        private String phoneNo;
        private String email;

    }


}
