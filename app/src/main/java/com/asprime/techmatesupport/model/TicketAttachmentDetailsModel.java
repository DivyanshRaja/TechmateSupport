package com.asprime.techmatesupport.model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

public class TicketAttachmentDetailsModel {
    @SerializedName("TicketNo")
    public String TicketNo;
    @SerializedName("EntryNo")
    public String EntryNo;
    @SerializedName("TrnDate")
    public String TrnDate;
    @SerializedName("TrnDetails")
    public String TrnDetails;
    @SerializedName("UserName")
    public String UserName;
    @SerializedName("FileDetials")
    public List<FileDetials> FileDetials;

    public String getTicketNo() {
        return TicketNo;
    }

    public void setTicketNo(String ticketNo) {
        TicketNo = ticketNo;
    }

    public String getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(String entryNo) {
        EntryNo = entryNo;
    }

    public String getTrnDate() {
        return TrnDate;
    }

    public void setTrnDate(String trnDate) {
        TrnDate = trnDate;
    }

    public String getTrnDetails() {
        return TrnDetails;
    }

    public void setTrnDetails(String trnDetails) {
        TrnDetails = trnDetails;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public List<FileDetials> getFileDetials() {
        return FileDetials;
    }

    public void setFileDetials(List<FileDetials> fileDetials) {
        FileDetials = fileDetials;
    }
}
