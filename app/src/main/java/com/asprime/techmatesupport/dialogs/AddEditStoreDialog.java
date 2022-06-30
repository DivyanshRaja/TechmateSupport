package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.AddEditCompanyDialogBinding;
import com.asprime.techmatesupport.databinding.AddEditStoreDialogBinding;
import com.asprime.techmatesupport.databinding.EditDeviceDialogLayoutBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.asprime.techmatesupport.viewmodel.CompanyAndStoreViewModel;
import com.asprime.techmatesupport.viewmodel.DeviceViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddEditStoreDialog extends DialogFragment implements OnClickHandlerInterface, ApiStatusListener {
    AddEditStoreDialogBinding addEditStoreDialogBinding;
    static RefreshListData refreshListData;
    PreferenceHandler preferenceHandler;
    CompanyAndStoreViewModel companyAndStoreViewModel;
    String flag;
    String custCode, custFullName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("f_type")) {
            flag = getArguments().getString("f_type");
            custCode = getArguments().getString("custCode");
            custFullName = getArguments().getString("custFullName");
        }
    }

    public static AddEditStoreDialog getInstance(RefreshListData refreshListData) {
        AddEditStoreDialog.refreshListData = refreshListData;
        return new AddEditStoreDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        addEditStoreDialogBinding = AddEditStoreDialogBinding.inflate(getLayoutInflater());
        companyAndStoreViewModel = ViewModelProviders.of(this).get(CompanyAndStoreViewModel.class);
        View v = addEditStoreDialogBinding.getRoot();
        companyAndStoreViewModel.statusListener = this;
        addEditStoreDialogBinding.setLifecycleOwner(this);
        addEditStoreDialogBinding.setOnClickInterface(this);

        /**
         * To check dialog type add new or edit details
         */
        if (flag.equalsIgnoreCase("addNew")) {
            addEditStoreDialogBinding.setFormTitle("Add Stores");
            addEditStoreDialogBinding.companyFormIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_add_24, null));
        } else {
            assert getArguments() != null;
            addEditStoreDialogBinding.setStoreName(getArguments().getString("storeName"));
            addEditStoreDialogBinding.setStoreCode(getArguments().getString("storeCode"));
            addEditStoreDialogBinding.storeCodeEditText.setClickable(false);
            addEditStoreDialogBinding.storeCodeEditText.setFocusable(false);
            addEditStoreDialogBinding.storeCodeEditText.setCursorVisible(false);
            addEditStoreDialogBinding.setFormTitle("Update Store Details");
            addEditStoreDialogBinding.companyFormIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_mode_edit_24, null));
        }
        addEditStoreDialogBinding.setCmpFullName(custFullName);
        /**
         * set content view
         */
        dialog.setContentView(v);
        preferenceHandler = new PreferenceHandler(requireContext());
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
        } else if (id == R.id.updateOrAddBtn){
            String storeName = addEditStoreDialogBinding.storeEditText.getText().toString();
            String storeCode = addEditStoreDialogBinding.storeCodeEditText.getText().toString();
            if(storeName.length() == 0){
                addEditStoreDialogBinding.storeEditText.setError("Enter Store Name");
                addEditStoreDialogBinding.storeEditText.requestFocus();
            } else if(storeCode.length() == 0){
                addEditStoreDialogBinding.storeCodeEditText.setError("Enter Store Code");
                addEditStoreDialogBinding.storeCodeEditText.requestFocus();
            } else{
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("CustCode", custCode);
                hashMap.put("CustStoreID", storeCode);
                hashMap.put("StoreName", storeName);
                hashMap.put("Edit", !flag.equalsIgnoreCase("addNew"));
                hashMap.put("Delete", false);
                companyAndStoreViewModel.updateStoreDetails(hashMap);
            }
        }
    }

    @Override
    public void onRequestStart() {
        addEditStoreDialogBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        addEditStoreDialogBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            refreshListData.onRefresh();
            CommonUtils.showAlertDialog(result, requireContext(), "success");
            Objects.requireNonNull(getDialog()).dismiss();
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            CommonUtils.showAlertDialog(result, requireContext(), "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        addEditStoreDialogBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
