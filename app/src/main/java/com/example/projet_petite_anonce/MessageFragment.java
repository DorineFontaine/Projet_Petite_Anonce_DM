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


public class MessageFragment extends Fragment {

    //Fragment qui permet l'affichage des messages de l'utilisateur
    //Accées au message



    ListView simpleList;

    String nomAcheteur[] = {"Manon ", " Dorine ",  "Louis  ", "Tristan"};
    String prix[] = {"Bonjour, je suis... ", "Bonjour, toujour...", "Bonjours, tj disp...", "Toujours dispo?.."};
    String temps[] = {"Aujourd'hui à 15h ", " 24/05/2022", "24/12/2022", "17/03/2022"};
    String titre[] = {"Robe ", " BD", "Chaussure Nike", "Chaussure à talon"};
    int photo[]={R.drawable.robe,R.drawable.bd,R.drawable.chaussure_nike,R.drawable.chaussure};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Implementation de la liste de message
       View view = inflater.inflate(R.layout.fragment_message, container, false);
        simpleList = (ListView) view.findViewById(R.id.listview_message);
        CustomAdaptater customAdapter = new CustomAdaptater(getContext(),titre ,temps, prix, photo,nomAcheteur,  R.layout.list_view_item);
        simpleList.setAdapter(customAdapter);

        //On met un ecouteur sur chaque élément de la liste
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //On transmet le prix le titre pour faire un affichage dynamique du coté de DiscussFragment
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DiscussFragment(titre[i],temps[i],nomAcheteur[i],photo[i])).commit();
            }
        });
       return view;
    }
}