
package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class EditPropietat extends AppCompatActivity {
    FirebaseFirestore db;
    String id;
    Propietat p;
    TextView t_nom, t_ubi, t_desc, t_area, t_preu, t_equip;
    private String savePath = Environment.getExternalStorageDirectory().getPath() +"/0" ;
    private static final int RESULT_LOAD_GALERY_IMAGE = 200;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;
    private ImageView img;
    Bitmap bitImg;
    boolean image_change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_propietat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.id = extras.getString("id");
        } else {
            Intent i = new Intent(EditPropietat.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        ActivityCompat.requestPermissions(EditPropietat.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(EditPropietat.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.edit_propietat);
        setSupportActionBar(myToolbar);

        this.t_nom = (TextView)findViewById(R.id.editName);
        this.t_ubi = (TextView)findViewById(R.id.editUbicacio);
        this.t_preu = (TextView)findViewById(R.id.editPreu);
        this.t_area = (TextView)findViewById(R.id.editArea);
        this.t_desc = (TextView)findViewById(R.id.editDesc);
        this.t_equip = (TextView)findViewById(R.id.editEquip);
        this.img = (ImageView)findViewById(R.id.imageView4);

        this.db = Singleton.getInstance().getDB();
        DocumentReference docRef = this.db.collection("propietats").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            JSONObject json = new JSONObject(document.getData());
                            String nom = json.getString("nom");
                            String ubicacio = json.getString("ubicacio");
                            String descripcio = json.getString("descripcio");
                            String equipaments = json.getString("equipaments");
                            String imatge = json.getString("imatge");
                            String user = json.getString("user_id");
                            int area = json.getInt("area");
                            double preu = json.getDouble("preu");

                            TextView t_nom = (TextView)findViewById(R.id.editName);
                            t_nom.setText(nom);

                            TextView t_ubi = (TextView)findViewById(R.id.editUbicacio);
                            t_ubi.setText(ubicacio);

                            TextView t_preu = (TextView)findViewById(R.id.editPreu);
                            t_preu.setText(new Double(preu).toString());

                            TextView t_area = (TextView)findViewById(R.id.editArea);
                            t_area.setText(new Integer(area).toString());

                            TextView t_desc = (TextView)findViewById(R.id.editDesc);
                            t_desc.setText(descripcio);

                            TextView t_equip = (TextView)findViewById(R.id.editEquip);
                            t_equip.setText(equipaments);

                            ImageView img = (ImageView)findViewById(R.id.imageView4);
                            try{
                                byte [] encodeByte= Base64.decode(imatge, Base64.DEFAULT);
                                InputStream inputStream  = new ByteArrayInputStream(encodeByte);
                                Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
                                img.setImageBitmap(bitmap);
                            }catch(Exception e){
                                e.getMessage();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("Test", "No such document");
                    }
                } else {
                    Log.d("Test", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    public void saveProp(View view){
        String bitmapS;
        if(image_change){
             bitmapS = BitMapToString(bitImg);
        } else {
            ImageView img = (ImageView)findViewById(R.id.imageView4);
            Bitmap bm = ((BitmapDrawable)img.getDrawable()).getBitmap();
            bitmapS = BitMapToString(bm);
        }
        DocumentReference edit = db.collection("propietats").document(this.id);
        edit.update("nom", t_nom.getText().toString(), "descripcio", t_desc.getText().toString(), "ubicacio", t_ubi.getText().toString(),
                "area", Integer.parseInt(t_area.getText().toString()), "preu", Double.parseDouble(t_preu.getText().toString()), "equipaments", t_equip.getText().toString(), "imatge", bitmapS)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Test", "DocumentSnapshot successfully updated!");
                        Singleton.getInstance().syncData();
                        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
                        pb.setVisibility(View.VISIBLE);
                        final long DELAY = 1000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(EditPropietat.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, DELAY);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Test", "Error updating document", e);
                    }
                });
    }

    public void deleteProp(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPropietat.this);
        builder.setMessage(R.string.dialog_message_edit);
        builder.setTitle(R.string.dialog_delete_title);
        final FirebaseFirestore _db = this.db;
        final String _id = this.id;
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                _db.collection("propietats").document(_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Singleton.getInstance().syncData();
                                final long DELAY = 500;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(EditPropietat.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }, DELAY);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Test", "Error deleting document", e);
                            }
                        });
            }
        });
        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialeg = builder.create();
        dialeg.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            Intent i = new Intent(EditPropietat.this, ListPropietatsUsuari.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.action_qr_generator) {
            String qr_id = this.id;
            QRGEncoder qrgEncoder = new QRGEncoder(qr_id, null, QRGContents.Type.TEXT, 100);
            qrgEncoder.setColorBlack(Color.RED);
            qrgEncoder.setColorWhite(Color.BLUE);

            Bitmap bitmap = qrgEncoder.getBitmap();

            FileOutputStream outputStream = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath());
            dir.mkdirs();

            String filename = String.format("%d.png",System.currentTimeMillis());
            File outFile = new File(dir,filename);
            try{
                outputStream = new FileOutputStream(outFile);
            }catch (Exception e){
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            try{
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                outputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            /*QRGSaver qrgSaver = new QRGSaver();

            int check = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (check == PackageManager.PERMISSION_GRANTED) {
                qrgSaver.save(savePath, qr_id.trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
            }*/
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onGallery(View view){
        this.accessGallery();
    }

    public void accessGallery(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_GALERY_IMAGE);
            } else {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RESULT_LOAD_GALERY_IMAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                this.accessGallery();
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_LOAD_GALERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap photo = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    img.setImageBitmap(photo);
                    img.setDrawingCacheEnabled(true);
                    img.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    this.bitImg = bitmap;
                    image_change = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("Test", "Galeria cancelada");
            } else {
                Log.i("Test", "Ha fallat");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}