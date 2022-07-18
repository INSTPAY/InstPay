package com.instpay.app;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.instpay.app.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    public static RequestQueue requestQueue;
    public static String MY_ACCOUNT;
    public static String MY_TOKEN;
    public static User ME;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static String mongoDbDateConverter(String format, String val) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(val);
            assert date != null;
            return new SimpleDateFormat(format, Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static String getVolleyErrorMessage(VolleyError error) {
//        String msg = "";
//        try {
//            msg = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8)).getString("message");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return msg;
//    }

    public static void hideKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
