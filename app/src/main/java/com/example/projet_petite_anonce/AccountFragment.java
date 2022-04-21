package com.example.projet_petite_anonce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AccountFragment extends Fragment {
    Button signin_button;

    TextView inscription, psw_forgotten;
    EditText editText_mail, editText_psw;
    String mail, psw;
    Intent page_inscription;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        //check if user already signed in
        //check email and password with Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new com.example.projet_petite_anonce.ProfilFragment()).commit();

        }
        else{

            inscription = view.findViewById(R.id.inscription);
            signin_button = view.findViewById(R.id.btn_connexion);
            psw_forgotten = view.findViewById(R.id.motdepasse);


            editText_mail = view.findViewById(R.id.editText_mail);
            editText_psw = view.findViewById(R.id.editText2_mail);



            /*****************************************AUTHENTIFICATION *********************************************/

            psw_forgotten.setOnClickListener(new View.OnClickListener() {
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

            signin_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //get mail and password written
                    mail = editText_mail.getText().toString();
                    psw = editText_psw.getText().toString();

                    if(!(mail.isEmpty()||psw.isEmpty())){

                        mAuth.signInWithEmailAndPassword(mail, psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    //transfered to profil account
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new com.example.projet_petite_anonce.ProfilFragment()).commit();

                                }
                                else{

                                    //wrong mail or password
                                    Toast.makeText(getContext(), R.string.incorrect, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        //fields arent supposed to be empty
                        Toast.makeText(getContext(), R.string.remplirTousLesChamps, Toast.LENGTH_LONG).show();
                    }

                }
            });

            //  page_inscription = new Intent(getActivity(), InscriptionActivity.class);

        }


        return view;
    }
}