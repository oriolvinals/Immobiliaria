package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.uvic.ad32021.ovinals_hvezentan.R;

public class InfoPropietat extends AppCompatActivity {
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_propietat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.position = extras.getInt("position");
        } else {
            Intent i = new Intent(InfoPropietat.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Informació de la propietat");
        setSupportActionBar(myToolbar);

        TextView position = (TextView)findViewById(R.id.idProp);
        position.setText(String.valueOf(this.position));
    }
}