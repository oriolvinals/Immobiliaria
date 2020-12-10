
package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

public class EditPropietat extends AppCompatActivity {
    FirebaseFirestore db;
    String id;
    Propietat p;
    TextView t_nom;

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_save){
            DocumentReference edit = db.collection("propietats").document(this.id);
            edit.update("nom", t_nom.getText().toString())
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

            return true;
        } else if(id == R.id.action_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(EditPropietat.this);
            builder.setMessage("Segur que vols eliminar-la?");
            builder.setTitle("Eliminar");
            final FirebaseFirestore _db = this.db;
            final String _id = this.id;
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    _db.collection("propietats").document(_id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Log.d("Test", "DocumentSnapshot successfully deleted!");
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
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });



            AlertDialog dialeg = builder.create();
            dialeg.show();

            return true;
        } else if(id == R.id.action_back){
            Intent i = new Intent(EditPropietat.this, ListPropietatsUsuari.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}