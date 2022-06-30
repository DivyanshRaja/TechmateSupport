package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.EditDeviceDialogLayoutBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.asprime.techmatesupport.viewmodel.DeviceViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddEditDeviceDialog extends DialogFragment implements OnClickHandlerInterface, ApiStatusListener {
    EditDeviceDialogLayoutBinding editDeviceDialogLayoutBinding;
    String deviceName, deviceIp, storeName, storeId, custCode;
    int deviceId;
    static RefreshListData refreshListData;
    DeviceViewModel deviceViewModel;
    static List<CompanyStoreDataModel> companyStoreDataModel;
    PreferenceHandler preferenceHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey("deviceName")) {
                deviceName = getArguments().getString("deviceName");
                deviceIp = getArguments().getString("deviceIp");
                storeName = getArguments().getString("StoreName");
                deviceId = Integer.parseInt(getArguments().getString("deviceID"));
            } else if(getArguments().containsKey("custCode")){
                custCode = getArguments().getString("custCode");
            }
        }
    }

    public static AddEditDeviceDialog getInstance(RefreshListData refreshListData, List<CompanyStoreDataModel> companyStoreDataModel) {
        AddEditDeviceDialog.refreshListData = refreshListData;
        AddEditDeviceDialog.companyStoreDataModel = companyStoreDataModel;
        return new AddEditDeviceDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        editDeviceDialogLayoutBinding = EditDeviceDialogLayoutBinding.inflate(getLayoutInflater());
        deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        View v = editDeviceDialogLayoutBinding.getRoot();
        editDeviceDialogLayoutBinding.setLifecycleOwner(this);
        editDeviceDialogLayoutBinding.setOnClickInterface(this);
        editDeviceDialogLayoutBinding.setDeviceName(deviceName);
        editDeviceDialogLayoutBinding.setDeviceIp(deviceIp);
        deviceViewModel.statusListener = this;
        if (deviceName == null) {
            editDeviceDialogLayoutBinding.userFormIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_add_24, null));
            editDeviceDialogLayoutBinding.userFormTitle.setText("Add New Device");
            editDeviceDialogLayoutBinding.updateDeviceBtn.setText("Add");
        }
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
        setStoreSpinnerData();

        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart)
                        + source.subSequence(start, end)
                        + destTxt.substring(dend);
                if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (int i = 0; i < splits.length; i++) {
                        if (Integer.parseInt(splits[i]) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        };

        editDeviceDialogLayoutBinding.deviceIpEditText.addTextChangedListener(new TextWatcher(){
            boolean deleting = false;
            final int lastCount = 0;

            @Override
            public void afterTextChanged(Editable s) {
                if (!deleting) {
                    String working = s.toString();
                    String[] split = working.split("\\.");
                    String string = split[split.length - 1];
                    if (string.length() == 3) {
                        s.append('.');
                        return;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (lastCount < count) {
                    deleting = false;
                }
                else {
                    deleting = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing happens here
            }
        });
        editDeviceDialogLayoutBinding.deviceIpEditText.setFilters(filters);
    }

    @Override
    public int getTheme() {
        return R.style.CenterDialogStyleTheme;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.closeEditDeviceDialogBtn) {
            Objects.requireNonNull(getDialog()).dismiss();
        } else {
            if (deviceName != null) {
                if (editDeviceDialogLayoutBinding.deviceEditText.length() == 0) {
                    editDeviceDialogLayoutBinding.deviceEditText.setError("Enter Device Name");
                    editDeviceDialogLayoutBinding.deviceEditText.requestFocus();
                } else if (editDeviceDialogLayoutBinding.deviceIpEditText.length() == 0) {
                    editDeviceDialogLayoutBinding.deviceIpEditText.setError("Enter Device Ip");
                    editDeviceDialogLayoutBinding.deviceIpEditText.requestFocus();
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
                    hashMap.put("DeviceID", deviceId);
                    hashMap.put("CustStoreID", storeId);
                    hashMap.put("DeviceName", Objects.requireNonNull(editDeviceDialogLayoutBinding.deviceEditText.getText()).toString());
                    hashMap.put("DeviceIP", Objects.requireNonNull(editDeviceDialogLayoutBinding.deviceIpEditText.getText()).toString());
                    deviceViewModel.updateDevice(hashMap);
                }
            } else {
                if (editDeviceDialogLayoutBinding.deviceEditText.length() == 0) {
                    editDeviceDialogLayoutBinding.deviceEditText.setError("Enter Device Name");
                    editDeviceDialogLayoutBinding.deviceEditText.requestFocus();
                } else if (editDeviceDialogLayoutBinding.deviceIpEditText.length() == 0) {
                    editDeviceDialogLayoutBinding.deviceIpEditText.setError("Enter Device Ip");
                    editDeviceDialogLayoutBinding.deviceIpEditText.requestFocus();
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("CustCode", custCode);
                    hashMap.put("CustStoreID", storeId);
                    hashMap.put("DeviceName", Objects.requireNonNull(editDeviceDialogLayoutBinding.deviceEditText.getText()).toString());
                    hashMap.put("DeviceIP", Objects.requireNonNull(editDeviceDialogLayoutBinding.deviceIpEditText.getText()).toString());
                    deviceViewModel.addDevice(hashMap);
                }
            }
        }
    }

    private void setStoreSpinnerData() {
        ArrayAdapter<CompanyStoreDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, AddEditDeviceDialog.companyStoreDataModel);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDeviceDialogLayoutBinding.storeSpinner.setAdapter(adapter);
        int pos = 0;
        for (CompanyStoreDataModel storeSpinnerData1 : AddEditDeviceDialog.companyStoreDataModel) {
            if (storeSpinnerData1.getStoreName().equals(storeName)) {
                editDeviceDialogLayoutBinding.storeSpinner.setSelection(pos);
            }
            pos++;
        }
        editDeviceDialogLayoutBinding.storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyStoreDataModel spinnerData = (CompanyStoreDataModel) adapterView.getSelectedItem();
                storeId = spinnerData.getCustStoreID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onRequestStart() {
        editDeviceDialogLayoutBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        editDeviceDialogLayoutBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.ACTION_SUCCESS_CODE){
            refreshListData.onRefresh();
            CommonUtils.showAlertDialog(result, requireContext(), "success");
            Objects.requireNonNull(getDialog()).dismiss();
        } else if(statusCode == ApiStatusCode.ACTION_ERROR_CODE){
            CommonUtils.showAlertDialog(result, requireContext(), "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        editDeviceDialogLayoutBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
