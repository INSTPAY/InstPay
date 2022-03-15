package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.USER_TOKEN;
import static com.instpay.app.App.requestQueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.instpay.app.Adapters.TransactionAdapter;
import com.instpay.app.Models.Transaction;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    ActivityHomeBinding binding;
    ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transactions = new ArrayList<>();
        binding.transactions.setLayoutManager(new LinearLayoutManager(this));

        binding.userName.setText(ME.getName());
        binding.userAcNo.setText(ME.getAccount());
        binding.mainBal.setText(String.format(Locale.getDefault(), "%.02f", ME.getBalance()));

        binding.qrCode.setOnClickListener(v -> startActivity(new Intent(this, QRCodeActivity.class)));

        fetchTransactions();
    }

    private void fetchTransactions(){
        JsonArrayRequest userRequest = new JsonArrayRequest(Request.Method.POST, getString(R.string.all_transactions_url), null, response -> {
            for(int i=0; i<response.length(); i++) {
                try {
                    transactions.add(new Gson().fromJson(response.get(i).toString(), Transaction.class));
                } catch (JSONException e) {e.printStackTrace();}
            }
            binding.transactions.setAdapter(new TransactionAdapter(transactions));
        }, error -> {
            Log.d(TAG, "error: ", error);
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }){
            @Override
            public byte[] getBody() {
                JSONObject object = new JSONObject();
                try {
                    object.put("account", ME.getAccount());
                    object.put("token", USER_TOKEN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        requestQueue.add(userRequest);
    }
}