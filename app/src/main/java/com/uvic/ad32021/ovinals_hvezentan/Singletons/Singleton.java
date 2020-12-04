package com.uvic.ad32021.ovinals_hvezentan.Singletons;

import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;

import java.util.ArrayList;

public class Singleton {
    ArrayList<Propietat> list_propietats;
    int user_id;
    private static class SingletonInstance {
        private static Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private Singleton() {
        this.user_id = 1;
        this.list_propietats = new ArrayList<Propietat>();
        Propietat p1 = new Propietat(0, "User 0", "Carrer A, Vic 21009", "Descripció", "Equipaments", "path imatge", 0, 99, 99999.99);
        Propietat p2 = new Propietat(1, "User 1", "Carrer A, Vic 21009", "Descripció", "Equipaments", "path imatge", 1, 99, 99999.99);
        this.list_propietats.add(p1);
        this.list_propietats.add(p2);

    }

    public ArrayList<Propietat> getPropietats(){
        return this.list_propietats;
    }

    public ArrayList<Propietat> getPropietatsByUser(){
        ArrayList<Propietat> propietats_user = new ArrayList<Propietat>();
        if(this.user_id != -1){
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
    }

    public int getUserId(){
        return this.user_id;
    }

    public void updatePropietats(){
        //Actualitzar la llista amb les noves propietats
    }
}