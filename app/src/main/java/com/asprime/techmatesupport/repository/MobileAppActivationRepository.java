package com.asprime.techmatesupport.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.DeviceModel;
import com.asprime.techmatesupport.model.MobileAppDeviceModel;
import com.asprime.techmatesupport.model.ResponseHandlerModel;
import com.asprime.techmatesupport.network.ClientApi;
import com.asprime.techmatesupport.network.RetrofitClient;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileAppActivationRepository {
    ClientApi apiInterface;
    PreferenceHandler preferences;
    Context context;

    public MobileAppActivationRepository(Context context) {
        preferences = new PreferenceHandler(context);
        apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        this.context = context;
    }

    public void getAndroidActivationDevice(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getAndroidActivation(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    if (!responseString.equals("")) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(responseString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Gson converter = new Gson();
                        Type type = new TypeToken<ArrayList<MobileAppDeviceModel>>() {
                        }.getType();
                        ArrayList<MobileAppDeviceModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        ArrayList<MobileAppDeviceModel> list = new ArrayList<>();
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", list);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void updateDevice(HashMap<String, Object> hashMap, ApiStatusListener statusListener) {
        statusListener.onRequestStart();
        apiInterface.updateDevice(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseHandlerModel> call, @NonNull Response<ResponseHandlerModel> response) {
                if (response.body() != null && !response.body().getD().equals("") && response.body().getD().contains("Updated")) {
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_SUCCESS_CODE, "Update device information successfully", null);
                } else if(response.body().getD().contains("Device IP in Use")){
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Device IP is used by other device", null);
                }  else if(response.body().getD().contains("Device Name in Use")){
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Device name is used by other device", null);
                } else {
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, response.body().getD(), null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
               statusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void addDevice(HashMap<String, Object> hashMap, ApiStatusListener statusListener) {
        statusListener.onRequestStart();
        apiInterface.addDevice(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseHandlerModel> call, @NonNull Response<ResponseHandlerModel> response) {
                if (response.body() != null && !response.body().getD().equals("") && response.body().getD().contains("Added")) {
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_SUCCESS_CODE, "New device added successfully", null);
                } else if(response.body().getD().contains("Device IP in Use")){
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Device IP is used by other device", null);
                }  else if(response.body().getD().contains("Device Name in Use")){
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Device name is used by other device", null);
                } else {
                    statusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, response.body().getD(), null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                statusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }
}
