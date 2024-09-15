package com.nanologic.eventify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryEventAdapter extends RecyclerView.Adapter<DiscoveryEventAdapter.EventViewHolder> implements Filterable {

    private final Context context;
    private final List<Event> eventList;
    private List<Event> eventListFiltered; // Keep track of filtered list separately

    // Constructor
    public DiscoveryEventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.eventListFiltered = new ArrayList<>(eventList); // Initialize filtered list with full event list
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventListFiltered.get(position); // Bind data from filtered list

        // Bind data to the views
        holder.eventName.setText(event.getEventName());
        holder.locationName.setText(event.getLocation());
        holder.numberOfSeats.setText(event.getNumberOfSeats() + " seats");
        holder.timeId.setText(event.getStartTime() + " - " + event.getEndTime());

        // Load image using Glide
        Glide.with(context)
                .load(event.getImageUrl())
                .into(holder.eventImage);

        // Set date and month
        holder.dateId.setText(event.getDateId());
        holder.monthId.setText(event.getMonthId());

        // Set onClickListener to open event details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReserveEvent.class);
            intent.putExtra("eventName", event.getEventName());
            intent.putExtra("eventLocation", event.getLocation());
            intent.putExtra("eventDate", event.getDate());
            intent.putExtra("eventStartTime", event.getStartTime());
            intent.putExtra("eventEndTime", event.getEndTime());
            intent.putExtra("eventSeats", event.getNumberOfSeats());
            intent.putExtra("eventImage", event.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventListFiltered.size(); // Return size of the filtered list
    }

    // Update events when data is fetched
    @SuppressLint("NotifyDataSetChanged")
    public void updateEvents(List<Event> newEvents) {
        eventList.clear();
        eventList.addAll(newEvents);
        eventListFiltered = new ArrayList<>(newEvents); // Reset filtered list
        notifyDataSetChanged();
    }

    // Filter logic for search functionality
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Event> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(eventList); // If no filter, show all events
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Event event : eventList) {
                        if (event.getEventName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(event); // Add matching events to the filtered list
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                eventListFiltered.clear();
                eventListFiltered.addAll((List<Event>) results.values); // Update filtered list
                notifyDataSetChanged(); // Notify adapter of data changes
            }
        };
    }

    // ViewHolder class to bind views
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName, locationName, numberOfSeats, timeId, dateId, monthId;
        private final ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.EventName);
            locationName = itemView.findViewById(R.id.locationName);
            numberOfSeats = itemView.findViewById(R.id.numberOfSeats);
            timeId = itemView.findViewById(R.id.timeId);
            dateId = itemView.findViewById(R.id.dateId);
            monthId = itemView.findViewById(R.id.MonthId);
            eventImage = itemView.findViewById(R.id.eventImage);
        }
    }
}
