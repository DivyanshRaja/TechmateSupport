package com.asprime.techmatesupport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.DeviceSingleItemBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.DeviceModel;
import java.util.ArrayList;
import java.util.List;

public class CustomerDeviceAdapter extends RecyclerView.Adapter<CustomerDeviceAdapter.DevcieViewHolder> implements Filterable {
    List<DeviceModel> deviceModels;
    List<DeviceModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;

    public CustomerDeviceAdapter(Context context, List<DeviceModel> deviceModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.deviceModels = deviceModels;
        this.filterList = deviceModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public DevcieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeviceSingleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.device_single_item, parent, false);
        return new DevcieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DevcieViewHolder holder, int position) {
        DeviceModel dataModel = deviceModels.get(position);
        holder.bind(dataModel);

        holder.deviceSingleItemBinding.editDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onPositionClicked(holder.getAdapterPosition(), "editDeviceList", dataModel);
            }
        });
    }

    public void updateData(List<DeviceModel> deviceModels) {
        this.deviceModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.deviceModels.addAll(deviceModels);
        this.filterList.addAll(deviceModels);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(deviceModels.size() > 0){
            recyclerViewClickListener.onPositionClicked(0, "found", null);
            return deviceModels.size();
        } else {
            recyclerViewClickListener.onPositionClicked(0, "noFound", null);
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    deviceModels = filterList;
                } else {
                    List<DeviceModel> filteredList = new ArrayList<>();
                    for(DeviceModel row : filterList){
                        if(row.getDeviceName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    deviceModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = deviceModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                deviceModels = (List<DeviceModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class DevcieViewHolder extends RecyclerView.ViewHolder {
        DeviceSingleItemBinding deviceSingleItemBinding;

        public DevcieViewHolder(DeviceSingleItemBinding deviceSingleItemBinding) {
            super(deviceSingleItemBinding.getRoot());
            this.deviceSingleItemBinding = deviceSingleItemBinding;
        }

        public void bind(Object obj) {
            deviceSingleItemBinding.setVariable(BR.deviceData, obj);
            deviceSingleItemBinding.executePendingBindings();
        }
    }
}
