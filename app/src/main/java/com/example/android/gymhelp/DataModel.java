package com.example.android.gymhelp;

// The DataModel class is used to define the objects for the drawer list items.
public class DataModel {
    public int icon;
    public String name;

    // Constructor.
    public DataModel(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public DataModel(String name) {
        this.name = name;
    }
}
