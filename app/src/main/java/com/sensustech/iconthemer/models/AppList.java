package com.sensustech.iconthemer.models;

import android.graphics.drawable.Drawable;

public class AppList {
    private String name;
    Drawable icon;
    private String packages;
    int id;
    public AppList(String name, Drawable icon, String packages, int id) {
        this.name = name;
        this.icon = icon;
        this.packages = packages;
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public Drawable getIcon() {
        return icon;
    }
    public String getPackages() {
        return packages;
    }
    public int getId() {
        return id;
    }
}