package com.Synctec.Synctec.domains;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private String profilePictureUrl;
    private String backgroundPictureUrl;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dob;
    @OneToOne(mappedBy = "profile")
    private BaseUser user;


}
