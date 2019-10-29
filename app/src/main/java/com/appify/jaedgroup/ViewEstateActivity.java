package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.appify.jaedgroup.model.Estate;
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
    private CarouselView carouselView;
    private ArrayList<String> estateImages;
    private StorageReference storageRef;
    private DatabaseReference estateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_estate);

        Estate estate = (Estate) getIntent().getSerializableExtra(Constants.ESTATE_EXTRA);
        String id = estate.getId();
        storageRef = FirebaseStorage.getInstance().getReference();
        estateRef = FirebaseDatabase.getInstance().getReference().child("EstateImages").child(id);

        carouselView = findViewById(R.id.carousel_view);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide.with(ViewEstateActivity.this).load(estateImages.get(position)).into(imageView);
            }
        });
    }

    private void buyEstate() {
        Intent intent = new Intent();
        startActivity(intent);
    }

    private void loadData() {
        estateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String url = snapshot.getValue(String.class);
                    estateImages.add(url);
                }
                carouselView.setPageCount(estateImages.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
