package Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;

import io.coderslab.yourheartbeat.EditUserProfile;

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
        ((Activity) _this).startActivity(intent);
    }

    public static void logInfo(String msg, String className) {
        Log.i("YHB", "(" + className + ") " + msg);
    }

    public static void logError(String msg, String className) {
        Log.e("YHB", "(" + className + ") " + msg);
    }

    public static void simulateBackBtnClick(Activity _this){
        _this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        _this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }
}