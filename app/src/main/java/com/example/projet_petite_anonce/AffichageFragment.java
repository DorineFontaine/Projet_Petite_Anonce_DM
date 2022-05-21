package com.example.projet_petite_anonce;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


public class AffichageFragment extends Fragment {

    ImageView imageAnonce;
    TextView textPrix, textTitre, textDate;
    View view;
    Advert a;
    private MapView map;
    private IMapController mapController;

    //Supposé etre vide !!!
    public AffichageFragment(Advert advert){
        a = advert;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_affichage, container, false);
        imageAnonce = view.findViewById(R.id.imageAnnonce);
        textPrix = view.findViewById(R.id.prix);
        textTitre = view.findViewById(R.id.titre);
        textDate = view.findViewById(R.id.date);

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

}