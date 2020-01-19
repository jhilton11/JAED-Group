package com.appify.jaedgroup.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appify.jaedgroup.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.Holder>{
    private Context context;

    public TransactionAdapter() {

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

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
