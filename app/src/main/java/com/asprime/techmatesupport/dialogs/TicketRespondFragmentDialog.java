package com.asprime.techmatesupport.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.SelectedImageAdapter;
import com.asprime.techmatesupport.databinding.TicketRespondLayoutBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.DashboardClickEventListener;
import com.asprime.techmatesupport.listners.OnClickHandlerInterface;
import com.asprime.techmatesupport.listners.RefreshListData;
import com.asprime.techmatesupport.model.SelectedImageModel;
import com.asprime.techmatesupport.repository.TicketRepository;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.FileUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TicketRespondFragmentDialog extends DialogFragment implements OnClickHandlerInterface, ApiStatusListener {
    TicketRespondLayoutBinding ticketRespondLayoutBinding;
    List<SelectedImageModel> selectedImageModelList;
    SelectedImageAdapter selectedImageAdapter;
    int ticketNo;
    String custCode;
    TicketViewModel ticketViewModel;
    int countCall = 0;
    static RefreshListData refreshListData;

    public static void registerListener(RefreshListData refreshListData) {
        TicketRespondFragmentDialog.refreshListData = refreshListData;
    }

    public static TicketRespondFragmentDialog getInstance(){
        return new TicketRespondFragmentDialog();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        ticketNo = getArguments().getInt("ticketNo");
        custCode = getArguments().getString("custCode");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        ticketRespondLayoutBinding = TicketRespondLayoutBinding.inflate(getLayoutInflater());
        View v = ticketRespondLayoutBinding.getRoot();
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        ticketRespondLayoutBinding.setLifecycleOwner(this);
        ticketRespondLayoutBinding.setOnClickInferface(this);
        ticketRespondLayoutBinding.setTicketViewModel(ticketViewModel);
        ticketRespondLayoutBinding.executePendingBindings();
        ticketRespondLayoutBinding.setTicketNo(String.valueOf(ticketNo));
        ticketViewModel.apiStatusListener = this;

        dialog.setContentView(v);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        setSelectedImageToRecyclerView();
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getTheme() {
        return R.style.CenterDialogStyleTheme;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.closeTicketRespondBtn) {
            this.dismiss();
        } else if (id == R.id.uploadDocument) {
            permissionGalleryLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else if (id == R.id.imageCapture) {
            permissionCameraLauncher.launch(Manifest.permission.CAMERA);
        } else if(id == R.id.updateTicketResponseBtn) {
            if(ticketRespondLayoutBinding.msgTxtEt.length() == 0){
                ticketRespondLayoutBinding.msgTxtEt.setError("Enter Message");
                ticketRespondLayoutBinding.msgTxtEt.requestFocus();
            } else {
                ticketViewModel.ticketRespond(ticketNo, custCode);
            }
        }
    }

    /* Request permission for camera */
    ActivityResultLauncher<String> permissionCameraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                takePhotoFromCamera();
            });

    /* Request permission for storage */
    ActivityResultLauncher<String> permissionGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                getImageFromAlbum();
            });

    /* To get image documents from storage */
    ActivityResultLauncher<String> mGetContent =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                MultipartBody.Part multipart= null;
                String mimeType = null;
                if(uri != null)
                    mimeType = CommonUtils.getMimeType(requireContext(), uri);

                if (mimeType != null) {
                    try {
                        String encodeImage = null;
                        if (mimeType.equalsIgnoreCase("jpg")
                                || mimeType.equalsIgnoreCase("png")
                                || mimeType.equalsIgnoreCase("jpeg")) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            File file = new File(FileUtils.getRealPath(requireContext(), uri));
                            @SuppressLint("DefaultLocale")
                            double fileSize = (file.length()/1024D)/1024D;
                            if(fileSize < 4.00) {
                                encodeImage = CommonUtils.encodeImageToBase64(bitmap);
                            } else {
                                CommonUtils.showAlertDialog("File size can not be more than 4MB", requireContext(), "");
                            }
                            RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            multipart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        }else {
                            InputStream inputStream = null;
                            try {
                                inputStream = requireContext().getContentResolver().openInputStream(uri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (inputStream != null){
                                byte[] byteArray = null;
                                byte[] byteArrayTemp = null;
                                try {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    byte[] b = new byte[1024 * 11];
                                    int bytesRead = 0;

                                    while ((bytesRead = inputStream.read(b)) != -1) {
                                        bos.write(b, 0, bytesRead);
                                    }

                                    byteArray = bos.toByteArray();
                                    byteArrayTemp = bos.toByteArray();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                double fileSize = (byteArray.length/1024D)/1024D;
                                if(fileSize < 4.00) {
                                    encodeImage = Base64.encodeToString(byteArrayTemp, Base64.DEFAULT);
                                } else {
                                    CommonUtils.showAlertDialog("File size can not be more than 4MB", requireContext(), "");
                                }

                                String fileName ="";
                                RequestBody requestFile;
                                if(mimeType.equalsIgnoreCase("mp3")
                                        || mimeType.equalsIgnoreCase("mp4")
                                        || mimeType.equalsIgnoreCase("WAV")) {
                                    File file = new File(FileUtils.getRealPath(requireContext(), uri));
                                    requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                    multipart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                                }else{
                                    fileName = FileUtils.getFilePath(requireContext(), uri);
                                    requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
                                    multipart = MultipartBody.Part.createFormData("file", fileName, requestFile);
                                }
                            }
                        }

                        if (encodeImage != null && !encodeImage.equals(""))
                            setNewSelectedDocument(encodeImage, mimeType, multipart);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    /* To capture image from storage */
    ActivityResultLauncher<Intent> captureImageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap tempBitmap = bitmap;

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
                    MultipartBody.Part multipart = MultipartBody.Part.createFormData("file", timeStamp + "_cp.png", requestFile);

                    double fileSize = (byteArray.length/1024D)/1024D;
                    if(fileSize < 4.00) {
                        String encodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        setNewSelectedDocument(encodeImage, "png", multipart);
                    } else {
                        CommonUtils.showAlertDialog("File size can not be more than 4MB", requireContext(), "");
                    }
                }
            });

    /* Launch gallery to select document and image */
    private void getImageFromAlbum() {
        mGetContent.launch("*/*");
    }

    /* Launch camera to capture image */
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageActivityResultLauncher.launch(intent);
    }

    /* method to showing selected image */
    private void setSelectedImageToRecyclerView() {
        selectedImageModelList = new ArrayList<>();
        ticketRespondLayoutBinding.selectedImageRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ticketRespondLayoutBinding.selectedImageRecycler.setHasFixedSize(true);
        selectedImageAdapter = new SelectedImageAdapter(selectedImageModelList, requireContext());
        selectedImageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (selectedImageModelList.size() == 0) {
                    ticketRespondLayoutBinding.selectedImageRecycler.setVisibility(View.GONE);
                } else {
                    ticketRespondLayoutBinding.selectedImageRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        ticketRespondLayoutBinding.selectedImageRecycler.setAdapter(selectedImageAdapter);
    }

    /* method to update selected image list after select document or capture image*/
    private void setNewSelectedDocument(@NonNull String base64UImage, @NonNull String imageType, @NonNull MultipartBody.Part file) {
        SelectedImageModel selectedImageModel = new SelectedImageModel();
        selectedImageModel.setBaseImage(base64UImage);
        selectedImageModel.setImageType(imageType);
        selectedImageModel.setFile(file);
        if(selectedImageAdapter.getListSize() != 5) {
            selectedImageAdapter.setEmployeeList(selectedImageModel);
        } else {
            CommonUtils.showAlertDialog("Max.  5 Files allowed", requireContext(), "");
        }
    }

    @Override
    public void onRequestStart() {
        ticketRespondLayoutBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        ticketRespondLayoutBinding.setProgressBarVisibility(false);
        if(statusCode == ApiStatusCode.ACTION_SUCCESS_CODE){
            if(selectedImageModelList.size() > 0) {
                postTicketDocument(ticketNo);
            } else {
                String msg = "Your request has been submitted, Ticket No. #" + ticketNo + " without file";
                CommonUtils.showAlertDialog(msg, requireContext(), "success");
                refreshListData.onRefresh();
                Objects.requireNonNull(getDialog()).dismiss();
            }
        } else {
            CommonUtils.showAlertDialog(result, requireContext(), "");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }


    private void postTicketDocument(int TicketNo){
        for (SelectedImageModel selectedImageModel : selectedImageModelList) {
            countCall += new TicketRepository(requireContext()).postDocument(TicketNo, selectedImageModel.getFile());
        }
        if(countCall == selectedImageModelList.size()){
            CommonUtils.progressBarState(false, requireContext());
            String msg = "Your request has been submitted, Ticket No. #" + TicketNo + " with file";
            CommonUtils.showAlertDialog(msg, requireContext(), "success");
            ticketRespondLayoutBinding.setProgressBarVisibility(false);
            ticketRespondLayoutBinding.msgTxtEt.setText("");
            selectedImageModelList.clear();
            selectedImageAdapter.notifyDataSetChanged();
            refreshListData.onRefresh();
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }
}
