package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
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
import com.asprime.techmatesupport.databinding.EditDeviceDialogLayoutBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.CustomerSetModel;
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

public class AddEditCompaniesDialog extends DialogFragment implements OnClickHandlerInterface, ApiStatusListener {
    AddEditCompanyDialogBinding addEditCompanyDialogBinding;
    static RefreshListData refreshListData;
    PreferenceHandler preferenceHandler;
    CompanyAndStoreViewModel companyAndStoreViewModel;
    String flag;
    int smUserId = 0;
    String userName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("f_type")) {
           flag = getArguments().getString("f_type");
        }
    }

    public static AddEditCompaniesDialog getInstance(RefreshListData refreshListData) {
        AddEditCompaniesDialog.refreshListData = refreshListData;
        return new AddEditCompaniesDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        addEditCompanyDialogBinding = AddEditCompanyDialogBinding.inflate(getLayoutInflater());
        companyAndStoreViewModel = ViewModelProviders.of(this).get(CompanyAndStoreViewModel.class);
        View v = addEditCompanyDialogBinding.getRoot();
        companyAndStoreViewModel.statusListener = this;
        addEditCompanyDialogBinding.setLifecycleOwner(this);
        addEditCompanyDialogBinding.setOnClickInterface(this);

        /**
         * To check dialog type add new or edit details
         */
        if (flag.equalsIgnoreCase("addNew")) {
            addEditCompanyDialogBinding.setFormTitle("Add New Company");
            addEditCompanyDialogBinding.companyFormIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_add_24, null));
        } else {
            assert getArguments() != null;
            addEditCompanyDialogBinding.setCompanyName(getArguments().getString("cmpName"));
            addEditCompanyDialogBinding.setCompanyCode(getArguments().getString("cmpCode"));
            userName = getArguments().getString("userName");
            addEditCompanyDialogBinding.companyCodeEditText.setClickable(false);
            addEditCompanyDialogBinding.companyCodeEditText.setFocusable(false);
            addEditCompanyDialogBinding.companyCodeEditText.setCursorVisible(false);
            addEditCompanyDialogBinding.setFormTitle("Edit Company Detail");
            addEditCompanyDialogBinding.companyFormIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_mode_edit_24, null));
        }

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

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("CustCode", "ALL");
        companyAndStoreViewModel.getSMUSerData(hashMap).observe(this, smUserDataModels -> {
            if (smUserDataModels != null && smUserDataModels.size() > 0) {
                setUserSpinnerData(smUserDataModels);
            } else {
                setUserSpinnerData(new ArrayList<>());
            }
        });

        addEditCompanyDialogBinding.companyCodeEditText.addTextChangedListener(new TextWatcher(){
            private boolean isRunning = false;
            private boolean isDeleting = false;
            private final String mask = "#/##";


            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                isDeleting = count > after;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isRunning || isDeleting) {
                    return;
                }
                isRunning = true;
                int editableLength = editable.length();
                if (editableLength < mask.length()) {
                    if (mask.charAt(editableLength) != '#') {
                        editable.append(mask.charAt(editableLength));
                    } else if (mask.charAt(editableLength - 1) != '#') {
                        editable.insert(editableLength - 1, mask, editableLength - 1, editableLength);
                    }
                }
                isRunning = false;
            }
        });
    }

    private void setUserSpinnerData(List<SMUserDataModel> userSpinnerDataList) {
        ArrayAdapter<SMUserDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addEditCompanyDialogBinding.supportAgentSpinner.setAdapter(adapter);
        int pos = 0;
        for (SMUserDataModel storeSpinnerData1 : userSpinnerDataList) {
            if (storeSpinnerData1.getTitle().equals(userName)) {
                addEditCompanyDialogBinding.supportAgentSpinner.setSelection(pos);
            }
            pos++;
        }
        addEditCompanyDialogBinding.supportAgentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SMUserDataModel smUserDataModel = (SMUserDataModel) parent.getSelectedItem();
                smUserId = smUserDataModel.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            String cmpName = addEditCompanyDialogBinding.companyEditText.getText().toString();
            String cmpCode = addEditCompanyDialogBinding.companyCodeEditText.getText().toString();
            if(smUserId == 0){
                addEditCompanyDialogBinding.supportAgentSpinner.requestFocus();
                TextView selectedTextView = (TextView) addEditCompanyDialogBinding.supportAgentSpinner.getSelectedView();
                selectedTextView.setError("error"); // any name of the error will do
                selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
                selectedTextView.setText("Select Support Agent"); // actual error message
            } else if(cmpName.length() == 0){
                addEditCompanyDialogBinding.companyEditText.setError("Enter Company Name");
                addEditCompanyDialogBinding.companyEditText.requestFocus();
            } else if(cmpCode.length() == 0){
                addEditCompanyDialogBinding.companyCodeEditText.setError("Enter Company Code");
                addEditCompanyDialogBinding.companyCodeEditText.requestFocus();
            } else{
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("CustCode", cmpCode);
                hashMap.put("CustName", cmpName);
                hashMap.put("Edit", !flag.equalsIgnoreCase("addNew"));
                hashMap.put("SM", smUserId);
                companyAndStoreViewModel.addEditCompany(hashMap);
            }
        }
    }

    @Override
    public void onRequestStart() {
        addEditCompanyDialogBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        addEditCompanyDialogBinding.setProgressBarVisibility(false);
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
        addEditCompanyDialogBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
