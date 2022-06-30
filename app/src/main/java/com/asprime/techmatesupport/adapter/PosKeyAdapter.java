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
import com.asprime.techmatesupport.databinding.PosKeySingleLayoutBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.PosKeyModel;
import java.util.ArrayList;
import java.util.List;

public class PosKeyAdapter extends RecyclerView.Adapter<PosKeyAdapter.ViewHolder>implements Filterable {
    List<PosKeyModel> posKeyModels;
    List<PosKeyModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;

    public PosKeyAdapter(Context context, List<PosKeyModel> posKeyModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.posKeyModels = posKeyModels;
        this.filterList = posKeyModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PosKeySingleLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.pos_key_single_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PosKeyModel dataModel = posKeyModels.get(position);
        holder.bind(dataModel);
    }

    public void updateData(List<PosKeyModel> posKeyModels) {
        this.posKeyModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.posKeyModels.addAll(posKeyModels);
        this.filterList.addAll(posKeyModels);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(posKeyModels == null){
            return 0;
        } else {
            return posKeyModels.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    posKeyModels = filterList;
                } else {
                    List<PosKeyModel> filteredList = new ArrayList<>();
                    for(PosKeyModel row : filterList){
                        if(row.getWSName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        } else if(row.getUserName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        } else if(row.getCustomerName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        } else if(row.getCustCode().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        } else if(row.getSoftName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    posKeyModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = posKeyModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                posKeyModels = (List<PosKeyModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PosKeySingleLayoutBinding posKeySingleLayoutBinding;

        public ViewHolder(PosKeySingleLayoutBinding posKeySingleLayoutBinding) {
            super(posKeySingleLayoutBinding.getRoot());
            this.posKeySingleLayoutBinding = posKeySingleLayoutBinding;
        }

        public void bind(Object obj) {
            posKeySingleLayoutBinding.setVariable(BR.posKeyData, obj);
            posKeySingleLayoutBinding.executePendingBindings();
        }
    }
}
