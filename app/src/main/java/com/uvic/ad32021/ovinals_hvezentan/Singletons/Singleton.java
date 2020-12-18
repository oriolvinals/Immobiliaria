package com.uvic.ad32021.ovinals_hvezentan.Singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Singleton {
    ArrayList<Propietat> list_propietats;
    Propietat actual_propietat;

    String user_id;
    FirebaseFirestore db;
    FirebaseStorage storage;

    private static class SingletonInstance {
        private static Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private Singleton() {
        this.list_propietats = new ArrayList<Propietat>();

        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

    
        this.storage = FirebaseStorage.getInstance("gs://immobiliaria-e7861.appspot.com/");
        this.syncData();
    }

    public ArrayList<Propietat> getPropietats(){
        return this.list_propietats;
    }

    public ArrayList<Propietat> getPropietatsByUser(){
        ArrayList<Propietat> propietats_user = new ArrayList<Propietat>();
        for (Propietat p: this.list_propietats) {
            if(p.getUser_id().equals(this.user_id)){
                propietats_user.add(p);
            }
        }
        return propietats_user;
    }

    public String getUserId(){
        return this.user_id;
    }

    public void setUserId(String id){
        this.user_id = id;
    }

    public FirebaseStorage getStorage(){
        return this.storage;
    }

    //Mostrar totes les propietats
    public void syncData(){
        this.list_propietats = new ArrayList<Propietat>();
        Log.i("Test", "Start Sync");
        this.db.collection("propietats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.i("Test", document.getId() + " => " + document.getData());
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
                                    Propietat p = new Propietat(document.getId().toString(), nom, ubicacio, descripcio, equipaments, imatge, user, area, preu);
                                    Singleton.getInstance().addPropietatToList(p);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } Log.i("Test", "Data sync");
                        } else {
                            Log.i("Test", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void addPropietatToList(Propietat p) {
        this.list_propietats.add(p);
    }

    public FirebaseFirestore getDB(){
        return this.db;
    }
}