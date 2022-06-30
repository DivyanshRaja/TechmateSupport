package com.asprime.techmatesupport.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.model.SoftwareModelData;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.TicketAttachmentDetailsModel;
import com.asprime.techmatesupport.repository.CommonRepository;
import com.asprime.techmatesupport.repository.TicketRepository;
import com.asprime.techmatesupport.utils.PreferenceHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TicketViewModel extends AndroidViewModel {
    public ApiStatusListener apiStatusListener;
    public String companyId, storeId, viewType, fromDate, toDate;
    public MutableLiveData<String> messageText = new MutableLiveData<>();
    PreferenceHandler preferenceHandler;
    TicketRepository ticketRepository;
    CommonRepository commonRepository;

    public TicketViewModel(@NonNull Application application) {
        super(application);
        preferenceHandler = new PreferenceHandler(application.getApplicationContext());
        ticketRepository = new TicketRepository(application.getApplicationContext());
        commonRepository = new CommonRepository(application.getApplicationContext());
    }

    public LiveData<List<SMUserDataModel>> getSMUSerData(HashMap<String, String> hashmap) {
        commonRepository.getSMUser(hashmap);
        return commonRepository.smUserMutableData;
    }

    public LiveData<List<SoftwareModelData>> getSoftwareData() {
        commonRepository.getAllSoftwareList();
        return commonRepository.softwareMutableData;
    }

    public LiveData<List<CompanyDataModel>> getAllowedCompanyData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        commonRepository.getAllowCompanyData(hashMap);
        return commonRepository.companyMutableData;
    }

    public LiveData<List<CompanyStoreDataModel>> getCompanyStoreList(HashMap<String, String> hashMap) {
        commonRepository.getCompanyStoreList(hashMap);
        return commonRepository.companyStoreMutableData;
    }

    public void createTicket(HashMap<String, Object> hashMap) {
        ticketRepository.createTicket(hashMap, apiStatusListener);
    }

    public void getPendingTicket() {
        if (companyId == null) {
            companyId = "All";
        }
        if (storeId == null) {
            storeId = "All";
        }
        if (viewType == null) {
            viewType = "0";
        }
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        hashmap.put("CustCode", companyId);
        hashmap.put("CustStoreID", storeId);
        hashmap.put("Others", viewType);
        ticketRepository.getPendingTicket(hashmap, apiStatusListener);
    }

    public void getTicketAttachmentList(int ticketNo) {
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("TicketNo", ticketNo);
        ticketRepository.getTicketAttachment(hashmap, apiStatusListener);
    }

    public void getTicketDetails(int ticketNo) {
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("TicketNo", ticketNo);
        ticketRepository.getTicketDetails(hashmap, apiStatusListener);
    }

    public void resolvedTicket(int ticketNo, String custCode) {
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("CustCode", custCode);
        hashmap.put("TicketNo", ticketNo);
        hashmap.put("GPS", "0.0");
        hashmap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        hashmap.put("UserName", preferenceHandler.getUser_name());
        hashmap.put("Resolve", true);
        hashmap.put("Details", "Sorted, Please check");
        ticketRepository.resolvedTicket(hashmap, apiStatusListener);
    }

    public void ticketRespond(int ticketNo, String custCode) {
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("CustCode", custCode);
        hashmap.put("TicketNo", ticketNo);
        hashmap.put("GPS", "0.0");
        hashmap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        hashmap.put("UserName", preferenceHandler.getUser_name());
        hashmap.put("Resolve", false);
        hashmap.put("Details", messageText.getValue());
        ticketRepository.resolvedTicket(hashmap, apiStatusListener);
    }

    public void deleteAttachmentFile(int ticketNo, String fileName, int entryNo) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("TicketNo", ticketNo);
        hashMap.put("FileName", fileName);
        hashMap.put("EntryNo", entryNo);
        ticketRepository.deleteTicket(hashMap, apiStatusListener);
    }

    public void editTicket(HashMap<String, Object> hashMap) {
        ticketRepository.editTicket(hashMap, apiStatusListener);
    }

    public void getHistoryTicket() {
        if (companyId == null) {
            companyId = "All";
        }
        if (storeId == null) {
            storeId = "All";
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        hashMap.put("CustCode", companyId);
        hashMap.put("CustStoreID", storeId);
        hashMap.put("FromDate", fromDate);
        hashMap.put("ToDate", toDate);
        ticketRepository.getHistoryTicket(hashMap, apiStatusListener);
    }

    public void reverseTicket(String custCode, int ticketNo){
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("CustCode", custCode);
        hashmap.put("TicketNo", ticketNo);
        hashmap.put("GPS", "0.0");
        hashmap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        hashmap.put("UserName", preferenceHandler.getUser_name());
        ticketRepository.reverseTicket(hashmap, apiStatusListener);
    }
}
