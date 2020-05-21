package com.appify.jaedgroup.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.paystack.android.Transaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.model.InvestmentTransaction;
import com.appify.jaedgroup.recyclerAdapters.EstateTransactionAdapter;
import com.appify.jaedgroup.recyclerAdapters.InvestmentTransactionAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

    private ArrayList<EstateTransaction> transactions;
    private ArrayList<InvestmentTransaction> investmentTransactions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_transactions, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Estates"));
        tabLayout.addTab(tabLayout.newTab().setText("Investments"));
        tabLayout.getTabAt(0).select();
        loadEstateTransactions();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Estates")) {
                    loadEstateTransactions();
                    Log.d("tabClicked", "Estate transactions clicked");
                } else {
                    loadInvestmentTransactions();
                    Log.d("tabClicked", "Investment transactions clicked");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getText().equals("Estates")) {
                    Log.d("tabClicked", "Estate transactions clicked");
                    loadEstateTransactions();
                } else {
                    loadInvestmentTransactions();
                    Log.d("tabClicked", "Investment transactions clicked");
                }
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
        //Toast.makeText(getContext(), "Loaded estates", Toast.LENGTH_SHORT).show();
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("estatetransaction");
        Query query = colRef.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                transactions = new ArrayList();
                if (e==null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        EstateTransaction transaction = snapshot.toObject(EstateTransaction.class);
                        transactions.add(transaction);
                    }

                    EstateTransactionAdapter adapter = new EstateTransactionAdapter(transactions);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e(getClass().getSimpleName(), "error: " + e.getMessage());
                }
            }
        });
    }

    private void loadInvestmentTransactions() {
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("investmentTransaction");
        Query query = colRef.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                investmentTransactions = new ArrayList();
                if (e==null) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        InvestmentTransaction transaction = snapshot.toObject(InvestmentTransaction.class);
                        investmentTransactions.add(transaction);
                    }

                    InvestmentTransactionAdapter adapter = new InvestmentTransactionAdapter(investmentTransactions);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e(getClass().getSimpleName(), "error: " + e.getMessage());
                }
            }
        });
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
