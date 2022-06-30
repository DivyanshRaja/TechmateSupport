package com.asprime.techmatesupport.model;

import com.google.gson.annotations.SerializedName;

public class CompanyStoreDataModel {
    @SerializedName("CustCode")
    private String CustCode;
    @SerializedName("CustStoreID")
    private String CustStoreID;
    @SerializedName("StoreName")
    private String StoreName;
    @SerializedName("CustName")
    private String CustName;

    public String getCustCode() {
        return CustCode;
    }

    public void setCustCode(String custCode) {
        CustCode = custCode;
    }

    public String getCustStoreID() {
        return CustStoreID;
    }

    public void setCustStoreID(String custStoreID) {
        CustStoreID = custStoreID;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    @Override
    public String toString() {
        return this.StoreName; // What to display in the Spinner list.
    }
}
