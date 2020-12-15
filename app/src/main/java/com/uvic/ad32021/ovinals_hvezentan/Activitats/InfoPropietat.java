package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

import java.util.ArrayList;

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

                            TextView t_imatge = (TextView)findViewById(R.id.imgProp);
                            t_imatge.setText(imatge);

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
                        Log.d("Test", "No such document");
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
}