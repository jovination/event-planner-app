package com.nanologic.eventify;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class ReserveEvent extends AppCompatActivity {

    private ImageView eventImage, backIcon;
    private TextView eventName, location, startTime, endTime;
    private Button reserveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_reserve_event);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventImage = findViewById(R.id.eventImage);
        eventName = findViewById(R.id.eventName);
        location = findViewById(R.id.location);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        reserveBtn = findViewById(R.id.reserveBtn);
        backIcon = findViewById(R.id.backICon);

        // Get data from the Intent
        String eventNameStr = getIntent().getStringExtra("eventName");
        String locationStr = getIntent().getStringExtra("eventLocation");
        String startTimeStr = getIntent().getStringExtra("eventStartTime");
        String endTimeStr = getIntent().getStringExtra("eventEndTime");
        String imageUrl = getIntent().getStringExtra("eventImage"); // Changed to String for URL

        // Set the data to the views
        eventName.setText(eventNameStr);
        location.setText(locationStr);
        startTime.setText(startTimeStr);
        endTime.setText(endTimeStr);

        // Use Glide to load the image from URL
        Glide.with(this)
                .load(imageUrl)
                .into(eventImage);

        // Set a click listener on the back icon
        backIcon.setOnClickListener(v -> onBackPressed());
    }
}
