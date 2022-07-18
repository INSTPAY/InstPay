package com.instpay.app.Activities;

import static com.instpay.app.App.ME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.instpay.app.R;
import com.instpay.app.databinding.ActivityProfileBinding;

import org.json.JSONException;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.profileToolbar);

        binding.profileToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            getSharedPreferences(getString(R.string.shared_preference_auth), MODE_PRIVATE).edit().remove("account").remove("token").apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUi() {
        Glide.with(this).load(ME.getPhoto()).placeholder(R.drawable.ic_person).into(binding.profileDp);
        binding.profileName.setText(ME.getName());
        binding.profileAcNo.setText(ME.getAccount());
        binding.profileEmail.setText(ME.getEmail());
        binding.profilePhone.setText(ME.getPhone());
        binding.profileDob.setText(ME.getDob());
        binding.profileAddress.setText(ME.getAddress());
        binding.profileAadhaar.setText(ME.getAadhaar());
        binding.profilePan.setText(ME.getPan());
    }
}