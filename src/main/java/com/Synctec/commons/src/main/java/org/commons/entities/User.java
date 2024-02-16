package org.commons.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.commons.enums.Gender;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private static  final long serialVersionUID=1l;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    //@Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    private Gender gender;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastLogin;

    @Column
    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    @Column
    @UpdateTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedDate;

    @OneToOne
    @JoinColumn(name = "verification_token_id")
    private VerificationToken verificationToken;

    @Column
    private String uuid;

    private Boolean isVerified = false;

    @PostLoad
    public void generateCustomId() {
        String prefix = ("SYN"+ generateNumericOTP() );
        //Long numericPart = id;
        customId = prefix; //+ numericPart;
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
