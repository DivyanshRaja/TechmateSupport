package com.asprime.techmatesupport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.TicketViewAttachmentParentLayoutBinding;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.model.FileDetials;
import com.asprime.techmatesupport.model.TicketAttachmentDetailsModel;
import com.asprime.techmatesupport.model.TicketListModel;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewAttachmentParentAdapter extends RecyclerView.Adapter<ViewAttachmentParentAdapter.ViewAttachmentViewHolder> {
    private List<TicketAttachmentDetailsModel> ticketAttachmentDetailsModels;
    private final Context context;

    public ViewAttachmentParentAdapter(Context context, List<TicketAttachmentDetailsModel> ticketAttachmentDetailsModels) {
        this.ticketAttachmentDetailsModels = ticketAttachmentDetailsModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TicketViewAttachmentParentLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.ticket_view_attachment_parent_layout, parent, false);
        return new ViewAttachmentViewHolder(itemBinding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewAttachmentViewHolder holder, int position) {
        TicketAttachmentDetailsModel ticketAttachmentDetailsModel = ticketAttachmentDetailsModels.get(position);
        holder.bind(ticketAttachmentDetailsModel);
        setChildRecyclerData(holder, ticketAttachmentDetailsModel.getFileDetials(), ticketAttachmentDetailsModel);
    }

    @Override
    public int getItemCount() {
        if (ticketAttachmentDetailsModels != null) {
            return ticketAttachmentDetailsModels.size();
        } else {
            return 0;
        }
    }

    public void updateData(List<TicketAttachmentDetailsModel> ticketAttachmentDetailsModels) {
        this.ticketAttachmentDetailsModels = new ArrayList<>();
        this.ticketAttachmentDetailsModels.addAll(ticketAttachmentDetailsModels);
        notifyDataSetChanged();
    }

    static class ViewAttachmentViewHolder extends RecyclerView.ViewHolder{
        TicketViewAttachmentParentLayoutBinding ticketViewAttachmentParentLayoutBinding;

        public ViewAttachmentViewHolder(TicketViewAttachmentParentLayoutBinding binding) {
            super(binding.getRoot());
            this.ticketViewAttachmentParentLayoutBinding = binding;
        }

        public void bind(TicketAttachmentDetailsModel obj){
            ticketViewAttachmentParentLayoutBinding.setVariable(BR.attachmentData, obj);
        }
    }

    private void setChildRecyclerData(ViewAttachmentViewHolder holder, List<FileDetials> fileDetials, TicketAttachmentDetailsModel ticketAttachmentDetailsModel){
        ViewAttachmentChildAdapter viewAttachmentChildAdapter = new ViewAttachmentChildAdapter(fileDetials,context, ticketAttachmentDetailsModel);
        holder.ticketViewAttachmentParentLayoutBinding.setChildAttachmentAdapter(viewAttachmentChildAdapter);
    }
}
