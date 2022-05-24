package com.example.projet_petite_anonce;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Advert {
    String key, title,ownername,price,location,date,category,description,contact, state;
    Bitmap image;
    /*
    Image is a bitmap
     */

 /*   public Advert(String image ,String title, String ownername,String price,String location,String date, String category,String description,String contact){
        this.image = image;
        this.title = title;
        this.ownername = ownername;
        this.price = price;
        this.location = location;
        this.date = date;
        this.category = category;
        this.description = description;
        this.contact = contact;
    }*/

    public Advert(String title,String price,String location,String description, String category, String state){

        this.title = title;
        this.state = state;
        this.category = category;
        this.price = price;
        this.location = location;
        this.description = description;

        Date currentDate = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MMM/yyyy");
        this.date = dateFormat.format(currentDate);

        image = null;
    }

    public String getKey(){return key;}

    public void setKey(String key){this.key = key;}

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
