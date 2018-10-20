package com.g05.itkmitl.multioder.cart;

import com.g05.itkmitl.multioder.food.Food;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private Food food;
    private String uid;
    private int amount;
    private double total;
    private List<String> deleteKeys = new ArrayList<>();

    public CartItem(Food food, int amount) {
        this.food = food;
        this.uid = food.getUid();
        this.amount = amount;
        this.total = food.getPrice()*amount;
        this.deleteKeys.add(food.getDeleteKey());
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
        return food.getPrice()*amount;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<String> getDeleteKeys() {
        return deleteKeys;
    }

    public void setDeleteKeys(List<String> deleteKeys) {
        this.deleteKeys = deleteKeys;
    }

    public void addDeleteKey(String deleteKey){
        this.deleteKeys.add(deleteKey);
    }
}
