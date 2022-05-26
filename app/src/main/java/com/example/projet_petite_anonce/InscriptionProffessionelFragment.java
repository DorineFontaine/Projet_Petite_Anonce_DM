package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class InscriptionProffessionelFragment extends Fragment {

    EditText edittext_mail, edittext_psw, edittext_siret, edittext_nom_societe, edittext_tel;
    Button submit;
    String mail;
    String password;
    String nom_societe;
    String siret;
    String client;
    String tel;
    FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inscription_proffessionel, container, false);
        edittext_mail = view.findViewById(R.id.editText_mail);
        edittext_psw = view.findViewById(R.id.editText_mdp);
        edittext_nom_societe = view.findViewById(R.id.editText_nom_de_societe);
        edittext_siret = view.findViewById(R.id.editText_SIRET);
        edittext_tel  = view.findViewById(R.id.editTextTel);
        submit = view.findViewById(R.id.btn2_inscription);
        client = "1";
        Switch mySwitch = view.findViewById(R.id.switch2);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!mySwitch.isChecked()){

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InscriptionFragment()).commit();



                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                /*************************************************ICI CHANGER LES SETERROR AVEC REQUISEMAIL,REQUISMDP etc ...***************/
                mail = edittext_mail.getText().toString();
                password = edittext_psw.getText().toString();
                nom_societe = edittext_nom_societe.getText().toString();
                siret = edittext_siret.getText().toString();
                tel = edittext_tel.getText().toString();

                if(mail.isEmpty()){
                    edittext_mail.setError(getResources().getString(R.string.requisEmail));
                    edittext_mail.requestFocus();
                    return;
                }
                if(tel.isEmpty()){
                    edittext_tel.setError(getResources().getString(R.string.telRequis));
                    edittext_tel.requestFocus();
                    return;
                }


                if(siret.isEmpty()){
                    edittext_siret.setError(getResources().getString(R.string.requisSiret));
                    edittext_siret.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    edittext_psw.setError(getResources().getString(R.string.requisMDP));
                    edittext_psw.requestFocus();
                    return;
                }
                if(nom_societe.isEmpty()){
                    edittext_nom_societe.setError(getResources().getString(R.string.requisSociete));
                    edittext_nom_societe.requestFocus();
                    return;
                }
                if(password.length()< 6){

                    edittext_psw.setError("Entrez un password qui contient au moins 6 lettre");
                    edittext_psw.requestFocus();
                    return;

                }

                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    edittext_mail.setError("Veuillez rentrez un email valide ");
                    edittext_mail.requestFocus();
                    return;

                }

                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    ClientProfessionel clientProfessionel = new ClientProfessionel(nom_societe,siret,mail,password,client,tel);
                                    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    FirebaseDatabase.getInstance().getReference("ClientProfessionnel")
                                            .child(user)
                                            .setValue(clientProfessionel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {

                                            if(task2.isSuccessful()){

                                                //transfered to profil account
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfilProfessionelFragment()).commit();

                                            }
                                            else{
                                                Toast.makeText(getContext(), "Echec de l'inscription", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });

                                }
                                else{
                                    Toast.makeText(getContext(), "Echec de l'enregistrement", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });






        return view;
    }
}