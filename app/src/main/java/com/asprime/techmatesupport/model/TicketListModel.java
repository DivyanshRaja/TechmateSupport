package com.asprime.techmatesupport.model;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import com.asprime.techmatesupport.R;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketListModel {
    @SerializedName("TrnDate")
    public String TrnDate;
    @SerializedName("TicketNo")
    public int TicketNo;
    @SerializedName("TrnTitle")
    public String TrnTitle;
    @SerializedName("SoftName")
    public String SoftName;
    @SerializedName("CustStoreID")
    public String CustStoreID;
    @SerializedName("StoreName")
    public String StoreName;
    @SerializedName("DeviceName")
    public String DeviceName;
    @SerializedName("DeviceIP")
    public String DeviceIP;
    @SerializedName("UserName")
    public String UserName;
    @SerializedName("SM")
    public String SM;
    @SerializedName("Details")
    public String Details;
    @SerializedName("AttachedFileNo")
    public String AttachedFileNo;
    @SerializedName("EntryNo")
    public int EntryNo;
    @SerializedName("Resolved")
    public boolean Resolved;
    @SerializedName("ResolvedDate")
    public String ResolvedDate;
    @SerializedName("LastUpdate")
    public String LastUpdate;
    @SerializedName("SAID")
    public String SAID;
    @SerializedName("GPS")
    public String GPS;
    @SerializedName("SoftID")
    public String SoftID;
    @SerializedName("TrnType")
    public String TrnType;
    @SerializedName("TrnTypeID")
    public String TrnTypeID;
    @SerializedName("CustCode")
    public String CustCode;


    public String getTrnDate() {
        return TrnDate;
    }

    public void setTrnDate(String trnDate) {
        TrnDate = trnDate;
    }

    public int getTicketNo() {
        return TicketNo;
    }

    public void setTicketNo(int ticketNo) {
        TicketNo = ticketNo;
    }

    public String getTrnTitle() {
        return TrnTitle;
    }

    public void setTrnTitle(String trnTitle) {
        TrnTitle = trnTitle;
    }

    public String getSoftName() {
        return SoftName;
    }

    public void setSoftName(String softName) {
        SoftName = softName;
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

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSM() {
        return SM;
    }

    public void setSM(String SM) {
        this.SM = SM;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getAttachedFileNo() {
        return AttachedFileNo;
    }

    public void setAttachedFileNo(String attachedFileNo) {
        AttachedFileNo = attachedFileNo;
    }

    public int getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(int entryNo) {
        EntryNo = entryNo;
    }

    public boolean isResolved() {
        return Resolved;
    }

    public void setResolved(boolean resolved) {
        Resolved = resolved;
    }

    public String getResolvedDate() {
        return ResolvedDate;
    }

    public void setResolvedDate(String resolvedDate) {
        ResolvedDate = resolvedDate;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public String getSAID() {
        return SAID;
    }

    public void setSAID(String SAID) {
        this.SAID = SAID;
    }

    public String getGPS() {
        return GPS;
    }

    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

    public String getSoftID() {
        return SoftID;
    }

    public void setSoftID(String softID) {
        SoftID = softID;
    }

    public String getTrnType() {
        return TrnType;
    }

    public void setTrnType(String trnType) {
        TrnType = trnType;
    }

    public String getTrnTypeID() {
        return TrnTypeID;
    }

    public void setTrnTypeID(String trnTypeID) {
        TrnTypeID = trnTypeID;
    }

    public String getCustCode() {
        return CustCode;
    }

    public void setCustCode(String custCode) {
        CustCode = custCode;
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
    @BindingAdapter("text")
    public static void bindServerDate(@NonNull TextView textView, boolean text) {
        String status = !text ? "Pending" : "Resolved";
        textView.setText(status);
        if(text) {
            textView.setBackgroundColor(ResourcesCompat.getColor(textView.getContext().getResources(), R.color.resolved_bg, null));
        } else {
            textView.setBackgroundColor(ResourcesCompat.getColor(textView.getContext().getResources(), R.color.colorError, null));
        }
    }

    @BindingAdapter("decideLayout")
    public static void decideLayout(@NonNull LinearLayout linearLayout, String text) {
        if (text.equals("false")){
            linearLayout.setGravity(Gravity.END);
            linearLayout.setPadding(100, 0 , 0, 0);
        } else {
            linearLayout.setGravity(Gravity.START);
            linearLayout.setPadding(0, 0 , 100, 0);
        }
    }

    @BindingAdapter("colorLayout")
    public static void colorLayout(@NonNull TextView linearLayout, String text) {
        if (text.equals("false")){
            linearLayout.setTextColor(ResourcesCompat.getColor(linearLayout.getContext().getResources(), R.color.white, null));
            linearLayout.setBackground(ResourcesCompat.getDrawable(linearLayout.getContext().getResources(),R.drawable.ticket_reply_bg, null));
        } else {
            linearLayout.setTextColor(ResourcesCompat.getColor(linearLayout.getContext().getResources(),R.color.aspireTextColor, null));
            linearLayout.setBackground(ResourcesCompat.getDrawable(linearLayout.getContext().getResources(),R.drawable.support_ticket_reply_bg, null));
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter({"bindReplyOwner", "dateData"})
    public static void bindServerDate(@NonNull TextView textView, String bindReplyOwner, String dateData) {
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
        String ownText = "Posted by: "+bindReplyOwner;
        textView.setText(ownText +" on "+formattedDate);
    }
}