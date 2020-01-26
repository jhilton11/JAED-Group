package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.recyclerAdapters.ImagePageAdapter;
import com.appify.jaedgroup.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewEstateActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private TextView estate_description, estate_address, promo_details;
    private Button payBtn;

    private ArrayList<String> estateImages;
    private DatabaseReference estateRef;
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

        ImagePageAdapter pageAdapter = new ImagePageAdapter(getImages(), this);
        viewPager.setAdapter(pageAdapter);
        indicator.setViewPager(viewPager);

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
        intent.putExtra("id", estate.getId());
        startActivity(intent);
    }

    private void loadDetails(Estate estate) {
        estate_address.setText(estate.getLocation());
        estate_description.setText(estate.getDescription());
        promo_details.setText(estate.getPromoDetails());
    }

    private void loadData() {
        estateImages = new ArrayList<>();
        estateRef = FirebaseDatabase.getInstance().getReference().child("EstateImages").child(id);

        estateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String imgUrl = (String) snapshot.getValue();
                    estateImages.add(imgUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Integer> getImages() {
        ArrayList<Integer> images = new ArrayList<>();

        images.add(R.drawable.image_1);
        images.add(R.drawable.image_2);
        images.add(R.drawable.image_3);
        images.add(R.drawable.image_4);
        images.add(R.drawable.image_5);

        return images;
    }
}
