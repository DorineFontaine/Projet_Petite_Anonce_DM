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
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfilFragment extends Fragment {
    Button modifProfil;
    FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        modifProfil = view.findViewById(R.id.btn_modif);
        modifProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ModifProfil()).commit();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
//************************************************************Probleme****************************************************************************
            //get and display client information

            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(user.getUid());
            DatabaseReference pseudo = userInformation.child("pseudo");

            DatabaseReference userInformation2 = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(user.getUid());
            DatabaseReference societe = userInformation.child("nom_societe");

           /* if (pseudo != null){

                Log.d("PSEUDO","JE SUIS NON NUL ");
                pseudo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TextView pseudo_textView = view.findViewById(R.id.pseudoProfil);
                        if( snapshot.getValue().toString()!= null)
                            pseudo_textView.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }else {
                Log.d("PSEUDO","JE SUIS  NUL ");
                if(societe != null){
                    Log.d("SOCIETE","JE SUIS NON NUL ");
                    societe.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            TextView pseudo_textView = view.findViewById(R.id.pseudoProfil);
                            if( snapshot.getValue().toString() != null)
                                pseudo_textView.setText(snapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
           // }
    */



            //sign out
            Button signout_button  = view.findViewById(R.id.btn_deconnexion);
            signout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //transfered to profil account
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                    mAuth.signOut();
                }
            });
        }



        return view;
    }
}