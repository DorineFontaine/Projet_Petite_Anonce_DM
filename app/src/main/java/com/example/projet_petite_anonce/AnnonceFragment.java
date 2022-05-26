package com.example.projet_petite_anonce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class AnnonceFragment extends Fragment {

    //Fragment qui permet d'afficher les annonces de l'utilisateur  dans le profil
    //Permet de supprimer un élément de la liste
    //Permet d'avoir accès à l'affichage de l'article

    ListView simpleList;
    View view;
    List<Advert> myAdverts;
    String[] ville ;
    String[] prix ;
    String[] temps ;
    String[] titre ;
    Bitmap[] photo ;
    String userUID;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference userRef;

    MutableLiveData<Bitmap> bitmapBuffer ;
    int display;
    int loading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_annonce, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user != null){
            userUID = user.getUid();

            bitmapBuffer = new MutableLiveData<>();
            bitmapBuffer.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
                @Override
                public void onChanged(Bitmap newBitmap) {
                    loading++;

                    if(display == loading){
                        ProgressBar progressBar = view.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        displayList(inflater);
                    }
                }
            });

            //check if he has advert is in myAdvert list
            FirebaseDatabase database = FirebaseDatabase.getInstance();

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

            DatabaseReference myFavDirectoryRef = userRef;

            myFavDirectoryRef.orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //get all key advert
                    List<String> keyAdverts = new ArrayList<>();

                    Iterable<DataSnapshot> iterableSnapshot = snapshot.getChildren();
                    loading = 0;
                    display = 0;

                    for (DataSnapshot snapshotBuffer : iterableSnapshot){
                        display++;
                    }


                    //Get all his adverts key  in Firebase
                    Consumer getAdvertKey = new Consumer<DataSnapshot>(){
                        public void accept(DataSnapshot snapshot2){

                            String aBuffer = snapshot2.getValue().toString();
                            keyAdverts.add(aBuffer);
                        };
                    };

                    snapshot.getChildren().forEach(getAdvertKey);


                    //get advert from Firebase in Annonce with keyAdverts
                    DatabaseReference advertDirectory = FirebaseDatabase.getInstance().getReference("Annonce");

                    advertDirectory.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myAdverts = new ArrayList<>();

                            //Get all his adverts key  in Firebase
                            Consumer getAdvertKey = new Consumer<DataSnapshot>(){
                                public void accept(DataSnapshot snapshot2){

                                    if( keyIsIn(snapshot2.getKey() , keyAdverts)) {
                                        Iterable<DataSnapshot> advertsBuffer = snapshot2.getChildren();

                                        List<String> advertChildren = new ArrayList<>();
                                        for (DataSnapshot postSnapshot: advertsBuffer) {
                                            advertChildren.add( postSnapshot.getValue().toString());
                                        }
                                        Advert advert = new Advert(advertChildren.get(7), advertChildren.get(5), advertChildren.get(4), advertChildren.get(1), advertChildren.get(0), advertChildren.get(6));
                                        advert.setKey(snapshot2.getKey());

                                        //get bitmap from Storage Firebase
                                        StorageReference storageReferenceImage = FirebaseStorage.getInstance().getReference("annonce/"+advert.getKey());

                                        try {
                                            File localeFile = File.createTempFile("tempfile", ".jpeg");
                                            storageReferenceImage.getFile(localeFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    Bitmap bitmap = BitmapFactory.decodeFile(localeFile.getAbsolutePath());
                                                    advert.setImage(bitmap);

                                                    bitmapBuffer.setValue(bitmap);

                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        myAdverts.add(advert);
                                    }

                                }
                            };
                            snapshot.getChildren().forEach(getAdvertKey);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    //Design : Afficher un texte "Vous n'avez pas déposer d'annonces pour le moment"
                    //Peut etre un bouton vers creation d'une annonce
                }
            });

        }

        return view;
    }

    public void displayList(LayoutInflater inflater){

        ville = new String[myAdverts.size()];
        prix = new String[myAdverts.size()];
        temps = new String[myAdverts.size()];
        photo = new Bitmap[myAdverts.size()];
        titre = new String[myAdverts.size()];

        for( int i = 0 ; i < myAdverts.size() ; i++){
            Advert a = myAdverts.get(i);
            ville[i] = a.getLocation();
            prix[i] = a.getPrice();
            temps[i] = a.getDate();
            titre[i] = a.getTitle();
            photo[i] = a.getImage();
        }

        // Implementation de la liste des articles
        simpleList = (ListView) view.findViewById(R.id.listview);
        CustomAdaptater customAdapter = new CustomAdaptater(view.getContext(), ville, prix, temps,photo,titre, R.layout.list_view_item);
        simpleList.setAdapter(customAdapter);
        //On met un ecouteur sur chaque élément de la liste
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment()).commit();

                try {
                    GeneralFunction.sendInfos(myAdverts.get(i), getParentFragmentManager(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Boolean keyIsIn(String key , List<String> keyList){
        for (String keyBuffer : keyList) {
            if(key.equals(keyBuffer)){

                return true;
            }
        }
        return false;
    }
}