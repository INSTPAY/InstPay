package com.instpay.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.instpay.app.Models.User;
import com.instpay.app.R;
import com.instpay.app.databinding.PayeeLayoutBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class PayeesAdapter extends RecyclerView.Adapter<PayeesAdapter.PayeesHolder> implements Filterable {
    private static final String TAG = PayeesAdapter.class.getSimpleName();
    private Context context;
    private final ArrayList<User> payees;
    private final ArrayList<User> filteredPayees;
    private final setOnClickListener listener;

    public PayeesAdapter(ArrayList<User> payees, setOnClickListener listener) {
        this.payees = payees;
        this.filteredPayees = new ArrayList<>(payees);
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
        Glide.with(context).load(filteredPayees.get(position).getPhoto()).placeholder(R.drawable.ic_person).into(holder.binding.payeePhoto);
        holder.binding.payeeName.setText(filteredPayees.get(position).getName());
        holder.itemView.setOnClickListener(v -> listener.OnClickListener(filteredPayees.get(position)));
    }

    @Override
    public int getItemCount() {
        return filteredPayees.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<User> filteredPosts = new ArrayList<>();
                if (constraint == null || constraint.toString().trim().isEmpty()) {
                    filteredPosts.addAll(payees);
                } else {
                    filteredPosts.addAll(payees.stream().filter(user -> user.getAccount().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT)) || user.getName().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = filteredPosts;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPayees.clear();
                filteredPayees.addAll((Collection<? extends User>) results.values);
                notifyDataSetChanged();
            }
        };
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
