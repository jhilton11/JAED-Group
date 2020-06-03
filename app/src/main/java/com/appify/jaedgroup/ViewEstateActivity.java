package com.appify.jaedgroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appify.jaedgroup.model.CarouselItem;
import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.model.EstateImage;
import com.appify.jaedgroup.recyclerAdapters.ImageAdapter;
import com.appify.jaedgroup.recyclerAdapters.ImagePageAdapter;
import com.appify.jaedgroup.utils.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewEstateActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private TextView estate_description, estate_address, promo_details;
    private Button payBtn;

    private ArrayList<EstateImage> estateImages;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_estate);

        Estate estate = (Estate) getIntent().getSerializableExtra(Constants.ESTATE_EXTRA);
        id = estate.getId();

        viewPager = findViewById(R.id.view_pager);
        indicator = findViewById(R.id.circular_indicator);
        estate_address = findViewById(R.id.estate_address);
        estate_description = findViewById(R.id.estate_description);
        promo_details = findViewById(R.id.promo_details);
        payBtn = findViewById(R.id.buy_btn);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyEstate(estate);
            }
        });

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(estate.getName());
        toolbar.setHomeButtonEnabled(true);
        toolbar.setDisplayHomeAsUpEnabled(true);

        loadDetails(estate);
        loadData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void buyEstate(Estate estate) {
        Intent intent = new Intent(this, FillDetailsActivity.class);
        intent.putExtra(Constants.ESTATE_EXTRA, estate);
        startActivity(intent);
    }

    private void loadDetails(Estate estate) {
        estate_address.setText(estate.getAddress());
        Log.d("address", estate.getAddress());
        estate_description.setText(estate.getDescription());
        promo_details.setText(estate.getPromoDetails());
    }

    private void loadData() {
        Query estateRef = FirebaseFirestore.getInstance().collection("estate_images")
                .whereEqualTo("id", id);
        estateRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    estateImages = new ArrayList<>();
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                        EstateImage item = snapshot.toObject(EstateImage.class);
                        estateImages.add(item);
                        Log.d("imgUrl", item.getImageUrl());
                    }
                    ImageAdapter pageAdapter = new ImageAdapter(estateImages, getApplicationContext());
                    viewPager.setAdapter(pageAdapter);
                    indicator.setViewPager(viewPager);
                }
            }
        });
    }
}
