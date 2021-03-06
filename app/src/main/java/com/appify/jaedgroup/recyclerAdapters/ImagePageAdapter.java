package com.appify.jaedgroup.recyclerAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.CarouselItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImagePageAdapter extends PagerAdapter {

    private ArrayList<CarouselItem> items;
    private Context context;

    public ImagePageAdapter(ArrayList<CarouselItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(items.get(position).getImgUrl()).placeholder(R.drawable.background).into(imageView);
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
