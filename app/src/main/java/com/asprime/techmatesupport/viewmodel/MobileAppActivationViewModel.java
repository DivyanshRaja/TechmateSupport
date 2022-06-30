package com.asprime.techmatesupport.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.model.SoftwareModelData;
import com.asprime.techmatesupport.repository.CommonRepository;
import com.asprime.techmatesupport.repository.CompanyAndStoreRepository;
import com.asprime.techmatesupport.repository.MobileAppActivationRepository;
import com.asprime.techmatesupport.utils.PreferenceHandler;

import java.util.HashMap;
import java.util.List;

public class MobileAppActivationViewModel extends AndroidViewModel {
    PreferenceHandler preferenceHandler;
    CommonRepository commonRepository;
    MobileAppActivationRepository mobileAppActivationRepository;
    public String custCode, softCode;
    public ApiStatusListener statusListener;

    public MobileAppActivationViewModel(@NonNull Application application) {
        super(application);
        preferenceHandler = new PreferenceHandler(application.getApplicationContext());
        commonRepository = new CommonRepository(application.getApplicationContext());
        mobileAppActivationRepository = new MobileAppActivationRepository(application.getApplicationContext());
    }

    public LiveData<List<CustomerSetModel>> getAllCustomerSet() {
        HashMap<String, Object> hashMap = new HashMap<>();
        commonRepository.getAllowCustomerSet(hashMap);
        return commonRepository.customerSetMutableData;
    }

    public LiveData<List<SoftwareModelData>> getSoftwareData() {
        commonRepository.getAllSoftwareList();
        return commonRepository.softwareMutableData;
    }

    public void getMobileAppDeviceList(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        hashMap.put("SoftType", softCode);
        mobileAppActivationRepository.getAndroidActivationDevice(hashMap, statusListener);
    }
}
