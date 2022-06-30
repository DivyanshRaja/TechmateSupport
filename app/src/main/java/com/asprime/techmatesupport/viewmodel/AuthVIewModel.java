package com.asprime.techmatesupport.viewmodel;

import android.app.Application;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.repository.AuthRepository;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import java.util.HashMap;

public class AuthVIewModel extends AndroidViewModel {
    public ApiStatusListener apiStatusListener;
    AuthRepository authRepository;
    PreferenceHandler preferenceHandler;
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> userPassword = new MutableLiveData<>();
    public ObservableField<String> userNameError = new ObservableField<>();
    public ObservableField<String> userPasswordError = new ObservableField<>();

    public AuthVIewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        preferenceHandler = new PreferenceHandler(application.getApplicationContext());
    }

    public void loginClick() {
        if(userName.getValue() == null) {
            userNameError.set("Enter Username");
        } else if(userPassword.getValue() == null) {
            userPasswordError.set("Enter Password");
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("UserName", userName.getValue());
            hashMap.put("Password", userPassword.getValue());
            hashMap.put("Token", preferenceHandler.getUser_firebase_token());
            authRepository.userLogin(hashMap, apiStatusListener);
        }
    }

    public void forgotPasswordClick() {
        if(userName.getValue() == null) {
            userNameError.set("Enter Username");
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("UserName", userName.getValue());
            authRepository.userForgotPassword(hashMap, apiStatusListener);
        }
    }


    @BindingAdapter({"editTextError"})
    public static void editTextError(EditText et, String editTextError) {
        if (editTextError != null && editTextError.length() > 0) {
            et.setError(editTextError);
            et.requestFocus();
        }
    }
}
