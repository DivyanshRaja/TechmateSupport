package com.asprime.techmatesupport.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.asprime.techmatesupport.BR;
import com.asprime.techmatesupport.BuildConfig;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.AttachmentChildLayoutBinding;
import com.asprime.techmatesupport.listners.DeleteAttachmentListner;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.model.FileDetials;
import com.asprime.techmatesupport.model.TicketAttachmentDetailsModel;

import java.util.List;

public class ViewAttachmentChildAdapter extends RecyclerView.Adapter<ViewAttachmentChildAdapter.ViewHolder>{
    List<FileDetials> fileDetials;
    Context context;
    public static DeleteAttachmentListner deleteAttachmentListner;
    TicketAttachmentDetailsModel ticketAttachmentDetailsModel;
    int selectedPosition = -1;

    public ViewAttachmentChildAdapter(List<FileDetials> fileDetails, Context context, TicketAttachmentDetailsModel ticketAttachmentDetailsModel) {
        this.context = context;
        this.fileDetials = fileDetails;
        this.ticketAttachmentDetailsModel = ticketAttachmentDetailsModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AttachmentChildLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.attachment_child_layout, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileDetials fileDetail = fileDetials.get(position);
        holder.bind(fileDetail);
        holder.attachmentChildLayoutBinding.clickBtnToView.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.IMAGE_URL+fileDetail.getFilePath()));
            context.startActivity(browserIntent);
        });

        holder.attachmentChildLayoutBinding.deleteBtn.setOnClickListener((v) -> {
            deleteAttachmentListner.attachmentTODelete(Integer.parseInt(ticketAttachmentDetailsModel.getTicketNo()), fileDetail.getFileName(), Integer.parseInt(ticketAttachmentDetailsModel.getEntryNo()));
            selectedPosition = holder.getAdapterPosition();
        });
    }

    @Override
    public int getItemCount() {
        return fileDetials.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AttachmentChildLayoutBinding attachmentChildLayoutBinding;

        public ViewHolder(@NonNull AttachmentChildLayoutBinding attachmentChildLayoutBinding) {
            super(attachmentChildLayoutBinding.getRoot());
            this.attachmentChildLayoutBinding = attachmentChildLayoutBinding;
        }

        public void bind(FileDetials fileDetials){
            attachmentChildLayoutBinding.setVariable(BR.filesDetails, fileDetials);
        }
    }
}
