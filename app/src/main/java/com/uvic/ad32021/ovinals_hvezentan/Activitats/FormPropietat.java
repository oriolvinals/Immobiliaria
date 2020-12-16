package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class FormPropietat extends AppCompatActivity {
    FirebaseStorage storage;
    FirebaseFirestore db;
    private ImageView img;
    private byte[] imgData;
    private static final int RESULT_LOAD_GALERY_IMAGE = 200;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;
    private byte[] data;
    boolean photoUp = false;
    Bitmap bitImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_propietat);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.form);
        setSupportActionBar(myToolbar);

        this.storage = Singleton.getInstance().getStorage();
        this.db = Singleton.getInstance().getDB();
        this.img = (ImageView)findViewById(R.id.imageView);
    }

    public void addPropietat(View view) {
        String user_id = Singleton.getInstance().getUserId();
        String imageName = randomNameImage() + ".jpg";

        //Form
        TextView nom = (TextView) findViewById(R.id.formNom);
        TextView ubi = (TextView) findViewById(R.id.formUbi);
        TextView des = (TextView) findViewById(R.id.formDesc);
        TextView equip = (TextView) findViewById(R.id.formEquip);
        TextView area = (TextView) findViewById(R.id.formArea);
        TextView preu = (TextView) findViewById(R.id.formPreu);

        int errors = 0;

        if(TextUtils.isEmpty(nom.getText().toString().trim())){
            nom.setError(view.getResources().getString(R.string.error_field_required));
            errors++;
        }

        if(TextUtils.isEmpty(ubi.getText().toString().trim())){
            ubi.setError(view.getResources().getString(R.string.error_field_required));
            errors++;
        }

        if(TextUtils.isEmpty(des.getText().toString().trim())){
            des.setError(view.getResources().getString(R.string.error_field_required));
            errors++;
        }

        if(TextUtils.isEmpty(equip.getText().toString().trim())){
            equip.setError(view.getResources().getString(R.string.error_field_required));
            errors++;
        }

        if(TextUtils.isEmpty(area.getText().toString().trim())){
            area.setError(view.getResources().getString(R.string.error_field_required));
            errors++;
        }

        if(TextUtils.isEmpty(preu.getText().toString().trim())){
            preu.setError(view.getResources().getString(R.string.error_field_required));
            errors++;
        }

        if(!photoUp){
            Toast.makeText(FormPropietat.this, R.string.form_photo, Toast.LENGTH_SHORT).show();
            errors++;
        }

        if(errors == 0){
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            //Image
            StorageReference storageRef = this.storage.getReference();
            StorageReference imageRef = storageRef.child(imageName);
            UploadTask uploadTask = imageRef.putBytes(this.data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });

            String bitmapS = BitMapToString(bitImg);
            //Add
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Propietat p = new Propietat("", nom.getText().toString(),
                    ubi.getText().toString(), des.getText().toString(),
                    equip.getText().toString(), bitmapS, user_id,
                    Integer.parseInt(area.getText().toString()), Double.parseDouble(preu.getText().toString()));
            this.addPropietatToFirebase(p);
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
                    this.data = baos.toByteArray();
                    photoUp = true;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RESULT_LOAD_GALERY_IMAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                this.accessGallery();
        }
    }

    public String randomNameImage(){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 20; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public void addPropietatToFirebase(final Propietat p){
        Map<String, Object> propietat = new HashMap<>();

        propietat.put("nom", p.getNom());
        propietat.put("ubicacio", p.getUbicacio());
        propietat.put("descripcio", p.getDescripcio());
        propietat.put("equipaments", p.getEquipaments());
        propietat.put("imatge", p.getImatge());
        propietat.put("user_id", p.getUser_id());
        propietat.put("area", p.getArea());
        propietat.put("preu", p.getPreu());

        // Add a new document with a generated ID
        this.db.collection("propietats")
                .add(propietat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(FormPropietat.this, R.string.form_success, Toast.LENGTH_SHORT).show();
                        p.setId(documentReference.getId());
                        Singleton.getInstance().addPropietatToList(p);
                        Intent i = new Intent(FormPropietat.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FormPropietat.this, R.string.form_error, Toast.LENGTH_SHORT).show();
                    }
                });
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
            Intent i = new Intent(FormPropietat.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}