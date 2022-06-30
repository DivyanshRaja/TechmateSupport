package com.asprime.techmatesupport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.PendingTicketSingleLayoutBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;

public class PendingTicketAdapter extends RecyclerView.Adapter<PendingTicketAdapter.PendingTicketViewHolder>implements Filterable {
    List<TicketListModel> ticketListModels;
    List<TicketListModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;
    int clickPos = -1;

    public PendingTicketAdapter(Context context, List<TicketListModel> ticketListModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.ticketListModels = ticketListModels;
        this.filterList = ticketListModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public PendingTicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PendingTicketSingleLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.pending_ticket_single_layout, parent, false);
        return new PendingTicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingTicketViewHolder holder, int position) {
        TicketListModel dataModel = ticketListModels.get(position);
        holder.bind(dataModel);
        int pos = position;

        holder.pendingTicketSingleLayoutBinding.viewDetails.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onPositionClicked(dataModel.getTicketNo(), "ticketMenu", dataModel);
            }
        });

        holder.pendingTicketSingleLayoutBinding.attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(holder.pendingTicketSingleLayoutBinding.attachmentBtn.getText().toString()) > 0) {
                    recyclerViewClickListener.onPositionClicked(dataModel.getTicketNo(), "attachmentList", dataModel);
                } else {
                    CommonUtils.showAlertDialog("Ticket has no attached file",context,"");
                }
            }
        });
    }

    public void updateData(List<TicketListModel> ticketListModels) {
        this.ticketListModels = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.ticketListModels.addAll(ticketListModels);
        this.filterList.addAll(ticketListModels);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return ticketListModels.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    ticketListModels = filterList;
                } else {
                    List<TicketListModel> filteredList = new ArrayList<>();
                    for(TicketListModel row : filterList){
                        if(row.getTrnTitle().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }

                    ticketListModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = ticketListModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ticketListModels = (List<TicketListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class PendingTicketViewHolder extends RecyclerView.ViewHolder {
        PendingTicketSingleLayoutBinding pendingTicketSingleLayoutBinding;

        public PendingTicketViewHolder(PendingTicketSingleLayoutBinding pendingTicketSingleLayoutBinding) {
            super(pendingTicketSingleLayoutBinding.getRoot());
            this.pendingTicketSingleLayoutBinding = pendingTicketSingleLayoutBinding;
        }

        public void bind(Object obj) {
            pendingTicketSingleLayoutBinding.setVariable(BR.ticketData, obj);
            pendingTicketSingleLayoutBinding.executePendingBindings();
        }
    }
}
