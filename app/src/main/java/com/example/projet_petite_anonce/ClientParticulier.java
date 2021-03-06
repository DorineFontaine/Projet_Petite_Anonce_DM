package com.example.projet_petite_anonce;

/**
 * First type of client : ClientParticulier
 */
public class ClientParticulier {

    private String pseudo;
    private String password;
    private String mail;
    private String lastname ;
    private String firstname;
    private String tel;
    private String picture;

    public ClientParticulier(String name, String password, String mail, String tel){
        this.pseudo = name;
        this.password = password;
        this.mail = mail;
        lastname = null;
        firstname = null;
        this.tel = tel;
        picture = null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
