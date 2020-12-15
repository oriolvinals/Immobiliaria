
package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class EditPropietat extends AppCompatActivity {
    FirebaseFirestore db;
    String id;
    Propietat p;
    TextView t_nom, t_ubi, t_desc, t_area, t_preu, t_equip;
    private String savePath = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_propietat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.id = extras.getString("id");
        } else {
            Intent i = new Intent(EditPropietat.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Editar la propietat");
        setSupportActionBar(myToolbar);

        this.t_nom = (TextView)findViewById(R.id.editName);
        this.t_ubi = (TextView)findViewById(R.id.editUbicacio);
        this.t_preu = (TextView)findViewById(R.id.editPreu);
        this.t_area = (TextView)findViewById(R.id.editArea);
        this.t_desc = (TextView)findViewById(R.id.editDesc);
        this.t_equip = (TextView)findViewById(R.id.editEquip);


        this.db = Singleton.getInstance().getDB();
        DocumentReference docRef = this.db.collection("propietats").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                            TextView t_nom = (TextView)findViewById(R.id.editName);
                            t_nom.setText(nom);

                            TextView t_ubi = (TextView)findViewById(R.id.editUbicacio);
                            t_ubi.setText(ubicacio);

                            TextView t_preu = (TextView)findViewById(R.id.editPreu);
                            t_preu.setText(new Double(preu).toString());

                            TextView t_area = (TextView)findViewById(R.id.editArea);
                            t_area.setText(new Integer(area).toString());

                            TextView t_desc = (TextView)findViewById(R.id.editDesc);
                            t_desc.setText(descripcio);

                            TextView t_equip = (TextView)findViewById(R.id.editEquip);
                            t_equip.setText(equipaments);

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
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    public void saveProp(View view){
        DocumentReference edit = db.collection("propietats").document(this.id);
        edit.update("nom", t_nom.getText().toString(), "descripcio", t_desc.getText().toString(), "ubicacio", t_ubi.getText().toString(),
                "area", Integer.parseInt(t_area.getText().toString()), "preu", Double.parseDouble(t_preu.getText().toString()), "equipaments", t_equip.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Test", "DocumentSnapshot successfully updated!");
                        Singleton.getInstance().syncData();
                        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
                        pb.setVisibility(View.VISIBLE);
                        final long DELAY = 1000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(EditPropietat.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, DELAY);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Test", "Error updating document", e);
                    }
                });
    }

    public void deleteProp(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPropietat.this);
        builder.setMessage(R.string.dialog_message_edit);
        builder.setTitle(R.string.dialog_delete_title);
        final FirebaseFirestore _db = this.db;
        final String _id = this.id;
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                _db.collection("propietats").document(_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Singleton.getInstance().syncData();
                                final long DELAY = 500;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(EditPropietat.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }, DELAY);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Test", "Error deleting document", e);
                            }
                        });
            }
        });
        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialeg = builder.create();
        dialeg.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_back){
            Intent i = new Intent(EditPropietat.this, ListPropietatsUsuari.class);
            startActivity(i);
            finish();
            return true;
        } else if(id == R.id.action_qr_generator){
            String qr_id = this.id;
            QRGEncoder qrgEncoder = new QRGEncoder(qr_id, null, QRGContents.Type.TEXT, 100);
            qrgEncoder.setColorBlack(Color.RED);
            qrgEncoder.setColorWhite(Color.BLUE);
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.getBitmap();
            QRGSaver qrgSaver = new QRGSaver();
            qrgSaver.save("/", qr_id, bitmap, QRGContents.ImageType.IMAGE_JPEG);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}