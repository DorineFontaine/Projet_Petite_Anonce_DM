package com.example.projet_petite_anonce;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOPetiteAnnonce {


        private DatabaseReference myRef;
        private DatabaseReference userRef;
        private DatabaseReference databaseReference;
        private String key;

        public DAOPetiteAnnonce(String reference, String userUID){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference(reference);
            userRef = database.getReference("ClientParticulier").child(userUID).child("MyAdvert");

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


