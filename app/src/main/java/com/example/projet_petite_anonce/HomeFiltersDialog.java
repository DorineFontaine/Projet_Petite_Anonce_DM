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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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




        ImageButton closeBtn = dialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }
}
