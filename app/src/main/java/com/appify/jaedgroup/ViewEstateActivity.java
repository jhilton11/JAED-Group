package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator3;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.recyclerAdapters.ImagePageAdapter;
import com.appify.jaedgroup.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class ViewEstateActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private CircleIndicator indicator;
    private Button payBtn;

    private ArrayList<String> estateImages;
    private DatabaseReference estateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_estate);

        Estate estate = (Estate) getIntent().getSerializableExtra(Constants.ESTATE_EXTRA);
        String id = estate.getId();

        estateRef = FirebaseDatabase.getInstance().getReference().child("EstateImages").child(id);

        viewPager = findViewById(R.id.view_pager);
        recyclerView = findViewById(R.id.recyclerview);
        indicator = findViewById(R.id.circular_indicator);
        payBtn = findViewById(R.id.buy_btn);

        ImagePageAdapter pageAdapter = new ImagePageAdapter(getImages(), this);
        viewPager.setAdapter(pageAdapter);
        indicator.setViewPager(viewPager);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyEstate();
            }
        });

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(estate.getName());
        toolbar.setHomeButtonEnabled(true);
        toolbar.setDisplayHomeAsUpEnabled(true);

        //loadData();
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

    private void buyEstate() {
        Intent intent = new Intent(this, FillEstateDetailsActivity.class);
        startActivity(intent);
    }

    private void loadData() {
        estateImages = new ArrayList<>();

        estateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
