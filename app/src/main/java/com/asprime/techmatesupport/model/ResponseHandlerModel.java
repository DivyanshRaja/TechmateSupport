package com.asprime.techmatesupport.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseHandlerModel {
    @SerializedName("d")
    @Expose
    private String d;

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }
}
