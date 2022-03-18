package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.USER_TOKEN;
import static com.instpay.app.App.mongoDbDateConverter;
import static com.instpay.app.App.requestQueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
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

        }));

        binding.payBtn.setOnClickListener(v -> {
            if (binding.payableAmount.getText().toString().trim().isEmpty()) {
                binding.payableAmount.setError("Enter amount first!");
                return;
            }
            startActivity(new Intent(this, PaymentActivity.class).putExtra("PAYEE", Parcels.wrap(payee)).putExtra("AMOUNT", binding.payableAmount.getText().toString().trim()));
        });

        JsonArrayRequest transactionsRequest = new JsonArrayRequest(Request.Method.POST, getString(R.string.payees_transactions), null, response -> {
            for (int i=0; i<response.length(); i++){
                try {
                    transactions.add(new Gson().fromJson(response.getJSONObject(i).toString(), Transaction.class));
                    Log.d(TAG, "onCreate: "+ mongoDbDateConverter("MMMM dd, yyyy - hh:mm aa", new Gson().fromJson(response.getJSONObject(i).toString(), Transaction.class).getCreatedAt()));
                    if (binding.payeeTransactions.getAdapter() != null) binding.payeeTransactions.getAdapter().notifyItemInserted(transactions.size()-1);
                } catch (JSONException e) {e.printStackTrace();}
            }
            binding.payeeTransactions.smoothScrollToPosition(binding.payeeTransactions.getBottom());
        }, error -> {
            Log.d(TAG, "error: ", error);
        }){
            @Override
            public byte[] getBody() {
                JSONObject object = new JSONObject();
                try {
                    object.put("payee", payee.getAccount());
                    object.put("account", ME.getAccount());
                    object.put("token", USER_TOKEN);
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