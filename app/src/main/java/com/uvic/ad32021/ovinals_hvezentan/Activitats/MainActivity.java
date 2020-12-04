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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.uvic.ad32021.ovinals_hvezentan.Adaptadors.Adapter_Propietat;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Propietat> list_propietats;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.list = (ListView)findViewById(R.id.listPropietats);
        this.list_propietats = Singleton.getInstance().getPropietats();

        Adapter_Propietat adapter = new Adapter_Propietat (this, R.layout.adapter_propietat, this.list_propietats);
        this.list.setAdapter(adapter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Llista de propietats");
        setSupportActionBar(myToolbar);
    }

    public void goForm(View view){
        Intent i = new Intent(MainActivity.this, FormPropietat.class);
        startActivity(i);
        finish();
    }

    public void goUserPropietats(View view){
        Intent i = new Intent(MainActivity.this, ListPropietatsUsuari.class);
        startActivity(i);
        finish();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/
}