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

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.UserListAdapter;
import com.asprime.techmatesupport.databinding.FragmentUsersBinding;
import com.asprime.techmatesupport.dialogs.UserOptionsDialogBottomFragment;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.UserListModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientUserFragment extends Fragment implements OnClickHandlerInterface, RefreshListData, ApiStatusListener {
    FragmentUsersBinding fragmentUsersBinding;
    UserListAdapter userListAdapter;
    UserViewModel userViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentUsersBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_users, container, false);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        fragmentUsersBinding.setOnClickInterface(this);
        userViewModel.statusListener = this;
        return fragmentUsersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerData();

        userViewModel.getAllowedCompanyData().observe(getViewLifecycleOwner(), companyDataModels -> {
            CompanyDataModel companyDataModel = new CompanyDataModel();
            companyDataModel.setCustCode("ALL");
            companyDataModel.setCustName("ALL");
            if (companyDataModels != null && companyDataModels.size() > 0) {
                companyDataModels.add(0, companyDataModel);
                setCompanySpinnerData(companyDataModels);
            } else {
                List<CompanyDataModel> companyDataModels1 = new ArrayList<>();
                companyDataModels1.add(0, companyDataModel);
                setCompanySpinnerData(companyDataModels1);
            }
        });

        fragmentUsersBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userListAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setRecyclerData() {
        userListAdapter = new UserListAdapter(requireContext(), new ArrayList<>(), (position, flag, dataModel) -> showBottomDialog(dataModel));
        fragmentUsersBinding.setUserListAdapter(userListAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.refreshBtn){
            onRefresh();
        }
    }

    private void setCompanySpinnerData(List<CompanyDataModel> storeSpinnerDataList) {
        ArrayAdapter<CompanyDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentUsersBinding.companySpinner.setAdapter(adapter);
        fragmentUsersBinding.companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyDataModel spinnerData = (CompanyDataModel) adapterView.getSelectedItem();
                String selectedStoreId = spinnerData.getCustCode();
                userViewModel.custCode = selectedStoreId;
                userViewModel.getClientUserList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showBottomDialog(Object obj) {
        UserOptionsDialogBottomFragment userOptionsDialogBottomFragment = UserOptionsDialogBottomFragment.newInstance(this);
        Bundle bundle = new Bundle();
        UserListModel userListModel = (UserListModel) obj;
        bundle.putString("popUpFor", "clientUser");
        bundle.putInt("userId", Integer.parseInt(userListModel.getUserID()));
        bundle.putString("userName", userListModel.getUserName());
        bundle.putString("emailAddress", userListModel.getEmailID());
        bundle.putBoolean("userStatus", Boolean.parseBoolean(userListModel.getAllow()));
        userOptionsDialogBottomFragment.setArguments(bundle);
        userOptionsDialogBottomFragment.show(requireActivity().getSupportFragmentManager(), "USerOptionFragment");
    }

    @Override
    public void onRefresh() {
        userViewModel.getClientUserList();
    }

    @Override
    public void onRequestStart() {
        fragmentUsersBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        fragmentUsersBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            fragmentUsersBinding.setNoFoundVisible(false);
            userListAdapter.updateData((ArrayList<UserListModel>) obj);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            fragmentUsersBinding.setNoFoundVisible(true);
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        fragmentUsersBinding.setProgressBarVisibility(false);
        fragmentUsersBinding.setNoFoundVisible(true);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }

//    /**
//     * @param type always "add_user"
//     */
//    public void showAddUserFragment(String type) {
//        AddEditUserDialog.registerRefreshListener(this);
//        AddEditUserDialog dialogFragment = new AddEditUserDialog();
//        Bundle args = new Bundle();
//        args.putString("f_type", type);
//        dialogFragment.setArguments(args);
//        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
//        Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("addUser");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//        dialogFragment.show(ft, "addUser");
//    }
}
