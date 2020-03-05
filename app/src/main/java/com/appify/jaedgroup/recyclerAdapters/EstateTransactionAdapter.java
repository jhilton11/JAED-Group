package com.appify.jaedgroup.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.EstateTransaction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EstateTransactionAdapter extends RecyclerView.Adapter<EstateTransactionAdapter.Holder>{
    private Context context;
    private ArrayList<EstateTransaction> transactions;

    public EstateTransactionAdapter(ArrayList<EstateTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transaction_row_layout, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        EstateTransaction transaction = transactions.get(position);

        holder.estate_name.setText(transaction.getEstateName());
        holder.priceTv.setText(String.valueOf(transaction.getAmountPaid()));
        if (transaction.getDatePaid() != null) {
            holder.dateTv.setText("Date:: " + transaction.getDatePaid());
        }
        holder.nameTv.setText(transaction.getName());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView nameTv, dateTv, priceTv, estate_name;
        public Holder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.person_name);
            dateTv = itemView.findViewById(R.id.purchase_date);
            priceTv = itemView.findViewById(R.id.estate_price);
            estate_name = itemView.findViewById(R.id.estate_name);
        }
    }
}
