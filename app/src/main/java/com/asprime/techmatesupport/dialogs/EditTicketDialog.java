package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.TicketEditDialogBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.model.SoftwareModelData;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;
import com.google.android.gms.common.internal.service.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EditTicketDialog extends DialogFragment implements OnClickHandlerInterface, ApiStatusListener {
    TicketEditDialogBinding ticketEditDialogBinding;
    static RefreshListData refreshListData;
    TicketViewModel ticketViewModel;
    String custCode, smUserName;
    int selectedSoftwareId = 0, smUserId;
    int trnType, ticketNo;
    String softName = "", smUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            custCode = getArguments().getString("custCode");
            softName = getArguments().getString("softwareName");
            smUser = getArguments().getString("smUser");
            ticketNo = getArguments().getInt("ticketNo");
        }
    }

    public static void registerListener(RefreshListData refreshListData) {
        EditTicketDialog.refreshListData = refreshListData;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        ticketEditDialogBinding = TicketEditDialogBinding.inflate(getLayoutInflater());
        View v = ticketEditDialogBinding.getRoot();
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        ticketEditDialogBinding.setLifecycleOwner(this);
        ticketViewModel.apiStatusListener = this;
        ticketEditDialogBinding.setOnClickInterface(this);
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
    public int getTheme() {
        return R.style.CenterDialogStyleTheme;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.closeEditDeviceDialogBtn) {
            Objects.requireNonNull(getDialog()).dismiss();
        } else if (id == R.id.editTicketBtn) {
            PreferenceHandler preferenceHandler = new PreferenceHandler(requireContext());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("CustCode", custCode);
            hashMap.put("TicketNo", ticketNo);
            hashMap.put("TUserID", smUserId);
            hashMap.put("TUserName", smUserName);
            hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
            hashMap.put("UserName", preferenceHandler.getUser_name());
            hashMap.put("TrnType", trnType);
            hashMap.put("GPS", "0.0");
            hashMap.put("SoftID", selectedSoftwareId);
            ticketViewModel.editTicket(hashMap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ticketViewModel.getSoftwareData().observe(this, uList -> {
            if (uList != null && uList.size() > 0) {
                setSoftwareSpinnerData(uList);
            }
        });

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("CustCode", custCode);
        ticketViewModel.getSMUSerData(hashMap).observe(this, smUserDataModels -> {
            if (smUserDataModels != null && smUserDataModels.size() > 0) {
                setUserSpinnerData(smUserDataModels);
            } else {
                setUserSpinnerData(new ArrayList<>());
            }
        });

        getTrnTypeSpinner();
    }

    private void setSoftwareSpinnerData(List<SoftwareModelData> softwareSpinnerData) {
        // Create the instance of ArrayAdapter
        ArrayAdapter<SoftwareModelData> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, softwareSpinnerData);
        /* set simple layout resource file
        for each item of spinner */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketEditDialogBinding.softwareSpinner.setAdapter(adapter);
        int pos = 0;
        for (SoftwareModelData storeSpinnerData1 : softwareSpinnerData) {
            if (storeSpinnerData1.getSoftName().equals(softName)) {
                ticketEditDialogBinding.softwareSpinner.setSelection(pos);
            }
            pos++;
        }
        ticketEditDialogBinding.softwareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SoftwareModelData softwareSpinnerData = (SoftwareModelData) adapterView.getSelectedItem();
                selectedSoftwareId = Integer.parseInt(softwareSpinnerData.getSoftID());
                Log.d("TAG", "Received Spinner Data soft: " + selectedSoftwareId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUserSpinnerData(List<SMUserDataModel> userSpinnerDataList) {
        ArrayAdapter<SMUserDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketEditDialogBinding.transferTicketToSpinner.setAdapter(adapter);
        int pos = 0;
        for (SMUserDataModel storeSpinnerData1 : userSpinnerDataList) {
            if (storeSpinnerData1.getTitle().equals(smUser)) {
                ticketEditDialogBinding.transferTicketToSpinner.setSelection(pos);
            }
            pos++;
        }
        ticketEditDialogBinding.transferTicketToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SMUserDataModel smUserDataModel = (SMUserDataModel) parent.getSelectedItem();
                smUserId = smUserDataModel.getId();
                smUserName = smUserDataModel.getTitle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getTrnTypeSpinner() {
        ticketEditDialogBinding.transactionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "Received Spinner Data type: " + parent.getSelectedItem());
                if (parent.getSelectedItem().equals("General Issues")) {
                    trnType = 1;
                } else if (parent.getSelectedItem().equals("Customisation")) {
                    trnType = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onRequestStart() {
        ticketEditDialogBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        ticketEditDialogBinding.setProgressBarVisibility(false);
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
        ticketEditDialogBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
