package com.nanologic.eventify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;

    int[] images = {
               R.drawable.illustration_01,
               R.drawable.illustration_02,
               R.drawable.illustration_03,
               R.drawable.illustration_04
    };

    int[] headings = {
               R.string.titleDescription1,
               R.string.titleDescription2,
               R.string.titleDescription3,
               R.string.titleDescription4
    };
    int[]  descriptions = {
            R.string.description1,
            R.string.description2,
            R.string.description3,
            R.string.description4

    };

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public  int getCount(){
        return  headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);
        ImageView slideTitleImage =  (ImageView) view.findViewById(R.id.titleImage);
        TextView  slideHeading = (TextView) view.findViewById(R.id.textTitle);
        TextView  slideDescription = (TextView) view.findViewById(R.id.textDescription);

        slideTitleImage.setImageResource(images[position]);
        slideHeading.setText(headings[position]);
        slideDescription.setText(descriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);
    }
}
