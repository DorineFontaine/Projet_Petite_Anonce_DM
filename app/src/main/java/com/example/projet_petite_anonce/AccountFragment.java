package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountFragment extends Fragment {
    Button signin_button;

    TextView inscription, psw_forgotten;
    EditText editText_mail, editText_psw;
    String mail, psw;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view =  inflater.inflate(R.layout.fragment_account, container, false);



        // Inflate the layout for this fragment

        //check if user already signed in
        //check email and password with Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
           DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(user.getUid());
            DatabaseReference client = userInformation.child("client");

            client.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String clientb = (String)snapshot.getValue();
                    if (clientb != null){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfilProfessionelFragment()).commit();

                    }else{

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new com.example.projet_petite_anonce.ProfilFragment()).commit();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



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

                                    mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(user.getUid());
                                    DatabaseReference client = userInformation.child("client");

                                    client.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String clientb = (String)snapshot.getValue();
                                            if (clientb != null){
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfilProfessionelFragment()).commit();

                                            }else{

                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new com.example.projet_petite_anonce.ProfilFragment()).commit();


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });





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
                        if(mail.isEmpty())
                            editText_mail.setError(getResources().getString(R.string.requisEmail));
                        if(psw.isEmpty())
                            editText_psw.setError(getResources().getString(R.string.requisMDP));
                    }

                }
            });

            //  page_inscription = new Intent(getActivity(), InscriptionActivity.class);

        }


        return view;
    }
}