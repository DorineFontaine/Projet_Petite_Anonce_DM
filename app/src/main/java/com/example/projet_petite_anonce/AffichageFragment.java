package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class AffichageFragment extends Fragment {

    ImageView imageAnonce;
    TextView textPrix, textTitre, textDate;

    int image;
    String prix, titre,date;

    public AffichageFragment(int image, String prix,String titre,String date){
        this.image = image;
        this.prix = prix;
        this.titre = titre;
        this.date = date;

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_affichage, container, false);
        imageAnonce = view.findViewById(R.id.imageAnnonce);
        textPrix = view.findViewById(R.id.prix);
        textTitre = view.findViewById(R.id.titre);
        textDate = view.findViewById(R.id.date);


        textDate.setText(date);
        textPrix.setText(prix);
        imageAnonce.setImageResource(image);
        textTitre.setText(titre);

        return view;
    }
}