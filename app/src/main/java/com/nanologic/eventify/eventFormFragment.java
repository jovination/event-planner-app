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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class eventFormFragment extends Fragment {

    private EditText eventName, location, date, startTime, endTime, numberOfSeats;
    private ImageView startTimeIcon, endTimeIcon, uploadedImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024; // 4 MB
    private Button uploadButton, submitButton;
    private ProgressBar uploadProgressBar;
    private CardView cardUploaded;

    private StorageReference storageReference;
    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_form, container, false);

        ImageView calendarIcon = view.findViewById(R.id.calenderIcon);
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

        storageReference = FirebaseStorage.getInstance().getReference("EventImages");

        startTimeIcon.setOnClickListener(v -> startTimePicker());
        endTimeIcon.setOnClickListener(v -> endTimePicker());

        calendarIcon.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
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
            datePickerDialog.show();
        });

        uploadButton.setOnClickListener(v -> openImagePicker());

        submitButton.setOnClickListener(v -> submitEvent());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null && isValidImage(imageUri)) {
                displayImage(imageUri);
            } else {
                Toast.makeText(getContext(), "File is too large or not a valid image", Toast.LENGTH_SHORT).show();
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
            cardUploaded.setVisibility(View.VISIBLE);
            uploadedImageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to display image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEventDetails(String eventId, @Nullable Uri imageUri, Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId)
                .set(event)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (imageUri != null) {
                            // Upload image if present
                            uploadImage(eventId, imageUri);
                        } else {
                            // No image to upload, just clear the form
                            clearForm();
                            Toast.makeText(getContext(), "Event saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to save event", Toast.LENGTH_SHORT).show();
                        uploadProgressBar.setVisibility(View.GONE);
                        submitButton.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    uploadProgressBar.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                });
    }

    private void uploadImage(String eventId, Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            uploadProgressBar.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
            return;
        }

        StorageReference imageRef = storageReference.child(eventId + ".jpg");
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the event document with the image URL
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("events").document(eventId)
                                .update("imageUrl", uri.toString())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Event saved successfully with image", Toast.LENGTH_SHORT).show();
                                    uploadProgressBar.setVisibility(View.GONE);
                                    submitButton.setVisibility(View.VISIBLE);
                                    clearForm();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to update event with image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    uploadProgressBar.setVisibility(View.GONE);
                                    submitButton.setVisibility(View.VISIBLE);
                                });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        uploadProgressBar.setVisibility(View.GONE);
                        submitButton.setVisibility(View.VISIBLE);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    uploadProgressBar.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                });
    }

    private void clearForm() {
        eventName.setText("");
        location.setText("");
        date.setText("");
        startTime.setText("");
        endTime.setText("");
        numberOfSeats.setText("");
        cardUploaded.setVisibility(View.GONE);
        uploadedImageView.setImageBitmap(null);
        imageUri = null;
    }

    private void submitEvent() {
        // Show progress bar and hide submit button at the beginning
        uploadProgressBar.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);

        // Generate a unique event ID
        String eventId = FirebaseFirestore.getInstance().collection("events").document().getId();
        if (eventId == null) {
            Toast.makeText(getContext(), "Error generating event ID", Toast.LENGTH_SHORT).show();
            uploadProgressBar.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
            return;
        }

        // Create the Event object with default null image URL
        Event event = new Event(
                eventId,
                eventName.getText().toString().trim(),
                location.getText().toString().trim(),
                date.getText().toString().trim(),
                startTime.getText().toString().trim(),
                endTime.getText().toString().trim(),
                Integer.parseInt(numberOfSeats.getText().toString().trim()),
                null // Placeholder for image URL
        );
         saveEventDetails(eventId, imageUri, event);
    }

    private void startTimePicker() {
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
            startTime.setText(selectedTime);
        });

        picker.show(fragmentManager, "TIME_PICKER");
    }

    private void endTimePicker() {
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
            endTime.setText(selectedTime);
        });

        picker.show(fragmentManager, "TIME_PICKER");
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}
