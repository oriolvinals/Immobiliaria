package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.uvic.ad32021.ovinals_hvezentan.Adaptadors.Adapter_Propietat;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Propietat> list_propietats;
    ListView list;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null) {
            Singleton.getInstance().setUserId(fAuth.getCurrentUser().getEmail());
            FloatingActionButton bList = findViewById(R.id.listProps);
            FloatingActionButton bAdd = findViewById(R.id.addProp);
            bList.setVisibility(View.VISIBLE);
            bAdd.setVisibility(View.VISIBLE);
        }

        this.list = (ListView)findViewById(R.id.listPropietats);
        this.list_propietats = Singleton.getInstance().getPropietats();

        Adapter_Propietat adapter = new Adapter_Propietat (this, R.layout.adapter_propietat, this.list_propietats);
        this.list.setAdapter(adapter);

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Propietat item = (Propietat) parent.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, InfoPropietat.class);
                i.putExtra("id", item.getId());
                startActivity(i);
                finish();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Llista de propietats");
        setSupportActionBar(myToolbar);
    }

    public void goForm(View view){
        Intent i = new Intent(MainActivity.this, FormPropietat.class);
        startActivity(i);
        finish();
    }

    public void goList(View view){
        Intent i = new Intent(MainActivity.this, ListPropietatsUsuari.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        if(fAuth.getCurrentUser() != null){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_login){
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
            return true;
        } else if(id == R.id.action_register){
            Intent i = new Intent(MainActivity.this, Register.class);
            startActivity(i);
            finish();
            return true;
        } else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            Singleton.getInstance().setUserId("");
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}