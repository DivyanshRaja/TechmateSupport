package com.asprime.techmatesupport.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.PendingTicketAdapter;
import com.asprime.techmatesupport.databinding.FragmentPendingTicketBinding;
import com.asprime.techmatesupport.dialogs.ActionBottomDialogFragment;
import com.asprime.techmatesupport.dialogs.EditTicketDialog;
import com.asprime.techmatesupport.dialogs.TicketRespondFragmentDialog;
import com.asprime.techmatesupport.dialogs.ViewAttachmentFragmentDialog;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.listners.TicketResolveListner;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PendingTicketFragment extends Fragment implements OnClickHandlerInterface, RefreshListData, ApiStatusListener, TicketResolveListner {
    FragmentPendingTicketBinding fragmentPendingTicketBinding;
    PendingTicketAdapter myRecyclerViewAdapter;
    TicketViewModel ticketViewModel;
    boolean isStartLoad = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentPendingTicketBinding = FragmentPendingTicketBinding.inflate(getLayoutInflater());
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        fragmentPendingTicketBinding.setOnCLickInterface(this);
        fragmentPendingTicketBinding.setTicketViewModel(ticketViewModel);
        ticketViewModel.apiStatusListener = this;
        return fragmentPendingTicketBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        ticketViewModel.getPendingTicket();

        ticketViewModel.getAllowedCompanyData().observe(getViewLifecycleOwner(), companyDataModels -> {
            CompanyDataModel companyDataModel = new CompanyDataModel();
            companyDataModel.setCustName("ALL");
            companyDataModel.setCustCode("ALL");
            if (companyDataModels != null && companyDataModels.size() > 0) {
                companyDataModels.add(0, companyDataModel);
                setCompanySpinnerData(companyDataModels);
            } else {
                List<CompanyDataModel> companyDataModels1 = new ArrayList<>();
                companyDataModels1.add(companyDataModel);
                setCompanySpinnerData(companyDataModels1);
            }
        });

        getViewTicketSpinnerData();

        setRecyclerViewData();

        fragmentPendingTicketBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                myRecyclerViewAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.filterBtn) {
            if (fragmentPendingTicketBinding.filterLayout.getVisibility() == View.VISIBLE) {
                fragmentPendingTicketBinding.filterLayout.setVisibility(View.GONE);
            } else {
                fragmentPendingTicketBinding.filterLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setCompanySpinnerData(List<CompanyDataModel> storeSpinnerDataList) {
        ArrayAdapter<CompanyDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentPendingTicketBinding.companySpinner.setAdapter(adapter);
        fragmentPendingTicketBinding.companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyDataModel spinnerData = (CompanyDataModel) adapterView.getSelectedItem();
                String selectedStoreId = spinnerData.getCustCode();
                ticketViewModel.companyId = selectedStoreId;

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("CustCode", selectedStoreId);

                ticketViewModel.getCompanyStoreList(hashMap).observe(getViewLifecycleOwner(), companyStoreDataModels -> {
                    CompanyStoreDataModel companyStoreDataModel = new CompanyStoreDataModel();
                    companyStoreDataModel.setStoreName("ALL");
                    companyStoreDataModel.setCustStoreID("ALL");
                    if (companyStoreDataModels != null && companyStoreDataModels.size() > 0) {
                        companyStoreDataModels.add(0, companyStoreDataModel);
                        setStoreSpinnerData(companyStoreDataModels);
                    } else {
                        List<CompanyStoreDataModel> companyStoreDataModelsList = new ArrayList<>();
                        companyStoreDataModelsList.add(companyStoreDataModel);
                        setStoreSpinnerData(companyStoreDataModelsList);
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
        fragmentPendingTicketBinding.storeSpinner.setAdapter(adapter);
        fragmentPendingTicketBinding.storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyStoreDataModel spinnerData = (CompanyStoreDataModel) adapterView.getSelectedItem();
                ticketViewModel.storeId = spinnerData.getCustStoreID();
                if(isStartLoad){
                   ticketViewModel.getPendingTicket();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getViewTicketSpinnerData() {
        fragmentPendingTicketBinding.viewTicketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals("Other")) {
                    ticketViewModel.viewType = "1";
                } else if (adapterView.getSelectedItem().equals("Only Mine")) {
                    ticketViewModel.viewType = "0";
                }
                if(isStartLoad){
                    ticketViewModel.getPendingTicket();
                }
                isStartLoad = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setRecyclerViewData() {
        List<TicketListModel> listData = new ArrayList<>();
        myRecyclerViewAdapter = new PendingTicketAdapter(requireContext(), listData, new RecyclerViewClickListener() {
            @Override
            public void onPositionClicked(int ticketNo, String flag, Object obj) {
                if (flag.equalsIgnoreCase("ticketMenu")) {
                    TicketListModel ticketListModel =  (TicketListModel)obj;
                    showBottomDialog(ticketNo, ticketListModel.getCustCode(), ticketListModel);
                } else if (flag.equalsIgnoreCase("attachmentList")) {
                    showAttachmentList(ticketNo);
                }
            }
        });
        fragmentPendingTicketBinding.setPendingAdapter(myRecyclerViewAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        fragmentPendingTicketBinding.recyclerViewPending.addItemDecoration(decoration);
    }

    private void showBottomDialog(int ticketNo, String custCode, TicketListModel obj) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment = ActionBottomDialogFragment.newInstance(this);
        TicketRespondFragmentDialog.registerListener(this);
        EditTicketDialog.registerListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("type", "pendingTicketMenu");
        bundle.putInt("ticketNo", ticketNo);
        bundle.putString("custCode", custCode);
        bundle.putString("softwareName", obj.getSoftName());
        bundle.putString("smUser", obj.getSM());
        addPhotoBottomDialogFragment.setArguments(bundle);
        addPhotoBottomDialogFragment.show(requireActivity().getSupportFragmentManager(), ActionBottomDialogFragment.TAG);
    }

    private void showAttachmentList(int ticketNo) {
        ViewAttachmentFragmentDialog viewAttachmentFragmentDialog = ViewAttachmentFragmentDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("ticketNo", ticketNo);
        viewAttachmentFragmentDialog.setArguments(bundle);
        viewAttachmentFragmentDialog.show(requireActivity().getSupportFragmentManager(), "viewAttachment");
        ViewAttachmentFragmentDialog.refreshListData = this;
    }

    @Override
    public void onRefresh() {
        ticketViewModel.getPendingTicket();
    }

    @Override
    public void onRequestStart() {
        fragmentPendingTicketBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        fragmentPendingTicketBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            myRecyclerViewAdapter.updateData((ArrayList<TicketListModel> )obj);
            fragmentPendingTicketBinding.setNofoundVisible(false);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            fragmentPendingTicketBinding.setNofoundVisible(true);
            myRecyclerViewAdapter.updateData(new ArrayList<>());
        } else if(statusCode == ApiStatusCode.ACTION_SUCCESS_CODE) {
            ticketViewModel.getPendingTicket();
        } else if(statusCode == ApiStatusCode.ACTION_ERROR_CODE) {
            CommonUtils.showAlertDialog(result, requireContext(), "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        fragmentPendingTicketBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }

    @Override
    public void ticketToResolve(int ticketNo, String custCode) {
        if(ticketNo > 0){
            ticketViewModel.resolvedTicket(ticketNo, custCode);
        }
    }
}
