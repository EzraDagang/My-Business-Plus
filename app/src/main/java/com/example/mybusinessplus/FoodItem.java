package com.example.mybusinessplus;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String name;
    private double price;
    private int quantity;

    public FoodItem(String name, double price) {
        this.name = name;
        this.price = price;
        this.quantity = 0; // Default to zero
    }

    // Getters and Setters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
