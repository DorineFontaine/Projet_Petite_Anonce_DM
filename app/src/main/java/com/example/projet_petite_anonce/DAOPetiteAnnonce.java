package com.example.projet_petite_anonce;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DAOPetiteAnnonce {


        private DatabaseReference myRef;
        private DatabaseReference userRef;
        private DatabaseReference databaseReference;
        private String key;

        public DAOPetiteAnnonce(String reference, String userUID){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference(reference);
            userRef = null;
            //check which kind of user is it
            database.getReference("ClientParticulier").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(userUID))
                        userRef = database.getReference("ClientParticulier").child(userUID).child("MyAdvert");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            if(userRef == null)
                userRef = database.getReference("ClientProfessionnel").child(userUID).child("MyAdvert");


        }
        public Task<Void> add(Object objet){
            DatabaseReference keyRef = myRef.push();
            String key = keyRef.getKey();

            Advert advert = (Advert) objet;
            advert.setKey(key);

            //Add the advert in ClientParticulier user myadvert list
            userRef.push().setValue(key);
            return keyRef.setValue(objet);

        }
        public String getKey(){return key;}

        public Task<Void> update(String key, HashMap<String,Object> hashMap){

            return myRef.child(key).updateChildren(hashMap);

        }

        public Task<Void> remove(String key){
            return myRef.child(key).removeValue();
        }
    }


