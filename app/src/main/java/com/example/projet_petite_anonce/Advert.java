package com.example.projet_petite_anonce;

public class Advert {
    String image, title,ownerid,price,location,date,category,description,contact, state;


 /*   public Advert(String image ,String title, String ownername,String price,String location,String date, String category,String description,String contact){
        this.image = image;

        this.date = date;
        this.category = category;
        this.description = description;
        this.contact = contact;
    }*/

    public Advert(String title, String price, String location, String description, String category){

        this.title = title;

        this.price = price;
        this.location = location;
        this.ownerid = ownerid;
        this.description = description;
        this.category = category;
        this.state = state;


    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnername() {
        return ownerid;
    }

    public void setOwnername(String ownername) {
        this.ownerid = ownername;
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
