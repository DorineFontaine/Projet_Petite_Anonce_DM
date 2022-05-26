package com.example.projet_petite_anonce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeneralFunction {

    /**
     *
     * @param advert
     * @param fragmentManager
     * @param affiche if true then requestKey will be affiche else it will be modif
     * @throws IOException
     */
    public static void sendInfos(Advert advert, FragmentManager fragmentManager, Boolean affiche) throws IOException {

        Bundle result = new Bundle();
        byte[] image = convert(advert.getImage());
        result.putString("location", advert.getLocation());
        result.putString("price", advert.getPrice());
        result.putString("date", advert.getDate());
        result.putString("titre", advert.getTitle());
        result.putString("key", advert.getKey());
        result.putString("category", advert.getCategory());
        result.putString("description", advert.getDescription());
        result.putString("state", advert.getState());
        result.putByteArray("image", image);

        fragmentManager.setFragmentResult(affiche?"affiche":"modif", result);
    }

    public static byte[] convert(Bitmap b) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] array = stream.toByteArray();
        stream.close();
        return array;
    }

    public static Bitmap convert(byte[] array) {
        return BitmapFactory.decodeByteArray(array,0,array.length);
    }

    public static Advert getInfos(Bundle bundle){

        String location = bundle.getString("location");
        String price = bundle.getString("price");
        String date = bundle.getString("date");
        String title = bundle.getString("titre");
        String key = bundle.getString("key");
        String category = bundle.getString("category");
        String description = bundle.getString("description");
        String state = bundle.getString("state");
        byte[] image = bundle.getByteArray("image");
        Advert a = new Advert(title, price, location, description, category, state);
        a.setKey(key);
        a.setDate(date);
        a.setImage(convert(image));

        return a;
    }

    public static void selectImage(ActivityResultLauncher someActivityResultLauncher){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }



}
