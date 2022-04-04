package com.example.projet_petite_anonce;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static int TIME_OUT = 4000;

    private Handler mHandler = new Handler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout page_anonce = findViewById(R.id.page_annonce);
        Intent page_suivant = new Intent(this, Bottom_Ng_Activity.class);

        // rediriger vers le menu principale apres quelque secondes
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent page_suivant = new Intent(MainActivity.this, Bottom_Ng_Activity.class);
                startActivity(page_suivant);
                finish();
            }
        };

        //met en pause
        mHandler.postDelayed(runnable, 4000); //4 seconds


       /* page_anonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(page_suivant);

            }
        });*/

       // startActivity(page_suivant);

    }
}