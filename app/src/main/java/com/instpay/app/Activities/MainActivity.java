package com.instpay.app.Activities;

import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences(getString(R.string.shared_preference_auth), MODE_PRIVATE);

        MY_ACCOUNT = preferences.getString("account", null);
        MY_TOKEN = preferences.getString("token", null);

        if (MY_ACCOUNT != null && MY_TOKEN != null) {
            fetchAccount();
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }, 1500);
        }
    }

    private void fetchAccount() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.my_account_url), null, response -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, error -> {
            Log.d(TAG, "error: ", error);
            if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_preference_auth), MODE_PRIVATE).edit();
                editor.remove("account");
                editor.remove("token");
                editor.apply();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Server error! We are retrying.", Toast.LENGTH_SHORT).show();
                fetchAccount();
            }
            finish();
        }){
            @Override
            public byte[] getBody() {
                JSONObject object = new JSONObject();
                try {
                    object.put("account", MY_ACCOUNT);
                    object.put("token", MY_TOKEN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object.toString().getBytes(StandardCharsets.UTF_8);
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}