package com.instpay.app.Activities;

import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivitySignupBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();
    ActivitySignupBinding binding;
    int y, m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        y = Calendar.getInstance().get(Calendar.YEAR);
        m = Calendar.getInstance().get(Calendar.MONTH);
        d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        binding.uDob.setOnClickListener(view1 -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, (datePicker, year, month, day) -> {
                Calendar date = Calendar.getInstance();
                date.set(year, month, day);
                binding.uDob.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date.getTime()));
                y = year; m = month; d = day;
            }, y, m, d);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });
        binding.setPin.setOnClickListener(v -> {
            if (binding.uName.getText().toString().trim().isEmpty()) {
                binding.uName.setError("User name can't be empty!");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.uEmail.getText().toString().trim()).matches()) {
                binding.uEmail.setError("Please enter a valid email!");
                return;
            }
            if (!Patterns.PHONE.matcher(binding.uPhone.getText().toString().trim()).matches()) {
                binding.uPhone.setError("Please enter a valid phone number!");
                return;
            }
            if (binding.uDob.getText().toString().trim().isEmpty()) {
                binding.uDob.setError("User date of birth can't be empty!");
                return;
            }
            if (binding.uAddress.getText().toString().trim().isEmpty()) {
                binding.uAddress.setError("User address can't be empty!");
                return;
            }
            if (binding.uAadhaar.getText().toString().trim().length() < 12) {
                binding.uAadhaar.setError("A valid Aadhaar number is required for kyc!");
                return;
            }
            if (binding.uPan.getText().toString().trim().length() < 10) {
                binding.uPan.setError("A valid PAN number is required for kyc!");
                return;
            }

            Toast.makeText(this, "Checking email.", Toast.LENGTH_SHORT).show();
            binding.setPin.setEnabled(false);
            JsonObjectRequest checkEmail = new JsonObjectRequest(Request.Method.POST, getString(R.string.send_otp_url), null, response -> {
                try {
                    if (response.getBoolean("status")){
                        binding.detailsArea.setVisibility(View.GONE);
                        binding.passwordArea.setVisibility(View.VISIBLE);
                        binding.createPin.setEnabled(false);
                        binding.confirmPin.setEnabled(false);
                        binding.keepLoggedInSignUp.setEnabled(false);
                        binding.submit.setEnabled(false);
                    } else {
                        binding.setPin.setEnabled(true);
                    }
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {e.printStackTrace();}
            }, error -> {
                Log.d(TAG, "error: ", error);
                binding.setPin.setEnabled(true);
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("email", binding.uEmail.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };
            requestQueue.add(checkEmail);
        });
        binding.back.setOnClickListener(v -> {
            binding.detailsArea.setVisibility(View.VISIBLE);
            binding.passwordArea.setVisibility(View.GONE);
            binding.setPin.setEnabled(true);
        });
        binding.verifyBtn.setOnClickListener(v -> {
            binding.verifyBtn.setEnabled(false);
            JsonObjectRequest verifyOTP = new JsonObjectRequest(Request.Method.POST, getString(R.string.verify_otp_url), null, response -> {
                try {
                    if (response.getBoolean("status")){
                        binding.otp.setEnabled(false);
                        binding.verifyBtn.setEnabled(false);
                        binding.createPin.setEnabled(true);
                        binding.confirmPin.setEnabled(true);
                        binding.keepLoggedInSignUp.setEnabled(true);
                        binding.submit.setEnabled(true);
                        binding.back.setEnabled(false);
                    } else {
                        binding.verifyBtn.setEnabled(true);
                    }
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {e.printStackTrace();}
            }, error -> {
                Log.d(TAG, "error: ", error);
                binding.verifyBtn.setEnabled(true);
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("email", binding.uEmail.getText().toString().trim());
                        object.put("otp", binding.otp.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };
            requestQueue.add(verifyOTP);
        });
        binding.submit.setOnClickListener(v -> {
            if (binding.createPin.getText().toString().trim().length() < 6){
                binding.createPin.setError("Please enter a 6 digit pin!");
                return;
            }
            if (!binding.confirmPin.getText().toString().trim().equals(binding.confirmPin.getText().toString().trim())){
                binding.confirmPin.setError("Pins doesn't match!");
                return;
            }

            binding.submit.setEnabled(false);
            JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.auth_signup_url), null, authResponse -> {
                try {
                    MY_ACCOUNT = authResponse.getString("account");
                    MY_TOKEN = authResponse.getString("token");
                } catch (JSONException e) { e.printStackTrace(); }

                if (binding.keepLoggedInSignUp.isChecked()){
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
                Log.d(TAG, "error: ", error);
                binding.submit.setEnabled(true);
            }){
                @Override
                public byte[] getBody() {
                    User user = new User(binding.uName.getText().toString().trim(), binding.uEmail.getText().toString().trim(), binding.uPhone.getText().toString().trim(), binding.uDob.getText().toString().trim(), binding.uAddress.getText().toString().trim(), binding.uAadhaar.getText().toString().trim(), binding.uPan.getText().toString().trim(), binding.createPin.getText().toString().trim());
                    return new Gson().toJson(user).getBytes(StandardCharsets.UTF_8);
                }
            };
            requestQueue.add(authRequest);
        });
    }
}