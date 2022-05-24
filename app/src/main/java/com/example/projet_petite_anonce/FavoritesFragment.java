package com.example.projet_petite_anonce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class FavoritesFragment extends Fragment {

    //Fragment qui permet l'affichage de la liste des articles enregistrée par l'utilisateur
    //Permet de supprimer un élément de la liste
    //Permet d'avoir accées à l'affichage de l'article

    FirebaseAuth mAuth ;
    ListView simpleList;
    List<Advert> adverts;
    View view;
    String[] ville;
    String[] prix ;
    String[] temps ;
    String[] titre;
    Bitmap[] photo;

    DatabaseReference userRef;
    String userUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites, container, false);

        //check if user signed in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        if(user == null){
            //show a connexion button

            //we create an relativelayout (new view)
            RelativeLayout frameLayout = new RelativeLayout(view.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setBackgroundResource(R.drawable.petite_anonce_logo);

            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams1.addRule(RelativeLayout.CENTER_VERTICAL);

            //a child view where text and button are
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setLayoutParams(layoutParams1);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.setMargins(20, 20, 20, 50);
            TextView titre = new TextView(view.getContext());
            titre.setLayoutParams(layoutParams2);
            titre.setText(getResources().getString(R.string.connexionrequise));
            linearLayout.addView(titre);

            Button btn_connexion = new Button(view.getContext());
            btn_connexion.setText(getResources().getString(R.string.connexion));
            btn_connexion.setBackgroundResource(R.drawable.button_sumit);
            btn_connexion.setPadding(20,20,20,20);
            btn_connexion.setTextColor(getResources().getColor(R.color.white));
            btn_connexion.setGravity(Gravity.CENTER);

            btn_connexion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                }
            });

            linearLayout.addView(btn_connexion);

            frameLayout.addView(linearLayout);
            view = frameLayout;

        }
        else{
            //user signed in : it shows his favourites adverts

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            userUID = user.getUid();

            userRef = null;
            //check which kind of user is it
            database.getReference("ClientParticulier").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(userUID))
                        userRef = database.getReference("ClientParticulier").child(userUID);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            if(userRef == null)
                userRef = database.getReference("ClientProfessionnel").child(userUID);

            //if he already has favourite adverts


            DatabaseReference advertDirectory = userRef.child("MyFavAdvert");

            advertDirectory.orderByValue().addValueEventListener(new ValueEventListener() {

               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {

                   //Get favourites keys adverts
                   List<String> keyAdverts = new ArrayList<>();

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
                           //Get the advert in Firebase
                           Consumer getAdvertData = new Consumer<DataSnapshot>(){
                               public void accept(DataSnapshot snapshot2){

                                   if( keyIsIn(snapshot2.getKey() , keyAdverts)) {
                                       Iterable<DataSnapshot> advertsBuffer = snapshot2.getChildren();

                                       List<String> advertChildren = new ArrayList<>();
                                       for (DataSnapshot postSnapshot: advertsBuffer) {
                                           advertChildren.add( postSnapshot.getValue().toString());
                                       }
                                       Advert advert = new Advert(advertChildren.get(7), advertChildren.get(5), advertChildren.get(4), advertChildren.get(2), advertChildren.get(0), advertChildren.get(6));
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
                                               }

                                           }).addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {

                                               }
                                           });
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                       adverts.add(advert);
                                   }

                               };
                           };

                           adverts = new ArrayList<>();
                           snapshot.getChildren().forEach(getAdvertData);

                           ville = new String[adverts.size()];
                           prix = new String[adverts.size()];
                           temps= new String[adverts.size()];
                           titre= new String[adverts.size()];
                           photo= new Bitmap[adverts.size()];

                           //Créer la liste des éléments d'advert
                           for(int i = 0; i < adverts.size(); i++){
                               Advert a = adverts.get(i);
                               ville[i] = a.getLocation();
                               prix[i] = a.getPrice();
                               temps[i] = a.getDate();
                               titre[i] = a.getTitle();
                               photo[i] = a.getImage();
                           }


                           if(ville.length != 0){
                               // Implementation de la liste de favoris
                               simpleList = (ListView) view.findViewById(R.id.listview);
                               CustomAdaptater customAdapter = new CustomAdaptater(getContext(), ville, prix, temps,photo,titre, R.layout.list_view_item);
                               simpleList.setAdapter(customAdapter);
                               //On met un ecouteur sur chaque élément de la liste
                               simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                   @Override
                                   public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                       getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment(adverts.get(i))).commit();

                                   }
                               });
                           }

                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {}
                   });

               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
            });
        }

       return view;
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