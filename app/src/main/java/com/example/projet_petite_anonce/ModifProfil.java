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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class ModifProfil extends Fragment {

    View view;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    MutableLiveData<Bitmap> bitmap ;
    Uri imageUri;
    FirebaseUser user;
    String pswOld;
    String userUID;
    EditText editMail;


    public ModifProfil() {
        // Required empty public constructor
    }


    public static ModifProfil newInstance() {
        ModifProfil fragment = new ModifProfil();
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
        View view = inflater.inflate(R.layout.fragment_modif_profil, container, false);
        bitmap = new MutableLiveData<>();
        editMail = view.findViewById(R.id.editTextMail);

        ImageButton imageButton = view.findViewById(R.id.btn_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFunction.selectImage(someActivityResultLauncher);
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
            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("ClientParticulier").child(user.getUid());
            DatabaseReference pseudo = userInformation.child("pseudo");

            TextView pseudo_textView = view.findViewById(R.id.pseudoProfil);
            TextView type_profil = view.findViewById(R.id.typeProfil);
            //pseudo
            pseudo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Object pseudoTest = snapshot.getValue();
                    if( pseudoTest != null){
                        pseudo_textView.setText((String)pseudoTest);
                        type_profil.setText(R.string.particulier);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });



            //mail hint
            DatabaseReference mail = userInformation.child("mail");
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


            //tel hint
            DatabaseReference tel = userInformation.child("tel");
            tel.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EditText editTextTel = view.findViewById(R.id.editTextNum);
                    Object telTest = snapshot.getValue();
                    if(telTest != null){
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfilFragment()).commit();
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

                        DatabaseReference particulier = FirebaseDatabase.getInstance().getReference("ClientParticulier");
                        particulier.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //check if he created adverts
                                if (snapshot.hasChild(userUID))
                                    deleteInformations(particulier, snapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        DatabaseReference professionnel = FirebaseDatabase.getInstance().getReference("ClientProfessionnel");
                        professionnel.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //check if he created adverts
                                if (snapshot.hasChild(userUID))
                                    deleteInformations(professionnel, snapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                    }
                });



            }
        });

        return view ;
    }

    public void deleteInformations(DatabaseReference userInformation, DataSnapshot snapshot){
        // get adverts List
        List<String> keyAdverts = new ArrayList<>();
        if(snapshot.child(userUID).hasChild("MyAdvert")){
            DatabaseReference advertsRef = userInformation.child(userUID).child("MyAdvert");
            advertsRef.orderByKey().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                    //get all key advert

                    //Get all his adverts key in Firebase
                    Consumer getAdvertKey = new Consumer<DataSnapshot>(){
                        public void accept(DataSnapshot snapshot3){

                            String aBuffer = snapshot3.getValue().toString();
                            keyAdverts.add(aBuffer);
                        };
                    };

                    snapshot2.getChildren().forEach(getAdvertKey);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}

            });
        }

        //delete profil picture
        StorageReference profilPicRef = FirebaseStorage.getInstance().getReference(userUID+"/profil/profil");
        profilPicRef.delete();

        //delete adverts pictures
        for(String advertUID : keyAdverts){
            StorageReference profilAdvertRef = FirebaseStorage.getInstance().getReference("annonce/"+advertUID);
            profilAdvertRef.delete();
        }

        //delete his adverts in MyFavAdverts of others users (ClientProfessionnel and ClientParticulier)
        DatabaseReference allUsers = FirebaseDatabase.getInstance().getReference("ClientProfessionnel");
        allUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotUsers) {
                deleteFav(snapshotUsers, keyAdverts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        allUsers = FirebaseDatabase.getInstance().getReference("ClientParticulier");
        allUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotUsers) {
                deleteFav(snapshotUsers, keyAdverts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //delete adverts from Annonce
        DatabaseReference annonces = FirebaseDatabase.getInstance().getReference("Annonce");
        for(String key : keyAdverts){
            annonces.child(key).removeValue();
        }

        //delete user information
        userInformation.child(userUID).removeValue();
    }

    /**
     * snapshotUsers : all users
     * @param snapshotUsers
     */
    public void deleteFav(DataSnapshot snapshotUsers, List<String> keyAdverts){
        List<DatabaseReference> refToDelete = new ArrayList<>();

        Consumer deleteFavAdverts = new Consumer<DataSnapshot>(){
            public void accept(DataSnapshot snapshot2){
                //get the myFavAdverts from the user
                if(snapshot2.hasChild("MyFavAdvert")){

                    //List<String> favAdverts = (List<String>) snapshot2.child("MyFavAdverts").getValue();
                    Iterable<DataSnapshot> listFav = snapshot2.child("MyFavAdvert").getChildren();

                    for (DataSnapshot postSnapshot: listFav) {
                        if(keyAdverts.contains(postSnapshot.getValue().toString()))
                            refToDelete.add(postSnapshot.getRef());
                    }
                }
            }
        };

        snapshotUsers.getChildren().forEach(deleteFavAdverts);

        for (DatabaseReference toDelete : refToDelete){
            toDelete.removeValue();
        }
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
