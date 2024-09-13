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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UpcomingFragment extends Fragment {

    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        db = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
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
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

                            LocalDate currentDate = LocalDate.now();
                            LocalTime currentTime = LocalTime.now();

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

                                    // Parse date and time
                                    LocalDate eventDate = LocalDate.parse(date, dateFormat);
                                    LocalTime eventStartTime = LocalTime.parse(startTime, timeFormat);
                                    LocalTime eventEndTime = LocalTime.parse(endTime, timeFormat);

                                    // Filter: Include only events that are upcoming and not ongoing
                                    if (eventDate.isAfter(currentDate) ||
                                            (eventDate.isEqual(currentDate) && eventStartTime.isAfter(currentTime))) {
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
                                    Log.e("DateParseError", "Error parsing date/time: ", e);
                                }
                            }

                            // Sort the filtered list by date and time (earliest first)
                            filteredList.sort(Comparator.comparing(e -> {
                                LocalDate eventDate = LocalDate.parse(e.getDate(), dateFormat);
                                LocalTime eventStartTime = LocalTime.parse(e.getStartTime(), timeFormat);
                                return eventDate.atTime(eventStartTime);
                            }));

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
