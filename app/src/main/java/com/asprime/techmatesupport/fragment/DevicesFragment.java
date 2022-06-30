package com.asprime.techmatesupport.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.CustomerDeviceAdapter;
import com.asprime.techmatesupport.databinding.FragmentDevicesBinding;
import com.asprime.techmatesupport.dialogs.AddEditDeviceDialog;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.DeviceModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.DeviceViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DevicesFragment extends Fragment implements OnClickHandlerInterface, RefreshListData, ApiStatusListener {
    FragmentDevicesBinding fragmentDevicesBinding;
    CustomerDeviceAdapter customerDeviceAdapter;
    List<DeviceModel> deviceModelList;
    DeviceViewModel deviceViewModel;
    List<CompanyStoreDataModel> companyStoreDataModelToParse;
    String custCode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentDevicesBinding = DataBindingUtil.inflate(LayoutInflater.from(inflater.getContext()), R.layout.fragment_devices, container, false);
        deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        fragmentDevicesBinding.setClickHandler(this);
        fragmentDevicesBinding.setDeviceViewModel(deviceViewModel);
        deviceViewModel.statusListener = this;
        return fragmentDevicesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.addNewDevice) {
            if(companyStoreDataModelToParse.size() != 0) {
                showDeviceAddDialog();
            } else {
                CommonUtils.showAlertDialog("No store available to add device", requireContext(), "");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initRecyclerAdapter();

        deviceViewModel.getAllowedCompanyData().observe(getViewLifecycleOwner(), companyDataModels -> {
            if (companyDataModels != null && companyDataModels.size() > 0) {
                setCompanySpinnerData(companyDataModels);
            } else {
                setCompanySpinnerData(new ArrayList<>());
            }
        });

        fragmentDevicesBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                customerDeviceAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setCompanySpinnerData(List<CompanyDataModel> storeSpinnerDataList) {
        ArrayAdapter<CompanyDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentDevicesBinding.companySpinner.setAdapter(adapter);
        fragmentDevicesBinding.companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyDataModel spinnerData = (CompanyDataModel) adapterView.getSelectedItem();
                String selectedStoreId = spinnerData.getCustCode();
                custCode = selectedStoreId;
                deviceViewModel.companyId = selectedStoreId;

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("CustCode", selectedStoreId);
                deviceViewModel.getCompanyStoreList(hashMap).observe(getViewLifecycleOwner(), companyStoreDataModels -> {
                    if (companyStoreDataModels != null && companyStoreDataModels.size() > 0) {
                        setStoreSpinnerData(companyStoreDataModels);
                    } else {
                        setStoreSpinnerData(new ArrayList<>());
                        deviceViewModel.getDeviceList();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setStoreSpinnerData(List<CompanyStoreDataModel> companyStoreDataModels) {
        ArrayAdapter<CompanyStoreDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, companyStoreDataModels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentDevicesBinding.storeSpinner.setAdapter(adapter);
        fragmentDevicesBinding.storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyStoreDataModel spinnerData = (CompanyStoreDataModel) adapterView.getSelectedItem();
                deviceViewModel.storeId = spinnerData.getCustStoreID();
                deviceViewModel.getDeviceList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        companyStoreDataModelToParse = companyStoreDataModels;
    }

    private void initRecyclerAdapter() {
        customerDeviceAdapter = new CustomerDeviceAdapter(requireContext(), new ArrayList<>(), (position, flag, obj) -> {
            if(flag.contains("editDeviceList")) {
                showDeviceEditDialog(obj);
            }
        });
        fragmentDevicesBinding.setDeviceAdapter(customerDeviceAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        fragmentDevicesBinding.deviceRecycler.addItemDecoration(decoration);
    }

    private void showDeviceEditDialog(Object obj) {
        AddEditDeviceDialog dialogFragment = AddEditDeviceDialog.getInstance(this, companyStoreDataModelToParse);
        Bundle bundle = new Bundle();
        DeviceModel deviceModel = (DeviceModel) obj;
        bundle.putString("deviceName", deviceModel.getDeviceName());
        bundle.putString("StoreName", deviceModel.getStoreName());
        bundle.putString("deviceIp", deviceModel.getDeviceIP());
        bundle.putString("deviceID", deviceModel.getDeviceID());
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("EditDeviceDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "EditDeviceDialog");
    }

    private void showDeviceAddDialog() {
        AddEditDeviceDialog dialogFragment = AddEditDeviceDialog.getInstance(this, companyStoreDataModelToParse);
        Bundle bundle = new Bundle();
        bundle.putString("custCode", custCode);
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("EditDeviceDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "EditDeviceDialog");
    }

    @Override
    public void onRefresh() {
        deviceViewModel.getDeviceList();
    }

    @Override
    public void onRequestStart() {
        fragmentDevicesBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        fragmentDevicesBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            fragmentDevicesBinding.setNoFoundVisible(false);
            deviceModelList = (ArrayList<DeviceModel>) obj;
            customerDeviceAdapter.updateData(deviceModelList);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            fragmentDevicesBinding.setNoFoundVisible(true);
            customerDeviceAdapter.updateData(new ArrayList<>());
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        fragmentDevicesBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
