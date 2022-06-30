package com.asprime.techmatesupport.model;

import com.google.gson.annotations.SerializedName;

public class UserListModel {
    @SerializedName("UserID")
    String UserID;
    @SerializedName("CustCode")
    String CustCode;
    @SerializedName("CustName")
    String CustName;
    @SerializedName("UserName")
    String UserName;
    @SerializedName("Pwd")
    String Pwd;
    @SerializedName("EmailID")
    String EmailID;
    @SerializedName("Rights")
    String Rights;
    @SerializedName("Allow")
    String Allow;
    @SerializedName("SM")
    String SM;

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

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String pwd) {
        Pwd = pwd;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getRights() {
        return Rights;
    }

    public void setRights(String rights) {
        Rights = rights;
    }

    public String getAllow() {
        return Allow;
    }

    public void setAllow(String allow) {
        Allow = allow;
    }

    public String getSM() {
        return SM;
    }

    public void setSM(String SM) {
        this.SM = SM;
    }
}
