package com.instpay.app.Activities;

import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.hideKeyboard;
import static com.instpay.app.App.requestQueue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.instpay.app.Models.Transaction;
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
        setSupportActionBar(binding.paymentToolbar);

        binding.paymentToolbar.setNavigationOnClickListener(v -> {
            if (!binding.pay.isEnabled()) {
                Toast.makeText(this, "You can't go back while payment is processing.", Toast.LENGTH_SHORT).show();
                return;
            }
            finish();
        });

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
            if (Double.parseDouble(binding.paymentAmount.getText().toString().trim()) < 1) {
                binding.paymentAmount.setError("Minimum transaction amount is 1 rupee!");
                return;
            }

            hideKeyboard(this);
            binding.paymentProgress.setVisibility(View.VISIBLE);
            binding.pay.setEnabled(false);
            binding.paymentAmount.setEnabled(false);
            binding.paymentSendNote.setEnabled(false);

            JsonObjectRequest paymentRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.payment_url), null, paymentResponse -> {
                startActivity(new Intent(this, TransactionDetailsActivity.class).putExtra("TRANSACTION", Parcels.wrap(new Gson().fromJson(paymentResponse.toString(), Transaction.class))).putExtra("PAYEE", Parcels.wrap(payee)));
                finish();
            }, error -> {
                Log.d(TAG, "error: ", error);
                binding.paymentProgress.setVisibility(View.GONE);
                binding.pay.setEnabled(true);
                binding.paymentAmount.setEnabled(true);
                binding.paymentSendNote.setEnabled(true);
            }){
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("to", payee.getAccount());
                        object.put("amount", Double.parseDouble(binding.paymentAmount.getText().toString().trim()));
                        object.put("note", binding.paymentSendNote.getText().toString().trim());
                        object.put("account", MY_ACCOUNT);
                        object.put("token", MY_TOKEN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };
            paymentRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(paymentRequest);
        });
    }

    @Override
    public void onBackPressed() {
        if (!binding.pay.isEnabled()) {
            Toast.makeText(this, "You can't go back while payment is processing.", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }
}