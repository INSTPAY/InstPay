package com.instpay.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.PayeeLayoutBinding;

import java.util.ArrayList;

public class PayeesAdapter extends RecyclerView.Adapter<PayeesAdapter.PayeesHolder> {
    private static final String TAG = PayeesAdapter.class.getSimpleName();
    private Context context;
    private final ArrayList<User> payees;
    private final setOnClickListener listener;

    public PayeesAdapter(ArrayList<User> payees, setOnClickListener listener) {
        this.payees = payees;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PayeesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new PayeesHolder(PayeeLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PayeesHolder holder, int position) {
        Glide.with(context).load(payees.get(position).getPhoto()).placeholder(R.drawable.ic_person).into(holder.binding.payeePhoto);
        holder.binding.payeeName.setText(payees.get(position).getName());
        holder.itemView.setOnClickListener(v -> listener.OnClickListener(payees.get(position)));
    }

    @Override
    public int getItemCount() {
        return payees.size();
    }

    public interface setOnClickListener{
        void OnClickListener(User payee);
    }


    public static class PayeesHolder extends RecyclerView.ViewHolder {
        PayeeLayoutBinding binding;

        public PayeesHolder(@NonNull PayeeLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
