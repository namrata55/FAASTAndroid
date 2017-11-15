package com.faast.mobile.apps;

/**
 * Created by namrata.s on 11/25/2016.
 */
public class PlanChangeDataModel {

    String name;
    String id;
    String price;
    String total_data;
    String speed;
    String postspeed;
    String city;
    String total_data_daily;


    public PlanChangeDataModel(String name, String id, String price, String total_data, String speed, String postspeed,String city,String total_data_daily) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.total_data = total_data;
        this.speed = speed;
        this.postspeed = postspeed;
        this.city = city;
        this.total_data_daily = total_data_daily;
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
    public void setCity(String city) {
        this.city = city;
    }

    public void setTotal_data(String total_data) {
        this.total_data = total_data;

    }
    public void setTotal_data_daily(String total_data_daily) {
        this.total_data_daily = total_data_daily;

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
    public String getTotalDataDaily() { return total_data_daily; }
    public String getCity() { return city; }
}
