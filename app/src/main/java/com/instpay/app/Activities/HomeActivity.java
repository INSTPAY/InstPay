package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.MY_ACCOUNT;
import static com.instpay.app.App.MY_TOKEN;
import static com.instpay.app.App.requestQueue;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.instpay.app.Adapters.PayeesAdapter;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityHomeBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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
    public static ArrayList<User> payees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        binding.searchPayees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.allPayees.getAdapter() != null){
                    ((PayeesAdapter) binding.allPayees.getAdapter()).getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.qrCode.setOnClickListener(v -> {
            Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    startActivity(new Intent(HomeActivity.this, QRCodeActivity.class));
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    if (permissionDeniedResponse.isPermanentlyDenied()) {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Camera permission required!")
                                .setMessage("We need the camera permission to access camera for scanning QR Codes that you have denied! You can give the permission by clicking \"OPEN SETTINGS\"")
                                .setIcon(R.drawable.ic_camera)
                                .setPositiveButton("OPEN SETTINGS", (dialog, which) -> {
                                    dialog.dismiss();
                                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));
                                }).setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        }).create().show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).onSameThread().check();
        });
        binding.transactionHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (ME != null) {
            Glide.with(this).load(ME.getPhoto()).placeholder(R.drawable.ic_person).into((ImageView) menu.findItem(R.id.profileBtn).getActionView().findViewById(R.id.profileMenuBtnPhoto));
            menu.findItem(R.id.profileBtn).setTitle(ME.getName());
        }
        menu.findItem(R.id.profileBtn).getActionView().findViewById(R.id.profileMenuBtnPhoto).setOnClickListener(v -> {
            if (ME != null) startActivity(new Intent(this, ProfileActivity.class));
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profileBtn && ME != null) startActivity(new Intent(this, ProfileActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void updateUi(){
        // Updating ME...
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getString(R.string.my_account_url), null, response -> {
            ME = new Gson().fromJson(response.toString(), User.class);
            binding.userName.setText(ME.getName());
            binding.userAcNo.setText(ME.getAccount());
            binding.mainBal.setText(String.format(Locale.getDefault(), "%.02f", ME.getBalance()));
            invalidateOptionsMenu();
        }, error -> {
            Log.d("TAG", "error: ", error);
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
        requestQueue.add(request);

        // Updating Payees...
        payees = new ArrayList<>();
        JsonArrayRequest userRequest = new JsonArrayRequest(Request.Method.POST, getString(R.string.all_payees_url), null, response -> {
            for (int i=0; i<response.length(); i++){
                try {
                    payees.add(new Gson().fromJson(response.getJSONObject(i).toString(), User.class));
                } catch (JSONException e) {e.printStackTrace();}
            }
            binding.allPayees.setLayoutManager(new GridLayoutManager(this, 4));
            binding.allPayees.setAdapter(new PayeesAdapter(payees, payee -> startActivity(new Intent(this, PayeeTransactionsActivity.class).putExtra("PAYEE", Parcels.wrap(payee)))));
        }, error -> {
            Log.d(TAG, "error: ", error);
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
        requestQueue.add(userRequest);
    }
}