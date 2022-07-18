package com.instpay.app.Activities;

import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.instpay.app.Adapters.PayeeTransactionsAdapter;
import com.instpay.app.Models.Transaction;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityPayeeTransactionsBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PayeeTransactionsActivity extends AppCompatActivity {
    private static final String TAG = PayeeTransactionsActivity.class.getSimpleName();
    ActivityPayeeTransactionsBinding  binding;
    User payee;
    ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayeeTransactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        payee = Parcels.unwrap(getIntent().getParcelableExtra("PAYEE"));

        binding.payeeActivityToolbar.setTitle(payee.getName());
        binding.payeeActivityToolbar.setSubtitle(payee.getAccount());
        setSupportActionBar(binding.payeeActivityToolbar);

        binding.payeeActivityToolbar.setNavigationOnClickListener(v -> finish());

        transactions = new ArrayList<>();
        binding.payeeTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.payeeTransactions.setAdapter(new PayeeTransactionsAdapter(payee, transactions, transaction -> {
            startActivity(new Intent(this, TransactionDetailsActivity.class).putExtra("TRANSACTION", Parcels.wrap(transaction)).putExtra("PAYEE", Parcels.wrap(payee)));
        }));

        binding.payBtn.setOnClickListener(v -> {
            if (binding.payableAmount.getText().toString().trim().isEmpty()) {
                binding.payableAmount.setError("Enter amount first!");
                return;
            }
            startActivity(new Intent(this, PaymentActivity.class).putExtra("PAYEE", Parcels.wrap(payee)).putExtra("AMOUNT", binding.payableAmount.getText().toString().trim()));
            binding.payableAmount.getText().clear();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        JsonArrayRequest transactionsRequest = new JsonArrayRequest(Request.Method.POST, getString(R.string.payees_transactions_url), null, response -> {
            for (int i=transactions.size(); i<response.length(); i++){
                try {
                    transactions.add(new Gson().fromJson(response.getJSONObject(i).toString(), Transaction.class));
                    if (binding.payeeTransactions.getAdapter() != null) binding.payeeTransactions.getAdapter().notifyItemInserted(transactions.size()-1);
                    binding.payeeTransactions.smoothScrollToPosition(binding.payeeTransactions.getBottom());
                } catch (JSONException e) {e.printStackTrace();}
            }
        }, error -> {
            Log.d(TAG, "error: ", error);
        }){
            @Override
            public byte[] getBody() {
                JSONObject object = new JSONObject();
                try {
                    object.put("payee", payee.getAccount());
                    object.put("account", MY_ACCOUNT);
                    object.put("token", MY_TOKEN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object.toString().getBytes(StandardCharsets.UTF_8);
            }
        };
        transactionsRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(transactionsRequest);
    }
}