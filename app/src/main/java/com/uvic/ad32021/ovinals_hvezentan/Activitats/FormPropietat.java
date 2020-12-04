package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.w3c.dom.Text;

public class FormPropietat extends AppCompatActivity {
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_propietat);

        this.user_id = Singleton.getInstance().getUserId();

        if(user_id == -1){
            Intent i = new Intent(FormPropietat.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void addPropietat(View view){
        TextView nom = (TextView)findViewById(R.id.formNom);
        Propietat p = new Propietat(3, nom.getText().toString(), "Carrer A, Vic 21009", "Descripció", "Equipaments", "path imatge", 1, 99, 99999.99);
        Singleton.getInstance().addPropietat(p);
        Intent i = new Intent(FormPropietat.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}