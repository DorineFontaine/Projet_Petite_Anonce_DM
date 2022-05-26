package com.example.projet_petite_anonce;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Advert created by a user
 */
public class Advert {
    String key, title, ownerid,price,location,date,category,description,contact, state;
    Bitmap image;


    public Advert(String title,String price,String location,String description, String category, String state, String ownerid){

        this.title = title;
        this.state = state;
        this.category = category;
        this.price = price;
        this.location = location;
        this.description = description;
        this.ownerid = ownerid;
        Date currentDate = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MMM/yyyy");
        this.date = dateFormat.format(currentDate).replace("/", "");

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

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
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
