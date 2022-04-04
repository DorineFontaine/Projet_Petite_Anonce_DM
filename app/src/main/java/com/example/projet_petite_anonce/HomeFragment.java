package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;


public class HomeFragment extends Fragment {

    GridView simpleList;

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
        simpleList = (GridView) view.findViewById(R.id.grid_view);
        CustomAdaptater customAdapter = new CustomAdaptater(getContext(), ville, prix, temps,photo,titre,  R.layout.grid_view_item);
        simpleList.setAdapter(customAdapter);
        return view;
    }
}