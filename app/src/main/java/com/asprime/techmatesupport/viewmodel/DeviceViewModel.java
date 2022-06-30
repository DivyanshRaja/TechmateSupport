package com.asprime.techmatesupport.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.repository.CommonRepository;
import com.asprime.techmatesupport.repository.DeviceRepository;
import com.asprime.techmatesupport.utils.PreferenceHandler;

import java.util.HashMap;
import java.util.List;

public class DeviceViewModel extends AndroidViewModel {
    PreferenceHandler preferenceHandler;
    CommonRepository commonRepository;
    DeviceRepository deviceRepository;
    public String companyId, storeId;
    public ApiStatusListener statusListener;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        preferenceHandler = new PreferenceHandler(application.getApplicationContext());
        commonRepository = new CommonRepository(application.getApplicationContext());
        deviceRepository = new DeviceRepository(application.getApplicationContext());
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

    public void getDeviceList() {
        if (companyId == null) {
            companyId = "All";
        }
        if (storeId == null) {
            storeId = "All";
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", companyId);
        hashMap.put("CustStoreID", storeId);
        hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        deviceRepository.getDeviceList(hashMap, statusListener);
    }

    public void updateDevice(HashMap<String, Object> hashMap){
        deviceRepository.updateDevice(hashMap, statusListener);
    }

    public void addDevice(HashMap<String, Object> hashMap){
        deviceRepository.addDevice(hashMap, statusListener);
    }
}
