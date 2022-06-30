package com.asprime.techmatesupport.listners;

public interface ApiStatusListener{
    void onRequestStart();
    void onRequestComplete(int statusCode, String result, Object obj);
    void onRequestFailure(String msg);
}
