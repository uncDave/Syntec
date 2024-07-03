package com.Synctec.Synctec.domains;

import com.Synctec.Synctec.security.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Random;



@Entity
@Table(name = "verifcation_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String Id;

    private Instant expiry;
    @Convert(converter = AttributeEncryptor.class)
    private String confirmationToken;
    private String email;
    private String purpose;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    private boolean expired;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_Id")
    private BaseUser baseUser;

    public VerificationToken(BaseUser baseUser,String purpose) {
        this.baseUser = baseUser;
        this.email = baseUser.getEmail();
        createdDate = new Date();
        this.purpose = purpose;
        this.expired = false;
        this.expiry = Instant.now().plus(Duration.ofSeconds(300));
        confirmationToken = generateNumericOTP();
    }
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

}
