package com.asprime.techmatesupport.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.asprime.techmatesupport.adapter.PosKeyAdapter;
import com.asprime.techmatesupport.databinding.PositiveLicenseKeyLayoutBinding;
import com.asprime.techmatesupport.model.PosKeyModel;

import java.util.ArrayList;
import java.util.List;

public class PosKeyFragment extends Fragment implements View.OnClickListener {
    PositiveLicenseKeyLayoutBinding positiveLicenseKeyLayoutBinding;
    PosKeyAdapter posKeyAdapter;
    int selectedSoftwareId = 0;
    boolean alreadyRun = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        positiveLicenseKeyLayoutBinding = PositiveLicenseKeyLayoutBinding.inflate(getLayoutInflater());
        return positiveLicenseKeyLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onResume() {
        super.onResume();
        positiveLicenseKeyLayoutBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                posKeyAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setRecyclerView();

        setPositiveKeysData();
    }

    private void setPositiveKeysData(){
        List<PosKeyModel> posKeyModelList = new ArrayList<>();
        PosKeyModel posKeyModel = new PosKeyModel();
        posKeyModel.setPunchDate("/Date(1378942066580)/");
        posKeyModel.setSoftName("Demo");
        posKeyModel.setWSKey("Demokey665256");
        posKeyModel.setWSName("Demo");
        posKeyModel.setHDSrNo("dshjdsgf623t378rjhfgdk");
        posKeyModelList.add(posKeyModel);
        posKeyModelList.add(posKeyModel);
        posKeyModelList.add(posKeyModel);
        posKeyModelList.add(posKeyModel);

        posKeyAdapter.updateData(posKeyModelList);
    }

    private void setRecyclerView() {
        posKeyAdapter = new PosKeyAdapter(requireContext(), new ArrayList<>(), null);
        positiveLicenseKeyLayoutBinding.setPosAdapter(posKeyAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        positiveLicenseKeyLayoutBinding.recyclerView.addItemDecoration(decoration);
    }
}
