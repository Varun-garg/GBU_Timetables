package com.varun.gbu_timetables.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.varun.gbu_timetables.MainActivity;
import com.varun.gbu_timetables.R;

import java.util.Map;
import java.util.Random;

/**
 * Created by varun on 7/30/2016.
 */
public class FirebaseNotificationReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = this.getClass().getSimpleName();
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Map<String, String> data = remoteMessage.getData();

        String DebugTAG = data.get("debug");

        if (DebugTAG != null) {
            if (DebugTAG.equalsIgnoreCase("true"))
                return;
        }

        String title = getString(R.string.app_name);
        sendNotification(title, remoteMessage.getNotification().getBody(), data);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String messageTitle, String messageBody, Map<String, String> Data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500, 500, 500, 500, 500};

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_statue_buddha)
                .setColor(getResources().getColor(R.color.app_bg_dark))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        for (Map.Entry<String, String> entry : Data.entrySet()) {
            Log.d(this.getClass().getSimpleName(), "adding " + entry.getKey() + "/" + entry.getValue());
            notificationIntent.putExtra(entry.getKey(), entry.getValue());
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int DummyUniqueInt = new Random().nextInt(543254);
        PendingIntent myIntent = PendingIntent.getActivity(getApplicationContext(), DummyUniqueInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(myIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

}
