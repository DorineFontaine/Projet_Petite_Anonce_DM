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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;


public class HomeFragment extends Fragment {

    //Fragment qui permet l'affichage d'annonce aléatoire
    //Possibilité d'effectué une recherche dans la base de données de l'application
    //Possibilité de mettre des filtres sur cette recherche
    //Permet d'avoir accées à l'affichage de l'article
    //Permet d'ajouter des articles à la liste de favoris

    GridView simpleList;
    ImageView favoris;
    FirebaseAuth mAuth;
    FirebaseUser user;
    View view;
    String[] ville;
    String[] prix;
    String[] date;
    String[] titre;
    Bitmap[] photo;
    List<Advert> adverts;

    MutableLiveData<Bitmap> bitmapBuffer ;
    int display;
    int loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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

        //Get all the advert in Firebase
        Consumer getAdvertData = new Consumer<DataSnapshot>(){
            public void accept(DataSnapshot snapshot){

                Iterable<DataSnapshot> advertsBuffer = snapshot.getChildren();

                List<String> advertChildren = new ArrayList<>();
                for (DataSnapshot postSnapshot: advertsBuffer) {
                    advertChildren.add( postSnapshot.getValue().toString());
                }
                Advert advert = new Advert(advertChildren.get(7), advertChildren.get(5), advertChildren.get(4), advertChildren.get(2), advertChildren.get(0), advertChildren.get(6), user.getUid());
                advert.setKey(snapshot.getKey());

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
                        public void onFailure(@NonNull Exception e) {}
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                adverts.add(advert);

            };
        };
        DatabaseReference advertDirectory = FirebaseDatabase.getInstance().getReference("Annonce");

        advertDirectory.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> iterableSnapshot = snapshot.getChildren();
                loading = 0;
                display = 0;
                for (DataSnapshot snapshotBuffer : iterableSnapshot){
                    display++;
                }

                adverts = new ArrayList<>();
                snapshot.getChildren().forEach(getAdvertData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(view.getContext(), "NOOOOOOOOO", Toast.LENGTH_SHORT).show();
                //Design : Affichage un message au centre "Pas d'annonce disponible"
            }
        });

        return view;
    }

    public void displayList(LayoutInflater inflater){

        ville = new String[adverts.size()];
        prix = new String[adverts.size()];
        date= new String[adverts.size()];
        titre= new String[adverts.size()];
        photo= new Bitmap[adverts.size()];

        //Créer la liste des éléments d'advert
        for(int i = 0; i < adverts.size(); i++){
            Advert a = adverts.get(i);
            ville[i] = a.getLocation();
            prix[i] = a.getPrice();
            date[i] = a.getDate();
            titre[i] = a.getTitle();
            photo[i] = a.getImage();
        }
        // Implementation de la liste de message
        simpleList = (GridView) view.findViewById(R.id.grid_view);

        View viewTest = view = inflater.inflate(R.layout.grid_view_item,(ViewGroup) simpleList.getParent(), false);

        CustomAdaptater customAdapter = new CustomAdaptater(view.getContext(), ville, prix, date,photo,titre, R.layout.grid_view_item);

        simpleList.setAdapter(customAdapter);

        //On met un ecouteur sur chaque élément de la liste
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment()).commit();

                try {
                    GeneralFunction.sendInfos(adverts.get(i), getParentFragmentManager(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}