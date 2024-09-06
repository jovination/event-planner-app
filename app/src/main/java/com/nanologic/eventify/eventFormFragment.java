package com.nanologic.eventify;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class eventFormFragment extends Fragment {

    private EditText timeEditText, eventName, location, date, startTime, endTime, numberOfSeats;
    private ImageView startTimeIcon, endTimeIcon ;
    private ImageView uploadedImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024; // 4 MB
    private Button uploadButton, submitButton;
    private ProgressBar uploadProgressBar;
    private CardView    cardUploaded;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri imageUri;
    private Bitmap bitmap;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_form, container, false);


        ImageView calendarIcon = view.findViewById(R.id.calenderIcon);
        timeEditText = view.findViewById(R.id.timeNameId);
        startTimeIcon = view.findViewById(R.id.startTimeIcon);
        endTimeIcon = view.findViewById(R.id.EndTimeIcon);
        uploadedImageView = view.findViewById(R.id.uploadedImageView);
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar);
        cardUploaded = view.findViewById(R.id.card_uploaded);

        eventName = view.findViewById(R.id.eventNameId);
        location = view.findViewById(R.id.locationNameId);
        date = view.findViewById(R.id.dateId);
        numberOfSeats = view.findViewById(R.id.numberOfSeatId);
        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);


        uploadButton = view.findViewById(R.id.uploadButton);
        submitButton = view.findViewById(R.id.submitBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        storageReference = FirebaseStorage.getInstance().getReference("EventImages");



        startTimeIcon.setOnClickListener(v -> showTimePicker());
        endTimeIcon.setOnClickListener(v -> showTimePicker());
        // Set an onClickListener on the calendar icon
        calendarIcon.setOnClickListener(v -> {
            // Get the current date
            Calendar calendar = Calendar.getInstance();

            // Create a DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        // When the date is selected, format it and set it to the EditText
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                        date.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Show the DatePickerDialog
            datePickerDialog.show();
        });

        // Set click listener on the upload button
        uploadButton.setOnClickListener(v -> {
            // Open file picker to choose an image
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // Only allow images
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                if (isValidImage(imageUri)) {
                    // Show the progress bar
                    uploadProgressBar.setVisibility(View.VISIBLE);

                    // Display the selected image
                    displayImage(imageUri);

                    // Start the upload process
                    uploadImage(imageUri);
                } else {
                    Toast.makeText(getContext(), "File is too large or not a valid image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean isValidImage(Uri imageUri) {
        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri)) {
            if (inputStream != null) {
                long fileSize = inputStream.available();
                return fileSize <= MAX_FILE_SIZE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void displayImage(Uri imageUri) {
        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            uploadedImageView.setImageBitmap(bitmap);

            // Make the CardView containing the ImageView visible
            cardUploaded.setVisibility(View.VISIBLE);
            uploadedImageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to display image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri) {

        uploadProgressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Upload complete", Toast.LENGTH_SHORT).show();
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
