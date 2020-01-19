package com.appify.jaedgroup.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.appify.jaedgroup.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvestmentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InvestmentFragment extends Fragment {

    private CarouselView carouselView;
    private Button newInvestmentBtn, viewInvestmentsBtn;

    private OnFragmentInteractionListener mListener;

    public InvestmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_investment, container, false);

        carouselView = view.findViewById(R.id.carousel_view);

        newInvestmentBtn = view.findViewById(R.id.new_investment_btn);
        viewInvestmentsBtn = view.findViewById(R.id.view_investments_btn);

        newInvestmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewInvestment();
            }
        });

        viewInvestmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewInvestments();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNewInvestmentClick();

        void onViewInvestmentClick();
    }

    //TODO: Write code for loading the images into the carousel
    private void loadCarouselImages() {
        final ArrayList<String> imageUrls = new ArrayList<>();
        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference().child("CarouselImages");
        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String imgUrl = snapshot.getValue(String.class);
                    imageUrls.add(imgUrl);
                }

                carouselView.setImageListener(new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        Glide.with(getContext()).load(imageUrls.get(position)).into(imageView);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewInvestment() {
        mListener.onNewInvestmentClick();
    }

    private void viewInvestments() {
        mListener.onViewInvestmentClick();
    }
}
