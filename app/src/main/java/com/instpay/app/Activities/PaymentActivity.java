package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.USER_TOKEN;
import static com.instpay.app.App.requestQueue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityPaymentBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    ActivityPaymentBinding binding;
    User payee;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        payee = Parcels.unwrap(getIntent().getParcelableExtra("PAYEE"));
        amount = getIntent().getStringExtra("AMOUNT");

        Glide.with(this).load(payee.getPhone()).placeholder(R.drawable.ic_person).into(binding.paymentDp);
        binding.paymentName.setText(payee.getName());
        binding.paymentAccount.setText(payee.getAccount());
        binding.paymentAmount.setText(amount);
        binding.pay.setOnClickListener(v -> {
            if (binding.paymentAmount.getText().toString().trim().isEmpty()){
                binding.paymentAmount.setError("Enter amount first!");
                return;
            }
            JsonObjectRequest paymentRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.payment_url), null, paymentResponse -> {

            }, error -> {
                Log.d(TAG, "error: ", error);
                finish();
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("to", payee.getAccount());
                        object.put("method", 0);
                        object.put("amount", Double.parseDouble(binding.paymentAmount.getText().toString().trim()));
                        object.put("account", ME.getAccount());
                        object.put("token", USER_TOKEN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(paymentRequest);
        });
    }
}