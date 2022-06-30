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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.MobileDeviceAdapter;
import com.asprime.techmatesupport.databinding.MobileAppActivationFragmentBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.model.MobileAppDeviceModel;
import com.asprime.techmatesupport.model.SoftwareModelData;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.MobileAppActivationViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileAppDeviceFragment extends Fragment implements View.OnClickListener, RefreshListData, ApiStatusListener {
    MobileAppActivationFragmentBinding mobileAppActivationFragmentBinding;
    MobileDeviceAdapter mobileDeviceAdapter;
    MobileAppActivationViewModel mobileAppActivationViewModel;
    String flag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mobileAppActivationFragmentBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.mobile_app_activation_fragment, container, false);
        mobileAppActivationViewModel = ViewModelProviders.of(this).get(MobileAppActivationViewModel.class);
        mobileAppActivationViewModel.statusListener = this;
        return mobileAppActivationFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mobileAppActivationFragmentBinding.refreshBtn.setOnClickListener(this);
        assert getArguments() != null;
        flag = getArguments().getString("flag");
    }

    @Override
    public void onResume() {
        super.onResume();

        setRecyclerData();

        mobileAppActivationViewModel.getAllCustomerSet().observe(getViewLifecycleOwner(), companyDataModels -> {
            if (companyDataModels != null && companyDataModels.size() > 0) {
                setCompanySpinnerData(companyDataModels);
            } else {
                List<CustomerSetModel> companyDataModels1 = new ArrayList<>();
                setCompanySpinnerData(companyDataModels1);
            }
        });

        mobileAppActivationFragmentBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mobileDeviceAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setCompanySpinnerData(List<CustomerSetModel> storeSpinnerDataList) {
        CustomerSetModel customerSetModel = new CustomerSetModel();
        customerSetModel.setCustCode("");
        customerSetModel.setCustName("Not Activated");
        storeSpinnerDataList.add(0, customerSetModel);
        ArrayAdapter<CustomerSetModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobileAppActivationFragmentBinding.companySpinner.setAdapter(adapter);
        mobileAppActivationFragmentBinding.companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerSetModel spinnerData = (CustomerSetModel) adapterView.getSelectedItem();
                mobileAppActivationViewModel.custCode = spinnerData.getCustCode();

                mobileAppActivationViewModel.getSoftwareData().observe(getViewLifecycleOwner(), uList -> {
                    if (uList != null && uList.size() > 0) {
                        setSoftwareSpinnerData(uList);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSoftwareSpinnerData(List<SoftwareModelData> softwareSpinnerData) {
        SoftwareModelData softwareModelData = new SoftwareModelData();
        softwareModelData.setSoftID("-1");
        softwareModelData.setSoftName("All");
        softwareSpinnerData.add(0, softwareModelData);
        // Create the instance of ArrayAdapter
        ArrayAdapter<SoftwareModelData> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, softwareSpinnerData);
        /* set simple layout resource file
        for each item of spinner */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobileAppActivationFragmentBinding.softwareSpinner.setAdapter(adapter);
        mobileAppActivationFragmentBinding.softwareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SoftwareModelData softwareSpinnerData = (SoftwareModelData) adapterView.getSelectedItem();
                mobileAppActivationViewModel.softCode = String.valueOf(softwareSpinnerData.getSoftID());
                mobileAppActivationViewModel.getMobileAppDeviceList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRecyclerData() {
        mobileDeviceAdapter = new MobileDeviceAdapter(requireContext(), new ArrayList<>(), (position, flag, obj) -> {

        }, flag);
        mobileAppActivationFragmentBinding.setMobileDeviceAdapter(mobileDeviceAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mobileAppActivationFragmentBinding.recyclerView.addItemDecoration(decoration);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onRequestStart() {
        mobileAppActivationFragmentBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        mobileAppActivationFragmentBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            mobileAppActivationFragmentBinding.setVisibleState(false);
            List<MobileAppDeviceModel> mobileAppDeviceModels = (List<MobileAppDeviceModel>) obj;
            mobileDeviceAdapter.updateData(mobileAppDeviceModels);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            mobileAppActivationFragmentBinding.setVisibleState(true);
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        mobileAppActivationFragmentBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
