package com.asprime.techmatesupport.model;

import com.google.gson.annotations.SerializedName;

public class SoftwareModelData {
    @SerializedName("SoftID")
    private String SoftID;
    @SerializedName("SoftName")
    private String SoftName;

    public String getSoftID() {
        return SoftID;
    }

    public void setSoftID(String softID) {
        SoftID = softID;
    }

    public String getSoftName() {
        return SoftName;
    }

    public void setSoftName(String softName) {
        SoftName = softName;
    }

    @Override
    public String toString() {
        return this.SoftName; // What to display in the Spinner list.
    }

}
