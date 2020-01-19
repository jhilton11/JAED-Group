package com.appify.jaedgroup.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.paystack.android.Transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appify.jaedgroup.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link OnTransactionClickListener} interface
 * to handle interaction events.
 */
public class ViewTransactionsFragment extends Fragment {

    private OnTransactionClickListener mListener;

    private TabLayout tabLayout;
    private RecyclerView recyclerView;

    public ViewTransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_transactions, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Estates"));
        tabLayout.addTab(tabLayout.newTab().setText("Investments"));

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Estates")) {
                    loadEstateTransactions();
                } else {
                    loadInvestmentTransactions();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTransactionClickListener) {
            mListener = (OnTransactionClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTransactionClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadEstateTransactions() {
        Toast.makeText(getContext(), "Loaded estates", Toast.LENGTH_SHORT).show();
    }

    private void loadInvestmentTransactions() {
        Toast.makeText(getContext(), "Loaded investments", Toast.LENGTH_SHORT).show();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTransactionClickListener {
        // TODO: Update argument type and name
        void onTransactionsClick(Transaction transaction);
    }

}
