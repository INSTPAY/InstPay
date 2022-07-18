package com.instpay.app.Activities;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.mongoDbDateConverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.instpay.app.Models.Transaction;
import com.instpay.app.Models.User;
import com.instpay.app.databinding.ActivityTransactionDetailsBinding;

import org.parceler.Parcels;

import java.util.Locale;

public class TransactionDetailsActivity extends AppCompatActivity {
    ActivityTransactionDetailsBinding binding;
    Transaction transaction;
    User payee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.transactionDetailsToolbar);

        transaction = Parcels.unwrap(getIntent().getParcelableExtra("TRANSACTION"));
        payee = Parcels.unwrap(getIntent().getParcelableExtra("PAYEE"));

        binding.transactionDetailsToolbar.setNavigationOnClickListener(v -> finish());

        if (transaction.getTo().equals(ME.getAccount()) && transaction.getFrom().equals(payee.getAccount())) {
            binding.toName.setText(ME.getName());
            binding.toAccount.setText(ME.getAccount());
            binding.fromName.setText(payee.getName());
            binding.fromAccount.setText(payee.getAccount());
            binding.transactionTitle.setText(String.format(Locale.getDefault(), "Received from %s", payee.getName()));
        } else if(transaction.getTo().equals(payee.getAccount()) && transaction.getFrom().equals(ME.getAccount())) {
            binding.toName.setText(payee.getName());
            binding.toAccount.setText(payee.getAccount());
            binding.fromName.setText(ME.getName());
            binding.fromAccount.setText(ME.getAccount());
            binding.transactionTitle.setText(String.format(Locale.getDefault(), "Send to %s", payee.getName()));
        }
        binding.transactionId.setText(transaction.get_id());
        binding.transactionAmount.setText(String.format(Locale.getDefault(), "%.02f", transaction.getAmount()));
        binding.transactionDate.setText(mongoDbDateConverter("MMMM dd, yyyy - hh:mm aa", transaction.getCreatedAt()));
        binding.transactionNote.setText(transaction.getNote());

        if (transaction.getNote() != null && !transaction.getNote().isEmpty()) {
            binding.transactionNote.setVisibility(View.VISIBLE);
        } else {
            binding.transactionNote.setVisibility(View.GONE);
        }
    }
}