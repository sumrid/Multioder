package com.g05.itkmitl.multioder.cart;

import com.g05.itkmitl.multioder.User;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.map.LatLng;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private Food food;
    private String uid;
    private int amount;
    private double total;
    private String description;
    private User user;
    private LatLng location;

    public CartItem(Food food, int amount) {
        this.food = food;
        this.uid = food.getUid();
        this.amount = amount;
        this.total = food.getPrice() * amount;
    }

    public CartItem() {
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getTotal() {
        return food.getPrice() * amount;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
