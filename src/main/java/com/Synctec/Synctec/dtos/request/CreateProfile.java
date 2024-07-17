package com.Synctec.Synctec.dtos.request;

import lombok.*;

import java.time.Instant;

public class CreateProfile {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String userId;
        private String profilePicture;
        private String backgroundPicture;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String dob;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateProfileResponse {
        private String firstName;
        private String lastName;
        private String phoneNumber;


    }


}
