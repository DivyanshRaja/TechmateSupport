package com.asprime.techmatesupport.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.DeviceModel;
import com.asprime.techmatesupport.model.ResponseHandlerModel;
import com.asprime.techmatesupport.model.UserListModel;
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

public class UserRepository {
    ClientApi apiInterface;
    PreferenceHandler preferences;
    Context context;

    public UserRepository(Context context) {
        preferences = new PreferenceHandler(context);
        apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        this.context = context;
    }

    public void getUserList(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getUserList(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<UserListModel>>() {
                        }.getType();
                        ArrayList<UserListModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        ArrayList<UserListModel> list = new ArrayList<>();
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

    public void getSuperUserList(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getSuperUserList(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<UserListModel>>() {
                        }.getType();
                        ArrayList<UserListModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        ArrayList<UserListModel> list = new ArrayList<>();
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
}
