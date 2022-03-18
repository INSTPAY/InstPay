package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.USER_TOKEN;
import static com.instpay.app.App.requestQueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.instpay.app.Adapters.PayeesAdapter;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    ActivityHomeBinding binding;
    ArrayList<User> payees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        payees = new ArrayList<>();
        binding.allPayees.setLayoutManager(new GridLayoutManager(this, 4));
        binding.allPayees.setAdapter(new PayeesAdapter(payees, payee -> startActivity(new Intent(this, PayeeTransactionsActivity.class).putExtra("PAYEE", Parcels.wrap(payee)))));

        binding.userName.setText(ME.getName());
        binding.userAcNo.setText(ME.getAccount());
        binding.mainBal.setText(String.format(Locale.getDefault(), "%.02f", ME.getBalance()));

        binding.qrCode.setOnClickListener(v -> startActivity(new Intent(this, QRCodeActivity.class)));

        JsonArrayRequest userRequest = new JsonArrayRequest(Request.Method.POST, getString(R.string.all_payees_url), null, response -> {
            for (int i=0; i<response.length(); i++){
                try {
                    payees.add(new Gson().fromJson(response.getJSONObject(i).toString(), User.class));
                    if (binding.allPayees.getAdapter() != null) binding.allPayees.getAdapter().notifyItemInserted(payees.size()-1);
                } catch (JSONException e) {e.printStackTrace();}
            }
        }, error -> {
            Log.d(TAG, "error: ", error);
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
        userRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(userRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        Glide.with(this).load(ME.getPhoto()).placeholder(R.drawable.ic_person).into((ImageView) menu.findItem(R.id.profileBtn).getActionView().findViewById(R.id.profileMenuBtnPhoto));
        menu.findItem(R.id.profileBtn).setTitle(ME.getName());
        return super.onCreateOptionsMenu(menu);
    }
}