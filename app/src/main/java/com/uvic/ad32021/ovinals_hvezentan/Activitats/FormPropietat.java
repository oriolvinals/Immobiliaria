package com.uvic.ad32021.ovinals_hvezentan.Activitats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;
import com.uvic.ad32021.ovinals_hvezentan.Singletons.Singleton;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class FormPropietat extends AppCompatActivity {
    int user_id;
    FirebaseStorage storage;

    private ImageView img;
    private byte[] imgData;
    private static final int RESULT_LOAD_GALERY_IMAGE = 200;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;
    private byte[] data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_propietat);

        //No user
        if(Singleton.getInstance().getUserId() == ""){
            Intent i = new Intent(FormPropietat.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        this.storage = Singleton.getInstance().getStorage();
        this.img = (ImageView)findViewById(R.id.imageView);
    }

    public void addPropietat(View view){
        String imageName = randomNameImage() + ".jpg";

        //Form
        TextView nom = (TextView)findViewById(R.id.formNom);

        //Image
        StorageReference storageRef = this.storage.getReference();
        StorageReference imageRef = storageRef.child(imageName);
        UploadTask uploadTask = imageRef.putBytes(this.data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        //Add
        Propietat p = new Propietat("", nom.getText().toString(), "Carrer A, Vic 21009", "Descripció", "Equipaments", imageName, "AAD", 99, 99999.99);
        Singleton.getInstance().addPropietat(p);

        //Go to list
        Intent i = new Intent(FormPropietat.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onGallery(View view){
        Log.i("Test", "Gallery Click");
        this.accessGallery();
    }

    public void accessGallery(){
        Log.i("Test","captureImage");
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_GALERY_IMAGE);
            }
        } else  {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
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
                    this.data = baos.toByteArray();
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
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RESULT_LOAD_GALERY_IMAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                this.accessGallery();
        }
    }

    public String randomNameImage(){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 20;
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}