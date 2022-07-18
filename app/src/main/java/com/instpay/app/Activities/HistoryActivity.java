package com.instpay.app.Activities;

import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.instpay.app.Adapters.TransactionAdapter;
import com.instpay.app.Models.Transaction;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityHistoryBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();
    ActivityHistoryBinding binding;
    ArrayList<Transaction> transactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.historyToolbar);

        transactions = new ArrayList<>();
        binding.allTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.allTransactions.setAdapter(new TransactionAdapter(transactions, (payee, transaction) -> startActivity(new Intent(this, TransactionDetailsActivity.class).putExtra("PAYEE", Parcels.wrap(payee)).putExtra("TRANSACTION", Parcels.wrap(transaction)))));

        binding.historyToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi(){
        JsonArrayRequest transactionRequest = new JsonArrayRequest(Request.Method.POST, getString(R.string.all_transactions_url), null, transactionResponse -> {
            for (int i=transactions.size(); i<transactionResponse.length(); i++){
                try {
                    transactions.add(0, new Gson().fromJson(transactionResponse.getJSONObject(i).toString(), Transaction.class));
                    if (binding.allTransactions.getAdapter() != null) binding.allTransactions.getAdapter().notifyItemInserted(0);
                } catch (JSONException e) {e.printStackTrace();}
            }
        }, error -> {
            Log.d(TAG, "updateUi: ", error);
        }) {
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
        transactionRequest.setRetryPolicy(new DefaultRetryPolicy());
        requestQueue.add(transactionRequest);
    }
}