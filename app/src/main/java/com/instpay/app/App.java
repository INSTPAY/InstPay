package com.instpay.app;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.instpay.app.Models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    public static RequestQueue requestQueue;
    public static String USER_TOKEN;
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
}
