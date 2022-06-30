package com.asprime.techmatesupport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.asprime.techmatesupport.databinding.ActivityLoginBinding;
import com.asprime.techmatesupport.dialogs.ForgotPasswordDialog;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.AuthVIewModel;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements ApiStatusListener, OnClickHandlerInterface {
    private ActivityLoginBinding activityLoginBinding;
    private AuthVIewModel authVIewModel;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Data Binding & attach view model start */
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        authVIewModel = ViewModelProviders.of(this).get(AuthVIewModel.class);
        activityLoginBinding.setAuthViewModel(authVIewModel);
        activityLoginBinding.setOnClickHandle(this);
        activityLoginBinding.setLifecycleOwner(this);
        authVIewModel.apiStatusListener = this;
        activityLoginBinding.executePendingBindings();
        /* Data Binding & attach view model end */

        /* To change status bar color*/
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        /* Method call to set copyright message in footer */
        CommonUtils.poweredByMessage(activityLoginBinding.poweredByTv);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        showForgotPasswordDialog();
    }

    @Override
    public void onRequestStart() {
        activityLoginBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        activityLoginBinding.setProgressBarVisibility(false);
        if (statusCode == ApiStatusCode.SUCCESS_CODE) {
            startActivity(new Intent(context, CommonActivity.class));
            finish();
        } else {
            CommonUtils.showAlertDialog(result, context, "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        activityLoginBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, context, "");
    }

    /* Show dialog for forgot password */
    private void showForgotPasswordDialog() {
        ForgotPasswordDialog dialogFragment = new ForgotPasswordDialog();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("forgetDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "forgetDialog");
    }

}