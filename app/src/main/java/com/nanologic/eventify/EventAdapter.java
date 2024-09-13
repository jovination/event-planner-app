package com.nanologic.eventify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // If you're using Glide for image loading

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Context context; // Add this
    private final List<Event> eventList;
    

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context; // Initialize context
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Bind data to the views
        holder.eventName.setText(event.getEventName());
        holder.locationName.setText(event.getLocation());
        holder.numberOfSeats.setText(event.getNumberOfSeats() + " seats");
        holder.timeId.setText(event.getStartTime() + " - " + event.getEndTime());

        // Load image
        Glide.with(context)
                .load(event.getImageUrl())
                .into(holder.eventImage);

        // Set date and month
        holder.dateId.setText(event.getDateId());
        holder.monthId.setText(event.getMonthId());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReserveEvent.class);
            intent.putExtra("eventName", event.getEventName());
            intent.putExtra("eventLocation", event.getLocation());
            intent.putExtra("eventDate", event.getDate());
            intent.putExtra("eventStartTime", event.getStartTime());
            intent.putExtra("eventEndTime", event.getEndTime());
            intent.putExtra("eventSeats", event.getNumberOfSeats());
            intent.putExtra("eventImage", event.getImageUrl()); // Change from `event.getImageResourceId()` to `event.getImageUrl()`
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Event> newEvents) {
        eventList.clear();
        eventList.addAll(newEvents);
        notifyDataSetChanged();
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView locationName;
        private final TextView numberOfSeats;
        private final TextView timeId;
        private final TextView dateId;
        private final TextView monthId;
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
