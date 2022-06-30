package com.asprime.techmatesupport.model;

import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;

public class SMUserDataModel {
    @SerializedName("UserName")
    private String title;
    @SerializedName("UserID")
    private int id;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.title; // What to display in the Spinner list.
    }
}
