package com.example.projet_petite_anonce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projet_petite_anonce.R;

public class CustomAdaptater extends BaseAdapter {
    Context context;



    String countryList[];
    String prixList[];
    String tempList[];
    int photoList[];
    String titreList[];
    int layout;

    LayoutInflater inflater;

    public CustomAdaptater(Context applicationContext, String[] countryList, String[] prixList, String[] tempList, int photoList[],String titreList[], int layout ) {
        this.context = context;
        this.countryList = countryList;
        this.prixList = prixList;
        this.tempList = tempList;
        this.photoList = photoList;
        this.titreList = titreList;
        this.layout = layout;

        inflater = (LayoutInflater.from(applicationContext));
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
            image.setImageResource(photoList[i]);

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
            image.setImageResource(photoList[i]);

        }




        return view;
    }

}
