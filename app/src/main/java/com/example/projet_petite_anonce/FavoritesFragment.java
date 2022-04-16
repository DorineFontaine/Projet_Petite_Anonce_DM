package com.example.projet_petite_anonce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


public class FavoritesFragment extends Fragment {

    //Fragment qui permet l'affichage de la liste des articles enregistrée par l'utilisateur
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        Dialog dialog = new Dialog(getContext());



        // Implementation de la liste de favoris
        simpleList = (ListView) view.findViewById(R.id.listview);
        CustomAdaptater customAdapter = new CustomAdaptater(getContext(), ville, prix, temps,photo,titre,  R.layout.list_view_item);
        simpleList.setAdapter(customAdapter);
        //On met un ecouteur sur chaque élément de la liste
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout affiche = view.findViewById(R.id.linearLayout2);
                LinearLayout supprime = view.findViewById(R.id.supprimer);
                ImageView imageView = view.findViewById(R.id.icon);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //On transmet le prix le titre pour faire un affichage dynamique du coté de AffichageFragment
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment(photo[i],prix[i],titre[i],temps[i])).commit();


                    }
                });

                affiche.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //On transmet le prix le titre pour faire un affichage dynamique du coté de AffichageFragment
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment(photo[i],prix[i],titre[i],temps[i])).commit();

                    }
                });

                supprime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setMessage(R.string.suppression);
                        alertDialogBuilder.setPositiveButton(R.string.oui,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // la tache à executer une fois le bouton Oui est appuyé
                                    }
                                });

                        alertDialogBuilder.setNegativeButton(R.string.non
                                ,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // la tache à exécuter une fois le bouton est Non appuyé

                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();







                      /*******************************************************************************/
                    }
                });

            }/****/
        });




       return view;
    }
}