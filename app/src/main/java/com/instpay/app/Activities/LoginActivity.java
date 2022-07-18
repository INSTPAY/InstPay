package com.instpay.app.Activities;

import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupBtn.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));

        binding.loginBtn.setOnClickListener(v -> {
            if (binding.accountNo.getText().toString().trim().length() < 13) {
                binding.accountNo.setError("Please enter a valid 13 digit account number.");
                return;
            }
            if (binding.accountNo.getText().toString().trim().length() < 6) {
                binding.accountNo.setError("Please enter a valid 6 digit pin.");
                return;
            }

            JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.auth_login_url), null, authResponse -> {
                try {
                    MY_ACCOUNT = authResponse.getString("account");
                    MY_TOKEN = authResponse.getString("token");
                } catch (JSONException e) { e.printStackTrace(); }

                if (binding.keepLoggedIn.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_preference_auth), MODE_PRIVATE).edit();
                    try {
                        editor.putString("account", authResponse.getString("account"));
                        editor.putString("token", authResponse.getString("token"));
                    } catch (JSONException e) { e.printStackTrace(); }
                    editor.apply();
                }

                startActivity(new Intent(this, HomeActivity.class));
                finish();

            }, error -> {
                Log.d("TAG", "error: ", error);
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("account", binding.accountNo.getText().toString().trim());
                        object.put("pin", binding.mPin.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(authRequest);
        });
    }
}