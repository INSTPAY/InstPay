package com.instpay.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instpay.app.Models.Transaction;
import com.instpay.app.databinding.TransactionLayoutBinding;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private static final String TAG = TransactionAdapter.class.getSimpleName();
    private Context context;
    private final ArrayList<Transaction> transactions;

    public TransactionAdapter(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new TransactionHolder(TransactionLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }


    public static class TransactionHolder extends RecyclerView.ViewHolder {
        TransactionLayoutBinding binding;

        public TransactionHolder(@NonNull TransactionLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
