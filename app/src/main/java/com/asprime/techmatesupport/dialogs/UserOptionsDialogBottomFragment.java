package com.asprime.techmatesupport.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.UserOptionsBottomSheetBinding;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Objects;

public class UserOptionsDialogBottomFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    UserOptionsBottomSheetBinding userOptionsBottomSheetBinding;
    int userId;
    String userName, userEmail, popUpFOr;
    boolean userStatus;
    static RefreshListData refreshListData;

    public static UserOptionsDialogBottomFragment newInstance(RefreshListData listener) {
        refreshListData = listener;
        return new UserOptionsDialogBottomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userOptionsBottomSheetBinding = UserOptionsBottomSheetBinding.inflate(getLayoutInflater());
        userOptionsBottomSheetBinding.setSuperUserVisible(false);
        return userOptionsBottomSheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userOptionsBottomSheetBinding.cancelBtn.setOnClickListener(this);
        userOptionsBottomSheetBinding.userEditBtn.setOnClickListener(this);
        userOptionsBottomSheetBinding.userRightBtn.setOnClickListener(this);
        userOptionsBottomSheetBinding.blockUnblockBtn.setOnClickListener(this);

        if (getArguments() != null && getArguments().size() > 0) {
            /*
             * {"clientUser, superUser, supportUser"}
             */
            popUpFOr = getArguments().getString("popUpFor");
            if(getArguments().getString("popUpFor").equalsIgnoreCase("superUser")){
                userOptionsBottomSheetBinding.setSuperUserVisible(true);
            }
        }

//        userId = getArguments().getInt("userId");
//        userEmail = getArguments().getString("emailAddress");
//        userName = getArguments().getString("userName");
//        userStatus = getArguments().getBoolean("userStatus");
//        if (!userStatus) {
//            userOptionsBottomSheetBinding.userStatusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_outline_close_24, null));
//            userOptionsBottomSheetBinding.userStatusIcon.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorError, null));
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onClick(View view) {

    }
}
