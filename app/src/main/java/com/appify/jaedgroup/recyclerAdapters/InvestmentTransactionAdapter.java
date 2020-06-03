package com.appify.jaedgroup.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.InvestmentTransaction;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InvestmentTransactionAdapter extends RecyclerView.Adapter<InvestmentTransactionAdapter.Holder>{
    private ArrayList<InvestmentTransaction> transactions;
    private Context context;

    public InvestmentTransactionAdapter(ArrayList<InvestmentTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.investment_row_layout, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        InvestmentTransaction transaction = transactions.get(position);
        holder.amountTv.setText(Constants.NAIRA + tasks.getCurrencyString(transaction.getAmountPaid()));
        holder.indateTv.setText(transaction.getDatePaidString());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView amountTv, indateTv;
        public Holder(@NonNull View itemView) {
            super(itemView);
            amountTv = itemView.findViewById(R.id.amount_tv);
            indateTv = itemView.findViewById(R.id.investment_date_tv);
        }
    }
}
