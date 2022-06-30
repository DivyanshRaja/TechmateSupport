package com.asprime.techmatesupport.model;

import com.google.gson.annotations.SerializedName;

public class DeviceModel {
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("CustCode")
    private String CustCode;
    @SerializedName("CustStoreID")
    private String CustStoreID;
    @SerializedName("StoreName")
    private String StoreName;
    @SerializedName("DeviceID")
    private String DeviceID;
    @SerializedName("DeviceName")
    private String DeviceName;
    @SerializedName("DeviceIP")
    private String DeviceIP;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

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

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getDeviceIP() {
        return DeviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        DeviceIP = deviceIP;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    @Override
    public String toString() {
        return this.DeviceName; // What to display in the Spinner list.
    }
}
