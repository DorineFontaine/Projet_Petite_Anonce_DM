package com.example.projet_petite_anonce;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    //Page de chargement
    //Affiche la page principale aprés 4 secondes

    private static int TIME_OUT = 4000;
    private Handler mHandler = new Handler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Redirection vers la page principale après quelques secondes

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent page_suivant = new Intent(MainActivity.this, Bottom_Ng_Activity.class);
                startActivity(page_suivant);
                finish();
            }
        };

        //Mise en pause
        mHandler.postDelayed(runnable, 4000); //4 seconds



    }
}