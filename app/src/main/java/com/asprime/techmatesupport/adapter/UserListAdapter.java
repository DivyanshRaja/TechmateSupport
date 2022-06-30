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
import com.asprime.techmatesupport.databinding.UserListSingleItemBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.UserListModel;
import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>implements Filterable {
    List<UserListModel> userListModels;
    List<UserListModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;
    int clickPos = -1;

    public UserListAdapter(Context context, List<UserListModel> userListModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.userListModels = userListModels;
        this.filterList = userListModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserListSingleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.user_list_single_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserListModel dataModel = userListModels.get(position);
        holder.bind(dataModel);

        holder.userListSingleItemBinding.optionsBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onPositionClicked(holder.getAdapterPosition(), "optionMenu",  dataModel);
            }
        });
    }

    public void updateData(List<UserListModel> userListModels) {
        this.userListModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.userListModels.addAll(userListModels);
        this.filterList.addAll(userListModels);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(userListModels == null){
            return 0;
        } else {
            return userListModels.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    userListModels = filterList;
                } else {
                    List<UserListModel> filteredList = new ArrayList<>();
                    for(UserListModel row : filterList){
                        if(row.getUserName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        } else if(row.getEmailID().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    userListModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userListModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userListModels = (List<UserListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        UserListSingleItemBinding userListSingleItemBinding;

        public ViewHolder(UserListSingleItemBinding userListSingleItemBinding) {
            super(userListSingleItemBinding.getRoot());
            this.userListSingleItemBinding = userListSingleItemBinding;
        }

        public void bind(Object obj) {
            userListSingleItemBinding.setVariable(BR.userData, obj);
            userListSingleItemBinding.executePendingBindings();
        }
    }
}
