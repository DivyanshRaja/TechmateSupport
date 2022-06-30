package com.asprime.techmatesupport.model;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PosKeyModel {
    @SerializedName("SoftID")
    String SoftID;
    @SerializedName("SoftName")
    String SoftName;
    @SerializedName("CustCode")
    String CustCode;
    @SerializedName("CustomerName")
    String CustomerName;
    @SerializedName("HDSrNo")
    String HDSrNo;
    @SerializedName("WSKey")
    String WSKey;
    @SerializedName("WSName")
    String WSName;
    @SerializedName("PunchDate")
    String PunchDate;
    @SerializedName("UserName")
    String UserName;
    @SerializedName("PCName")
    String PCName;

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

    public String getCustCode() {
        return CustCode;
    }

    public void setCustCode(String custCode) {
        CustCode = custCode;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getHDSrNo() {
        return HDSrNo;
    }

    public void setHDSrNo(String HDSrNo) {
        this.HDSrNo = HDSrNo;
    }

    public String getWSKey() {
        return WSKey;
    }

    public void setWSKey(String WSKey) {
        this.WSKey = WSKey;
    }

    public String getWSName() {
        return WSName;
    }

    public void setWSName(String WSName) {
        this.WSName = WSName;
    }

    public String getPunchDate() {
        return PunchDate;
    }

    public void setPunchDate(String punchDate) {
        PunchDate = punchDate;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPCName() {
        return PCName;
    }

    public void setPCName(String PCName) {
        this.PCName = PCName;
    }

    @BindingAdapter("bindServerDate")
    public static void bindServerDate(@NonNull TextView textView, String dateData) {
        String formattedDate;
        if (dateData.equals("")) {
            formattedDate = "";
        } else {
            String timestamp = dateData.split("\\(")[1].split("\\+")[0];
            String timeStamp1 = timestamp.replace(")", "").replace("/", "");
            Date createdOn = new Date(Long.parseLong(timeStamp1));
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            formattedDate = sdf.format(createdOn);
        }
        textView.setText(formattedDate);
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("dateWithText")
    public static void dateWithText(@NonNull TextView textView, String dateData) {
        String formattedDate;
        if (dateData.equals("")) {
            formattedDate = "";
        } else {
            String timestamp = dateData.split("\\(")[1].split("\\+")[0];
            String timeStamp1 = timestamp.replace(")", "").replace("/", "");
            Date createdOn = new Date(Long.parseLong(timeStamp1));
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            formattedDate = sdf.format(createdOn);
        }
        textView.setText("Date: " + formattedDate);
    }
}
