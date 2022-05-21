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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    String ville[];
    String prix[];
    String temps[];
    String titre[];
    int photo[]; //
    List<Advert> adverts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        favoris = view.findViewById(R.id.fav);

        //Get all the advert in Firebase
        Consumer getAdvertData = new Consumer<DataSnapshot>(){
            public void accept(DataSnapshot snapshot){

                String aBuffer = snapshot.getValue().toString();
                //price : "price=" et ","
                String price = aBuffer.substring(aBuffer.indexOf("price=")+6, aBuffer.indexOf(","));

                //suppression de price dans aBuffer
                aBuffer = aBuffer.substring(aBuffer.indexOf(",")+1);

                //description : "description=" et ","
                String description =aBuffer.substring(aBuffer.indexOf("description=")+12, aBuffer.indexOf(","));
                aBuffer = aBuffer.substring(aBuffer.indexOf(",")+1);

                //location : "location=" et ","
                String location =aBuffer.substring(aBuffer.indexOf("location=")+9, aBuffer.indexOf(","));
                aBuffer = aBuffer.substring(aBuffer.indexOf(",")+1);

                //title "title="
                String title =aBuffer.substring(aBuffer.indexOf("title=")+6,aBuffer.indexOf("}"));

                String aKey = snapshot.getKey();

                Advert a = new Advert(title, price, location, description);
                a.setKey(aKey);
                adverts.add(a);
            };
        };
        DatabaseReference advertDirectory = FirebaseDatabase.getInstance().getReference("Annonce");
        advertDirectory.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adverts = new ArrayList<>();
                snapshot.getChildren().forEach(getAdvertData);

                ville = new String[adverts.size()];
                prix = new String[adverts.size()];
                temps= new String[adverts.size()];
                titre= new String[adverts.size()];
                photo= new int[adverts.size()];

                //Créer la liste des éléments d'advert
                for(int i = 0; i < adverts.size(); i++){
                    Advert a = adverts.get(i);
                    ville[i] = a.getLocation();
                    prix[i] = a.getPrice();
                    temps[i] = "pas encore dans la bdd";
                    titre[i] = a.getTitle();
                    photo[i] = R.drawable.robe; //R.drawable pas encore dans bdd
                }


                // Implementation de la liste de message
                simpleList = (GridView) view.findViewById(R.id.grid_view);
                CustomAdaptater customAdapter = new CustomAdaptater(getContext(), ville, prix, temps,photo,titre,  R.layout.grid_view_item);
                simpleList.setAdapter(customAdapter);

                //On met un ecouteur sur chaque élément de la liste
                simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        ImageView fav = view.findViewById(R.id.fav);
                        fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fav.setBackgroundResource(R.drawable.ic_baseline_favorite_24);


                            }
                        });

                        LinearLayout linearLayout = view.findViewById(R.id.affiche);
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //On transmet le prix le titre pour faire un affichage dynamique du coté de AffichageFragment

                                //FAIRE PASSER adverts.get(i) dans AffichageFragment
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AffichageFragment(photo[i],prix[i],titre[i],temps[i])).commit();


                            }
                        });



                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


    //Marquage des articles favoris (remplissage du coeur)
    //Pas fonctionnel pour le moment
    public void favoris() {

        favoris.setImageResource(R.drawable.ic_baseline_favorite_24);

        //if user is connected
        if(user != null){
            //Save this advert with the other in a special directory
            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(user.getUid()).child("liked_advert");

            //Get the liked advert key in Firebase
            String key_advert = null;

            DatabaseReference liked_advert = userInformation.child(key_advert);
        }
    }
}