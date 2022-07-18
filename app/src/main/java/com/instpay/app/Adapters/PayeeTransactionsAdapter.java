package com.instpay.app.Adapters;

import static com.instpay.app.App.ME;
import static com.instpay.app.App.mongoDbDateConverter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.instpay.app.Models.Transaction;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.PayeeTransactionLayoutBinding;

import java.util.ArrayList;
import java.util.Locale;

public class PayeeTransactionsAdapter extends RecyclerView.Adapter<PayeeTransactionsAdapter.PayeeTransactionHolder> {
    private static final String TAG = PayeeTransactionsAdapter.class.getSimpleName();
    private Context context;
    private final User payee;
    private final ArrayList<Transaction> transactions;
    private final setOnClickListener listener;

    public PayeeTransactionsAdapter(User payee, ArrayList<Transaction> transactions, setOnClickListener listener) {
        this.payee = payee;
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PayeeTransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new PayeeTransactionHolder(PayeeTransactionLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PayeeTransactionHolder holder, int position) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.binding.payeeTransactionContainer.getLayoutParams();
        params.horizontalBias = (transactions.get(position).getTo().equals(ME.getAccount())) ? 0 : 1;
        holder.binding.payeeTransactionContainer.setLayoutParams(params);

        holder.binding.payeeTransactionContainer.setBackground((transactions.get(position).getTo().equals(ME.getAccount())) ? ContextCompat.getDrawable(context, R.drawable.bubble_receiver) : ContextCompat.getDrawable(context, R.drawable.bubble_sender));

        holder.binding.paymentTitle.setText((transactions.get(position).getTo().equals(ME.getAccount())) ? String.format(Locale.getDefault(), "Received from %s", payee.getName()) : String.format(Locale.getDefault(), "Send to %s", payee.getName()));
        holder.binding.amount.setText(String.format(Locale.getDefault(), "%.02f", transactions.get(position).getAmount()));
        holder.binding.paymentTime.setText(mongoDbDateConverter("MMMM dd, yyyy - hh:mm aa", transactions.get(position).getCreatedAt()));
        holder.binding.paymentNote.setText(transactions.get(position).getNote());

        if (transactions.get(position).getNote() != null && !transactions.get(position).getNote().isEmpty()) {
            holder.binding.paymentNote.setVisibility(View.VISIBLE);
        } else {
            holder.binding.paymentNote.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.OnClickListener(transactions.get(position)));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public interface setOnClickListener{
        void OnClickListener(Transaction transaction);
    }


    public static class PayeeTransactionHolder extends RecyclerView.ViewHolder {
        PayeeTransactionLayoutBinding binding;

        public PayeeTransactionHolder(@NonNull PayeeTransactionLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
