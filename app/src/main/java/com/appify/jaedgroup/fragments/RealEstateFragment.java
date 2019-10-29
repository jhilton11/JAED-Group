package com.appify.jaedgroup.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.recyclerAdapters.EstateRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RealEstateFragment.OnFragmentRealEstateListener} interface
 * to handle interaction events.
 */
public class RealEstateFragment extends Fragment {
    private RecyclerView recyclerView;

    private OnFragmentRealEstateListener mListener;
    private DatabaseReference estateRef;
    private ArrayList<Estate> estateArrayList;

    public RealEstateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_real_estate, container, false);

        estateRef = FirebaseDatabase.getInstance().getReference().child("Estates");

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentRealEstateListener {
        // TODO: Update argument type and name
        void onEstateClick(Estate estate);
    }

    private void loadData() {
        estateArrayList = new ArrayList<>();
        estateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Estate estate = snapshot.getValue(Estate.class);
                    estateArrayList.add(estate);
                }
                Log.d("msg", estateArrayList.size()+" estates discovered");
                EstateRecyclerAdapter adapter = new EstateRecyclerAdapter(estateArrayList, mListener);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
