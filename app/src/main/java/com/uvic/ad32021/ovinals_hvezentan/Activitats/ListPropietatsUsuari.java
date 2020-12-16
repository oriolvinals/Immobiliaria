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
import android.widget.TextView;

import com.uvic.ad32021.ovinals_hvezentan.Adaptadors.Adapter_Propietat;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import java.util.ArrayList;

public class ListPropietatsUsuari extends AppCompatActivity {
    ArrayList<Propietat> list_propietats;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_propietats_usuari);

        this.list = (ListView)findViewById(R.id.listPref);

        Adapter_Propietat adapter = new Adapter_Propietat (this, R.layout.adapter_propietat, Singleton.getInstance().getPropietatsByUser());
        this.list.setAdapter(adapter);

        TextView noProps = findViewById(R.id.noProps);
        if(Singleton.getInstance().getPropietatsByUser().size() == 0){
            noProps.setText(R.string.empty_user_props);
        }

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Propietat item = (Propietat) parent.getItemAtPosition(position);
                Intent i = new Intent(ListPropietatsUsuari.this, EditPropietat.class);
                i.putExtra("id", item.getId());
                startActivity(i);
                finish();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.property_user_list);
        setSupportActionBar(myToolbar);
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
            Intent i = new Intent(ListPropietatsUsuari.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}