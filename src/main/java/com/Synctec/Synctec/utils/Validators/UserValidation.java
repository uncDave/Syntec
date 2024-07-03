package com.Synctec.Synctec.utils.Validators;

import com.Synctec.Synctec.domains.BaseUser;
import com.Synctec.Synctec.domains.VerificationToken;
import com.Synctec.Synctec.email.EmailService;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.EmailValidator;

import java.util.Random;

@RequiredArgsConstructor
public class UserValidation {

    private final EmailService emailService;

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static String normalizePhoneNumber(String phoneNumber) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            // Parse the phone number with the region code for Nigeria
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "NG");
            // Format the phone number to E.164 standard
            return phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{10,15}");
    }


    public static String generateNumericOTP() {
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
