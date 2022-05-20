package com.example.projet_petite_anonce;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomAdaptater extends BaseAdapter {

    //Classe qui nous permet d'implementer nos liste

    Context context;



    String[] countryList;
    String[] prixList;
    String[] tempList;
    Bitmap[] photoList;
    String[] titreList;
    int[]fav;
    int layout;

    LayoutInflater inflater;

    public CustomAdaptater(Context applicationContext, String[] countryList, String[] prixList, String[] tempList, Bitmap[] photoList, String[] titreList, int []fav, int layout ) {
        Log.i("Custom", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT");

        this.context = context;
        this.countryList = countryList;
        this.prixList = prixList;
        this.tempList = tempList;
        this.photoList = photoList;
        this.titreList = titreList;
        this.layout = layout;
        this.fav = fav;
        Log.i("Custom Robe", ""+photoList[0]);

        if(fav != null)
         Log.i("Custom Heart 0 ", ""+fav[0]);

        inflater = (LayoutInflater.from(applicationContext));

/*
        Log.i("Custom1", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT");

        if(fav != null){
            this.fav = new int[fav.length];
            Log.i("Custom1bis", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT");

            Log.i("Custom2", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT"+fav.length);

            for(int i = 0 ; i < fav.length ; i++){
                if(fav[i] != null && fav[i])
                    this.fav[i] = R.drawable.ic_baseline_favorite_24;
                else if(fav[i] != null && !fav[i])
                    this.fav[i] = R.drawable.ic_baseline_favorite_border_24;
            }
        }else
            Log.i("Custom3", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT");

        Log.i("Custom4", "WHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAT");
*/
    }

    @Override
    public int getCount() {
        return countryList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(layout, null);
        if (layout == R.layout.list_view_item){
            TextView country = (TextView) view.findViewById(R.id.ville);
            TextView temps = (TextView) view.findViewById(R.id.temps);
            TextView prix = (TextView) view.findViewById(R.id.prix);
            TextView titre = (TextView) view.findViewById(R.id.titre);
            ImageView image = (ImageView) view.findViewById(R.id.icon);


            country.setText(countryList[i]);
            prix.setText(prixList[i]);
            temps.setText(tempList[i]);
            titre.setText(titreList[i]);
            image.setImageBitmap(photoList[i]);

            if(fav != null){
                ImageView imageFav = (ImageView) view.findViewById(R.id.fav);
                imageFav.setImageResource(fav[i]);
            }

        }else{
            TextView country = (TextView) view.findViewById(R.id.grid_ville);
            TextView temps = (TextView) view.findViewById(R.id.grid_temps);
            TextView prix = (TextView) view.findViewById(R.id.grid_prix);
            TextView titre = (TextView) view.findViewById(R.id.grid_title);
            ImageView image = (ImageView) view.findViewById(R.id.grid_icon);

            country.setText(countryList[i]);
            prix.setText(prixList[i]);
            temps.setText(tempList[i]);
            titre.setText(titreList[i]);
            image.setImageBitmap(photoList[i]);

            if(fav != null){
                ImageView imageFav = (ImageView) view.findViewById(R.id.fav);
                //imageFav.setImageResource(fav[i]);
                imageFav.setImageResource(fav[i]);
                Log.i("Custom Heart", ""+fav[i]);
            }
        }




        return view;
    }


}

