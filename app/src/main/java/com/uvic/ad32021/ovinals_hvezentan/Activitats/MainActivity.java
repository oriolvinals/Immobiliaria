package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;
import com.uvic.ad32021.ovinals_hvezentan.Adaptadors.Adapter_Propietat;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import java.io.IOException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    ArrayList<Propietat> list_propietats;
    ListView list;
    FirebaseAuth fAuth;

    private static final int RESULT_CAMERA_IMAGE = 100;
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private ZXingScannerView mScannerView;
    private boolean cameraOn;
    private ConstraintLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.contentFrame = findViewById(R.id.layoutMain);
        this.mScannerView = new ZXingScannerView(this);
        this.cameraOn = false;

        this.fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null) {
            Singleton.getInstance().setUserId(fAuth.getCurrentUser().getUid());
            FloatingActionButton bList = findViewById(R.id.listProps);
            FloatingActionButton bAdd = findViewById(R.id.addProp);
            bList.setVisibility(View.VISIBLE);
            bAdd.setVisibility(View.VISIBLE);
        }

        this.list = (ListView)findViewById(R.id.listPropietats);
        this.list_propietats = Singleton.getInstance().getPropietats();

        Adapter_Propietat adapter = new Adapter_Propietat (this, R.layout.adapter_propietat, this.list_propietats);
        this.list.setAdapter(adapter);

        TextView noProps = findViewById(R.id.noPropsMain);
        if(this.list_propietats.size() == 0){
            noProps.setText(R.string.empty_props);
        }

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
        myToolbar.setTitle(R.string.property_list);
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
            Intent i = new Intent(MainActivity.this, UserInfo.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void captureQR(View view){
        Log.i("AD_C11", "captureQR");
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                MainActivity.this.startCamera();
            } else {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                this.camera();
        }
    }

    public void startCamera() {
        Log.i("Test", "startCamera");
        contentFrame.addView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        cameraOn = true;
    }
    public void stopCamera() {
        Log.i("Test", "stopCamera");
        mScannerView.stopCamera();
        contentFrame.removeView(mScannerView);
        cameraOn = false;
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.i("Test", "handleResult");
        if (rawResult != null) {
            Intent i = new Intent(MainActivity.this, InfoPropietat.class);
            i.putExtra("id", rawResult.getText().toString());
            startActivity(i);
        }
        stopCamera();
    }

    @Override
    public void onBackPressed() {
        Log.i("Test", "onBackPressed");
        if (cameraOn) {
            stopCamera();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        Log.i("Test", "onPause");
        if (cameraOn) {
            stopCamera();
        }
        super.onPause();
    }

    public void camera(){
        Log.i("Test", "Camera function");
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, RESULT_CAMERA_IMAGE);
            }
        } else  {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

}