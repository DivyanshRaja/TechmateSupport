package com.asprime.techmatesupport.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.SelectedImageSingleListItemBinding;
import com.asprime.techmatesupport.model.SelectedImageModel;
import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder> {
    private final List<SelectedImageModel> selectedImageModels;

    public SelectedImageAdapter(List<SelectedImageModel> selectedImageModels, Context context) {
        this.selectedImageModels = selectedImageModels;
    }

    @NonNull
    @Override
    public SelectedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SelectedImageSingleListItemBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.selected_image_single_list_item, parent, false);
        return new SelectedImageViewHolder(itemBinding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SelectedImageViewHolder holder, int position) {
        SelectedImageModel selectedImageModel = selectedImageModels.get(position);
        holder.selectedImageSingleListItemBinding.setSelectImages(selectedImageModel);
        holder.selectedImageSingleListItemBinding.deleteImage.setOnClickListener((v)->{
            selectedImageModels.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (selectedImageModels != null) {
            return selectedImageModels.size();
        } else {
            return 0;
        }
    }

    public int getListSize(){
        return selectedImageModels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEmployeeList(SelectedImageModel selectedImageModelList) {
        selectedImageModels.add(selectedImageModelList);
        notifyDataSetChanged();
    }

    class SelectedImageViewHolder extends RecyclerView.ViewHolder{
        SelectedImageSingleListItemBinding selectedImageSingleListItemBinding;

        public SelectedImageViewHolder(SelectedImageSingleListItemBinding binding) {
            super(binding.getRoot());
            this.selectedImageSingleListItemBinding = binding;
        }
    }
}
