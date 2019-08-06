package Utilities;

import android.content.Intent;
import android.util.Log;

public class utils {
    public static boolean isNumberValid(String number) {
        Boolean isValid = true;

        if (number.matches("^(?=(?:[8-9]){1})(?=[0-9]{9}).*")) {
            //Integer.valueOf(number.trim());
        } else {
            isValid = false;
            Log.i("YHB", "Not valid number");
        }

        // check length
        if (number.length() != 10) {
            isValid = false;
            Log.i("YHB", "Invalid phone number length");
        }

        return isValid;
    }
}