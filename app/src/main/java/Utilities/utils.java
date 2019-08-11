package Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class utils {
    public static void alertError(String errorMsg, Context _this) {
        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_this);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(errorMsg);
        alertDialog.setPositiveButton("Okay", null);
        alertDialog.show();
    }

    public static void alertInfo(String message, Context _this) {
        // Alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_this);
        alertDialog.setTitle("Info");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Okay", null);
        alertDialog.show();
    }

    public static void moveToActivity(Context _this, Class _class) {
        Intent intent = new Intent(_this, _class);
        ((Activity) _this).startActivity(intent);
    }

    public static void logInfo(String msg, String className) {
        Log.i("YHB", "(" + className + ") " + msg);
    }

    public static void logError(String msg, String className) {
        Log.e("YHB", "(" + className + ") " + msg);
    }
}