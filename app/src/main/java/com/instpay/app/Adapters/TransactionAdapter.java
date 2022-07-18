package com.instpay.app.Adapters;

import static com.instpay.app.Activities.HomeActivity.payees;
import static com.instpay.app.App.ME;
import static com.instpay.app.App.mongoDbDateConverter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.instpay.app.Models.Transaction;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.TransactionLayoutBinding;

import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private static final String TAG = TransactionAdapter.class.getSimpleName();
    private Context context;
    private final ArrayList<Transaction> transactions;
    private final setOnClickListener listener;

    public TransactionAdapter(ArrayList<Transaction> transactions, setOnClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new TransactionHolder(TransactionLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        User payee;
        if (transactions.get(position).getTo().equals(ME.getAccount())) {
            payee = payees.stream().filter(user -> user.getAccount().equals(transactions.get(position).getFrom())).findFirst().orElse(null);
        } else {
            payee = payees.stream().filter(user -> user.getAccount().equals(transactions.get(position).getTo())).findFirst().orElse(null);
        }

        if (payee != null) {
            Glide.with(context).load(payee.getPhoto()).placeholder(R.drawable.ic_person).into(holder.binding.historyPayeePhoto);
            if (transactions.get(position).getTo().equals(ME.getAccount())) {
                holder.binding.historyTransactionTitle.setText(String.format(Locale.getDefault(), "Money received from %s", payee.getName()));
                holder.binding.historyTransactionAmount.setText(String.format(Locale.getDefault(), "+%.02f", transactions.get(position).getAmount()));
                holder.binding.historyTransactionAmount.setTextColor(Color.GREEN);
            } else {
                holder.binding.historyTransactionTitle.setText(String.format(Locale.getDefault(), "Money send to %s", payee.getName()));
                holder.binding.historyTransactionAmount.setText(String.format(Locale.getDefault(), "-%.02f", transactions.get(position).getAmount()));
                holder.binding.historyTransactionAmount.setTextColor(Color.RED);
            }
            holder.binding.historyTransactionDate.setText(mongoDbDateConverter("MMMM dd, yyyy - hh:mm aa", transactions.get(position).getCreatedAt()));
            holder.itemView.setOnClickListener(v -> listener.OnClickListener(payee, transactions.get(position)));
        }
    }

    public interface setOnClickListener{
        void OnClickListener(User payee, Transaction transaction);
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
