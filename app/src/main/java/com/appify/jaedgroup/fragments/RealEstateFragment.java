package com.appify.jaedgroup.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appify.jaedgroup.MainActivity;
import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.recyclerAdapters.EstateRecyclerAdapter;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RealEstateFragment.OnFragmentRealEstateListener} interface
 * to handle interaction events.
 */
public class RealEstateFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog dialog;
    private TextView connectionStatusTv;

    private OnFragmentRealEstateListener mListener;
    private CollectionReference estateRef;
    private ArrayList<Estate> estateArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_real_estate, container, false);

        estateRef = FirebaseFirestore.getInstance().collection("estates");

        connectionStatusTv = view.findViewById(R.id.connection_status_tv);
        dialog = new ProgressDialog(getContext());
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentRealEstateListener) {
            mListener = (OnFragmentRealEstateListener) context;
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

    public interface OnFragmentRealEstateListener {
        // TODO: Update argument type and name
        void onEstateClick(Estate estate);
    }

    private void loadData() {
        if (tasks.checkNetworkStatus(getContext())) {
            estateArrayList = new ArrayList<>();
            connectionStatusTv.setVisibility(View.GONE);
            estateRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        Estate estate = snapshot.toObject(Estate.class);
                        estateArrayList.add(estate);
                    }
                    Log.d("msg", estateArrayList.size()+" estates discovered");
                    EstateRecyclerAdapter adapter = new EstateRecyclerAdapter(estateArrayList, mListener);
                    recyclerView.setAdapter(adapter);

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            connectionStatusTv.setVisibility(View.VISIBLE);
        }
    }
}
