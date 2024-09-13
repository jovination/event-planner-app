package com.nanologic.eventify;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryFragment extends Fragment {

    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        db = FirebaseFirestore.getInstance();


        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(eventAdapter);

        int bottomSpacingInPixels = (int) getResources().getDimension(R.dimen.bottom_spacing);
        recyclerView.addItemDecoration(new BottomSpacingItemDecoration(bottomSpacingInPixels));


        // Fetch data from Firestore
        fetchEventData();


        return view;
    }

    private void fetchEventData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                                    // Create Event object and add to list
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

                                } catch (Exception e) {
                                    e.printStackTrace(); // Handle potential errors
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
