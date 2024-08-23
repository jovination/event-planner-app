package com.nanologic.eventify;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);




         bottomNavigationView = findViewById(R.id.bottomNavigationView);
         frameLayout = findViewById(R.id.frame_layout);

         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 int itemId = item.getItemId();
                 if (itemId == R.id.navHome) {
                     replaceFragment(new HomeFragment(), false);
                 } else if (itemId == R.id.navDiscovery) {
                     replaceFragment(new DiscoveryFragment(), false);
                 } else if (itemId == R.id.navBookmark) {
                     replaceFragment(new BookmarkFragment(), false);
                 } else  {
                     replaceFragment(new ProfileFragment(), false);
                 }
                  return true;
             }
         });

         replaceFragment(new HomeFragment(), true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemActiveIndicatorColor(ColorStateList.valueOf(Color.TRANSPARENT));
        ImageView profileImageView = findViewById(R.id.profileID);
        profileImageView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        profileImageView.setClipToOutline(true);

    }
    private void replaceFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized){
            fragmentTransaction.add(R.id.frame_layout, fragment);
        } else{
            fragmentTransaction.replace(R.id.frame_layout, fragment);

        }
        fragmentTransaction.commit();
    }
}