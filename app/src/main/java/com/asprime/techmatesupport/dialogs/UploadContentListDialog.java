package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.EditDeviceDialogLayoutBinding;
import com.asprime.techmatesupport.databinding.UploadContentListDialogBinding;
import com.asprime.techmatesupport.listners.RefreshListData;

import java.util.Objects;

public class UploadContentListDialog extends DialogFragment implements View.OnClickListener {
    UploadContentListDialogBinding uploadContentListDialogBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey("deviceName")) {

            }
        }
    }


    public static UploadContentListDialog getInstance() {
        return new UploadContentListDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        uploadContentListDialogBinding = UploadContentListDialogBinding.inflate(getLayoutInflater());
        View v = uploadContentListDialogBinding.getRoot();
        uploadContentListDialogBinding.setLifecycleOwner(this);

        dialog.setContentView(v);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
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

    }
}
