package com.Synctec.Synctec.utils.Validators;

import lombok.Data;

@Data
public class PasswordValidator {

    public  static boolean validatePassword(String password, String confirmPassword){
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
}
