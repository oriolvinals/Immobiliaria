package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mPhone;
    Button mRegisterButton;
    TextView textLogin;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.register_name);
        setSupportActionBar(myToolbar);

        mFullName = findViewById(R.id.editName);
        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPassword);
        mPhone = findViewById(R.id.editPhone);
        mRegisterButton = findViewById(R.id.buttonReg);
        textLogin = findViewById(R.id.textLogin);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = Singleton.getInstance().getDB();

        if(fAuth.getCurrentUser() != null){
            Intent i =  new Intent(Register.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullname = mFullName.getText().toString();
                final String phone = mPhone.getText().toString().trim();

                View context = v;
                int errors = 0;
                final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

                if(!EMAIL_REGEX.matcher(email).matches()){
                    mEmail.setError(context.getResources().getString(R.string.register_email_sintaxi));
                    errors++;
                }

                if(TextUtils.isEmpty(fullname)){
                    mFullName.setError(context.getResources().getString(R.string.error_field_required));
                    errors++;
                }

                if(TextUtils.isEmpty(email)){
                    mEmail.setError(context.getResources().getString(R.string.error_field_required));
                    errors++;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError(context.getResources().getString(R.string.error_field_required));
                    errors++;
                }

                if(password.length() < 6){
                    mPassword.setError(context.getResources().getString(R.string.register_password_minimum));
                    errors++;
                }

                if(errors == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, R.string.register_user_created, Toast.LENGTH_SHORT).show();

                                user_id = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(user_id);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fName", fullname);
                                user.put("email", email);
                                user.put("phone", phone);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("Test", "User profile data send " + user_id);
                                    }
                                });

                                Intent i = new Intent(Register.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(Register.this, R.string.register_user_error, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
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
            Intent i = new Intent(Register.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void toLogin(View view){
        Intent i = new Intent(Register.this, Login.class);
        startActivity(i);
        finish();
    }
}