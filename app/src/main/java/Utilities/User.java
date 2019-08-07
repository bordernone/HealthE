package Utilities;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.coderslab.yourheartbeat.R;

public class User {
    public static boolean isUserLoggedIn() {
        Boolean isLoggedIn;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            isLoggedIn = true;
        } else {
            // No user is signed in
            isLoggedIn = false;
        }
        return isLoggedIn;
    }

    public static boolean isValidBloodGrpId(Integer id, Context _this){
        String[] bloodGrpArray = _this.getResources().getStringArray(R.array.blood_grp_array_reg);
        Integer lengthOfArray = bloodGrpArray.length;
        if (id < lengthOfArray && id > 0){
            return true;
        } else {
            return false;
        }
    }
}
