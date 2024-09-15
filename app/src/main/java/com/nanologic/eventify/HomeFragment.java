package com.nanologic.eventify;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db;
    private ImageSlider imageSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        imageSlider = view.findViewById(R.id.slider);

        // Setup card click listeners
        setupCardClickListeners(view);

        // Fetch and display upcoming events
        fetchUpcomingEventData();

        return view;
    }

    private void setupCardClickListeners(View view) {
        setupCardClickListener(view, R.id.card_upcoming, new UpcomingFragment());
        setupCardClickListener(view, R.id.card_ongoing, new OngoingFragment());
        setupCardClickListener(view, R.id.card_reserved, new ReservedFragment());
        setupCardClickListener(view, R.id.card_created, new CreatedFragment());

        ImageView eventForm = view.findViewById(R.id.addEvent);
        eventForm.setOnClickListener(v -> replaceFragment(new eventFormFragment()));
    }

    private void setupCardClickListener(View view, int cardViewId, Fragment fragment) {
        CardView cardView = view.findViewById(cardViewId);
        cardView.setOnClickListener(v -> replaceFragment(fragment));
    }

    private void fetchUpcomingEventData() {
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<SlideModel> slideModels = new ArrayList<>();
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

                            LocalDate currentDate = LocalDate.now();
                            LocalTime currentTime = LocalTime.now();

                            List<Event> filteredList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    // Get event data
                                    String date = document.getString("date");
                                    String startTime = document.getString("startTime");
                                    String imageUrl = document.getString("imageUrl");

                                    // Parse date and time
                                    LocalDate eventDate = LocalDate.parse(date, dateFormat);
                                    LocalTime eventStartTime = LocalTime.parse(startTime, timeFormat);

                                    // Filter: Include only upcoming events
                                    if (eventDate.isAfter(currentDate) ||
                                            (eventDate.isEqual(currentDate) && eventStartTime.isAfter(currentTime))) {
                                        filteredList.add(new Event(
                                                document.getId(),
                                                document.getString("eventName"),
                                                document.getString("location"),
                                                date,
                                                startTime,
                                                document.getString("endTime"),
                                                document.getLong("numberOfSeats") != null ? document.getLong("numberOfSeats").intValue() : 0,
                                                imageUrl
                                        ));
                                    }
                                } catch (DateTimeParseException e) {
                                    Log.e("DateParseError", "Error parsing date/time: ", e);
                                }
                            }

                            // Sort the list by date and start time
                            filteredList.sort(Comparator.comparing(e -> {
                                LocalDate eventDate = LocalDate.parse(e.getDate(), dateFormat);
                                LocalTime eventStartTime = LocalTime.parse(e.getStartTime(), timeFormat);
                                return eventDate.atTime(eventStartTime);
                            }));

                            // Limit the list to the top 4 upcoming events
                            List<Event> topEvents = filteredList.size() > 4 ? filteredList.subList(0, 4) : filteredList;

                            // Populate the ImageSlider
                            for (Event event : topEvents) {
                                if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                                    slideModels.add(new SlideModel(event.getImageUrl(), ScaleTypes.CENTER_CROP));
                                }
                            }

                            imageSlider.setImageList(slideModels);
                        } else {
                            // Handle errors
                            Log.e(TAG, "Error fetching events: ", task.getException());
                        }
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        String fragmentTag = fragment.getClass().getSimpleName();

        // Only replace fragment if it's not already visible
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (currentFragment == null || !currentFragment.isVisible()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, fragmentTag);
            fragmentTransaction.addToBackStack(null); // Enable back navigation
            fragmentTransaction.commit();
        }
    }
}
