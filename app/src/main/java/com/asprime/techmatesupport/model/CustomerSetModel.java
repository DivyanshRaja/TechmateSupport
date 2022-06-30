package com.asprime.techmatesupport.model;

import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;

import com.asprime.techmatesupport.R;
import com.google.gson.annotations.SerializedName;

public class CustomerSetModel {
    @SerializedName("CustCode")
    String CustCode;
    @SerializedName("CustName")
    String CustName;
    @SerializedName("Allow")
    boolean Allow;
    @SerializedName("SM")
    String SM;
    @SerializedName("UserName")
    String UserName;

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

    public boolean isAllow() {
        return Allow;
    }

    public void setAllow(boolean allow) {
        Allow = allow;
    }

    public String getSM() {
        return SM;
    }

    public void setSM(String SM) {
        this.SM = SM;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @Override
    public String toString() {
        return this.getCustCode().equals("") ? this.getCustName() : this.getCustCode() + " - "+this.getCustName();
    }

    @BindingAdapter("btnType")
    public static void btnSelect(ImageView imageView, boolean btnType){
        if(btnType){
            imageView.setBackgroundTintList(AppCompatResources.getColorStateList(imageView.getContext(), R.color.colorPrimary));
            imageView.setImageDrawable(ResourcesCompat.getDrawable(imageView.getContext().getResources(), R.drawable.ic_baseline_check_24, null));
        } else {
            imageView.setBackgroundTintList(AppCompatResources.getColorStateList(imageView.getContext(), R.color.colorError));
            imageView.setImageDrawable(ResourcesCompat.getDrawable(imageView.getContext().getResources(), R.drawable.ic_outline_close_24, null));
        }
    }
}
