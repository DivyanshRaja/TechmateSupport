package com.asprime.techmatesupport.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHandler {
    private final SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;

    public PreferenceHandler(Context context) {
        sharedPreferences = context.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void clearPref(){
        editor.clear();
    }

    private final String user_firebase_token = "user_firebase_token";
    private final String user_name = "user_name";
    private final String user_id = "user_id";
    private final String user_rights_count = "user_rights_count";
    private final String user_rights = "user_rights";
    private final String company_information = "company_information";

    public String getUser_rights_count() {
        return sharedPreferences.getString(user_rights_count,"");
    }

    public String getUser_rights() {
        return sharedPreferences.getString(user_rights,"");
    }

    public String getCompany_information() {
        return sharedPreferences.getString(company_information,"");
    }

    public String getUser_name() {
        return sharedPreferences.getString(user_name,"");
    }

    public String getUser_firebase_token() {
        return sharedPreferences.getString(user_firebase_token,"");
    }

    public String getUser_id() {
        return sharedPreferences.getString(user_id,"");
    }

    public void setUser_rights_count(String user_rights_count){
        editor.putString(this.user_rights_count, user_rights_count);
        editor.commit();
    }

    public void setUser_id(String user_id){
        editor.putString(this.user_id, user_id);
        editor.commit();
    }

    public void setUser_rights(String user_rights){
        editor.putString(this.user_rights, user_rights);
        editor.commit();
    }

    public void setCompany_information(String company_information){
        editor.putString(this.company_information, company_information);
        editor.commit();
    }

    public void setUser_firebase_token(String user_firebase_token){
        editor.putString(this.user_firebase_token, user_firebase_token);
        editor.commit();
    }

    public void setUser_name(String user_name){
        editor.putString(this.user_name, user_name);
        editor.commit();
    }
}
