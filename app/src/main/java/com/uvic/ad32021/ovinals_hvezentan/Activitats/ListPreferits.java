package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.uvic.ad32021.ovinals_hvezentan.Adaptadors.Adapter_Propietat;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import java.util.ArrayList;

public class ListPreferits extends AppCompatActivity {
    ArrayList<Propietat> list_propietats;
    ListView list;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_preferits);

        //No user
        if(Singleton.getInstance().getUserId() == ""){
            Intent i = new Intent(ListPreferits.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        this.list = (ListView)findViewById(R.id.listPref);
        this.list_propietats = Singleton.getInstance().getPropietatsByUser();

        Adapter_Propietat adapter = new Adapter_Propietat (this, R.layout.adapter_propietat, this.list_propietats);
        this.list.setAdapter(adapter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Llista preferits");
        setSupportActionBar(myToolbar);
    }
}