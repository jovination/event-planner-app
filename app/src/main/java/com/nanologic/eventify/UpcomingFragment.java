package com.nanologic.eventify;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class UpcomingFragment extends Fragment {

    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);

        // Setup RecyclerView LayoutManager and Adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(eventAdapter);

        // Add bottom spacing decoration
        int bottomSpacingInPixels = (int) getResources().getDimension(R.dimen.bottom_spacing);
        recyclerView.addItemDecoration(new BottomSpacingItemDecoration(bottomSpacingInPixels));

        // Fetch and filter data from Firestore
        fetchUpcomingEventData();

        return view;
    }

    private void fetchUpcomingEventData() {

        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventList.clear(); // Clear the existing list
                            List<Event> filteredList = new ArrayList<>();
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");

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

                                    // Parse the date to filter upcoming events
                                    LocalDate eventDate = LocalDate.parse(date, dateFormat);
                                    LocalDate currentDate = LocalDate.now();

                                    // Filter: Include only events that are upcoming (future dates)
                                    if (eventDate.isAfter(currentDate)) {
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

                                } catch (DateTimeParseException e) {
                                    Log.e("DateParseError", "Error parsing date: ", e);
                                }
                            }

                            // Sort the filtered list by date (earliest first)
                            filteredList.sort(Comparator.comparing(e -> LocalDate.parse(e.getDate(), dateFormat)));

                            eventList.addAll(filteredList); // Add filtered and sorted events to the main list
                            eventAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        } else {
                            // Handle errors
                            Log.e("FirestoreError", "Error fetching events: ", task.getException());
                        }
                    }
                });
    }
}
