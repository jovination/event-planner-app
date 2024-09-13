package com.nanologic.eventify;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OngoingFragment extends Fragment {

    private static final String TAG = "OngoingFragment";
    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);

        db = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);

        // Setup RecyclerView LayoutManager and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventAdapter);

        // Add bottom spacing decoration
        int bottomSpacingInPixels = (int) getResources().getDimension(R.dimen.bottom_spacing);
        recyclerView.addItemDecoration(new BottomSpacingItemDecoration(bottomSpacingInPixels));

        // Fetch and filter data from Firestore
        fetchOngoingEventData();

        return view;
    }

    private void fetchOngoingEventData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {
                            eventList.clear(); // Clear the existing list
                            List<Event> filteredList = new ArrayList<>();
                            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MMMM d, yyyy HH:mm", Locale.getDefault());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    // Get event data
                                    String eventName = document.getString("eventName");
                                    String location = document.getString("location");
                                    String date = document.getString("date");
                                    Long seats = document.getLong("numberOfSeats");
                                    String startTime = document.getString("startTime");
                                    String endTime = document.getString("endTime");
                                    String imageUrl = document.getString("imageUrl");

                                    // Combine date and time
                                    String startDateTimeString = date + " " + startTime;
                                    String endDateTimeString = date + " " + endTime;

                                    // Convert to Date objects
                                    Date startDateTime = dateTimeFormatter.parse(startDateTimeString);
                                    Date endDateTime = dateTimeFormatter.parse(endDateTimeString);
                                    Date now = new Date(); // Current time

                                    // Filter: Include only ongoing events
                                    if (startDateTime != null && endDateTime != null && now.after(startDateTime) && now.before(endDateTime)) {
                                        Event event = new Event(
                                                document.getId(),
                                                eventName,
                                                location,
                                                date,
                                                startTime,
                                                endTime,
                                                seats != null ? seats.intValue() : 0,
                                                imageUrl
                                        );
                                        filteredList.add(event);
                                    }

                                } catch (ParseException e) {
                                    Log.e(TAG, "Error parsing date/time: ", e);
                                }
                            }

                            // Optionally sort the filtered list if needed
                            filteredList.sort(Comparator.comparing(e -> {
                                try {
                                    return dateTimeFormatter.parse(e.getDate() + " " + e.getStartTime());
                                } catch (ParseException ex) {
                                    Log.e(TAG, "Error sorting events: ", ex);
                                    return new Date();
                                }
                            }));

                            eventList.addAll(filteredList); // Add filtered events to the main list
                            eventAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        } else {
                            // Handle errors
                            Log.e(TAG, "Error fetching events: ", task.getException());
                        }
                    }
                });
    }
}
