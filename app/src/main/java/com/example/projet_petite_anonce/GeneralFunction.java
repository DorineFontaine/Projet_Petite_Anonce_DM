package com.example.projet_petite_anonce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        result.putString("ownerid", advert.getOwnerid());
        result.putByteArray("image", image);

        fragmentManager.setFragmentResult(affiche?"affiche":"modif", result);
    }

    /**
     * Converting a bitmap into a byte array
     * @param b Bitmap
     * @return byte array
     * @throws IOException in case of an IOException
     */
    public static byte[] convert(Bitmap b) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] array = stream.toByteArray();
        stream.close();
        return array;
    }

    /**
     * Convertinf a byte array into a bitmap
     * @param array byte array
     * @return bitmap
     */
    public static Bitmap convert(byte[] array) {
        return BitmapFactory.decodeByteArray(array,0,array.length);
    }

    /**
     * Getting informations in Bundle
     * @param bundle bundle
     * @return A advert with all the informations
     */
    public static Advert getInfos(Bundle bundle){

        String location = bundle.getString("location");
        String price = bundle.getString("price");
        String date = bundle.getString("date");
        String title = bundle.getString("titre");
        String key = bundle.getString("key");
        String category = bundle.getString("category");
        String description = bundle.getString("description");
        String state = bundle.getString("state");
        String ownerid = bundle.getString("ownerid");
        byte[] image = bundle.getByteArray("image");
        Advert a = new Advert(title, price, location, description, category, state,ownerid);
        a.setKey(key);
        a.setDate(date);
        a.setImage(convert(image));

        return a;
    }

    /**
     * Selecting an image in the phone
     * @param someActivityResultLauncher you can find it in HomeFragment for example
     */
    public static void selectImage(ActivityResultLauncher someActivityResultLauncher){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }



}
