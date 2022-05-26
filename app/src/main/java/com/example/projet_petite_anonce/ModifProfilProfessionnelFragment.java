package com.example.projet_petite_anonce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ModifProfilProfessionnelFragment extends Fragment {

    View view;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    MutableLiveData<Bitmap> bitmap ;
    Uri imageUri;
    FirebaseUser user;
    String pswOld;
    String userUID;
    EditText editMail;



    public static ModifProfilProfessionnelFragment newInstance() {
        ModifProfilProfessionnelFragment fragment = new ModifProfilProfessionnelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modif_profil_professionnel, container, false);
        bitmap = new MutableLiveData<>();
        editMail = view.findViewById(R.id.editTextMail);

        ImageButton imageButton = view.findViewById(R.id.btn_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                bitmap.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
                    @Override
                    public void onChanged(Bitmap newBitmap) {
                        ImageButton imageButton = view.findViewById(R.id.btn_image);
                        imageButton.setImageBitmap(newBitmap);
                    }
                });
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user != null){

            //get and display client information

            DatabaseReference userInformationPro = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(user.getUid());
            DatabaseReference nom_societe = userInformationPro.child("nom_societe");



            TextView pseudo_textView = view.findViewById(R.id.pseudoProfil);
            TextView type_profil = view.findViewById(R.id.typeProfil);
            //pseudo


            nom_societe.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    Object nomSocieteTest = snapshot.getValue();
                    if( nomSocieteTest != null){
                        pseudo_textView.setText((String)nomSocieteTest);
                        type_profil.setText(R.string.professionel);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //mail hint
            DatabaseReference mail = userInformationPro.child("mail");
            mail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText editTextMail = view.findViewById(R.id.editTextMail);
                    Object mailTest = snapshot.getValue();
                    if( mailTest != null)
                        editTextMail.setHint((String)mailTest);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            //numero de siret hint
            DatabaseReference siret = userInformationPro.child("numero_siret");
            siret.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText editTextMail = view.findViewById(R.id.editTextSiret);
                    Object siretTest = snapshot.getValue();
                    if( siretTest != null)
                        editTextMail.setHint((String)siretTest);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });


            //tel hint
            DatabaseReference tel = userInformationPro.child("tel");
            tel.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText editTextTel = view.findViewById(R.id.editTextNum);
                    Object telTest = snapshot.getValue();
                    if( telTest != null){
                        editTextTel.setHint(String.valueOf(telTest));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            //profil image
            String imageString = user.getUid();//get user id

            //each user have only one profil id, if there isn't a profil id we display buffer image
            storageReference = FirebaseStorage.getInstance().getReference(imageString+"/profil/profil");

            try {
                File localeFile = File.createTempFile("tempfile", ".jpeg");
                storageReference.getFile(localeFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmapImage = BitmapFactory.decodeFile(localeFile.getAbsolutePath());
                        imageButton.setImageBitmap(bitmapImage);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageButton btn_revert = view.findViewById(R.id.btn_revert);
        btn_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //transfered to profil account
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfilProfessionelFragment()).commit();
            }
        });

        Button change = view.findViewById(R.id.btn_change);
        EditText editTextTel = view.findViewById(R.id.editTextNum);

        EditText editTextMail = view.findViewById(R.id.editTextMail);
        EditText editTextPsw = view.findViewById(R.id.editTextPassword);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verify if there is changement

                //mail and/or password
                String newMail = editTextMail.getText().toString();
                String newPsw = editTextPsw.getText().toString();
                if(!newPsw.isEmpty() || !newMail.isEmpty()){

                    //Get actual password
                    DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(user.getUid());
                    DatabaseReference pswd = userInformation.child("password");
                    pswd.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Object pswTest = snapshot.getValue();
                            pswOld = null;

                            if( pswTest != null){
                                pswOld = pswTest.toString();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                    if(pswOld != null){
                        // Get auth credentials from the user for re-authentication
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pswOld); // Current Login Credentials \\
                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!newMail.isEmpty()){
                                            //To change mail
                                            user.updateEmail(newMail)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                DatabaseReference myRef = database.getReference("ClientParticulier").child(user.getUid()).child("mail");
                                                                myRef.setValue(newMail);
                                                            }
                                                        }
                                                    });
                                        }

                                        if(!newPsw.isEmpty()){
                                            //To change password
                                            user.updatePassword(newPsw)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                DatabaseReference myRef = database.getReference("ClientParticulier").child(user.getUid()).child("password");
                                                                myRef.setValue(newPsw);
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                    }
                    //else
                    //Toast.makeText(, "", Toast.LENGTH_SHORT).show();

                }



                //tel
                String tel = editTextTel.getText().toString();
                if(!tel.isEmpty()){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("ClientParticulier").child(user.getUid()).child("tel");
                    myRef.setValue(tel);
                }

                //image
                if(bitmap.getValue() != null)
                    saveBitmapFirebase(view);
            }
        });

        Button supp = view.findViewById(R.id.btn_supp);
        userUID = user.getUid();

        supp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //delete ClientParticulier
                        DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(userUID);
                        userInformation.removeValue();

                        userInformation = FirebaseDatabase.getInstance().getReference("ClientProfessionnel").child(userUID);
                        userInformation.removeValue();

                        //il faut supprimer ses annonces (dans annonce et image annonce)
                        //il faut supprimer ses annonces des listes MyFavAdvert des autres utilisateurs
                        /********************************************************************************************/

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                    }
                });



            }
        });

        return view ;
    }

    public void saveBitmapFirebase(View view){
        //profil image
        String imageString = user.getUid();//get user id

        //each user have only one profil id, if there isn't a profil id we display buffer image
        storageReference = FirebaseStorage.getInstance().getReference(imageString+"/profil/profil");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(), "Veuillez réessayer l'image n'a pas été enregistré dans la base de données.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!= null) {

                        imageUri = result.getData().getData();
                        Bitmap srcBmp = null;
                        try {
                            srcBmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        } catch (FileNotFoundException e) {
                            Log.e("GET_IMAGE", e.getMessage(), e);
                        } catch (IOException e) {
                            Log.e("GET_IMAGE", e.getMessage(), e);
                        }

                        bitmap.setValue(srcBmp);
                    }
                }
            });
}
