package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ModifAnnonce extends Fragment {


    View view;
    public ModifAnnonce() {
        // Required empty public constructor
    }

    public static ModifAnnonce newInstance() {
        return new ModifAnnonce();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_modif_annonce, container, false);
        return view;
    }
}