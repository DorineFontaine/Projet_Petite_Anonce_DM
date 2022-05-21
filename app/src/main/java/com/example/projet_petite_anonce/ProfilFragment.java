package com.example.projet_petite_anonce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class ProfilFragment extends Fragment {
    Button modifProfil;
    FirebaseAuth mAuth;
    StorageReference storageReference;

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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new com.example.projet_petite_anonce.ModifProfil()).commit();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){

            //get and display client information
            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(user.getUid());
            DatabaseReference pseudo = userInformation.child("pseudo");

            pseudo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    TextView pseudo_textView = view.findViewById(R.id.pseudoProfil);
                    Object pseudoTest = snapshot.getValue();
                    if( pseudoTest != null)
                        pseudo_textView.setText((String)pseudoTest);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });


            //profil image
            String imageString = user.getUid().toString();//get user id

            //each user have only one profil id, if there isn't a profil id we display buffer image
            storageReference = FirebaseStorage.getInstance().getReference(imageString+"/profil/profil");///be carefull for the extension

            try {
                File localeFile = File.createTempFile("tempfile", ".jpeg");
                storageReference.getFile(localeFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localeFile.getAbsolutePath());
                        ImageView imageView = view.findViewById(R.id.imageProfil);
                        imageView.setImageBitmap(bitmap);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }



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