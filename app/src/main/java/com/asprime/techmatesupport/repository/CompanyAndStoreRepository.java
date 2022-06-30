package com.asprime.techmatesupport.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.CustomerSetModel;
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

public class CompanyAndStoreRepository {
    ClientApi apiInterface;
    PreferenceHandler preferences;
    Context context;

    public CompanyAndStoreRepository(Context context) {
        preferences = new PreferenceHandler(context);
        apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        this.context = context;
    }

    public void getCompanyList(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getCustomerSet(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<CustomerSetModel>>() {
                        }.getType();
                        ArrayList<CustomerSetModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "customerSet", list);
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

    public void getStoreSetByCustomer(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getStoreSETByCustomer(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<CompanyStoreDataModel>>() {
                        }.getType();
                        ArrayList<CompanyStoreDataModel> list = converter.fromJson(String.valueOf(jsonArray), type);
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

    public void addEditCompany(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.addEditCompany(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    if (!responseString.equals("") && responseString.contains("Added")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "New Company Added", null);
                    } else if (!responseString.equals("") && responseString.contains("Already Exist")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Company already exist", null);
                    } else if (!responseString.equals("") && responseString.contains("Updated")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "Company details updated", null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete request. Try again!..", null);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void allowCompany(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.allowCompany(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    int responseString = Integer.parseInt(response.body().getD());
                    if (responseString == -1) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_SUCCESS_CODE, "", null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Unable to complete request. Try again!..", null);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void updateStoreDetails(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.updateStore(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    if (!responseString.equals("") && responseString.contains("Added")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "Store details has added", null);
                    } else if (!responseString.equals("") && responseString.contains("Already Exist")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Store details already exists", null);
                    } else if (!responseString.equals("") && responseString.contains("Updated")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "Store details has updated", null);
                    } else if (!responseString.equals("") && responseString.contains("Deleted")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_SUCCESS_CODE, "Store details has deleted", null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete request. Try again!..", null);
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
