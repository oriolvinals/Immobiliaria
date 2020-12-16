package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import static android.Manifest.permission.CALL_PHONE;


public class InfoPropietat extends AppCompatActivity {
    String id;
    FirebaseFirestore db;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_propietat);

        this.fAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.id = extras.getString("id");
        } else {
            Intent i = new Intent(InfoPropietat.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.detail_property);
        setSupportActionBar(myToolbar);

        this.db = Singleton.getInstance().getDB();

        DocumentReference docRef = this.db.collection("propietats").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            private FirebaseFirestore db;

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            JSONObject json = new JSONObject(document.getData());
                            String nom = json.getString("nom");
                            String ubicacio = json.getString("ubicacio");
                            String descripcio = json.getString("descripcio");
                            String equipaments = json.getString("equipaments");
                            String imatge = json.getString("imatge");
                            String user = json.getString("user_id");
                            int area = json.getInt("area");
                            double preu = json.getDouble("preu");

                            TextView t_nom = (TextView)findViewById(R.id.nomProp);
                            t_nom.setText(nom);

                            TextView t_ubicacio = (TextView)findViewById(R.id.ubiProp);
                            t_ubicacio.setText(ubicacio);

                            TextView t_descripcio = (TextView)findViewById(R.id.descProp);
                            t_descripcio.setText(descripcio);

                            TextView t_equipaments = (TextView)findViewById(R.id.equipProp);
                            t_equipaments.setText(equipaments);

                            ImageView img = (ImageView)findViewById(R.id.imageView3);
                            try{
                                byte [] encodeByte= Base64.decode(imatge,Base64.DEFAULT);
                                InputStream inputStream  = new ByteArrayInputStream(encodeByte);
                                Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
                                img.setImageBitmap(bitmap);
                            }catch(Exception e){
                                e.getMessage();
                            }


                            TextView t_area = (TextView)findViewById(R.id.areaProp);
                            t_area.setText(String.valueOf(area) +" m²");

                            TextView t_preu = (TextView)findViewById(R.id.preuProp);
                            t_preu.setText(String.valueOf(preu) +" €");

                            TextView uuid = (TextView)findViewById(R.id.uuid);
                            uuid.setText(user);

                            FirebaseFirestore db = Singleton.getInstance().getDB();
                            DocumentReference docRef2 = db.collection("users").document(user);
                            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            try {
                                                JSONObject json = new JSONObject(document.getData());
                                                String nom = json.getString("fName");
                                                String email = json.getString("email");
                                                String phone = json.getString("phone");

                                                TextView t_nomp = (TextView)findViewById(R.id.nompProp);
                                                t_nomp.setText(nom);

                                                TextView t_email = (TextView)findViewById(R.id.emailProp);
                                                t_email.setText(email);

                                                TextView t_phone = (TextView)findViewById(R.id.phoneProp);
                                                t_phone.setText(phone);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.d("Test", "No such document");
                                        }
                                    } else {
                                        Log.d("Test", "get failed with ", task.getException());
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent i = new Intent(InfoPropietat.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Log.d("Test", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_back){
            Intent i = new Intent(InfoPropietat.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onCall(View view){
        TextView t_phone = (TextView)findViewById(R.id.phoneProp);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + t_phone.getText().toString()));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }

    public void onMail(View view){
        TextView t_email = (TextView)findViewById(R.id.emailProp);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", t_email.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(InfoPropietat.this, R.string.copy, Toast.LENGTH_SHORT).show();
    }
}