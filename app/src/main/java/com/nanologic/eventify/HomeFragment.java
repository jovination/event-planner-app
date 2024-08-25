package com.nanologic.eventify;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView cardUpcoming = view.findViewById(R.id.card_upcoming);
        cardUpcoming.setOnClickListener(v -> {
            // Replace fragment within the shared FrameLayout
            replaceFragment(new UpcomingFragment());
        });

        CardView cardOngoing = view.findViewById(R.id.card_ongoing);
        cardOngoing.setOnClickListener(v -> {
            // Replace fragment within the shared FrameLayout
            replaceFragment(new OngoingFragment());
        });

        CardView cardReserved = view.findViewById(R.id.card_reserved);
        cardReserved.setOnClickListener(v -> {
            // Replace fragment within the shared FrameLayout
            replaceFragment(new ReservedFragment());
        });
        CardView cardCreated = view.findViewById(R.id.card_created);
        cardCreated.setOnClickListener(v -> {
            // Replace fragment within the shared FrameLayout
            replaceFragment(new CreatedFragment());
        });


        ImageSlider imageSlider = view.findViewById(R.id.slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.event0, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.event1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.event2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.event3, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);

        return view;
    }
    private void replaceFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

}
