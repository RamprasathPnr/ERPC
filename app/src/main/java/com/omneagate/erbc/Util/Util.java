package com.omneagate.erbc.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.UndoBar.UndoBar;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for entire application
 */
public class Util {

    public static int oneTotal;
    public static int oneHalfTotal;
    public static int twoTotal;
    public static int twoHalfTotal;
    public static int threeTotal;
    public static int totalTotal;


    //simple messageBar for FPS
    public static void messageBar(Activity activity, String message) {
        if (StringUtils.isEmpty(message)) {
            message = "Internal Error";
        }
        UndoBar undoBar = new UndoBar.Builder(activity)//
                .setMessage(message)//
                .setStyle(UndoBar.Style.KITKAT)
                .setAlignParentBottom(true)
                .create();
        undoBar.show();

    }

    /**
     * Registration store in local preference
     *
     * @param context context passing
     */
    public static void storePreferenceRegister(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("register", true);
        editor.apply();
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Change language in android
     *
     * @param languageCode,context
     */
    public static void changeLanguage(Context context, String languageCode) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(languageCode);
        res.updateConfiguration(conf, dm);

        SharedPreferences prefs = context.getSharedPreferences("ERPC", Context.MODE_PRIVATE);
        GlobalAppState.language = prefs.getString("language", languageCode);
        Log.e("lan", GlobalAppState.language);
    }


    public static class CustomComparator implements Comparator<ConnectionDto> {

        @Override
        public int compare(ConnectionDto dto1, ConnectionDto dto2) {
            return dto2.getId() - dto1.getId();
        }
    }


    public String unicodeToLocalLanguage(String keyString) {
        String unicodeString = null;
        try {
            unicodeString = new String(keyString.getBytes(), "UTF8");
        } catch (UnsupportedEncodingException e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
            Log.e("Exception while parsing UTF", keyString);
        }
        return unicodeString;
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static void hideKeyBoard(Context context, View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static InputFilter inputFilter(final String blockCharacterSet) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        return filter;
    }

    public static String appDateFormat(String dateString) {

        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            DateFormat returnFormat = new SimpleDateFormat("dd-MMM-yyyy");
            calendar.setTime(dateFormat.parse(dateString));
            return returnFormat.format(calendar.getTime());
        } catch (ParseException e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static void changeAppLanguage(Context context, boolean language) {

        if (language) {
            Util.changeLanguage(context, "ta");
            MySharedPreference.writeString(context.getApplicationContext(),
                    MySharedPreference.LANGUAGE_SELECT, "ta");
            Log.e("lan", "ta");

        } else {
            Util.changeLanguage(context, "en");
            MySharedPreference.writeString(context.getApplicationContext(),
                    MySharedPreference.LANGUAGE_SELECT, "en");
            Log.e("lan", "en");
        }
    }

    public static String checkAppLanguage(Context mContext) {


        return MySharedPreference.readString(mContext,
                MySharedPreference.LANGUAGE_SELECT, "");
    }
}
