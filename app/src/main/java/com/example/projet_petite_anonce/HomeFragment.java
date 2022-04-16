package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


public class HomeFragment extends Fragment {

    //Fragment qui permet l'affichage d'annonce aléatoire
    //Possibilité d'effectué une recherche dans la base de données de l'application
    //Possibilité de mettre des filtres sur cette recherche
    //Permet d'avoir accées à l'affichage de l'article
    //Permet d'ajouter des articles à la liste de favoris

    GridView simpleList;
    ImageView favoris;

    String ville[] = {"GARE SAINT ROCH ", " TOULOUSE ",  "ILE DE  FRANCE  ", "PARIS"};
    String prix[] = {"15$ ", "5$", "90$", "20$"};
    String temps[] = {"Aujourd'hui à 15h ", " 24/05/2022", "24/12/2022", "17/03/2022"};
    String titre[] = {"Robe ", " BD", "Chaussure Nike", "Chaussure à talon"};
    int photo[]={R.drawable.robe,R.drawable.bd,R.drawable.chaussure_nike,R.drawable.chaussure};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        favoris = view.findViewById(R.id.fav);


        // Implementation de la liste de message
        simpleList = (GridView) view.findViewById(R.id.grid_view);
        CustomAdaptater customAdapter = new CustomAdaptater(getContext(), ville, prix, temps,photo,titre,  R.layout.grid_view_item);
        simpleList.setAdapter(customAdapter);
        //On met un ecouteur sur chaque élément de la liste
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView fav = view.findViewById(R.id.fav);
                fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fav.setBackgroundResource(R.drawable.ic_baseline_favorite_24);


                    }
                });

                LinearLayout linearLayout = view.findViewById(R.id.affiche);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //On transmet le prix le titre pour faire un affichage dynamique du coté de AffichageFragment
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment(photo[i],prix[i],titre[i],temps[i])).commit();


                    }
                });



            }
        });
        return view;
    }


    //Marquage des articles favoris (remplissage du coeur)
    //Pas fonctionnel pour le moment
    public void favoris() {
        favoris.setImageResource(R.drawable.ic_baseline_favorite_24);
    }
}