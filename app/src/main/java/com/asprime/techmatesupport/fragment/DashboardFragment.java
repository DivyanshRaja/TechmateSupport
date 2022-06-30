package com.asprime.techmatesupport.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.DashboardFragmentBinding;

public class DashboardFragment extends Fragment {
    DashboardFragmentBinding dashboardFragmentBinding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardFragmentBinding = DataBindingUtil.inflate(LayoutInflater.from(inflater.getContext()), R.layout.dashboard_fragment, container, false);
        return dashboardFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
