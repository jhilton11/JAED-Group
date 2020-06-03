package com.appify.jaedgroup.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.CarouselItem;
import com.appify.jaedgroup.recyclerAdapters.ImagePageAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class InvestmentFragment extends Fragment {

    private static final long DELAY_MS = 1000;
    private static final long PERIOD_MS = 3000;
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private Button newInvestmentBtn, viewInvestmentsBtn;

    private OnFragmentInteractionListener mListener;
    private ImagePageAdapter adapter;
    
    private int currentPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_investment, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        newInvestmentBtn = view.findViewById(R.id.new_investment_btn);
        viewInvestmentsBtn = view.findViewById(R.id.view_investments_btn);
        indicator = view.findViewById(R.id.circular_indicator);

        loadCarouselImages();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNewInvestmentClick();

        void onViewInvestmentClick();
    }

    //TODO: Write code for loading the images into the carousel
    private void loadCarouselImages() {
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("carousel_images");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e==null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        ArrayList<CarouselItem> items = new ArrayList<>();
                        for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                            CarouselItem item = snapshot.toObject(CarouselItem.class);
                            items.add(item);
                        }
                        Log.d(getClass().getSimpleName(), items.size() + " items");

                        adapter = new ImagePageAdapter(items, getContext());
                        viewPager.setAdapter(adapter);
                        indicator.setViewPager(viewPager);
                        setupCarousel(items.size());
                    }
                }
            }
        });
    }

    private void addNewInvestment() {
        mListener.onNewInvestmentClick();
    }

    private void viewInvestments() {
        mListener.onViewInvestmentClick();
    }

    private void setupCarousel(int size) {
        int NUM_PAGES = size;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }
}
