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
import com.asprime.techmatesupport.repository.UserRepository;
import com.asprime.techmatesupport.utils.PreferenceHandler;

import java.util.HashMap;
import java.util.List;

public class UserViewModel extends AndroidViewModel {
    PreferenceHandler preferenceHandler;
    CommonRepository commonRepository;
    UserRepository userRepository;
    public String custCode;
    public ApiStatusListener statusListener;

    public UserViewModel(@NonNull Application application) {
        super(application);
        preferenceHandler = new PreferenceHandler(application.getApplicationContext());
        commonRepository = new CommonRepository(application.getApplicationContext());
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public LiveData<List<CompanyDataModel>> getAllowedCompanyData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
        commonRepository.getAllowCompanyData(hashMap);
        return commonRepository.companyMutableData;
    }

    public void getClientUserList(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        hashMap.put("UserType", "Client");
        userRepository.getUserList(hashMap, statusListener);
    }

    public void getSupportUserList() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        hashMap.put("UserType", "Support");
        userRepository.getUserList(hashMap, statusListener);
    }

    public void getSuperUserList() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        hashMap.put("UserType", "Client");
        userRepository.getSuperUserList(hashMap, statusListener);
    }
}
