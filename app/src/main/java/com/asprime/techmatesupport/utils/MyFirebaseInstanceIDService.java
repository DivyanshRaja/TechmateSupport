package com.asprime.techmatesupport.utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.asprime.techmatesupport.CommonActivity;
import com.asprime.techmatesupport.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.util.Objects;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    public static String TAG = "Notification Service";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            sendNotification(title, body);
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message payload Notification Body: " + new Gson().toJson(remoteMessage.getNotification()));
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        PreferenceHandler preferenceHandler = new PreferenceHandler(getApplication().getApplicationContext());
        preferenceHandler.setUser_firebase_token(s);
    }

    private void sendNotification(String title, String msg) {
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(getApplicationContext(), CommonActivity.class);
        notificationIntent.putExtra("notificationFlag", "notificationFlag");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String[] separated = Objects.requireNonNull(title).split("-");
        String titleAppName = separated[0];
        String titleCompanyName = separated[1];
        String contentTitle = titleAppName + "-" + titleCompanyName;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.support_logo)
                        .setContentTitle(contentTitle)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setContentIntent(intent)
                        .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
