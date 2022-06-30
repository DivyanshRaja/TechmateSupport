package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.FragmentChangePasswordBinding;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.viewmodel.AuthVIewModel;

import java.util.Objects;

public class ChangePasswordDialog extends DialogFragment implements OnClickHandlerInterface {
    FragmentChangePasswordBinding changePasswordBinding;
    AuthVIewModel authVIewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        changePasswordBinding = FragmentChangePasswordBinding.inflate(getLayoutInflater());
        View v = changePasswordBinding.getRoot();
        authVIewModel = ViewModelProviders.of(this).get(AuthVIewModel.class);
        changePasswordBinding.setOnCclickInterface(this);
        changePasswordBinding.setAuthViewModel(authVIewModel);
        changePasswordBinding.setLifecycleOwner(this);
        changePasswordBinding.executePendingBindings();

        dialog.setContentView(v);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getTheme() {
        return R.style.CenterDialogStyleTheme;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.closeDialogBtn) {
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }
}
