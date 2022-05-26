package com.example.projet_petite_anonce;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class HomeFiltersDialog extends BottomSheetDialogFragment {

    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;

    //put old filters parameters in argument
    public HomeFiltersDialog (){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.home_filters_dialog_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        CoordinatorLayout layout = dialog.findViewById(R.id.bottomSheetLayout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        //category checkbox text
        CheckBox checkBox = view.findViewById(R.id.livres);
        checkBox.setText(getResources().getStringArray(R.array.categorie)[0]);


        checkBox = view.findViewById(R.id.deco);
        checkBox.setText(getResources().getStringArray(R.array.categorie)[1]);

        checkBox = view.findViewById(R.id.vehicule);
        checkBox.setText(getResources().getStringArray(R.array.categorie)[2]);

        checkBox = view.findViewById(R.id.musique);
        checkBox.setText(getResources().getStringArray(R.array.categorie)[3]);

        checkBox = view.findViewById(R.id.immo);
        checkBox.setText(getResources().getStringArray(R.array.categorie)[4]);

        checkBox = view.findViewById(R.id.multimedia);
        checkBox.setText(getResources().getStringArray(R.array.categorie)[5]);


        Button restart = view.findViewById(R.id.btn_clear);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                //clear category
                CheckBox checkBox = view.findViewById(R.id.livres);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.deco);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.vehicule);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.musique);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.immo);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.multimedia);
                checkBox.setChecked(false);

                //clear state
                checkBox = view.findViewById(R.id.neuf);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.tresbon);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.bon);
                checkBox.setChecked(false);
                checkBox = view.findViewById(R.id.satif);
                checkBox.setChecked(false);

                //set ranger slider to 0 and 1100
                RangeSlider rangeSlider = view.findViewById(R.id.rangeSlider);
                List<Float> initial = new ArrayList<>();
                initial.add((float) 0);
                initial.add(1100F);

                rangeSlider.setValues(initial);

                Bundle result = new Bundle();
                result.putString("Reinitialiser", "oui");
                getParentFragmentManager().setFragmentResult("Reinitialiser", result);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        Button filter = view.findViewById(R.id.btn_search);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {

                //get filter
                //category
                boolean[] categories = new boolean[6];
                CheckBox checkBox = view.findViewById(R.id.livres);
                categories[0] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.deco);
                categories[1] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.vehicule);
                categories[2] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.musique);
                categories[3] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.immo);
                categories[4] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.multimedia);
                categories[5] = (checkBox.isChecked());

                //state
                boolean[] states = new boolean[4];
                checkBox = view.findViewById(R.id.neuf);
                states[0] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.tresbon);
                states[1] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.bon);
                states[2] = (checkBox.isChecked());
                checkBox = view.findViewById(R.id.satif);
                states[3] = (checkBox.isChecked());

                //prices
                String minPrice, maxPrice;
                RangeSlider rangeSlider = view.findViewById(R.id.rangeSlider);
                List<Float> prices = rangeSlider.getValues();
                minPrice = prices.get(0).toString();
                maxPrice = prices.get(1).toString();


                Bundle result = new Bundle();
                result.putBooleanArray("categories", categories);
                result.putBooleanArray("states", states);
                result.putString("minPrice", minPrice);
                result.putString("maxPrice", maxPrice);

                getParentFragmentManager().setFragmentResult("filter", result);

                //close filters dialog
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });


        ImageButton closeBtn = dialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }
}
