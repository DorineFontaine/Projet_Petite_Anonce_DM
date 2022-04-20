package com.example.projet_petite_anonce;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class AccountFragment extends Fragment {
    Button bouton_connexion;

    TextView inscription, mdp_oublie;
    EditText editText_mail, editText_mdp;
    String mail, mdp;
    Intent page_inscription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);
        inscription = view.findViewById(R.id.inscription);
        bouton_connexion = view.findViewById(R.id.btn_connexion);
        mdp_oublie = view.findViewById(R.id.motdepasse);


        editText_mail = view.findViewById(R.id.editText_mail);
        editText_mdp = view.findViewById(R.id.editText2_mail);

        mail = editText_mail.getText().toString();
        mdp = editText_mdp.getText().toString();



        /*****************************************AUTHENTIFICATION *********************************************/

        mdp_oublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InscriptionFragment()).commit();

            }
        });

        bouton_connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new projetAndroid.petitesannonces.ProfilFragment()).commit();

            }
        });

        //  page_inscription = new Intent(getActivity(), InscriptionActivity.class);

        return view;
    }
}