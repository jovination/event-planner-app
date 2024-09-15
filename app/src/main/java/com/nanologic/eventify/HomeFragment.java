package com.nanologic.eventify;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupCardClickListeners(view);  // Setup click listeners for the cards
        setupImageSlider(view);         // Setup the image slider

        return view;
    }

    // Method to initialize and set click listeners for CardViews
    private void setupCardClickListeners(View view) {
        setupCardClickListener(view, R.id.card_upcoming, new UpcomingFragment());
        setupCardClickListener(view, R.id.card_ongoing, new OngoingFragment());
        setupCardClickListener(view, R.id.card_reserved, new ReservedFragment());
        setupCardClickListener(view, R.id.card_created, new CreatedFragment());

        ImageView eventForm = view.findViewById(R.id.addEvent);
        eventForm.setOnClickListener(v -> replaceFragment(new eventFormFragment()));
    }

    // Method to reduce redundancy for setting up click listeners for each card
    private void setupCardClickListener(View view, int cardViewId, Fragment fragment) {
        CardView cardView = view.findViewById(cardViewId);
        cardView.setOnClickListener(v -> replaceFragment(fragment));
    }

    // Method to initialize the ImageSlider
    private void setupImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.event0, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.event1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.event2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.event3, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);
    }

    // Method to handle fragment replacement and avoid loading the same fragment multiple times
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        String fragmentTag = fragment.getClass().getSimpleName();

        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (currentFragment == null || !currentFragment.isVisible()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, fragmentTag);
            fragmentTransaction.addToBackStack(fragmentTag);
            fragmentTransaction.commit();
        }
    }

}
