package com.example.aloma.project_2;

/**
 * Created by ratisaxena on 22-11-2016.
 */

public class Contact {

    private String name;
    private String number;
    private String type;

    public Contact() {
    }

    public Contact(String name, String number, String type) {
        this.name = name;
        this.number = number;
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        String str = name + ": " + number;
        return str;
    }
}
