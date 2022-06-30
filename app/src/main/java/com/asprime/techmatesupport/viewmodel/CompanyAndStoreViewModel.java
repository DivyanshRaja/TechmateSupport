package com.asprime.techmatesupport.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.repository.CommonRepository;
import com.asprime.techmatesupport.repository.CompanyAndStoreRepository;
import com.asprime.techmatesupport.utils.PreferenceHandler;

import java.util.HashMap;
import java.util.List;

public class CompanyAndStoreViewModel extends AndroidViewModel {
    PreferenceHandler preferenceHandler;
    CommonRepository commonRepository;
    CompanyAndStoreRepository companyAndStoreRepository;
    public String custCode;
    public ApiStatusListener statusListener;

    public CompanyAndStoreViewModel(@NonNull Application application) {
        super(application);
        preferenceHandler = new PreferenceHandler(application.getApplicationContext());
        commonRepository = new CommonRepository(application.getApplicationContext());
        companyAndStoreRepository = new CompanyAndStoreRepository(application.getApplicationContext());
    }


    public LiveData<List<CustomerSetModel>> getAllCustomerSet() {
        HashMap<String, Object> hashMap = new HashMap<>();
        commonRepository.getAllowCustomerSet(hashMap);
        return commonRepository.customerSetMutableData;
    }

    public LiveData<List<SMUserDataModel>> getSMUSerData(HashMap<String, String> hashmap) {
        commonRepository.getSMUser(hashmap);
        return commonRepository.smUserMutableData;
    }

    public void getCustomerSetList(){
        HashMap<String, Object> hashMap = new HashMap<>();
        companyAndStoreRepository.getCompanyList(hashMap, statusListener);
    }

    public void getStoreSetByCustomer() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        companyAndStoreRepository.getStoreSetByCustomer(hashMap, statusListener);
    }

    public void addEditCompany(HashMap<String, Object> hashMap){
        companyAndStoreRepository.addEditCompany(hashMap, statusListener);
    }

    public void allowCompany(HashMap<String, Object> hashMap) {
        companyAndStoreRepository.allowCompany(hashMap, statusListener);
    }

    public void updateStoreDetails(HashMap<String, Object> hashMap) {
        companyAndStoreRepository.updateStoreDetails(hashMap, statusListener);
    }
}
