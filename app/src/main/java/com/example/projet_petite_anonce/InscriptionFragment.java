package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class InscriptionFragment extends Fragment {

    //Recoit les informations sur le compte de l'utilisateur : Adresse Mail, Pseudo, etc ..
    //Stocke l'information dans une BDD

    EditText mail, mdp, pseudo;
    Button submit;
    String email, password, psd;
    FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inscription, container, false);

        mail = view.findViewById(R.id.editText2_mail);
        pseudo = view.findViewById(R.id.editText_pseudo);
        mdp = view.findViewById(R.id.editText2_mdp);
        submit = view.findViewById(R.id.btn_inscription);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                /*************************************************ICI CHANGER LES SETERROR AVEC REQUISEMAIL,REQUISMDP etc ...***************/
                email = mail.getText().toString();
                password = mdp.getText().toString();
                psd = pseudo.getText().toString();

                if(email.isEmpty()){
                    mail.setError("Email requis");
                    mail.requestFocus();
                    return;
                }


                if(password.isEmpty()){
                    mdp.setError("Mot de passe  requis ");
                    mdp.requestFocus();
                    return;
                }
                if(psd.isEmpty()){
                    pseudo.setError("Pseudo requis  ");
                    pseudo.requestFocus();
                    return;
                }
                if(password.length()< 6){

                    mdp.setError("Entrez un passport qui contient au moins 6 lettre");
                    mdp.requestFocus();
                    return;

                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mail.setError("Vueillez rentrez un email valide ");
                    mail.requestFocus();
                    return;

                }

                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    ClientParticulier clientParticulier = new ClientParticulier(psd,password,email);

                                    FirebaseDatabase.getInstance().getReference("ClientParticulier")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(clientParticulier).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(getContext(), "Succées de l'enregistrement", Toast.LENGTH_LONG).show();

                                            }
                                            else{
                                                Toast.makeText(getContext(), "Echec de l'enregistrement", Toast.LENGTH_LONG).show();
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