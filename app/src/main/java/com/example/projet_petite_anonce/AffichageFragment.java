package com.example.projet_petite_anonce;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;


public class AffichageFragment extends Fragment {

    ImageView imageAnonce;
    TextView textPrix, textTitre, textDate;
    View view;
    Advert a;
    private MapView map;
    private IMapController mapController;
    private static final int MY_PERMISSION_REQUEST_CODE_CALL_PHONE = 555;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String userUID;
    DatabaseReference favRef;
    DatabaseReference userRef;
    DatabaseReference userAdvertRef;
    Button contacter;

    public AffichageFragment(){}


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_affichage, container, false);
        imageAnonce = view.findViewById(R.id.imageAnnonce);
        textPrix = view.findViewById(R.id.prix);
        textTitre = view.findViewById(R.id.titre);
        textDate = view.findViewById(R.id.date);
        contacter = view.findViewById(R.id.btn_contacter);

        getParentFragmentManager().setFragmentResultListener("affiche", this, (requestKey, bundle) -> {
            a = GeneralFunction.getInfos(bundle);

            textDate.setText(a.getDate());
            textPrix.setText(a.getPrice());
            imageAnonce.setImageBitmap(a.getImage());
            textTitre.setText(a.getTitle());

            Button nombreImage = view.findViewById(R.id.nombreImage);
            nombreImage.setText("1/1");

            TextView description = view.findViewById(R.id.description);
            description.setText(a.getDescription());

            TextView etat = view.findViewById(R.id.etatAnnonce);
            etat.setText(a.getState());

            TextView categorie = view.findViewById(R.id.categorieAnnonce);
            categorie.setText(a.getCategory());

            TextView localisation = view.findViewById(R.id.localisation);
            localisation.setText(a.getLocation());

            mapCreation(a.getLocation());

            if(user != null){
                userUID = user.getUid();
                //fav and share/delete buttons (depends if this is his advert)
                ImageButton star_button = view.findViewById(R.id.etoile);
                Button share = view.findViewById(R.id.btn_contacter);

                //we want to know if he is a ClientParticulier or ClientProfessionnel
                userRef = null;
                //check which kind of user is it
                FirebaseDatabase.getInstance().getReference("ClientParticulier").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userUID))
                            userRef = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(userUID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                if(userRef == null)
                    userRef = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(userUID);

                //check if this is one of his adverts
                userRef.child("MyAdvert").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Iterable<DataSnapshot> list_snapshot = snapshot.getChildren();
                        for (DataSnapshot snapshot1 : list_snapshot){

                            //he is
                            if(a.getKey().equals(snapshot1.getValue().toString())){
                                star_button.setVisibility(View.INVISIBLE);

                                //change contact button into modif button
                                share.setText(R.string.modif_annonce);
                                share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ModifAnnonce()).commit();

                                        try {
                                            GeneralFunction.sendInfos(a, getParentFragmentManager(), false);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        /**************************************************************************************/
                                    }
                                });

                                Dialog dialog = new Dialog(getContext());

                                Button delete_btn = view.findViewById(R.id.btn_supp);
                                delete_btn.setVisibility(View.VISIBLE);

                                delete_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                        alertDialogBuilder.setMessage(R.string.suppression);
                                        alertDialogBuilder.setPositiveButton(R.string.oui,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        // delete advert in myAdvert in Firebase
                                                        snapshot1.getRef().removeValue();

                                                        //delete advert in MyFavAdvert of users
                                                        //delete his advert in MyFavAdverts of others users (ClientProfessionnel and ClientParticulier)
                                                        DatabaseReference allUsers = FirebaseDatabase.getInstance().getReference("ClientProfessionnel");
                                                        allUsers.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshotUsers) {
                                                                deleteFav(snapshotUsers, a.getKey());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {}
                                                        });
                                                        allUsers = FirebaseDatabase.getInstance().getReference("ClientParticulier");
                                                        allUsers.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshotUsers) {
                                                                deleteFav(snapshotUsers, a.getKey());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {}
                                                        });

                                                        //delete advert's picture in Firebase Storage
                                                        StorageReference pictureRef = FirebaseStorage.getInstance().getReference("annonce/"+a.getKey());
                                                        pictureRef.delete();

                                                        //delete in annonce
                                                        DatabaseReference annonces = FirebaseDatabase.getInstance().getReference("Annonce");
                                                        annonces.child(a.getKey()).removeValue();
                                                    }
                                                });

                                        alertDialogBuilder.setNegativeButton(R.string.non
                                                ,new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                        //doing nothing

                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                //check if he already had it in his MyFavAdvert
                userRef.child("MyFavAdvert").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favRef = null;
                        Iterable<DataSnapshot> list_snapshot = snapshot.getChildren();
                        for (DataSnapshot snapshot1 : list_snapshot){
                            if(a.getKey().equals(snapshot1.getValue().toString())){
                                star_button.setImageResource(R.drawable.ic_baseline_favorite_24);
                                favRef = snapshot1.getRef();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });


                star_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(favRef != null){
                            favRef.removeValue();
                            star_button.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                        else{
                            star_button.setImageResource(R.drawable.ic_baseline_favorite_24);
                            userRef.child("MyFavAdvert").push().setValue(a.getKey());
                        }
                    }
                });

            }
        });

        //Contact with the seller

        contacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the user can choose to contact the seller by email or telephone

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage(R.string.contact_vendeur);
                alertDialogBuilder.setPositiveButton(R.string.Ptel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String ownerid = a.getOwnerid();
                        String annonce = a.getTitle();
                        Log.d("OWNERID" ,ownerid);


                        //we want to know if he is a ClientParticulier or ClientProfessionnel
                        userAdvertRef = null;
                        //check which kind of user is it
                        FirebaseDatabase.getInstance().getReference("ClientParticulier").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(ownerid))
                                    userAdvertRef = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(ownerid);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        if(userAdvertRef == null)
                            userAdvertRef = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(ownerid);

                        userAdvertRef.child("tel").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Object telTest = snapshot.getValue();

                                if (telTest != null) {
                                    Log.d("TELEPHONE","j ai un  tel ");
                                    askPermissionAndCall((String) telTest);
                                } else {
                                    Log.d("TELEPHONE","je n'est pas de tel ");
                                    sendMail(ownerid,annonce);
                                }
                            }




                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





                    }
                });

                alertDialogBuilder.setNegativeButton(R.string.Pmail,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ownerid = a.getOwnerid();
                        String annonce = a.getTitle();
                        sendMail(ownerid, annonce);

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        return view;
    }

    public void mapCreation(String localisation){
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        GeoPoint starPoint =new GeoPoint(43.610769 , 3.876716);//ma map est centrée sur montpellier
        mapController = map.getController();
        mapController.setCenter(starPoint);
        mapController.setZoom(13.0); //entre 0 et 25 nombre flotant FAUT JOUER AVEC LE ZOOM

        if(localisation != null)
            convertAddressToCoord(localisation);

    }

    public void convertAddressToCoord(String localisation){

        //recuperer les coordonnées de l'adresse

        try {
            Geocoder geocoder = new Geocoder(view.getContext(), Locale.FRENCH);
            List<Address> adresses = geocoder.getFromLocationName(localisation, 1);
            Address address = adresses.get(0);
            Double longitude = address.getLongitude();
            Double latitude = address.getLatitude();

            ArrayList<OverlayItem> items = new ArrayList<>();
            OverlayItem item = new OverlayItem("Mon annonce", address.getAddressLine(0), new GeoPoint(latitude, longitude));

            //on change le style du marker
            Drawable drawable = getActivity().getDrawable(R.drawable.ic_baseline_location_on_24);
            item.setMarker(drawable);
            items.add(item);
            //on recentre la map sur le marker
            mapController.setCenter(new GeoPoint(latitude, longitude));



            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(getActivity().getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                @Override
                public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    //quand je clique dessus
                    return true;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            });

            //ca focus quand on clique dessus
            mOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlay);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * snapshotUsers : all users
     * @param snapshotUsers
     */
    public void deleteFav(DataSnapshot snapshotUsers, String keyAdvert){
        List<DatabaseReference> refToDelete = new ArrayList<>();

        Consumer deleteFavAdverts = new Consumer<DataSnapshot>(){
            public void accept(DataSnapshot snapshot2){
                //get the myFavAdverts from the user
                if(snapshot2.hasChild("MyFavAdvert")){

                    //List<String> favAdverts = (List<String>) snapshot2.child("MyFavAdverts").getValue();
                    Iterable<DataSnapshot> listFav = snapshot2.child("MyFavAdvert").getChildren();

                    for (DataSnapshot postSnapshot: listFav) {
                        if(keyAdvert.equals(postSnapshot.getValue().toString()))
                            refToDelete.add(postSnapshot.getRef());
                    }
                }
            }
        };

        snapshotUsers.getChildren().forEach(deleteFavAdverts);

        for (DatabaseReference toDelete : refToDelete){
            toDelete.removeValue();
        }
    }
    private void askPermissionAndCall(String phoneNumber) {

        // With Android Level >= 23, you have to ask the user
        // for permission to Call.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23

            // Check if we have Call permission
            int sendSmsPermisson = ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CALL_PHONE);

            if (sendSmsPermisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSION_REQUEST_CODE_CALL_PHONE
                );
                return;
            }
        }
        this.callNow(phoneNumber);
    }

    private void callNow(String phoneNumber) {


        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            this.startActivity(callIntent);
        } catch (Exception ex) {
            Toast.makeText(getContext(),"Your call failed... " + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void sendMail(String ownerid, String annonce){

        userAdvertRef = FirebaseDatabase.getInstance().getReference().child(ownerid);
        userAdvertRef.child("mail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Object mailTest = snapshot.getValue();
                if (mailTest != null){

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{(String)mailTest});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Achat " + annonce);
                    i.putExtra(Intent.EXTRA_TEXT   , "Bonjour je suis trés intérréssé par votre annonce");

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }






}