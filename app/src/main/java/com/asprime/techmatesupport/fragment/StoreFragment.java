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

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.StoreAdapter;
import com.asprime.techmatesupport.databinding.StoreFragmentBinding;
import com.asprime.techmatesupport.dialogs.AddEditStoreDialog;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.CompanyAndStoreViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreFragment extends Fragment implements OnClickHandlerInterface, RefreshListData, ApiStatusListener {
    StoreFragmentBinding storeFragmentBinding;
    StoreAdapter storeAdapter;
    CompanyAndStoreViewModel companyAndStoreViewModel;
    String selectedStoreId, selectedCustName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        storeFragmentBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.store_fragment, container, false);
        companyAndStoreViewModel = ViewModelProviders.of(this).get(CompanyAndStoreViewModel.class);
        storeFragmentBinding.setOnClickInterface(this);
        companyAndStoreViewModel.statusListener = this;
        return storeFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        storeFragmentBinding.setProgressBarVisibility(true);
        setRecyclerData();

        companyAndStoreViewModel.getAllCustomerSet().observe(getViewLifecycleOwner(), companyDataModels -> {
            if (companyDataModels != null && companyDataModels.size() > 0) {
                setCompanySpinnerData(companyDataModels);
            } else {
                List<CustomerSetModel> companyDataModels1 = new ArrayList<>();
                setCompanySpinnerData(companyDataModels1);
            }
        });

        storeFragmentBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                storeAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setRecyclerData() {
        storeAdapter = new StoreAdapter(requireContext(), new ArrayList<>(), (position, flag, dataModel) -> {
            if(flag.contains("editClick")){
                addEditStoreDialog("editDetails", (CompanyStoreDataModel) dataModel);
            } else if(flag.contains("deleteClick")){
                CompanyStoreDataModel companyStoreDataModel = (CompanyStoreDataModel) dataModel;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("CustCode", selectedStoreId);
                hashMap.put("CustStoreID", companyStoreDataModel.getCustStoreID());
                hashMap.put("StoreName", companyStoreDataModel.getStoreName());
                hashMap.put("Edit", false);
                hashMap.put("Delete", true);
                companyAndStoreViewModel.updateStoreDetails(hashMap);
            }
        });
        storeFragmentBinding.setStoreAdapter(storeAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.refreshBtn){
            onRefresh();
        } else if(id == R.id.addStoreBtn){
            addEditStoreDialog("addNew", null);
        }
    }

    private void setCompanySpinnerData(List<CustomerSetModel> storeSpinnerDataList) {
        ArrayAdapter<CustomerSetModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeFragmentBinding.companySpinner.setAdapter(adapter);
        storeFragmentBinding.companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerSetModel spinnerData = (CustomerSetModel) adapterView.getSelectedItem();
                selectedStoreId = spinnerData.getCustCode();
                selectedCustName = spinnerData.getCustCode() +"-"+spinnerData.getCustName();
                companyAndStoreViewModel.custCode = selectedStoreId;
                companyAndStoreViewModel.getStoreSetByCustomer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onRefresh() {
        companyAndStoreViewModel.getStoreSetByCustomer();
    }

    @Override
    public void onRequestStart() {
        storeFragmentBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        storeFragmentBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            storeFragmentBinding.setNoFoundVisible(false);
            storeAdapter.updateData((List<CompanyStoreDataModel>) obj);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            storeFragmentBinding.setNoFoundVisible(true);
        } else if(statusCode == ApiStatusCode.ACTION_SUCCESS_CODE){
            onRefresh();
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        storeFragmentBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }

    public void addEditStoreDialog(String type, CompanyStoreDataModel companyStoreDataModel) {
        AddEditStoreDialog dialogFragment = AddEditStoreDialog.getInstance(this);
        Bundle args = new Bundle();
        args.putString("f_type", type);
        args.putString("custCode", selectedStoreId);
        args.putString("custFullName", selectedCustName);
        if(companyStoreDataModel != null) {
            args.putString("storeName", companyStoreDataModel.getStoreName());
            args.putString("storeCode", companyStoreDataModel.getCustStoreID());
        }
        dialogFragment.setArguments(args);
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("addUser");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "AddEditStoreDialog");
    }
}
