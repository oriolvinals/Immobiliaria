package com.uvic.ad32021.ovinals_hvezentan.Singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.Entitats.Usuari;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Singleton {
    ArrayList<Propietat> list_propietats;
    Usuari user;

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
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance("gs://immobiliaria-e7861.appspot.com/");

        this.user_id = "AD";
        this.list_propietats = new ArrayList<Propietat>();

        this.syncData();


        Propietat p1 = new Propietat("", "User 0", "Carrer A, Vic 21009", "Descripció", "Equipaments", "path imatge", "", 99, 99999.99);
        Propietat p2 = new Propietat("", "User 1", "Carrer A, Vic 21009", "Descripció", "Equipaments", "path imatge", "usuari1", 99, 99999.99);
        this.list_propietats.add(p1);
        this.list_propietats.add(p2);

    }

    public ArrayList<Propietat> getPropietats(){
        return this.list_propietats;
    }

    public ArrayList<Propietat> getPropietatsByUser(){
        ArrayList<Propietat> propietats_user = new ArrayList<Propietat>();
        if(this.user_id != ""){
            for (Propietat p: this.list_propietats) {
                if(p.getUser_id() == this.user_id){
                    propietats_user.add(p);
                }
            }
        }
        return propietats_user;
    }

    public void addPropietat(Propietat p){
        this.list_propietats.add(p);
        Map<String, Object> propietat = new HashMap<>();

        propietat.put("nom", p.getNom());
        propietat.put("ubicacio", p.getUbicacio());
        propietat.put("descripcio", p.getDescripcio());
        propietat.put("equipaments", p.getEquipaments());
        propietat.put("imatge", p.getImatge());
        propietat.put("user_id", this.user_id);
        propietat.put("area", p.getArea());
        propietat.put("preu", p.getPreu());


        // Add a new document with a generated ID
        db.collection("propietats")
                .add(propietat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Test", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Test", "Error adding document", e);
                    }
                });
    }

    public String getUserId(){
        return this.user_id;
    }

    public FirebaseStorage getStorage(){
        return this.storage;
    }

    public void syncData(){
        db.collection("propietats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //private ArrayList<Propietat> list_sync = new ArrayList<Propietat>();
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
                                    //Log.i("Test", p.toString());
                                    //this.list_sync.add(p);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.i("Test", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getPropietatById(String id){
        DocumentReference docRef = db.collection("propietats").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Test", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("Test", "No such document");
                    }
                } else {
                    Log.d("Test", "get failed with ", task.getException());
                }
            }
        });
    }
}