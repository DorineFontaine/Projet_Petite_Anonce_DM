package com.example.projet_petite_anonce;

public class ClientProfessionel {
    String nom_societe;
    String numero_siret;
    String mail;
    String psw;
    String client;
    String tel;

    public ClientProfessionel(String nom_societe, String numero_siret, String mail, String psw, String client, String tel){
        this.nom_societe = nom_societe;
        this.numero_siret = numero_siret;
        this.mail = mail;
        this.psw = psw;
        this.client = client;
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNom_societe() {
        return nom_societe;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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
