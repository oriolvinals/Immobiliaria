package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;

public class UserInfo extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String id;
    TextView nom, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.user_info);
        setSupportActionBar(myToolbar);

        this.fAuth = FirebaseAuth.getInstance();

        TextView email = (TextView)findViewById(R.id.textEmail);
        nom = (TextView)findViewById(R.id.editName);
        phone = (TextView)findViewById(R.id.editPhone);

        email.setText("Email: " + fAuth.getCurrentUser().getEmail());
        id = fAuth.getUid();

        this.db = Singleton.getInstance().getDB();
        DocumentReference docRef = this.db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            JSONObject json = new JSONObject(document.getData());
                            String nomU = json.getString("fName");
                            String phoneU = json.getString("phone");
                            nom.setText(nomU);
                            phone.setText(phoneU);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent i = new Intent(UserInfo.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Intent i = new Intent(UserInfo.this, MainActivity.class);
                    startActivity(i);
                    finish();
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
            Intent i = new Intent(UserInfo.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void logOut(View view){
        fAuth.getInstance().signOut();
        Singleton.getInstance().setUserId("");
        Intent i = new Intent(UserInfo.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void saveUser(View view){
        View context = view;
        int errors = 0;

        final String fullname = nom.getText().toString();

        if(TextUtils.isEmpty(fullname)){
            nom.setError(context.getResources().getString(R.string.error_field_required));
            errors++;
        }
        if(errors == 0) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.VISIBLE);
            DocumentReference edit = db.collection("users").document(id);
            edit.update("fName", nom.getText().toString(), "phone", phone.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent i = new Intent(UserInfo.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserInfo.this, R.string.register_user_error, Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}