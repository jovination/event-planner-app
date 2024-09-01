package com.nanologic.eventify;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class eventFormFragment extends Fragment {

    private EditText timeEditText;
    private ImageView timeIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_form, container, false);

        // Find the EditText and ImageView by their IDs
        EditText editText = view.findViewById(R.id.DateNameId);
        ImageView calendarIcon = view.findViewById(R.id.calenderIcon);
        // Initialize views
        timeEditText = view.findViewById(R.id.timeNameId);
        timeIcon = view.findViewById(R.id.timeIcon);

        // Set click listener on the time icon
        timeIcon.setOnClickListener(v -> showTimePicker());

        // Set an onClickListener on the calendar icon
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                // When the date is selected, format it and set it to the EditText
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(Calendar.YEAR, year);
                                selectedDate.set(Calendar.MONTH, month);
                                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                                editText.setText(dateFormat.format(selectedDate.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        return view;
    }

    private void showTimePicker() {
        FragmentManager fragmentManager = getChildFragmentManager();
        boolean isSystem24Hour = android.text.format.DateFormat.is24HourFormat(getContext());

        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
                .setTimeFormat(isSystem24Hour ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setTitleText("Select Time");

        MaterialTimePicker picker = builder.build();

        picker.addOnPositiveButtonClickListener(dialog -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();

            String selectedTime = String.format("%02d:%02d", hour, minute);
            timeEditText.setText(selectedTime);
        });

        picker.show(fragmentManager, "TIME_PICKER");
    }
}
