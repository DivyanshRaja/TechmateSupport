package com.asprime.techmatesupport.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.ContentUploadFragmentBinding;
import com.asprime.techmatesupport.databinding.UploadContentListDialogBinding;
import com.asprime.techmatesupport.dialogs.AddEditDeviceDialog;
import com.asprime.techmatesupport.dialogs.UploadContentListDialog;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.model.DeviceModel;

public class ContentUploadFragment extends Fragment implements OnClickHandlerInterface {
    ContentUploadFragmentBinding contentUploadFragmentBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentUploadFragmentBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.content_upload_fragment, container, false);

        if(getArguments().getString("fragmentFlag") != null && getArguments().getString("fragmentFlag").equalsIgnoreCase("WCU") ) {
            contentUploadFragmentBinding.setFormTitle("Whats New Add Content");
        } else if(getArguments().getString("fragmentFlag") != null && getArguments().getString("fragmentFlag").equalsIgnoreCase("UCU") ) {
            contentUploadFragmentBinding.setFormTitle("ERP Manual Add Content");
        }
        contentUploadFragmentBinding.setClickInterface(this);
        return contentUploadFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.floatingBtn) {
            showContentListDialog();
        } else if(id == R.id.addBtn){
            Toast.makeText(requireContext(), "Add Btn click", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.uploadBtn){
            Toast.makeText(requireContext(), "Upload Btn click", Toast.LENGTH_SHORT).show();
        }
    }

    private void showContentListDialog() {
        UploadContentListDialog dialogFragment = UploadContentListDialog.getInstance();
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("uploadContentListDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "uploadContentListDialog");
    }


}
