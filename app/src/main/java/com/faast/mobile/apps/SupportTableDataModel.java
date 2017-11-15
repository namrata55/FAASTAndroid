package com.faast.mobile.apps;

/**
 * Created by namrata.s on 27/04/2017.
 */

public class SupportTableDataModel { String name;
    String id;
    String price;
    String total_data;
    String speed;
    String postspeed;


    public SupportTableDataModel(String name, String id, String price, String total_data, String speed, String postspeed) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.total_data = total_data;
        this.speed = speed;
        this.postspeed = postspeed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTotal_data(String total_data) {
        this.total_data = total_data;

    }

    public String getName() {
        return name;
    }

    public String getSpeed() {
        return speed;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getTotalData() {
        return total_data;
    }

    public String getPostspeed() { return postspeed; }
}
