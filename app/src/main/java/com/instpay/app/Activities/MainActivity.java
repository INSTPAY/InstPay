package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.USER_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.instpay.app.Models.User;
import com.instpay.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(getString(R.string.shared_preference_auth), MODE_PRIVATE);

        if (preferences.contains("account") && preferences.contains("token")) {
            USER_TOKEN = preferences.getString("token", null);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.user_url), null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                startActivity(new Intent(this, HomeActivity.class));
            }, error -> {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG", "error: ", error);
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("account",preferences.getString("account", null));
                        object.put("token",preferences.getString("token", null));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };
            requestQueue.add(request);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}