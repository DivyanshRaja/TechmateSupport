package com.asprime.techmatesupport.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.adapter.SelectedImageAdapter;
import com.asprime.techmatesupport.databinding.FragmentRequestTicketBinding;
import com.asprime.techmatesupport.listners.ApiStatusListener;
import com.asprime.techmatesupport.listners.DashboardClickEventListener;
import com.asprime.techmatesupport.model.SMUserDataModel;
import com.asprime.techmatesupport.model.SelectedImageModel;
import com.asprime.techmatesupport.model.SoftwareModelData;
import com.asprime.techmatesupport.model.CompanyDataModel;
import com.asprime.techmatesupport.repository.TicketRepository;
import com.asprime.techmatesupport.utils.ApiStatusCode;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.FileUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.asprime.techmatesupport.viewmodel.TicketViewModel;

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

public class RequestTicketFragment extends Fragment implements View.OnClickListener, ApiStatusListener {
    FragmentRequestTicketBinding fragmentRequestTicketBinding;
    SelectedImageAdapter selectedImageAdapter;
    List<SelectedImageModel> selectedImageModelList;
    TicketViewModel ticketViewModel;
    int smUserId, selectedSoftwareId;
    String selectedStoreId;
    PreferenceHandler preferenceHandler;
    int countCall = 0;
    DashboardClickEventListener dashboardClickEventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRequestTicketBinding = FragmentRequestTicketBinding.inflate(getLayoutInflater());
        ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
        fragmentRequestTicketBinding.setLifecycleOwner(this);
        fragmentRequestTicketBinding.executePendingBindings();
        fragmentRequestTicketBinding.setViewModel(ticketViewModel);
        ticketViewModel.apiStatusListener = this;
        preferenceHandler = new PreferenceHandler(requireContext());
        return fragmentRequestTicketBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dashboardClickEventListener = (DashboardClickEventListener) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentRequestTicketBinding.uploadDocument.setOnClickListener(this);
        fragmentRequestTicketBinding.imageCapture.setOnClickListener(this);
        fragmentRequestTicketBinding.postDataBtn.setOnClickListener(this);

        setSelectedImageToRecyclerView();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.uploadDocument) {
            permissionGalleryLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else if (id == R.id.imageCapture) {
            permissionCameraLauncher.launch(Manifest.permission.CAMERA);
        } else if (id == R.id.postDataBtn) {
            checkValidation();
        }
    }

    ActivityResultLauncher<String> permissionCameraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> takePhotoFromCamera());

    ActivityResultLauncher<String> permissionGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> getImageFromAlbum());

    /* Launch gallery to select document and image */
    private void getImageFromAlbum() {
        mGetContent.launch("*/*");
    }

    /* Launch camera to capture image */
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageActivityResultLauncher.launch(intent);
    }

    /* To get image & documents from storage */
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                MultipartBody.Part multipart = null;
                String mimeType = null;
                if (uri != null)
                    mimeType = CommonUtils.getMimeType(requireContext(), uri);

                if (mimeType != null) {
                    try {
                        String encodeImage = null;
                        if (mimeType.equalsIgnoreCase("jpg")
                                || mimeType.equalsIgnoreCase("png")
                                || mimeType.equalsIgnoreCase("jpeg")
                                || mimeType.equalsIgnoreCase("webp")
                                || mimeType.equalsIgnoreCase("svg")) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);

                            File file = new File(FileUtils.getRealPath(requireContext(), uri));
                            @SuppressLint("DefaultLocale")
                            double fileSize = (file.length() / 1024D) / 1024D;
                            if (fileSize < 4.00) {
                                encodeImage = CommonUtils.encodeImageToBase64(bitmap);
                            } else {
                                CommonUtils.showAlertDialog("File size can not be more than 4MB", requireContext(), "");
                            }
                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            multipart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        } else {
                            InputStream inputStream = null;
                            try {
                                inputStream = requireContext().getContentResolver().openInputStream(uri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (inputStream != null) {
                                byte[] byteArray = null;
                                byte[] byteArrayTemp = null;
                                try {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    byte[] b = new byte[1024 * 11];
                                    int bytesRead;

                                    while ((bytesRead = inputStream.read(b)) != -1) {
                                        bos.write(b, 0, bytesRead);
                                    }

                                    byteArray = bos.toByteArray();
                                    byteArrayTemp = bos.toByteArray();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                assert byteArray != null;
                                double fileSize = (byteArray.length / 1024D) / 1024D;
                                if (fileSize < 4.00) {
                                    encodeImage = Base64.encodeToString(byteArrayTemp, Base64.DEFAULT);
                                } else {
                                    CommonUtils.showAlertDialog("File size can not be more than 4MB", requireContext(), "");
                                }

                                String fileName;
                                RequestBody requestFile;
                                if (mimeType.equalsIgnoreCase("mp3")
                                        || mimeType.equalsIgnoreCase("mp4")
                                        || mimeType.equalsIgnoreCase("WAV")) {
                                    File file = new File(FileUtils.getRealPath(requireContext(), uri));
                                    requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                    multipart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                                } else {
                                    fileName = FileUtils.getFilePath(requireContext(), uri);
                                    assert byteArray != null;
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

    /* To capture image from camera */
    ActivityResultLauncher<Intent> captureImageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
                    MultipartBody.Part multipart = MultipartBody.Part.createFormData("file", timeStamp + "_cp.png", requestFile);

                    double fileSize = (byteArray.length / 1024D) / 1024D;
                    if (fileSize < 4.00) {
                        String encodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        setNewSelectedDocument(encodeImage, "png", multipart);
                    } else {
                        CommonUtils.showAlertDialog("File size can not be more than 4MB", requireContext(), "");
                    }
                }
            });

    @SuppressLint("SetTextI18n")
    private void checkValidation() {
        if (smUserId == 0) {
            fragmentRequestTicketBinding.supportManagerSpinner.requestFocus();
            TextView selectedTextView = (TextView) fragmentRequestTicketBinding.supportManagerSpinner.getSelectedView();
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText("Select Support User"); // actual error message
            fragmentRequestTicketBinding.supportManagerSpinner.performClick();
        } else if (selectedSoftwareId == 0) {
            fragmentRequestTicketBinding.softwareSpinner.requestFocus();
            TextView selectedTextView = (TextView) fragmentRequestTicketBinding.softwareSpinner.getSelectedView();
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText("Select Software"); // actual error message
            fragmentRequestTicketBinding.softwareSpinner.performClick();
        } else if (selectedStoreId.length() == 0) {
            fragmentRequestTicketBinding.storeSpinner.requestFocus();
            TextView selectedTextView = (TextView) fragmentRequestTicketBinding.storeSpinner.getSelectedView();
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText("Select Store"); // actual error message
            fragmentRequestTicketBinding.storeSpinner.performClick();
        } else if (fragmentRequestTicketBinding.ticketTitle.length() == 0) {
            fragmentRequestTicketBinding.ticketTitle.setError("Enter Subject");
            fragmentRequestTicketBinding.ticketTitle.requestFocus();
        } else if (fragmentRequestTicketBinding.ticketDetailsEt.length() == 0) {
            fragmentRequestTicketBinding.ticketDetailsEt.setError("Enter Message");
            fragmentRequestTicketBinding.ticketDetailsEt.requestFocus();
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("UserName", preferenceHandler.getUser_name());
            hashMap.put("UserID", Integer.parseInt(preferenceHandler.getUser_id()));
            hashMap.put("TrnTitle", Objects.requireNonNull(fragmentRequestTicketBinding.ticketTitle.getText()).toString());
            hashMap.put("CustCode", selectedStoreId);
            hashMap.put("SoftID", selectedSoftwareId);
            hashMap.put("Details", Objects.requireNonNull(fragmentRequestTicketBinding.ticketDetailsEt.getText()).toString());
            hashMap.put("SupportUserID", smUserId);
            hashMap.put("LatLog", "0");
            ticketViewModel.createTicket(hashMap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ticketViewModel.getAllowedCompanyData().observe(getViewLifecycleOwner(), smUserDataModels -> {
            if (smUserDataModels != null && smUserDataModels.size() > 0) {
                setStoreSpinnerData(smUserDataModels);
            } else {
                setUserSpinnerData(new ArrayList<>());
            }
        });

        ticketViewModel.getSoftwareData().observe(getViewLifecycleOwner(), uList -> {
            if (uList != null && uList.size() > 0) {
                setSoftwareSpinnerData(uList);
            }
        });
    }

    private void setStoreSpinnerData(List<CompanyDataModel> storeSpinnerDataList) {
        // Create the instance of ArrayAdapter
        ArrayAdapter<CompanyDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, storeSpinnerDataList);
        /* set simple layout resource file
        for each item of spinner */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentRequestTicketBinding.storeSpinner.setAdapter(adapter);
        fragmentRequestTicketBinding.storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CompanyDataModel spinnerData = (CompanyDataModel) adapterView.getSelectedItem();
                selectedStoreId = spinnerData.getCustCode();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("CustCode", selectedStoreId);
                ticketViewModel.getSMUSerData(hashMap).observe(getViewLifecycleOwner(), smUserDataModels -> {
                    if (smUserDataModels != null && smUserDataModels.size() > 0) {
                        setUserSpinnerData(smUserDataModels);
                    } else {
                        setUserSpinnerData(new ArrayList<>());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUserSpinnerData(List<SMUserDataModel> userSpinnerDataList) {
        ArrayAdapter<SMUserDataModel> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userSpinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentRequestTicketBinding.supportManagerSpinner.setAdapter(adapter);
        fragmentRequestTicketBinding.supportManagerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SMUserDataModel smUserDataModel = (SMUserDataModel) parent.getSelectedItem();
                smUserId = smUserDataModel.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSoftwareSpinnerData(List<SoftwareModelData> softwareSpinnerData) {
        // Create the instance of ArrayAdapter
        ArrayAdapter<SoftwareModelData> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, softwareSpinnerData);
        /* set simple layout resource file
        for each item of spinner */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentRequestTicketBinding.softwareSpinner.setAdapter(adapter);
        fragmentRequestTicketBinding.softwareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SoftwareModelData softwareSpinnerData = (SoftwareModelData) adapterView.getSelectedItem();
                selectedSoftwareId = Integer.parseInt(softwareSpinnerData.getSoftID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /* method to showing selected image */
    private void setSelectedImageToRecyclerView() {
        selectedImageModelList = new ArrayList<>();
        fragmentRequestTicketBinding.selectedImageRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fragmentRequestTicketBinding.selectedImageRecycler.setHasFixedSize(true);
        selectedImageAdapter = new SelectedImageAdapter(selectedImageModelList, requireContext());
        selectedImageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (selectedImageModelList.size() == 0) {
                    fragmentRequestTicketBinding.selectedImageRecycler.setVisibility(View.GONE);
                } else {
                    fragmentRequestTicketBinding.selectedImageRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        fragmentRequestTicketBinding.selectedImageRecycler.setAdapter(selectedImageAdapter);
    }

    /* method to update selected image list after select document or capture image*/
    private void setNewSelectedDocument(@NonNull String base64UImage, @NonNull String imageType, @NonNull MultipartBody.Part file) {
        SelectedImageModel selectedImageModel = new SelectedImageModel();
        selectedImageModel.setBaseImage(base64UImage);
        selectedImageModel.setImageType(imageType);
        selectedImageModel.setFile(file);
        if (selectedImageAdapter.getListSize() != 5) {
            selectedImageAdapter.setEmployeeList(selectedImageModel);
        } else {
            CommonUtils.showAlertDialog("Max.  5 Files allowed", requireContext(), "");
        }
    }

    @Override
    public void onRequestStart() {
        fragmentRequestTicketBinding.setProgressBarVisibility(true);
    }

    @Override
    public void onRequestComplete(int statusCode, String result, Object obj) {
        if (statusCode == ApiStatusCode.SUCCESS_CODE) {
            countCall = 0;
            if(selectedImageModelList.size() > 0){
                postTicketDocument(Integer.parseInt(result));
            } else {
                fragmentRequestTicketBinding.setProgressBarVisibility(false);
                fragmentRequestTicketBinding.ticketTitle.setText("");
                fragmentRequestTicketBinding.ticketDetailsEt.setText("");
                dashboardClickEventListener.onMenuItemClick("pendingTicket");
                CommonUtils.showAlertDialog("Your request has been submitted, Ticket No. #"+result+" without file", requireContext(), "success");
            }
        } else if (statusCode == ApiStatusCode.ERROR_CODE) {
            CommonUtils.showAlertDialog(result, requireContext(), "");
        }
    }

    private void postTicketDocument(int TicketNo){
        for (SelectedImageModel selectedImageModel : selectedImageModelList) {
            countCall += new TicketRepository(requireContext()).postDocument(TicketNo, selectedImageModel.getFile());
        }

        if(countCall == selectedImageModelList.size()){
            CommonUtils.progressBarState(false, requireContext());
            String msg = "Your request has been submitted, Ticket No. #" + TicketNo + " with file";
            CommonUtils.showAlertDialog(msg, requireContext(), "success");
            fragmentRequestTicketBinding.setProgressBarVisibility(false);
            fragmentRequestTicketBinding.ticketTitle.setText("");
            fragmentRequestTicketBinding.ticketDetailsEt.setText("");
            CommonUtils.showAlertDialog(msg, requireContext(), "success");
            selectedImageModelList.clear();
            selectedImageAdapter.notifyDataSetChanged();
            dashboardClickEventListener.onMenuItemClick("pendingTicket");
        }
    }

    @Override
    public void onRequestFailure(String msg) {
        fragmentRequestTicketBinding.setProgressBarVisibility(false);
        CommonUtils.showAlertDialog(msg, requireContext(), "");
    }
}
