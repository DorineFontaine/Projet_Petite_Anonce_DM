package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class AnnonceFragment extends Fragment {

    //Fragment qui permet d'afficher les annonces de l'utilisateur  dans la liste
    //Permet de supprimer un élément de la liste
    //Permet d'avoir accées à l'affichage de l'article

    ListView simpleList;


    String ville[] = {"GARE SAINT ROCH ", " TOULOUSE ",  "ILE DE  FRANCE  ", "PARIS"};
    String prix[] = {"15$ ", "5$", "90$", "20$"};
    String temps[] = {"Aujourd'hui à 15h ", " 24/05/2022", "24/12/2022", "17/03/2022"};
    String titre[] = {"Robe ", " BD", "Chaussure Nike", "Chaussure à talon"};
    int photo[]={R.drawable.robe,R.drawable.bd,R.drawable.chaussure_nike,R.drawable.chaussure};




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_annonce, container, false);

        // Implementation de la liste des articles
        simpleList = (ListView) view.findViewById(R.id.listview);
        CustomAdaptater customAdapter = new CustomAdaptater(getContext(), ville, prix, temps,photo,titre,  R.layout.list_view_item);
        simpleList.setAdapter(customAdapter);
        //On met un ecouteur sur chaque élément de la liste
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //On transmet le prix le titre pour faire un affichage dynamique du coté de AffichageFragment
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment(photo[i],prix[i],titre[i],temps[i])).commit();
            }
        });
        return view;
    }
}