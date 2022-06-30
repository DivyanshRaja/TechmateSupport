package com.asprime.techmatesupport.model;

import com.google.gson.annotations.SerializedName;

public class MobileAppDeviceModel {
    @SerializedName("DeviceID")
    private String deviceID;
    @SerializedName("DeviceName")
    private String deviceName;
    @SerializedName("UserName")
    private String userName;
    @SerializedName("Mobile")
    private String mobile;
    @SerializedName("Email")
    private String email;
    @SerializedName("CustCode")
    private String custCode;
    @SerializedName("Company")
    private String company;
    @SerializedName("DOA")
    private String doa;
    @SerializedName("EOA")
    private String eoa;
    @SerializedName("ServerURL")
    private String serverURL;
    @SerializedName("DeviceActivation")
    private Boolean deviceActivation;
    @SerializedName("SoftwareType")
    private Integer softwareType;
    @SerializedName("SoftName")
    private String softName;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDoa() {
        return doa;
    }

    public void setDoa(String doa) {
        this.doa = doa;
    }

    public String getEoa() {
        return eoa;
    }

    public void setEoa(String eoa) {
        this.eoa = eoa;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public Boolean getDeviceActivation() {
        return deviceActivation;
    }

    public void setDeviceActivation(Boolean deviceActivation) {
        this.deviceActivation = deviceActivation;
    }

    public Integer getSoftwareType() {
        return softwareType;
    }

    public void setSoftwareType(Integer softwareType) {
        this.softwareType = softwareType;
    }

    public String getSoftName() {
        return softName;
    }

    public void setSoftName(String softName) {
        this.softName = softName;
    }
}
