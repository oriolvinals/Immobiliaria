package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

public class EditPropietat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_propietat);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Editar la propietat");
        setSupportActionBar(myToolbar);

        Singleton.getInstance().getPropietatById("T4F4nNhXTAtqGawq3HgJ");
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
            //Editar dades
            return true;
        } else if(id == R.id.action_delete){
            //Borrar dades
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}