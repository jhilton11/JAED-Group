package com.appify.jaedgroup.recyclerAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.fragments.RealEstateFragment;
import com.appify.jaedgroup.model.Estate;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EstateRecyclerAdapter extends RecyclerView.Adapter<EstateRecyclerAdapter.Holder>{
    private ArrayList<Estate> estateArrayList;
    private RealEstateFragment.OnFragmentRealEstateListener mListener;
    private Context context;

    public EstateRecyclerAdapter(ArrayList<Estate> estateArrayList, RealEstateFragment.OnFragmentRealEstateListener mListener) {
        this.estateArrayList = estateArrayList;
        this.mListener = mListener;
        Log.d("msg", "In the recycler adapter already");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.estate_row_layout, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Estate estate = estateArrayList.get(position);

        holder.estateName.setText(estate.getName());
        holder.estateAddress.setText(estate.getAddress());
        Glide.with(context).load(estate.getImgUrl()).into(holder.imageView);
        Log.d("msg", "added item "+position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onEstateClick(estate);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return estateArrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView estateName;
        TextView estateAddress;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.estate_image);
            estateName = itemView.findViewById(R.id.estate_name);
            estateAddress = itemView.findViewById(R.id.estate_address);
        }
    }
}
