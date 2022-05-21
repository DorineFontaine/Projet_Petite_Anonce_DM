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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class InscriptionFragment extends Fragment {

    //Create profil and store them in Firebase

    EditText edittext_mail, edittext_psw, edittext_pseudo;
    Button submit;
    String mail, password, pseudo,client;
    FirebaseAuth mAuth;
    Switch mySwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inscription, container, false);

        edittext_mail = view.findViewById(R.id.editText2_mail);
        edittext_pseudo = view.findViewById(R.id.editText_pseudo);
        edittext_psw = view.findViewById(R.id.editText2_mdp);
        submit = view.findViewById(R.id.btn_inscription);
        mySwitch = view.findViewById(R.id.switch1);
        client = "0";
        // Création d'un compte professionnel

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mySwitch.isChecked()){

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InscriptionProffessionelFragment()).commit();



                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                /*************************************************ICI CHANGER LES SETERROR AVEC REQUISEMAIL,REQUISMDP etc ...***************/
                mail = edittext_mail.getText().toString();
                password = edittext_psw.getText().toString();
                pseudo = edittext_pseudo.getText().toString();

                if(mail.isEmpty()){
                    edittext_mail.setError(getResources().getString(R.string.requisEmail));
                    edittext_mail.requestFocus();
                    return;
                }


                if(password.isEmpty()){
                    edittext_psw.setError(getResources().getString(R.string.requisMDP));
                    edittext_psw.requestFocus();
                    return;
                }
                if(pseudo.isEmpty()){
                    edittext_pseudo.setError(getResources().getString(R.string.requisPseudo));
                    edittext_pseudo.requestFocus();
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
                                    ClientParticulier clientParticulier = new ClientParticulier(pseudo,password,mail);
                                    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    FirebaseDatabase.getInstance().getReference("ClientParticulier")
                                            .child(user)
                                            .setValue(clientParticulier).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {

                                            if(task2.isSuccessful()){

                                                //transfered to profil account
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new com.example.projet_petite_anonce.ProfilFragment()).commit();

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