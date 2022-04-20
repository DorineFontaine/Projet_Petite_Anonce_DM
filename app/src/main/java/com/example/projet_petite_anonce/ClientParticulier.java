package com.example.projet_petite_anonce;

public class ClientParticulier {

    private String pseudo;
    private String password;
    private String mail;

    public ClientParticulier(String name, String password, String mail){
        this.pseudo = name;
        this.password = password;
        this.mail = mail;

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
}
