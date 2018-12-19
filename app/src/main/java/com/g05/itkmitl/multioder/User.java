package com.g05.itkmitl.multioder;

import java.io.Serializable;

public class User implements Serializable {
    public String name, phone, address;
    public User(){

    }

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
