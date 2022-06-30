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
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.CustomerSetModel;
import com.asprime.techmatesupport.model.UserListModel;
import java.util.ArrayList;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder>implements Filterable {
    List<CustomerSetModel> customerSetModels;
    List<CustomerSetModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;

    public CompanyAdapter(Context context, List<CustomerSetModel> customerSetModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.customerSetModels = customerSetModels;
        this.filterList = customerSetModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CompanySingleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.company_single_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerSetModel dataModel = customerSetModels.get(position);
        holder.bind(dataModel);
        holder.companySingleItemLayoutBinding.editBtn.setOnClickListener(v->{
            recyclerViewClickListener.onPositionClicked(position, "editClick", dataModel);
        });

        holder.companySingleItemLayoutBinding.userBlockBtn.setOnClickListener(v->{
            recyclerViewClickListener.onPositionClicked(position, "userBlockClick", dataModel);
        });
    }

    public void updateData(List<CustomerSetModel> customerSetModel) {
        this.customerSetModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.customerSetModels.addAll(customerSetModel);
        this.filterList.addAll(customerSetModel);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(customerSetModels == null){
            return 0;
        } else {
            return customerSetModels.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    customerSetModels = filterList;
                } else {
                    List<CustomerSetModel> filteredList = new ArrayList<>();
                    for(CustomerSetModel row : filterList){
                        if(row.getCustName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    customerSetModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = customerSetModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                customerSetModels = (List<CustomerSetModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CompanySingleItemLayoutBinding companySingleItemLayoutBinding;

        public ViewHolder(CompanySingleItemLayoutBinding companySingleItemLayoutBinding) {
            super(companySingleItemLayoutBinding.getRoot());
            this.companySingleItemLayoutBinding = companySingleItemLayoutBinding;
        }

        public void bind(Object obj) {
            companySingleItemLayoutBinding.setVariable(BR.customerSetModel, obj);
            companySingleItemLayoutBinding.executePendingBindings();
        }
    }
}
