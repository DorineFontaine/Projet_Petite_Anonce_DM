package com.example.projet_petite_anonce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralFunction {
    /**
     * Send advert to ParentFragmentManager to display it in AfficheFragment
     * @param advert advert to display
     */
    public static void sendInfos(Advert advert, FragmentManager fragmentManager) throws IOException {

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

        fragmentManager.setFragmentResult("affiche", result);
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
}
