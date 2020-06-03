package com.appify.jaedgroup.recyclerAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.EstateImage;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageAdapter extends PagerAdapter {
    private ArrayList<EstateImage> items;
    private Context context;

    public ImageAdapter(ArrayList<EstateImage> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setAdjustViewBounds(true);
        Glide.with(context).load(items.get(position).getImageUrl()).placeholder(R.drawable.background).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
