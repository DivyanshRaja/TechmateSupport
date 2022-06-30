package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.CommonActivity;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.ForgotPasswordDialogBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.AuthVIewModel;

import java.util.List;


public class ForgotPasswordDialog extends DialogFragment implements ApiStatusListener {
    ForgotPasswordDialogBinding forgotPasswordDialogBinding;
    AuthVIewModel authVIewModel;
    Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        forgotPasswordDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.forgot_password_dialog, null, false);
        dialog.setContentView(forgotPasswordDialogBinding.getRoot());
        authVIewModel = ViewModelProviders.of(this).get(AuthVIewModel.class);
        authVIewModel.apiStatusListener = this;
        forgotPasswordDialogBinding.setAuthViewModel(authVIewModel);
        forgotPasswordDialogBinding.setLifecycleOwner(this);
        context = requireContext();

        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        return dialog;
    }

    @Override
    public int getTheme() {
        return R.style.DialogStyleTheme;
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Method call to set copyright message in footer */
        CommonUtils.poweredByMessage(forgotPasswordDialogBinding.poweredByTv);
    }

    @Override
    public void onRequestStart() {
        forgotPasswordDialogBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        forgotPasswordDialogBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            CommonUtils.showAlertDialog(result, context, "success");
            authVIewModel.userName.setValue("");
        } else {
            CommonUtils.showAlertDialog(result, context, "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        forgotPasswordDialogBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, context, "");
    }
}
