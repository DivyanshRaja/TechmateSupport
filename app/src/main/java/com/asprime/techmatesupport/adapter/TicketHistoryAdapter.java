package com.asprime.techmatesupport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
import com.asprime.techmatesupport.databinding.TicketHistorySingleItemBinding;
import com.asprime.techmatesupport.listners.RecyclerViewClickListener;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;

public class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.TicketHistoryViewHolder> implements Filterable {
    List<TicketListModel> ticketListModels;
    List<TicketListModel> filterList;
    Context context;
    public RecyclerViewClickListener recyclerViewClickListener;

    public TicketHistoryAdapter(Context context, List<TicketListModel> ticketListModels, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.ticketListModels = ticketListModels;
        this.filterList = ticketListModels;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }


    @NonNull
    @Override
    public TicketHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TicketHistorySingleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ticket_history_single_item, parent, false);
        return new TicketHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketHistoryViewHolder holder, int position) {
        TicketListModel dataModel = ticketListModels.get(position);
        holder.bind(dataModel);
        int pos = position;


        holder.ticketHistorySingleItemBinding.viewDetails.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onPositionClicked(dataModel.getTicketNo(), "ticketDetails", dataModel);
            }
        });

        holder.ticketHistorySingleItemBinding.reverseTextView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onPositionClicked(dataModel.getTicketNo(), "reverseTicket", dataModel);
            }
        });

        holder.ticketHistorySingleItemBinding.attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(holder.ticketHistorySingleItemBinding.attachmentBtn.getText().toString()) > 0) {
                    recyclerViewClickListener.onPositionClicked(dataModel.getTicketNo(), "attachmentList", dataModel);
                } else {
                    CommonUtils.showAlertDialog("Ticket has no attached file", context, "");
                }
            }
        });
    }

    public void updateData(List<TicketListModel> ticketListModels) {
        this.ticketListModels = new ArrayList<>();
        this.ticketListModels.addAll(ticketListModels);
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
                Log.d("TAG", "Search string: " + charString);
                if (charString.isEmpty()) {
                    ticketListModels = filterList;
                } else {
                    List<TicketListModel> filteredList = new ArrayList<>();
                    for (TicketListModel row : filterList) {
                        if (row.getUserName().toLowerCase().contains(charString.toLowerCase())) {
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

    public static class TicketHistoryViewHolder extends RecyclerView.ViewHolder {
        TicketHistorySingleItemBinding ticketHistorySingleItemBinding;

        public TicketHistoryViewHolder(TicketHistorySingleItemBinding ticketHistorySingleItemBinding) {
            super(ticketHistorySingleItemBinding.getRoot());
            this.ticketHistorySingleItemBinding = ticketHistorySingleItemBinding;
        }

        public void bind(Object obj) {
            ticketHistorySingleItemBinding.setVariable(BR.ticketHistoryData, obj);
            ticketHistorySingleItemBinding.executePendingBindings();
        }
    }
}
