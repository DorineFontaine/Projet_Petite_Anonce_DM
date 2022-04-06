package com.example.projet_petite_anonce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Bottom_Ng_Activity extends AppCompatActivity {

    //Activité principale
    //Cette activité gére les changements de fragment au niveau de la bare de menu

    BottomNavigationView bottomNavigationView;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_ng);

        
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        //Creation d'un fragment

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();



        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        permissionDialog();


        //En fonction de l'item de la barre de menu selectionné, on change de fragment
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_home:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();


                        break;
                    case R.id.nav_account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                        break;
                    case R.id.nav_favorites:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FavoritesFragment()).commit();

                        break;
                    case R.id.nav_message:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MessageFragment()).commit();

                        break;
                    case R.id.nav_add:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddFragment()).commit();

                        break;

                }


                return false;
            }

        });


    }
    public void redirection (View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InscriptionFragment()).commit();
    }
    
    /**
     * Demander la permission afin d'accéder aux fichiers du téléphone
     */
    public void permissionDialog() {
        //nom du fichier qu'on veut prendre dans le dossier téléchargement/download

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Bottom_Ng_Activity.this, Manifest.permission.INTERNET)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission d'accès aux fichiers requise");
                builder.setMessage("Cette permission servira à télécharger le fichier fileJSONtest.json !");
                builder.setPositiveButton("D'accord", (dialog, which) -> {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(Bottom_Ng_Activity.this, new String[]{Manifest.permission.INTERNET}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                });
                builder.setNegativeButton("Non merci", (dialog, which) -> dialog.cancel());
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.INTERNET, false)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission d'accès aux fichiers requise");
                builder.setMessage("Cette permission servira à télécharger le fichier fileJSONtest.json !");
                builder.setPositiveButton("D'accord", (dialog, which) -> {
                    dialog.cancel();
                    sentToSettings = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                });
                builder.setNegativeButton("Non merci", (dialog, which) -> dialog.cancel());
                builder.show();

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.INTERNET, true);
            editor.apply();
        }
}
