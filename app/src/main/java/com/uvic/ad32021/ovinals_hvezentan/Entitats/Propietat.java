package com.uvic.ad32021.ovinals_hvezentan.Entitats;

public class Propietat {
    String nom, ubicacio, descripcio, equipaments, imatge;
    int id, user_id, area;
    double preu;

    public Propietat(int id, String nom, String ubicacio, String descripcio, String equipaments, String imatge, int user_id, int area, double preu) {
        this.id = id;
        this.nom = nom;
        this.ubicacio = ubicacio;
        this.descripcio = descripcio;
        this.equipaments = equipaments;
        this.imatge = imatge;
        this.user_id = user_id;
        this.area = area;
        this.preu = preu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getUbicacio() {
        return ubicacio;
    }

    public void setUbicacio(String ubicacio) {
        this.ubicacio = ubicacio;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getEquipaments() {
        return equipaments;
    }

    public void setEquipaments(String equipaments) {
        this.equipaments = equipaments;
    }

    public String getImatge() {
        return imatge;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public double getPreu() {
        return preu;
    }

    public void setPreu(double preu) {
        this.preu = preu;
    }

    @Override
    public String toString() {
        return "Propietat{" +
                "nom='" + nom + '\'' +
                ", ubicacio='" + ubicacio + '\'' +
                ", descripcio='" + descripcio + '\'' +
                ", equipaments='" + equipaments + '\'' +
                ", imatge='" + imatge + '\'' +
                ", id=" + id +
                ", user_id=" + user_id +
                ", area=" + area +
                ", preu=" + preu +
                '}';
    }
}
