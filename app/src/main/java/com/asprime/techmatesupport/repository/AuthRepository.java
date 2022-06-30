package com.asprime.techmatesupport.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.ResponseHandlerModel;
import com.asprime.techmatesupport.network.ClientApi;
import com.asprime.techmatesupport.network.RetrofitClient;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    ClientApi apiInterface;
    PreferenceHandler preferences;
    Context context;

    public AuthRepository(Context context) {
        preferences = new PreferenceHandler(context);
        apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        this.context = context;
    }

    public void userLogin(Map<String, String> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.loginUser(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                if (response != null) {
                    assert response.body() != null;
                    String jsonString = response.body().getD();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (Integer.parseInt(jsonObject.getString("RightsCount")) > 0) {
                            jsonObject.getJSONArray("UserRights");
                            List<String> rightList = CommonUtils.getRightListFromString(jsonObject.getString("UserRights"));
                            if (rightList.contains("Support")) {
                                preferences.setUser_rights(jsonObject.getString("UserRights"));
                                preferences.setUser_rights_count(jsonObject.getString("RightsCount"));
                                preferences.setUser_name(jsonObject.getString("UserName"));
                                preferences.setUser_id(jsonObject.getString("UserID"));
                                apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "Login successful", null);
                            } else {
                                apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Invalid UserName and Password", null);
                            }
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Invalid UserName and Password", null);
                        }
                    } catch (JSONException e) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete request. Try again!...", null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Invalid UserName and Password", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseHandlerModel> call, @NonNull Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void userForgotPassword(HashMap<String, String> hashMap, ApiStatusListener apiStatusListener){
        apiStatusListener.onRequestStart();
        apiInterface.forgotPassword(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                if(response != null) {
                    assert response.body() != null;
                    String responseString = response.body().getD();
                    if(!responseString.equalsIgnoreCase("User Not Found")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, responseString, null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, responseString, null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Invalid Username", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseHandlerModel> call,@NonNull Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }
}
