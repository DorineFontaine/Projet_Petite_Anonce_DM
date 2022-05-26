package com.example.projet_petite_anonce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * AddFragment : Add an advert if the user already signed in
 */
public class AddFragment extends Fragment {


    private View view;
    private MapView map;
    private IMapController mapController;

    FirebaseAuth mAuth ;
    Button btn_valider;
    EditText editTextTitle, editTextLocalisation, editTextPrice, editTextDescription;
    TextInputLayout typeTIL;
    RadioGroup etatRG;
    RadioButton actualEtat;
    MutableLiveData<Bitmap> bitmap ;
    Uri imageUri;
    Boolean location_validate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add, container, false);

        //check if user signed in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){
            //show a connexion button

            location_validate = false;

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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                }
            });

            linearLayout.addView(btn_connexion);
            frameLayout.addView(linearLayout);
            view = frameLayout;

        }else{

            //if user had an account

            DAOPetiteAnnonce dao = new DAOPetiteAnnonce("Annonce", user.getUid());
            ArrayAdapter arrayAdapter = new ArrayAdapter(view.getContext(), R.layout.dropdown_item, getResources().getStringArray(R.array.categorie));
            AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
            autoCompleteTextView.setAdapter(arrayAdapter);

            mapCreation();

            //advert image button
            bitmap = new MutableLiveData<>();
            ImageButton imageButton = view.findViewById(R.id.imageAnnonce);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GeneralFunction.selectImage(someActivityResultLauncher);
                    bitmap.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
                        @Override
                        public void onChanged(Bitmap newBitmap) {
                            ImageButton imageButton = view.findViewById(R.id.imageAnnonce);
                            imageButton.setImageBitmap(newBitmap);
                        }
                    });
                }
            });

            btn_valider = view.findViewById(R.id.btn_valider);
            editTextTitle = view.findViewById(R.id.editTextTitre);
            editTextDescription = view.findViewById(R.id.editTextDescription);
            editTextLocalisation = view.findViewById(R.id.editTextLocalisation);
            editTextPrice = view.findViewById(R.id.editTextPrix);
            typeTIL = view.findViewById(R.id.dropdown_menu);
            etatRG = view.findViewById(R.id.radioGroup);

            //validation button, every thing is required filed
            btn_valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    String title, des, localisation,price, type, etat;
                    Bitmap image;
                    title = editTextTitle.getText().toString();
                    des =  editTextDescription.getText().toString();
                    localisation= editTextLocalisation.getText().toString();
                    price = editTextPrice.getText().toString();
                    type = typeTIL.getEditText().getText().toString();

                    int selectedEtat = etatRG.getCheckedRadioButtonId();
                    actualEtat = view.findViewById(selectedEtat);
                    etat = (String) actualEtat.getText();

                    image = bitmap.getValue();
                    
                    
                    if(image == null){
                        Toast.makeText(view.getContext(), "Veuillez mettre une image pour votre annonce svp !", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(title.isEmpty()){
                        editTextTitle.setError(getResources().getString(R.string.requisTitle));
                        editTextTitle.requestFocus();
                        return;
                    }

                    if(type.isEmpty()){
                        typeTIL.setError(getResources().getString(R.string.requisType));
                        typeTIL.requestFocus();
                        return;
                    }


                    if(des.isEmpty()){
                        editTextDescription.setError(getResources().getString(R.string.requisDescription));
                        editTextDescription.requestFocus();
                        return;
                    }
                    if(localisation.isEmpty() && location_validate){
                        editTextLocalisation.setError(getResources().getString(R.string.requisLocalisation));
                        editTextLocalisation.requestFocus();
                        return;
                    }
                    if(price.isEmpty()){
                        editTextLocalisation.setError(getResources().getString(R.string.requisPrice));
                        editTextLocalisation.requestFocus();
                        return;
                    }

                    //change some details

                    //place : first letter in uppercase and the rest lowercase
                    localisation = localisation.toLowerCase(Locale.ROOT);
                    String firstLetter = String.valueOf(localisation.charAt(0)).toUpperCase(Locale.ROOT);
                    if(localisation.length() > 1)
                        localisation = firstLetter+localisation.substring(1);
                    else
                        localisation = firstLetter;


                    //add € to the price
                    price = price+" €";
                    String ownerid = user.getUid();

                    Advert advert = new Advert(title,price,localisation,des, type, etat,ownerid);
                    dao.add(advert).addOnSuccessListener(succ ->
                    {
                        //saving image in Firebase storage
                        saveBitmapFirebase(view, advert.getKey());
                        Toast.makeText(getContext(), "Enregistrement réussi", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(er->{
                        Toast.makeText(getContext(), "Enregistrement échoué", Toast.LENGTH_SHORT).show();
                    });


                }
            });


        }
        return view;
    }

    /**
     * Saving a Bitmap with his Uri in Firebase
     * @param view actual view to tell you user if operation doesnt success
     * @param keyAdvert unique key of th advert
     */
    public void saveBitmapFirebase(View view, String keyAdvert){
        //profil image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("annonce/"+keyAdvert+"/");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(), "Veuillez réessayer l'image n'a pas été enregistré dans la base de données.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Go pick up one image on the cellphone
     */
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!= null) {

                        imageUri = result.getData().getData();
                        Bitmap srcBmp = null;
                        try {
                            srcBmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        } catch (FileNotFoundException e) {
                            Log.e("GET_IMAGE", e.getMessage(), e);
                        } catch (IOException e) {
                            Log.e("GET_IMAGE", e.getMessage(), e);
                        }

                        bitmap.setValue(srcBmp);
                    }
                }
            });

    /**
     * Creating a map (where will be the advert)
     */
    public void mapCreation(){
        Configuration.getInstance().load(getActivity().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()));

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        //map centered on Montpellier
        GeoPoint starPoint =new GeoPoint(43.610769 , 3.876716);
        mapController = map.getController();
        mapController.setCenter(starPoint);
        mapController.setZoom(13.0);

        //buttons definition
        Button btn_localisation = view.findViewById(R.id.btn_localisation);
        btn_localisation.setOnClickListener(view -> convertAddressToCoord());

    }

    /**
     * Converting address given by the user into coord if it exists
     */
    public void convertAddressToCoord(){

        //get user address
        EditText editText = view.findViewById(R.id.editTextLocalisation);
        String localisation = editText.getText().toString();

        //get coord of the address

        try {
            Geocoder geocoder = new Geocoder(view.getContext(), Locale.FRENCH);
            List<Address> adresses = geocoder.getFromLocationName(localisation, 1);
            Address address = adresses.get(0);
            Double longitude = address.getLongitude();
            Double latitude = address.getLatitude();

            ArrayList<OverlayItem> items = new ArrayList<>();
            OverlayItem item = new OverlayItem("Mon annonce", address.getAddressLine(0), new GeoPoint(latitude, longitude));

            //maker style
            Drawable drawable = getActivity().getDrawable(R.drawable.ic_baseline_location_on_24);
            item.setMarker(drawable);
            items.add(item);
            //centering map on his address
            mapController.setCenter(new GeoPoint(latitude, longitude));

            //to verify if address's user is validate by osm
            location_validate = true;

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(getActivity().getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                @Override
                public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    return true;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            });

            //focus when clicked on
            mOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlay);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Veuillez entrer une adresse valide svp", Toast.LENGTH_SHORT).show();

            //clean the location
            editTextLocalisation.setText("");
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
