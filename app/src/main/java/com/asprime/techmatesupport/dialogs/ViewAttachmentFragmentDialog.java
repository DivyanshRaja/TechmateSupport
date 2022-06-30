package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.ViewAttachmentChildAdapter;
import com.asprime.techmatesupport.adapter.ViewAttachmentParentAdapter;
import com.asprime.techmatesupport.databinding.ViewAttachmentListLayoutBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.DeleteAttachmentListner;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.TicketAttachmentDetailsModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ViewAttachmentFragmentDialog extends DialogFragment implements View.OnClickListener, ApiStatusListener, DeleteAttachmentListner {
    ViewAttachmentListLayoutBinding viewAttachmentListLayoutBinding;
    int ticketNo;
    TicketViewModel ticketViewModel;
    ViewAttachmentParentAdapter viewAttachmentParentAdapter;
    public static RefreshListData refreshListData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ticketNo = getArguments().getInt("ticketNo");
    }

    public static ViewAttachmentFragmentDialog newInstance(){
        return new ViewAttachmentFragmentDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        viewAttachmentListLayoutBinding = ViewAttachmentListLayoutBinding.inflate(getLayoutInflater());
        View v = viewAttachmentListLayoutBinding.getRoot();
        dialog.setContentView(v);
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        viewAttachmentListLayoutBinding.setLifecycleOwner(this);
        viewAttachmentListLayoutBinding.executePendingBindings();
        ticketViewModel.apiStatusListener = this;

        dialog.setCancelable(true);
        Window window = dialog.getWindow();
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
        if (id == R.id.closeAddUserDialogBtn) {
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewAttachmentListLayoutBinding.closeAddUserDialogBtn.setOnClickListener(this);
        setRecyclerViewData();

        ticketViewModel.getTicketAttachmentList(ticketNo);
    }

    public void setRecyclerViewData(){
        List<TicketAttachmentDetailsModel> listData = new ArrayList<>();
        viewAttachmentParentAdapter = new ViewAttachmentParentAdapter(requireContext(), listData);
        viewAttachmentListLayoutBinding.setParentAdapter(viewAttachmentParentAdapter);
        viewAttachmentListLayoutBinding.setTicketNo(String.valueOf(ticketNo));

        ViewAttachmentChildAdapter.deleteAttachmentListner = this;
    }

    @Override
    public void onRequestStart() {
        viewAttachmentListLayoutBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        viewAttachmentListLayoutBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.SUCCESS_CODE) {
            if(result.contains("deleted")){
                viewAttachmentListLayoutBinding.setNoFoundVisibility(false);
                refreshListData.onRefresh();
                ticketViewModel.getTicketAttachmentList(ticketNo);
            } else {
                if(((ArrayList<TicketAttachmentDetailsModel>) obj).size() == 0){
                    viewAttachmentListLayoutBinding.setNoFoundVisibility(true);
                } else {
                    viewAttachmentListLayoutBinding.setNoFoundVisibility(false);
                    viewAttachmentParentAdapter.updateData((ArrayList<TicketAttachmentDetailsModel>) obj);
                }
            }
        } else if(statusCode == ApiStatusCode.ERROR_CODE) {
            viewAttachmentListLayoutBinding.setNoFoundVisibility(true);
            viewAttachmentParentAdapter.updateData(new ArrayList<>());
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        viewAttachmentListLayoutBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }

    @Override
    public void attachmentTODelete(int ticketNo , String fileName, int entryNo) {
        ticketViewModel.deleteAttachmentFile(ticketNo, fileName, entryNo);
    }
}
