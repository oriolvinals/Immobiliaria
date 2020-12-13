package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

public class UserInfo extends AppCompatActivity {
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.user_info);
        setSupportActionBar(myToolbar);

        this.fAuth = FirebaseAuth.getInstance();

        TextView email = (TextView)findViewById(R.id.textEmail);
        TextView nom = (TextView)findViewById(R.id.editName);
        TextView phone = (TextView)findViewById(R.id.editPhone);

        email.setText("Email: " + fAuth.getCurrentUser().getEmail());

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
        Intent i = new Intent(UserInfo.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}