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
    private RecyclerView recyclerView;
    private CircleIndicator indicator;
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
        Intent intent = new Intent(this, FillDetailsActivity.class);
        startActivity(intent);
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
