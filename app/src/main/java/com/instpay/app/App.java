package com.instpay.app;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.instpay.app.Models.User;

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
}
