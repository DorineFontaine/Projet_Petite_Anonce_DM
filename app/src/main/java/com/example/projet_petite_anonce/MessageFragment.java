package com.example.projet_petite_anonce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MessageFragment extends Fragment {

    //Fragment qui permet l'affichage des messages de l'utilisateur
    //Accées au message


    FirebaseAuth mAuth;
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

        //check if user signed in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        if(user == null){
            //show a connexion button

            //we create an relativelayout (new view)
            RelativeLayout frameLayout = new RelativeLayout(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setBackgroundResource(R.drawable.petite_anonce_logo);

            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams1.addRule(RelativeLayout.CENTER_VERTICAL);

            //a child view where text and button are
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setLayoutParams(layoutParams1);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.setMargins(20, 20, 20, 50);
            TextView titre = new TextView(view.getContext());
            titre.setLayoutParams(layoutParams2);
            titre.setText(getResources().getString(R.string.connexionrequise));
            linearLayout.addView(titre);

            Button btn_connexion = new Button(view.getContext());
            btn_connexion.setText(getResources().getString(R.string.connexion));
            btn_connexion.setBackgroundResource(R.drawable.button_sumit);
            btn_connexion.setPadding(20,20,20,20);
            btn_connexion.setTextColor(getResources().getColor(R.color.white));
            btn_connexion.setGravity(Gravity.CENTER);

            btn_connexion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                }
            });

            linearLayout.addView(btn_connexion);

            frameLayout.addView(linearLayout);
            view = frameLayout;

        }
        else {
            //user signed in : it shows his conversations

            simpleList = (ListView) view.findViewById(R.id.listview_message);
            CustomAdaptater customAdapter = new CustomAdaptater(getContext(),titre ,temps, prix, null,nomAcheteur,null,  R.layout.list_view_item);
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
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DiscussFragment(titre[i],temps[i],nomAcheteur[i],photo[i])).commit();


                        }
                    });

                    affiche.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //On transmet le prix le titre pour faire un affichage dynamique du coté de AffichageFragment
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DiscussFragment(titre[i],temps[i],nomAcheteur[i],photo[i])).commit();

                        }
                    });

                    supprime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setMessage(R.string.suppressionMessage);
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
        }

       return view;
    }
}