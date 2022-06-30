package com.asprime.techmatesupport.repository;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.model.ResponseHandlerModel;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.model.SoftwareModelData;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.network.ClientApi;
import com.asprime.techmatesupport.network.RetrofitClient;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonRepository {
    public MutableLiveData<List<SMUserDataModel>> smUserMutableData;
    public MutableLiveData<List<CompanyDataModel>> companyMutableData;
    public MutableLiveData<List<CustomerSetModel>> customerSetMutableData;
    public MutableLiveData<List<CompanyStoreDataModel>> companyStoreMutableData;
    public MutableLiveData<List<SoftwareModelData>> softwareMutableData;
    ClientApi apiInterface;
    PreferenceHandler preferences;
    Context context;

    public CommonRepository(Context context) {
        preferences = new PreferenceHandler(context);
        apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        this.context = context;
    }

    public void getSMUser(HashMap<String, String> hashMap){
        smUserMutableData = new MutableLiveData<>();
        apiInterface.getSupportManager(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson converter = new Gson();
                    Type type = new TypeToken<List<SMUserDataModel>>() {
                    }.getType();
                    List<SMUserDataModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                    smUserMutableData.setValue(list);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseHandlerModel> call, @Nullable Throwable t) {

            }
        });
    }

    public void getAllowCompanyData(HashMap<String, Object> hashMap){
        companyMutableData = new MutableLiveData<>();
        apiInterface.getUserCompany(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson converter = new Gson();
                    Type type = new TypeToken<List<CompanyDataModel>>() {
                    }.getType();
                    List<CompanyDataModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                    companyMutableData.setValue(list);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseHandlerModel> call, @Nullable Throwable t) {

            }
        });
    }

    public void getAllowCustomerSet(HashMap<String, Object> hashMap){
        customerSetMutableData = new MutableLiveData<>();
        apiInterface.getCustomerSet(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson converter = new Gson();
                    Type type = new TypeToken<List<CustomerSetModel>>() {
                    }.getType();
                    List<CustomerSetModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                    customerSetMutableData.setValue(list);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseHandlerModel> call, @Nullable Throwable t) {

            }
        });
    }

    public void getCompanyStoreList(HashMap<String, String> hashMap){
        companyStoreMutableData = new MutableLiveData<>();
        apiInterface.getCompanyStoresList(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson converter = new Gson();
                    Type type = new TypeToken<List<CompanyStoreDataModel>>() {
                    }.getType();
                    List<CompanyStoreDataModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                    companyStoreMutableData.setValue(list);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseHandlerModel> call, @Nullable Throwable t) {

            }
        });
    }

    public void getAllSoftwareList() {
        softwareMutableData = new MutableLiveData<>();
        HashMap<String, String> hashMap = new HashMap<>();
        apiInterface.getSoftwareset(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<List<SoftwareModelData>>() {
                        }.getType();
                        List<SoftwareModelData> list = converter.fromJson(String.valueOf(jsonArray), type);
                        softwareMutableData.postValue(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {

            }
        });
    }
}
