package com.example.projet_petite_anonce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
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
    View rootView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        bitmapBuffer = new MutableLiveData<>();
        bitmapBuffer.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap newBitmap) {
                loading++;
                if(display == loading){
                    ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                    displayList(inflater, adverts);
                }
            }
        });

        //when he clicked on filters button
        Button filters = rootView.findViewById(R.id.button3);
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFiltersDialog filtersDialog = new HomeFiltersDialog();

                filtersDialog.show(getActivity().getSupportFragmentManager(), filtersDialog.getTag());

                getParentFragmentManager().setFragmentResultListener("Reinitialiser", getViewLifecycleOwner(), (requestKey, bundle) -> {
                    if(!adverts.isEmpty())
                        displayList(inflater, adverts);

                });

                getParentFragmentManager().setFragmentResultListener("filter", getViewLifecycleOwner(), (requestKey, bundle) -> {
                    boolean[] categories = (boolean[]) bundle.get("categories");
                    boolean[] states = (boolean[]) bundle.get("states");
                    String minPrice = (String) bundle.get("minPrice");
                    String maxPrice = (String) bundle.get("maxPrice");

                    //filtering adverts
                    if(!adverts.isEmpty()){
                        List<Advert> filteredAdverts = new ArrayList<>();

                        //categories
                        List<String> categoryfilter = new ArrayList<>();
                        for(int iteration = 0; iteration <categories.length; iteration++){
                            if(categories[iteration])
                                categoryfilter.add(getResources().getStringArray(R.array.categorie)[iteration]);
                        }

                        //states
                        List<String> statefilter = new ArrayList<>();
                        if(states[0])
                            statefilter.add("Neuf");
                        if(states[1])
                            statefilter.add("Très bon état");
                        if(states[2])
                            statefilter.add("Bon état");
                        if(states[3])
                            statefilter.add("Satisfaisant");

                        float minP = Float.parseFloat(minPrice);
                        float maxP = Float.parseFloat(maxPrice);

                        if(categoryfilter.isEmpty() && statefilter.isEmpty() && minP == 0 && maxP == 1100 && !adverts.isEmpty()){
                            displayList(inflater, adverts);

                        }else{
                            for(Advert a : adverts){
                                float aPrice = Float.parseFloat(a.getPrice().replace(" €", ""));

                                if(!categoryfilter.isEmpty() && categoryfilter.contains(a.getCategory())){
                                    if(!statefilter.isEmpty() && statefilter.contains(a.getState()) ){
                                        if(priceFiltered(maxP, minP, aPrice))
                                            filteredAdverts.add(a);
                                    }
                                    else if (statefilter.isEmpty()){
                                        if(priceFiltered(maxP, minP, aPrice))
                                            filteredAdverts.add(a);
                                    }
                                }
                                else if (categoryfilter.isEmpty()){
                                    if(!statefilter.isEmpty() && statefilter.contains(a.getState()) ){
                                        if(priceFiltered(maxP, minP, aPrice))
                                            filteredAdverts.add(a);
                                    }
                                    else if (statefilter.isEmpty()){
                                        if(priceFiltered(maxP, minP, aPrice))
                                            filteredAdverts.add(a);
                                    }
                                }

                            }


                            Log.i("*****************FILTRAGE : ", String.valueOf(filteredAdverts.size()));

                            if(!filteredAdverts.isEmpty())
                                displayList(inflater, filteredAdverts);
                            else{
                                simpleList.setVisibility(View.INVISIBLE);
                            }

                        }
                    }

                });

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
                Advert advert = new Advert(advertChildren.get(8), advertChildren.get(6), advertChildren.get(4), advertChildren.get(2), advertChildren.get(0), advertChildren.get(7), advertChildren.get(5));
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

                ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

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

                Toast.makeText(rootView.getContext(), "NOOOOOOOOO", Toast.LENGTH_SHORT).show();
                //Design : Affichage un message au centre "Pas d'annonce disponible"
            }
        });

        return rootView;
    }

    public void displayList(LayoutInflater inflater, List<Advert> liste){

        //clean GridView

        Log.i("*****************Taille du filtrage : ", String.valueOf(liste.size()));

            ville = new String[liste.size()];
            prix = new String[liste.size()];
            date= new String[liste.size()];
            titre= new String[liste.size()];
            photo= new Bitmap[liste.size()];

            //Créer la liste des éléments d'advert
            for(int i = 0; i < liste.size(); i++){
                Advert a = liste.get(i);
                ville[i] = a.getLocation();
                prix[i] = a.getPrice();
                date[i] = a.getDate();
                titre[i] = a.getTitle();
                photo[i] = a.getImage();
                Log.i("Adverts affichage", ville[i]);
                Log.i("Adverts affichage", prix[i]);
                Log.i("Adverts affichage", date[i]);
                Log.i("Adverts affichage", titre[i]);
            }
            // Implementation de la liste de message
            simpleList = (GridView) rootView.findViewById(R.id.grid_view);


            CustomAdaptater customAdapter = new CustomAdaptater(rootView.getContext(), ville, prix, date,photo,titre, R.layout.grid_view_item);

            simpleList.setVisibility(View.VISIBLE);
            simpleList.setAdapter(customAdapter);

            //On met un ecouteur sur chaque élément de la liste
            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment()).commit();

                    try {
                        GeneralFunction.sendInfos(liste.get(i), getParentFragmentManager(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });


    }

    public Boolean priceFiltered(float maxP, float minP, float aPrice){
        if(maxP == 2500 && aPrice >= minP)
            return true;
        else if(aPrice <= maxP && aPrice >= minP)
            return true;
        else
            return false;
    }


}