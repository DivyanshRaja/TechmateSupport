package com.asprime.techmatesupport.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.CompanyAdapter;
import com.asprime.techmatesupport.databinding.FragmentCompaniesBinding;
import com.asprime.techmatesupport.dialogs.AddEditCompaniesDialog;
import com.asprime.techmatesupport.dialogs.UserOptionsDialogBottomFragment;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.model.UserListModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.CompanyAndStoreViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompaniesFragment extends Fragment implements OnClickHandlerInterface, RefreshListData, ApiStatusListener {
    FragmentCompaniesBinding fragmentCompaniesBinding;
    CompanyAdapter companyAdapter;
    CompanyAndStoreViewModel companyAndStoreViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCompaniesBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_companies, container, false);
        companyAndStoreViewModel = ViewModelProviders.of(this).get(CompanyAndStoreViewModel.class);
        fragmentCompaniesBinding.setOnClickInterface(this);
        companyAndStoreViewModel.statusListener = this;
        return fragmentCompaniesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerData();

        companyAndStoreViewModel.getCustomerSetList();

        fragmentCompaniesBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                companyAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setRecyclerData() {
        companyAdapter = new CompanyAdapter(requireContext(), new ArrayList<>(), (position, flag, dataModel) -> {
            if(flag.contains("editClick")){
                showAddCompanyDialog("editDetails", (CustomerSetModel) dataModel);
            } else if(flag.contains("userBlockClick")){
                updateStatus(dataModel);
            }
        });
        fragmentCompaniesBinding.setCompanyAdapter(companyAdapter);
    }

    private void updateStatus(Object dataModel) {
        CustomerSetModel customerSetModel = (CustomerSetModel) dataModel;
        boolean allow = !customerSetModel.isAllow();
        String custCode = customerSetModel.getCustCode();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        hashMap.put("Allow", allow);
        companyAndStoreViewModel.allowCompany(hashMap);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.refreshBtn){
            onRefresh();
        } else if(id == R.id.addCompanyBtn){
            showAddCompanyDialog("addNew", null);
        }
    }

    @Override
    public void onRefresh() {
        companyAndStoreViewModel.getCustomerSetList();
    }

    @Override
    public void onRequestStart() {
        fragmentCompaniesBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        fragmentCompaniesBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            fragmentCompaniesBinding.setNoFoundVisible(false);
            companyAdapter.updateData((List<CustomerSetModel>) obj);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            fragmentCompaniesBinding.setNoFoundVisible(true);
        } else if(statusCode == ApiStatusCode.ACTION_SUCCESS_CODE){
            onRefresh();
        } else if(statusCode == ApiStatusCode.ACTION_ERROR_CODE){
            CommonUtils.showAlertDialog(result, requireActivity(), "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        fragmentCompaniesBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireActivity(), "");
    }

    public void showAddCompanyDialog(String type, CustomerSetModel customerSetModel) {
        AddEditCompaniesDialog dialogFragment = AddEditCompaniesDialog.getInstance(this);
        Bundle args = new Bundle();
        args.putString("f_type", type);
        if(customerSetModel != null) {
            args.putString("cmpName", customerSetModel.getCustName());
            args.putString("cmpCode", customerSetModel.getCustCode());
            args.putString("userName", customerSetModel.getUserName());
        }
        dialogFragment.setArguments(args);
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("addUser");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "AddEditCompaniesDialog");
    }
}
