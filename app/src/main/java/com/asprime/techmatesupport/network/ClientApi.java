package com.asprime.techmatesupport.network;


import com.asprime.techmatesupport.model.ResponseHandlerModel;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ClientApi {

    /* Multipart data upload*/
    @Multipart
    @POST("UploadFileHandler.ashx")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file, @Query("TicketNo") int ticketNo, @Query("EntryNo") int entryNo);


    @POST("AttemptLogin")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> loginUser(@Body Map<String, String> loginCredential);

    @POST("ForgotPassword")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> forgotPassword(@Body Map<String, String> loginCredential);

    @POST("SupportGetUserRights")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> checkRights(@Body Map<String, String> username);

    /*
     * Request Ticket screen api
     */
    @POST("GetUserCompanyRights")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getUserCompany(@Body Map<String, Object> username);

    @POST("GetSMAgentList")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getSupportManager(@Body Map<String, String> username);

    @POST("GetSoftwareset")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getSoftwareset(@Body Map<String, String> username);

    @POST("InsertTicketTrnSupport")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> postTicketData(@Body HashMap<String, Object> hashMap);

    @POST("GetStoreDetailsByCompany")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getCompanyStoresList(@Body HashMap<String, String> hashMap);

    @POST("GetPendingTicketTrnBySupportUser")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getPendingTicket(@Body HashMap<String, Object> hashmap);

    @POST("GetTrnFileName")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getAttachmentList(@Body HashMap<String, Object> hashmap);

    @POST("GetPendingTicketHistoryClientByUserbyTicket")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> pendingTicketDetails(@Body Map<String, Object> data);

    @POST("InsertResponseTicketTrnWithResolved")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> resolvedOrEditTicket(@Body Map<String, Object> data);

    @POST("DeleteTrnFile")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> deleteTicket(@Body HashMap<String, Object> hashMap);

    @POST("TransferTicketFromSupportUser")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> transferTicket(@Body HashMap<String, Object> hashMap);

    @POST("GetPendingTicketHistorySupportByUser")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getHistoryTicket(@Body HashMap<String, Object> hashMap);

    @POST("ReversalInsertResponseTicketTrnWithResolved")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> reverseTicket(@Body HashMap<String, Object> hashmap);

    @POST("GetDeviceByUserIDSupport")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getDeviceList(@Body HashMap<String, Object> hashmap);

    @POST("EditDeviceByCustomer")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> updateDevice(@Body HashMap<String, Object> hashMap);

    @POST("InsertDeviceBySupport")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> addDevice(@Body HashMap<String, Object> hashMap);

    @POST("GetUserListing")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getUserList(@Body HashMap<String, Object> hashMap);

    @POST("GetUserListingSuper")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getSuperUserList(@Body HashMap<String, Object> hashMap);

    @POST("GetCustomerSet")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getCustomerSet(@Body HashMap<String, Object> hashMap);

    @POST("GetStoreSETByCustomer")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getStoreSETByCustomer(@Body HashMap<String, Object> hashMap);

    @POST("GetAndroidActivation")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> getAndroidActivation(@Body HashMap<String, Object> hashmap);

    @POST("InsertCustomerDetails")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> addEditCompany(@Body HashMap<String, Object> hashMap);

    @POST("AllowCustomer")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> allowCompany(@Body HashMap<String, Object> hashMap);

    @POST("AddUpdateStoreByCustomer")
    @Headers({
            "Content-Type:application/json"
    })
    Call<ResponseHandlerModel> updateStore(@Body HashMap<String, Object> hashMap);
}
