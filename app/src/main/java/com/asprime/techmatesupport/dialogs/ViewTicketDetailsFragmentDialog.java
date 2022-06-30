package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.TicketDetailsReplyAdapter;
import com.asprime.techmatesupport.databinding.ViewTicketDetailsLayoutBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewTicketDetailsFragmentDialog extends DialogFragment implements OnClickHandlerInterface, ApiStatusListener {
    ViewTicketDetailsLayoutBinding viewTicketDetailsLayoutBinding;
    int ticketNo;
    TicketViewModel ticketViewModel;
    TicketDetailsReplyAdapter ticketDetailsReplyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ticketNo = getArguments().getInt("ticketNo");
    }

    public static ViewTicketDetailsFragmentDialog newInstance(){
        return new ViewTicketDetailsFragmentDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        viewTicketDetailsLayoutBinding = ViewTicketDetailsLayoutBinding.inflate(getLayoutInflater());
        View v = viewTicketDetailsLayoutBinding.getRoot();
        dialog.setContentView(v);
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        viewTicketDetailsLayoutBinding.setLifecycleOwner(this);
        viewTicketDetailsLayoutBinding.setOnCLickInterface(this);
        viewTicketDetailsLayoutBinding.executePendingBindings();
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
        setRecyclerViewData();
        ticketViewModel.getTicketDetails(ticketNo);
    }

    public void setRecyclerViewData() {
        List<TicketListModel> listData = new ArrayList<>();
        ticketDetailsReplyAdapter = new TicketDetailsReplyAdapter(listData, requireContext());
        viewTicketDetailsLayoutBinding.setParentAdapter(ticketDetailsReplyAdapter);
    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        if(statusCode == ApiStatusCode.SUCCESS_CODE){
            List<TicketListModel> ticketListModels = (ArrayList<TicketListModel>)obj;
            viewTicketDetailsLayoutBinding.setTicketList(ticketListModels.get(0));
            ticketDetailsReplyAdapter.updateData(ticketListModels);
        } else if(statusCode == ApiStatusCode.ERROR_CODE){
            ticketDetailsReplyAdapter.updateData(new ArrayList<>());
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
