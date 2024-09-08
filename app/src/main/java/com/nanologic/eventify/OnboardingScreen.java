package com.nanologic.eventify;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

public class OnboardingScreen extends AppCompatActivity {

    ViewPager mSliderViewPager;
    LinearLayout mDotLayout;
    TextView  skipButton, nextButton;

    TextView[] dots;
     ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view only once
        setContentView(R.layout.activity_onboarding_screen);
        EdgeToEdge.enable(this);

        skipButton = findViewById(R.id.skipBtn);
        nextButton = findViewById(R.id.nextBtn);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OnboardingScreen.this, StartUp.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(0) < 3) {
                    mSliderViewPager.setCurrentItem(getItem(1), true);
                } else {
                    Intent i = new Intent(OnboardingScreen.this, StartUp.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });

        mSliderViewPager = findViewById(R.id.slideViewerPager);
        mDotLayout = findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(this);
        mSliderViewPager.setAdapter(viewPagerAdapter);

        setUpIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewLister);

        // Apply insets after setting content view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setUpIndicator ( int position){

        dots = new TextView[4];
        mDotLayout.removeAllViews();
        for ( int i = 0; i < dots.length; i++ ){
          dots[i] = new TextView(this);
          dots[i].setText(Html.fromHtml("&#8226"));
          dots[i].setTextSize(35);
          dots[i].setTextColor(getResources().getColor(R.color.grey, getApplicationContext().getTheme()));
          mDotLayout.addView(dots[i]);

        }

        dots[position].setTextColor(getResources().getColor(R.color.blue, getApplicationContext().getTheme()));
    }
    ViewPager.OnPageChangeListener viewLister = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
  private int getItem( int i){
      return mSliderViewPager.getCurrentItem() + i;
  }

}