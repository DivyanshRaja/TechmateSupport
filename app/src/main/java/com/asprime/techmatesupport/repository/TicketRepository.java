package com.asprime.techmatesupport.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.ResponseHandlerModel;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.model.TicketAttachmentDetailsModel;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.network.ClientApi;
import com.asprime.techmatesupport.network.RetrofitClient;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketRepository {
    public MutableLiveData<List<SMUserDataModel>> userSpinnerDataMutableLiveData;
    ClientApi apiInterface;
    PreferenceHandler preferences;
    Context context;

    public TicketRepository(Context context) {
        preferences = new PreferenceHandler(context);
        apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        this.context = context;
    }

    public void createTicket(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.postTicketData(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(Call<ResponseHandlerModel> call, Response<ResponseHandlerModel> response) {
                if (response.body() != null && !response.body().getD().equals("")) {
                    int id = Integer.parseInt(response.body().getD());
                    if (id != 0) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, String.valueOf(id), null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete your request. Try again..", null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete your request. Try again..", null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public int postDocument(int ticketNo, MultipartBody.Part body) {
        final int[] msg = new int[1];
        ClientApi apiInterface1 = RetrofitClient.getMultipartApiClient().create(ClientApi.class);
        apiInterface1.uploadImage(body, ticketNo, 1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        return 1;
    }

    public void getPendingTicket(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getPendingTicket(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<TicketListModel>>() {
                        }.getType();
                        ArrayList<TicketListModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        ArrayList<TicketListModel> list = new ArrayList<>();
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

    public void getTicketAttachment(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getAttachmentList(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<TicketAttachmentDetailsModel>>() {
                        }.getType();
                        ArrayList<TicketAttachmentDetailsModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void getTicketDetails(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.pendingTicketDetails(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<TicketListModel>>() {
                        }.getType();
                        ArrayList<TicketListModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void resolvedTicket(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.resolvedOrEditTicket(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    if (responseString.contains("Updated")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_SUCCESS_CODE, "", null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Unable to complete your request. Try again..", null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Unable to complete your request. Try again..", null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void deleteTicket(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiInterface.deleteTicket(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    if (responseString.contains("Deleted")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "deleted", null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete your request. Try again..", null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete your request. Try again..", null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void editTicket(HashMap<String, Object> hashMap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.transferTicket(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    if (responseString.contains("-1")) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "Ticket # " + hashMap.get("TicketNo") + " updated", null);
                    } else {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete your request. Try again..", null);
                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "Unable to complete your request. Try again..", null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }

    public void getHistoryTicket(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.getHistoryTicket(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
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
                        Type type = new TypeToken<ArrayList<TicketListModel>>() {
                        }.getType();
                        ArrayList<TicketListModel> list = converter.fromJson(String.valueOf(jsonArray), type);
                        if (list == null) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ERROR_CODE, "", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.SUCCESS_CODE, "", list);
                        }
                    } else {
                        ArrayList<TicketListModel> list = new ArrayList<>();
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

    public void reverseTicket(HashMap<String, Object> hashmap, ApiStatusListener apiStatusListener) {
        apiStatusListener.onRequestStart();
        apiInterface.reverseTicket(hashmap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                assert response != null;
                if (response.body() != null) {
                    String responseString = response.body().getD();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(responseString);
                        if (Integer.parseInt(object.getString("Entry")) > 0) {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_SUCCESS_CODE, "Ticket #" + hashmap.get("TicketNo") + " has Reversed", null);
                        } else {
                            apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, object.get("Response").toString(), null);
                        }
                    } catch (JSONException e) {
                        apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Unable to complete your request. Try again..", null);

                    }
                } else {
                    apiStatusListener.onRequestComplete(ApiStatusCode.ACTION_ERROR_CODE, "Unable to complete your request. Try again..", null);
                }
            }

            @Override
            public void onFailure(Call<ResponseHandlerModel> call, Throwable t) {
                apiStatusListener.onRequestFailure(context.getString(R.string.check_internet));
            }
        });
    }
}
