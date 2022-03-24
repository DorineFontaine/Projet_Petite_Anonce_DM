package com.example.projet_petite_anonce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout page_anonce = findViewById(R.id.page_annonce);
        Intent page_suivant = new Intent(this, Bottom_Ng_Activity.class);
        page_anonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(page_suivant);

            }
        });

    }
}