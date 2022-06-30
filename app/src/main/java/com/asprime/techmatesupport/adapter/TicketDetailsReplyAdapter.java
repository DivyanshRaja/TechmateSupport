package com.asprime.techmatesupport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.TicketDetialsReplySingleLayoutBinding;
import com.asprime.techmatesupport.model.TicketListModel;
import java.util.ArrayList;
import java.util.List;

public class TicketDetailsReplyAdapter extends RecyclerView.Adapter<TicketDetailsReplyAdapter.ViewHolder> {
    List<TicketListModel> ticketListModelList;
    Context context;

    public TicketDetailsReplyAdapter(List<TicketListModel> ticketListModelList, Context context) {
        this.context = context;
        this.ticketListModelList = ticketListModelList;
    }

    public void updateData(List<TicketListModel> ticketListModelList){
        this.ticketListModelList = new ArrayList<>();
        this.ticketListModelList.addAll(ticketListModelList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TicketDetialsReplySingleLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.ticket_detials_reply_single_layout, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketListModel ticketListModel = ticketListModelList.get(position);
        holder.bind(ticketListModel);
    }

    @Override
    public int getItemCount() {
        return ticketListModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TicketDetialsReplySingleLayoutBinding ticketDetialsReplySingleLayoutBinding;

        public ViewHolder(@NonNull TicketDetialsReplySingleLayoutBinding ticketDetialsReplySingleLayoutBinding) {
            super(ticketDetialsReplySingleLayoutBinding.getRoot());
            this.ticketDetialsReplySingleLayoutBinding = ticketDetialsReplySingleLayoutBinding;
        }

        public void bind(TicketListModel ticketListModel){
            ticketDetialsReplySingleLayoutBinding.setVariable(BR.replyData, ticketListModel);
        }
    }
}
