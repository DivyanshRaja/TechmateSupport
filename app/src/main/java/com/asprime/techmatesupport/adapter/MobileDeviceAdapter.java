package com.asprime.techmatesupport.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.MobileDeviceActivationSingleItemBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.MobileAppDeviceModel;
import java.util.ArrayList;
import java.util.List;

public class MobileDeviceAdapter extends RecyclerView.Adapter<MobileDeviceAdapter.MobileDeviceActivationHolder> implements Filterable {
    List<MobileAppDeviceModel> mobileAppDeviceModels;
    List<MobileAppDeviceModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;
    String flag;

    public MobileDeviceAdapter(Context context, List<MobileAppDeviceModel> mobileAppDeviceModels, RecyclerViewClickListener recyclerViewClickListener, String flag) {
        this.context = context;
        this.mobileAppDeviceModels = mobileAppDeviceModels;
        this.filterList = mobileAppDeviceModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.flag = flag;
    }

    @NonNull
    @Override
    public MobileDeviceActivationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MobileDeviceActivationSingleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.mobile_device_activation_single_item, parent, false);
        return new MobileDeviceActivationHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MobileDeviceActivationHolder holder, int position) {
        MobileAppDeviceModel dataModel = mobileAppDeviceModels.get(position);
        holder.bind(dataModel);
        if(flag.equalsIgnoreCase("viewDevices")){
            holder.mobileDeviceActivationSingleItemBinding.setVisibilityFlag(true);
            holder.mobileDeviceActivationSingleItemBinding.actvLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.mobileDeviceActivationSingleItemBinding.actvTitle.setTextColor(Color.WHITE);
            holder.mobileDeviceActivationSingleItemBinding.actvText.setTextColor(Color.WHITE);
            holder.mobileDeviceActivationSingleItemBinding.actvLayout.setPaddingRelative(10, 4,10, 4);
        }
    }

    public void updateData(List<MobileAppDeviceModel> mobileAppDeviceModels) {
        this.mobileAppDeviceModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.mobileAppDeviceModels.addAll(mobileAppDeviceModels);
        this.filterList.addAll(mobileAppDeviceModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mobileAppDeviceModels.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mobileAppDeviceModels = filterList;
                } else {
                    List<MobileAppDeviceModel> filteredList = new ArrayList<>();
                    for (MobileAppDeviceModel row : filterList) {
                        if (row.getUserName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mobileAppDeviceModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mobileAppDeviceModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mobileAppDeviceModels = (List<MobileAppDeviceModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MobileDeviceActivationHolder extends RecyclerView.ViewHolder {
        MobileDeviceActivationSingleItemBinding mobileDeviceActivationSingleItemBinding;

        public MobileDeviceActivationHolder(MobileDeviceActivationSingleItemBinding mobileDeviceActivationSingleItemBinding) {
            super(mobileDeviceActivationSingleItemBinding.getRoot());
            this.mobileDeviceActivationSingleItemBinding = mobileDeviceActivationSingleItemBinding;
        }

        public void bind(Object obj) {
            mobileDeviceActivationSingleItemBinding.setVariable(BR.mobileDeviceData, obj);
            mobileDeviceActivationSingleItemBinding.executePendingBindings();
        }
    }
}
