package com.asprime.techmatesupport.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.asprime.techmatesupport.LoginActivity;
import com.asprime.techmatesupport.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommonUtils {
    private static ProgressDialog progressBar;

    public static void changeStatusBar(Activity activityRef) {
        activityRef.getWindow().setStatusBarColor(activityRef.getResources().getColor(R.color.colorPrimary));
    }

    /**
     * To get powered by message
     * @param tv instance of TextView
     */
    @SuppressLint("SetTextI18n")
    public static void poweredByMessage(TextView tv) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String text = "<b>Powered by <a href=\"https://www.aspiresoft.co.ke/\">Aspire Software Ltd.</a></b>";
        tv.setText(Html.fromHtml(text));
        tv.append("\n v." + CommonUtils.getVersionName(tv.getContext()) + " " + Html.fromHtml("copyright \u00a9 2013 - " + year));
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Get app current version name
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Function to get file extension
     * @param encodedImage to get file extension
     * @return string value
     */
    public static String getFileExtension(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        InputStream is = new ByteArrayInputStream(decodedString);

        //Find out image type
        String mimeType = null;
        String fileExtension = null;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(is); //mimeType is something like "image/jpeg"
            String delimiter = "[/]";
            if (mimeType != null) {
                String[] tokens = mimeType.split(delimiter);
                fileExtension = tokens[1];
            }
        } catch (IOException ioException) {

        }
        return fileExtension;
    }

    /**
     * Get base64 image
     * @param bitmap to get base64 image
     * @return base64 string
     */
    public static String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static String convertFileToByteArray(InputStream inputStream) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 11];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Get mime type
     * @param context current context
     * @param uri uri to get MIME Type
     * @return string value
     */
    public static String getMimeType(Context context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension;
    }

    public static String encryptQR() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Africa/Nairobi"));
        String s = sdf.format(c.getTime());
        String Str = s + "!MaatajiHar@108";
        Log.d("TAG", "Time String: " + Str);
        String output = "";
        int nameLength = Str.length(); // length of the string used for the loop
        for (int i = 0; i < nameLength; i++) {   // while counting characters if less than the length add one
            char character = Str.charAt(i); // start on the first character
            int ascii = (int) character + 6; //convert the first character
            output += (char) ascii;
        }
        return output;
    }

    // To check internet connection is available or not
    public static boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("http://197.248.20.237/");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }

    public static void showAlertDialog(String msg, Context context, String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog_layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        TextView text = dialog.findViewById(R.id.messageText);
        if (type.equalsIgnoreCase("success")) {
            ImageView imageView = dialog.findViewById(R.id.imageIcon);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_check_circle_outline_24, null));
            text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        text.setText(msg);

        TextView dialogButton = dialog.findViewById(R.id.closeEditDeviceDialogBtn);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("exitToLogin")) {
                    logOut(context);
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public static List<String> getRightListFromString(String userRights) throws JSONException {
        JSONArray jsonArray = new JSONArray(userRights);
        Gson converter = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> list = converter.fromJson(String.valueOf(jsonArray), type);
        return list;
    }

    public static void progressBarState(boolean state, Context context) {
        if (progressBar == null) {
            progressBar = new ProgressDialog(context);
        }
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        if (state) {
            progressBar.show();
        } else {
            progressBar.hide();
        }
    }

    public static void logOut(Context context) {
        PreferenceHandler preferenceHandler = new PreferenceHandler(context);
        String firebase_token = preferenceHandler.getUser_firebase_token();
        preferenceHandler.clearPref();
        preferenceHandler.setUser_firebase_token(firebase_token);
        context.startActivity(new Intent(context, LoginActivity.class));
        ((Activity) context).finish();
    }
}
