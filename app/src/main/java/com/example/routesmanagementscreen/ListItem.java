package com.example.routesmanagementscreen;

public class ListItem {
    private String itemName;
    private String itemDescription;

    public ListItem(String name, String description) {
        this.itemName = name;
        this.itemDescription = description;
    }

    public String getItemName() {
        return this.itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }
}