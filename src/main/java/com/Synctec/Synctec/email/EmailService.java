package com.Synctec.Synctec.email;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.repository.BaseUserRepository;
import com.Synctec.Synctec.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TokenRepository tokenRepository;
    private final BaseUserRepository baseUserRepository;

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }



    @Async
    public void sendTokenToUsersEmail(BaseUser baseUser, String confirmationLink) {
        log.info("Trying to send the otp");

        String subject = "Email Verification";

        String senderName = "Syntec";

        String mailContent = "Hello, " +" \n" + "\n" +
                "Thank you for choosing Syntec! We're excited to have you on board. To complete your registration, please use the one-time password (OTP) provided below:" +
                "\n" +
                "OTP: " + confirmationLink + " \nIf you didn't request this OTP, please disregard this message. Your security is important to us at:" +
                "\nAdminOne@gmail.com" + "\nThank you for trusting us" +
                "\nSyntec Team.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(baseUser.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setFrom("chukwudileid@gmail.com" + senderName);
        mailMessage.setText(mailContent);
        sendEmail(mailMessage);
    }

    @Async
    public void sendNewPasswordToUser(BaseUser baseUser,String newPassword) {

        String subject = "Forgot Password";

        String senderName = "Syntec";

        String mailContent = "Hello, " + "\n" +
//                baseUser.getFirstName() +" " + baseUser.getLastName() + "\n" +
                "Kindly use Login with this password and do well to change it immediately. your new password is  "+ newPassword  +
                "\n" +
                "\nAdminOne@gmail.com\n" +
                "Thank you for trusting us." +
                "\n Syntec Team.";


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(baseUser.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setFrom("Syntec@gmail.com" + senderName);
        mailMessage.setText(mailContent);
        sendEmail(mailMessage);
    }

    @Async
    public void sendResetPasswordEmail(BaseUser user, String otp) {
        String subject = "Reset Password";

        String senderName = "Syntec";

        String mailContent = "Hello, " +
                user.getUsername() + " "  +
                "You requested a password reset. Please use OTP(One Time Password) below to activate your account.\n\n" +
                "\n <b>"  + otp +"</b>"+

                "\n\nIf you didn't initiate this request, please log in to your account and reset your password or contact support" +
                "\n Syntec Team.";


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setFrom("Syntec@gmail.com" + senderName);
        mailMessage.setText(mailContent);
        sendEmail(mailMessage);
    }

}