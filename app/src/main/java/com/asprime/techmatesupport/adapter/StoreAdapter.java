package com.asprime.techmatesupport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.CompanySingleItemLayoutBinding;
import com.asprime.techmatesupport.databinding.StoreSingleItemLayoutBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.CompanyStoreDataModel;
import com.asprime.techmatesupport.model.CustomerSetModel;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder>implements Filterable {
    List<CompanyStoreDataModel> companyStoreDataModels;
    List<CompanyStoreDataModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;

    public StoreAdapter(Context context, List<CompanyStoreDataModel> companyStoreDataModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.companyStoreDataModels = companyStoreDataModels;
        this.filterList = companyStoreDataModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StoreSingleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.store_single_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompanyStoreDataModel dataModel = companyStoreDataModels.get(position);
        holder.bind(dataModel);
        holder.storeSingleItemLayoutBinding.editBtn.setOnClickListener(v->{
            recyclerViewClickListener.onPositionClicked(position, "editClick", dataModel);
        });

        holder.storeSingleItemLayoutBinding.deleteBtn.setOnClickListener(v->{
            recyclerViewClickListener.onPositionClicked(position, "deleteClick", dataModel);
        });
    }

    public void updateData(List<CompanyStoreDataModel> companyStoreDataModels) {
        this.companyStoreDataModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.companyStoreDataModels.addAll(companyStoreDataModels);
        this.filterList.addAll(companyStoreDataModels);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(companyStoreDataModels == null){
            return 0;
        } else {
            return companyStoreDataModels.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    companyStoreDataModels = filterList;
                } else {
                    List<CompanyStoreDataModel> filteredList = new ArrayList<>();
                    for(CompanyStoreDataModel row : filterList){
                        if(row.getCustName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    companyStoreDataModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = companyStoreDataModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                companyStoreDataModels = (List<CompanyStoreDataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        StoreSingleItemLayoutBinding storeSingleItemLayoutBinding;

        public ViewHolder(StoreSingleItemLayoutBinding storeSingleItemLayoutBinding) {
            super(storeSingleItemLayoutBinding.getRoot());
            this.storeSingleItemLayoutBinding = storeSingleItemLayoutBinding;
        }

        public void bind(Object obj) {
            storeSingleItemLayoutBinding.setVariable(BR.storeDataModel, obj);
            storeSingleItemLayoutBinding.executePendingBindings();
        }
    }
}
