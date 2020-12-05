package com.uvic.ad32021.ovinals_hvezentan.Entitats;

public class Usuari {
    int id;
    String nom, cognomos, email, telefon, password;

    public Usuari(int id, String nom, String cognomos, String email, String telefon, String password) {
        this.id = id;
        this.nom = nom;
        this.cognomos = cognomos;
        this.email = email;
        this.telefon = telefon;
        this.password = password;
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

    public String getCognomos() {
        return cognomos;
    }

    public void setCognomos(String cognomos) {
        this.cognomos = cognomos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Usuari{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", cognomos='" + cognomos + '\'' +
                ", email='" + email + '\'' +
                ", telefon='" + telefon + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
