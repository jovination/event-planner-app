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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OngoingFragment extends Fragment {

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
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch and filter ongoing events
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    // Get event data
                                    String eventName = document.getString("eventName");
                                    String location = document.getString("location");
                                    String date = document.getString("date");
                                    Long seats = document.getLong("numberOfSeats"); // Assuming numberOfSeats is stored as a Long
                                    String startTime = document.getString("startTime");
                                    String endTime = document.getString("endTime");
                                    String imageUrl = document.getString("imageUrl");

                                    // Parse event times
                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
                                    LocalDateTime startDateTime = LocalDateTime.parse(date + " " + startTime, dateTimeFormatter);
                                    LocalDateTime endDateTime = LocalDateTime.parse(date + " " + endTime, dateTimeFormatter);
                                    LocalDateTime now = LocalDateTime.now();

                                    // Filter for ongoing events (events that started but haven't ended yet)
                                    if (now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
                                        // Create Event object and add to list if ongoing
                                        Event event = new Event(
                                                document.getId(), // Use document ID as event ID
                                                eventName,
                                                location,
                                                date,
                                                startTime,
                                                endTime,
                                                seats != null ? seats.intValue() : 0, // Convert Long to int
                                                imageUrl
                                        );
                                        eventList.add(event);
                                    }
                                } catch (Exception e) {
                                    Log.e("DateTimeParseError", "Error parsing event times: ", e);
                                }
                            }
                            eventAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        } else {
                            // Handle errors
                            Log.e("FirestoreError", "Error fetching events: ", task.getException());
                        }
                    }
                });
    }
}
