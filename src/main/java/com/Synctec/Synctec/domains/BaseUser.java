package com.Synctec.Synctec.domains;

;
import com.Synctec.Synctec.enums.Roles;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseUser extends BaseEntity implements UserDetails {

    private static  final long serialVersionUID=1l;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email; // Stores email

//    @Column(nullable = false)
    private String password;

    private boolean isEmail; // Flag to indicate if the identifier is an email

    private String userName;

    @Enumerated(EnumType.STRING)
    private Roles role;

//    @OneToOne
//    @JoinColumn(name = "verification_token_id")
//    private VerificationToken verificationToken;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isVerified;
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isActive;

    @Column(name = "google_id", unique = true)
    private String googleId; // Google user ID
    private String twitterId; // Twitter user ID

//    @Column(unique = true)
//    private String userId = ULIDUtil.nextValue();

//    private String firstName;
//    private String lastName;


    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private Instant lastLogin;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant enabledAt;
    private LocalDateTime disabledAt;

    @ManyToMany
    @JoinTable(
            name = "user_community",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id")
    )
    private Set<Community> communities;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes;


//    @Column(columnDefinition = "BOOLEAN DEFAULT false")
//    private boolean isBlocked;
//    private boolean isDeleted;

//    private int failedLoginAttempts;


    private String generateNumericOTP() {
        Random random = new Random();
        int otpLength = 6;
        StringBuilder otpBuilder = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            int digit = random.nextInt(10);
            otpBuilder.append(digit);
        }
        return otpBuilder.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().toString()));
//        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
