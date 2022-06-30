package com.asprime.techmatesupport.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.TicketHistoryAdapter;
import com.asprime.techmatesupport.databinding.TicketHistoryFragmentBinding;
import com.asprime.techmatesupport.dialogs.ViewAttachmentFragmentDialog;
import com.asprime.techmatesupport.dialogs.ViewTicketDetailsFragmentDialog;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TicketHistoryFragment extends Fragment implements OnClickHandlerInterface, ApiStatusListener, RefreshListData {
    TicketHistoryFragmentBinding ticketHistoryFragmentBinding;
    TicketHistoryAdapter ticketHistoryAdapter;
    TicketViewModel ticketViewModel;
    boolean isStartLoad = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ticketHistoryFragmentBinding = TicketHistoryFragmentBinding.inflate(getLayoutInflater());
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        ticketHistoryFragmentBinding.setOnClickInterface(this);
        ticketHistoryFragmentBinding.setTicketViewModel(ticketViewModel);
        ticketHistoryFragmentBinding.executePendingBindings();
        ticketViewModel.apiStatusListener = this;
        return ticketHistoryFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.filterBtn){
            if(ticketHistoryFragmentBinding.filterLayout.getVisibility() == View.VISIBLE){
                ticketHistoryFragmentBinding.filterLayout.setVisibility(View.GONE);
            } else {
                ticketHistoryFragmentBinding.filterLayout.setVisibility(View.VISIBLE);
            }
        } else if(id == R.id.fromDate){
            openFromDatePicker();
        } else if(id == R.id.toDate){
            openToDatePicker();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rangSpinner();
        setRecyclerData();

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

        ticketHistoryFragmentBinding.fromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(ticketHistoryFragmentBinding.toDate.length() != 0 && ticketHistoryFragmentBinding.fromDate.length() != 0) {
                    if(isStartLoad){
                        ticketViewModel.getHistoryTicket();
                    }
                    isStartLoad = true;
                }
            }
        });

        ticketHistoryFragmentBinding.toDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(ticketHistoryFragmentBinding.toDate.length() != 0 && ticketHistoryFragmentBinding.fromDate.length() != 0) {
                    if(isStartLoad){
                        ticketViewModel.getHistoryTicket();
                    }
                }
            }
        });

        // Call for initial loading
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd/MM/yyyy");
        ticketViewModel.toDate = postFormatter.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        ticketViewModel.fromDate  = postFormatter.format(calendar.getTime());
        ticketViewModel.getHistoryTicket();
    }

    private void rangSpinner() {
        List<String> dataList = new ArrayList<>();
        dataList.add("Weekly");
        dataList.add("Monthly");
        dataList.add("Yearly");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dataList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketHistoryFragmentBinding.dateRangeSpinner.setAdapter(dataAdapter);
        ticketHistoryFragmentBinding.dateRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals("Weekly")) {
                    setDateWeekly();
                } else if (adapterView.getSelectedItem().equals("Monthly")) {
                    setDateMonthly();
                } else if (adapterView.getSelectedItem().equals("Yearly")) {
                    setDateYearly();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setDateWeekly() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = postFormatter.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String previousDate = postFormatter.format(calendar.getTime());

        ticketHistoryFragmentBinding.toDate.setText(currentDate);
        ticketHistoryFragmentBinding.fromDate.setText(previousDate);
        ticketViewModel.toDate = currentDate;
        ticketViewModel.fromDate = previousDate;
    }

    private void setDateMonthly() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = postFormatter.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        String previousDate = postFormatter.format(date);

        ticketHistoryFragmentBinding.fromDate.setText(previousDate);
        ticketHistoryFragmentBinding.toDate.setText(currentDate);

        ticketViewModel.toDate = currentDate;
        ticketViewModel.fromDate = previousDate;
    }

    private void setDateYearly() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = postFormatter.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date date = calendar.getTime();
        String previousDate = postFormatter.format(date);

        ticketHistoryFragmentBinding.fromDate.setText(previousDate);
        ticketHistoryFragmentBinding.toDate.setText(currentDate);
        ticketViewModel.toDate = currentDate;
        ticketViewModel.fromDate = previousDate;
    }

    @SuppressLint("SimpleDateFormat")
    private void openFromDatePicker() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            ticketHistoryFragmentBinding.fromDate.setText(dateFormat.format(calendar.getTime()));
        };

        DatePickerDialog dateFromPickerDialog = new DatePickerDialog(requireContext(),
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dateFromPickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        dateFromPickerDialog.show();
    }

    private void openToDatePicker() {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            ticketHistoryFragmentBinding.toDate.setText(dateFormat.format(myCalendar.getTime()));
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void setRecyclerData() {
        ticketHistoryAdapter = new TicketHistoryAdapter(requireContext(), new ArrayList<>(), (ticketNo, flag, obj) -> {
            if (flag.equalsIgnoreCase("ticketDetails")) {
                showPendingTicketDetailsPopUp(ticketNo);
            } else if (flag.equalsIgnoreCase("attachmentList")) {
                showAttachmentList(ticketNo);
            } else if (flag.equalsIgnoreCase("reverseTicket")) {
                TicketListModel ticketListModel = (TicketListModel) obj;
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Reversal of Ticket");
                builder.setMessage("Do you wish to reverse Ticket #" + ticketNo);
                builder.setPositiveButton("Yes", (v, pos) -> {
                    ticketViewModel.reverseTicket(ticketListModel.getCustCode(), ticketNo);
                    v.dismiss();
                });
                builder.setNegativeButton("No", (v, pos) -> {
                    v.dismiss();
                });
                builder.show();
            }
        });
        ticketHistoryFragmentBinding.setTicketHistoryAdapter(ticketHistoryAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        ticketHistoryFragmentBinding.recyclerView.addItemDecoration(decoration);
    }

    private void setCompanySpinnerData(List<CompanyDataModel> storeSpinnerDataList) {
        ArrayAdapter<CompanyDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketHistoryFragmentBinding.companySpinner.setAdapter(adapter);
        ticketHistoryFragmentBinding.companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        ticketHistoryFragmentBinding.storeSpinner.setAdapter(adapter);
        ticketHistoryFragmentBinding.storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyStoreDataModel spinnerData = (CompanyStoreDataModel) adapterView.getSelectedItem();
                ticketViewModel.storeId = spinnerData.getCustStoreID();
                if(isStartLoad){
                    ticketViewModel.getHistoryTicket();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onRequestStart() {
        ticketHistoryFragmentBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        ticketHistoryFragmentBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            ticketHistoryAdapter.updateData((ArrayList<TicketListModel> )obj);
            ticketHistoryFragmentBinding.setNofoundVisible(false);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            ticketHistoryFragmentBinding.setNofoundVisible(true);
            ticketHistoryAdapter.updateData(new ArrayList<>());
        } else if(statusCode == ApiStatusCode.ACTION_SUCCESS_CODE){
            CommonUtils.showAlertDialog(result, requireContext(), "success");
            ticketViewModel.getHistoryTicket();
        } else if(statusCode == ApiStatusCode.ACTION_ERROR_CODE){
            CommonUtils.showAlertDialog(result, requireContext(), "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        ticketHistoryFragmentBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
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
        ticketViewModel.getHistoryTicket();
    }

    private void showPendingTicketDetailsPopUp(int ticketNo) {
        ViewTicketDetailsFragmentDialog viewTicketDetailsFragmentDialog = ViewTicketDetailsFragmentDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("ticketNo", ticketNo);
        viewTicketDetailsFragmentDialog.setArguments(bundle);
        viewTicketDetailsFragmentDialog.show(requireActivity().getSupportFragmentManager(), "viewTicketDetailsFragmentDialog");
    }
}
