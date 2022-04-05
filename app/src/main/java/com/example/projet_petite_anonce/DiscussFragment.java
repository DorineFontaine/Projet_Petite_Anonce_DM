package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DiscussFragment extends Fragment {

    //Fragment qui permet d'afficher les envoies de messages en temps réel
    //L'utilisateur peut recevoir ou envoyer des messages (des photo ? )
    //Possibilité que ce fragment se change en activité


    LinearLayout discution;
    ImageView imageView;

    int image;
    String titre, prix, nom;
    TextView textTitre,textPrix,textNom;

    public DiscussFragment(String titre, String prix, String nom, int image){
        this.titre = titre;
        this.prix = prix;
        this.nom = nom;
        this.image = image;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_discuss, container, false);

       //Implementation dynamique des element du LinearLayout
       discution = view.findViewById(R.id.discution);
       textNom = view.findViewById(R.id.NomUtili);
       textPrix = view.findViewById(R.id.prixAnonce);
       textTitre = view.findViewById(R.id.titreAnonce);
       imageView =  view.findViewById(R.id.imageView6);

       imageView.setImageResource(image);
       textTitre.setText(titre);
       textPrix.setText(prix);
       textNom.setText(nom);
       return view;
    }
}