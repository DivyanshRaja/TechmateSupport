package com.asprime.techmatesupport.model;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import com.asprime.techmatesupport.BuildConfig;
import com.asprime.techmatesupport.R;
import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;

public class FileDetials {
    @SerializedName("FileName")
    public String FileName;
    @SerializedName("FileExt")
    public String FileExt;
    @SerializedName("FilePath")
    public String FilePath;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileExt() {
        return FileExt;
    }

    public void setFileExt(String fileExt) {
        FileExt = fileExt;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    @BindingAdapter({"imageUrl","imageType"})
    public static void bindImage(@NonNull ImageView imageView, String imageUrl, String imageType) {
        if(imageUrl != null){
            Context context = imageView.getContext();
            if(imageType.equalsIgnoreCase("jpg")
                    || imageType.equalsIgnoreCase("jpeg")
                    || imageType.equalsIgnoreCase("png")){
                Glide.with(imageView.getContext())
                        .load(BuildConfig.IMAGE_URL+imageUrl)
                        .into(imageView);
            } else if(imageType.equalsIgnoreCase("mp4")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_videocam_24, null));
            } else if(imageType.equalsIgnoreCase("xlxs") || imageType.equalsIgnoreCase("xls")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.excel, null));
            } else if(imageType.equalsIgnoreCase("csv")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.csv, null));
            } else if(imageType.equalsIgnoreCase("docx") || imageType.equalsIgnoreCase("doc")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_insert_drive_file_24, null));
            } else if(imageType.equalsIgnoreCase("pdf")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.pdf, null));
            } else if(imageType.equalsIgnoreCase("txt")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_sharp_txt_24, null));
            } else if(imageType.equalsIgnoreCase("mp3") || imageType.equalsIgnoreCase("wav") || imageType.equalsIgnoreCase("amr")){
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_music_note_24, null));
            }
        }
    }
}
