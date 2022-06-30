package com.asprime.techmatesupport.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.DialogButtonBottomSheetBinding;
import com.asprime.techmatesupport.databinding.TicketEditDialogBinding;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.listners.TicketResolveListner;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class ActionBottomDialogFragment extends BottomSheetDialogFragment implements OnClickHandlerInterface {
    public static final String TAG = "ActionBottomDialog";
    DialogButtonBottomSheetBinding dialogButtonBottomSheetBinding;
    int ticketNo = 0;
    String custCode, softName, smUser;
    static TicketResolveListner ticketResolveListner;
    TicketViewModel ticketViewModel;


    public static ActionBottomDialogFragment newInstance(TicketResolveListner ticketResolveListner) {
        ActionBottomDialogFragment.ticketResolveListner = ticketResolveListner;
        return new ActionBottomDialogFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogButtonBottomSheetBinding = DialogButtonBottomSheetBinding.inflate(getLayoutInflater());
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        dialogButtonBottomSheetBinding.setLifecycleOwner(this);
        dialogButtonBottomSheetBinding.setOnClickInterface(this);
        dialogButtonBottomSheetBinding.executePendingBindings();
        return dialogButtonBottomSheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        if (getArguments() != null) {
            if (getArguments().containsKey("ticketNo")) {
                ticketNo = getArguments().getInt("ticketNo");
                custCode = getArguments().getString("custCode");
                softName = getArguments().getString("softwareName");
                smUser = getArguments().getString("smUser");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelBtn:
                Objects.requireNonNull(getDialog()).dismiss();
                break;

            case R.id.resolvedTicketBtn:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Do you wish to resolve Ticket #" + ticketNo);
                builder.setPositiveButton("Yes", (v, pos) -> {
                    ticketResolveListner.ticketToResolve(ticketNo, custCode);
                    v.dismiss();
                    Objects.requireNonNull(getDialog()).dismiss();
                });
                builder.setNegativeButton("No", (v, pos) -> {
                    v.dismiss();
                });
                builder.show();
                break;

            case R.id.editTicketBtn:
                Objects.requireNonNull(getDialog()).dismiss();
                editTicketDialog(ticketNo);
                break;

            case R.id.respondTicketBtn:
                Objects.requireNonNull(getDialog()).dismiss();
                showTicketRespondPopUp(ticketNo);
                break;

            case R.id.viewTicketBtn:
                Objects.requireNonNull(getDialog()).dismiss();
                showPendingTicketDetailsPopUp(ticketNo);
                break;
        }
    }

    private void showPendingTicketDetailsPopUp(int ticketNo) {
        ViewTicketDetailsFragmentDialog viewTicketDetailsFragmentDialog = ViewTicketDetailsFragmentDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("ticketNo", ticketNo);
        viewTicketDetailsFragmentDialog.setArguments(bundle);
        viewTicketDetailsFragmentDialog.show(requireActivity().getSupportFragmentManager(), "viewTicketDetailsFragmentDialog");
    }

    private void showTicketRespondPopUp(int ticketNo) {
        TicketRespondFragmentDialog ticketRespondFragmentDialog = TicketRespondFragmentDialog.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("ticketNo", ticketNo);
        bundle.putString("custCode", custCode);
        ticketRespondFragmentDialog.setArguments(bundle);
        ticketRespondFragmentDialog.show(requireActivity().getSupportFragmentManager(), "ticketRespondFragmentDialog");
    }

    private void editTicketDialog(int ticketNo) {
        EditTicketDialog editTicketDialog = new EditTicketDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ticketNo", ticketNo);
        bundle.putString("custCode", custCode);
        bundle.putString("softwareName", softName);
        bundle.putString("smUser", smUser);
        editTicketDialog.setArguments(bundle);
        editTicketDialog.show(requireActivity().getSupportFragmentManager(), "ticketRespondFragmentDialog");
    }
}
