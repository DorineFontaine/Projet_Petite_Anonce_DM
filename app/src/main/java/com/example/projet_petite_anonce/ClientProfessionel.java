package com.example.projet_petite_anonce;

public class ClientProfessionel {
    String nom_societe;
    String numero_siret;
    String mail;
    String psw;

    public ClientProfessionel(String nom_societe,String numero_siret,String mail,String psw){
        this.nom_societe = nom_societe;
        this.numero_siret = numero_siret;
        this.mail = mail;
        this.psw = psw;
    }

    public String getNom_societe() {
        return nom_societe;
    }

    public void setNom_societe(String nom_societe) {
        this.nom_societe = nom_societe;
    }

    public String getNumero_siret() {
        return numero_siret;
    }

    public void setNumero_siret(String numero_siret) {
        this.numero_siret = numero_siret;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }
}
