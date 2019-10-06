package Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class utils {

    public static void alertError(String errorMsg, Context _this) {
        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_this);
        alertDialog.setTitle("Error");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setMessage(errorMsg);
        alertDialog.setPositiveButton("Okay", null);
        alertDialog.show();
    }

    public static void alertInfo(String message, Context _this) {
        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_this);
        alertDialog.setTitle("Info");
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Okay", null);
        alertDialog.show();
    }

    public static void moveToActivity(Context _this, Class _class) {
        Intent intent = new Intent(_this, _class);
        _this.startActivity(intent);
    }

    public static void logInfo(String msg, String className) {
        Log.i("YHB", "(" + className + ") " + msg);
    }

    public static void logError(String msg, String className) {
        Log.e("YHB", "(" + className + ") " + msg);
    }

    public static void simulateBackBtnClick(Activity _this) {
        _this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        _this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    public static String generateRandomNumber(int Length) {
        ArrayList<Long> list = new ArrayList<Long>();
        Random random = new Random();
        Calendar rightNow = Calendar.getInstance();
        for (int i = 1; i < 11; i++) {
            list.add(random.nextInt(100) + rightNow.getTimeInMillis());
        }
        Collections.shuffle(list);
        String result = "";
        for (int i = 0; i < Length; i++) {
            result = result + list.get(i);
        }

        return result;
    }

    public static Task<String> sendMail() {
        FirebaseFunctions mFunctions;

        mFunctions = FirebaseFunctions.getInstance();
        return mFunctions
                .getHttpsCallable("sendMail")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData().toString();
                        return result;
                    }
                });
    }

}