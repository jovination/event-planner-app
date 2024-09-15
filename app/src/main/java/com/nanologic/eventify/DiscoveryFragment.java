package com.nanologic.eventify;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private DiscoveryEventAdapter eventAdapter;
    private List<Event> eventList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private EditText searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        db = FirebaseFirestore.getInstance();


        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        eventList = new ArrayList<>();
        eventAdapter = new DiscoveryEventAdapter(getContext(), eventList);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(eventAdapter);
        searchView =view.findViewById(R.id.searchView);

        int bottomSpacingInPixels = (int) getResources().getDimension(R.dimen.bottom_spacing);
        recyclerView.addItemDecoration(new BottomSpacingItemDecoration(bottomSpacingInPixels));


        fetchEventData();
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventAdapter.getFilter().filter(s); // Trigger filtering in the adapter
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



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
                            List<Event> events = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Parse event data
                                Event event = new Event(
                                        document.getId(),
                                        document.getString("eventName"),
                                        document.getString("location"),
                                        document.getString("date"),
                                        document.getString("startTime"),
                                        document.getString("endTime"),
                                        document.getLong("numberOfSeats").intValue(),
                                        document.getString("imageUrl")
                                );
                                events.add(event);
                            }
                            eventAdapter.updateEvents(events); // Update the adapter with new events
                        } else {
                            Log.e("FirestoreError",
                                    "Error fetching events: ",
                                    task.getException());
                        }
                    }
                });
    }
}
