package com.example.projet_petite_anonce;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class AddFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private int mParam1;

    private View view;
    private MapView map;
    private IMapController mapController;

    FirebaseAuth mAuth ;
    Button btn_valider;
    EditText editTextTitle, editTextLocalisation, editTextPrice, editTextDescription;
    RadioButton bon, satif, neuf,tbon;
    String etat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add, container, false);

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

        }else{

            DAOPetiteAnnonce dao = new DAOPetiteAnnonce("Annonce");
            ArrayAdapter arrayAdapter = new ArrayAdapter(view.getContext(), R.layout.dropdown_item, getResources().getStringArray(R.array.categorie));
            AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
            autoCompleteTextView.setAdapter(arrayAdapter);

            mapCreation();

            //INITIALISATION
            btn_valider = view.findViewById(R.id.btn_valider);
            editTextTitle = view.findViewById(R.id.editTextTitre);
            editTextDescription = view.findViewById(R.id.editTextDescription);
            editTextLocalisation = view.findViewById(R.id.editTextLocalisation);
            editTextPrice = view.findViewById(R.id.editTextPrix);
            bon = view.findViewById(R.id.bon);
            satif= view.findViewById(R.id.satis);
            neuf = view.findViewById(R.id.neuf);
            tbon = view.findViewById(R.id.tresbon);

            //ADD

            btn_valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title, des, localisation,price;
                    title = editTextTitle.getText().toString();
                    des =  editTextDescription.getText().toString();
                    localisation= editTextLocalisation.getText().toString();
                    price = editTextPrice.getText().toString();

                    if(title.isEmpty()){
                        editTextTitle.setError(getResources().getString(R.string.requisTitle));
                        editTextTitle.requestFocus();
                        return;
                    }


                    if(des.isEmpty()){
                        editTextDescription.setError(getResources().getString(R.string.requisDescription));
                        editTextDescription.requestFocus();
                        return;
                    }
                    if(localisation.isEmpty()){
                        editTextLocalisation.setError(getResources().getString(R.string.requisLocalisation));
                        editTextLocalisation.requestFocus();
                        return;
                    }
                    if(price.isEmpty()){
                        editTextLocalisation.setError(getResources().getString(R.string.requisPrice));
                        editTextLocalisation.requestFocus();
                        return;
                    }

                    if(bon.isChecked()){
                        etat = "Bon état";
                    }
                    if(tbon.isChecked()){
                        etat = "Trés bon état";
                    }
                    if(satif.isChecked()){
                        etat = "Satisfaisant";
                    }
                    if(neuf.isChecked()){
                        etat = "Neuf";
                    }



                    Advert advert = new Advert(title,price,localisation,des,etat);

                    dao.add(advert).addOnSuccessListener(succ ->
                    {
                        Toast.makeText(getContext(), "Enregistrement rèussie", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(er->{
                        Toast.makeText(getContext(), "Enregistrement echouer", Toast.LENGTH_SHORT).show();
                    });



                }
            });


        }
        return view;
    }

    public void mapCreation(){
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        GeoPoint starPoint =new GeoPoint(43.610769 , 3.876716);//ma map est centrée sur montpellier
        mapController = map.getController();
        mapController.setCenter(starPoint);
        mapController.setZoom(13.0); //entre 0 et 25 nombre flotant FAUT JOUER AVEC LE ZOOM

        //définition des boutons
        Button btn_localisation = view.findViewById(R.id.btn_localisation);
        btn_localisation.setOnClickListener(view -> convertAddressToCoord());

    }

    public void convertAddressToCoord(){

        //recuperer ce qui est dans l'edit text
        EditText editText = view.findViewById(R.id.editTextLocalisation);
        String localisation = editText.getText().toString();

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
            Toast.makeText(view.getContext(), "Veuillez entrer une adresse valide svp", Toast.LENGTH_SHORT).show();
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
}
