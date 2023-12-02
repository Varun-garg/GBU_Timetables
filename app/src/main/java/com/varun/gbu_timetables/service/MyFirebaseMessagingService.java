package com.varun.gbu_timetables.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.varun.gbu_timetables.MainActivity;
import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.Utility;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String savedToken = Utility.getFirebaseInstanceId(getApplicationContext());
        String defaultToken = getApplication().getString(R.string.pref_firebase_instance_id_default_key);
        Log.e("NEW_TOKEN", s);
        Log.d("NEW_TOKEN", s);
        if (s != null && !savedToken.equalsIgnoreCase(defaultToken))
        //currentToken is null when app is first installed and token is not available
        //also skip if token is already saved in preferences...
        {
            Utility.setFirebaseInstanceId(getApplicationContext(), s);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " +
                    remoteMessage.getNotification().getBody());
        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        final String title = remoteMessage.getData().get("title");
        final String msg = remoteMessage.getData().get("body");
        if (title != null && msg != null) {
            final Notification not = new NotificationCompat.Builder(this, "CHANNEL_WELCOME")
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setSmallIcon(R.drawable.ic_notification_statue_buddha)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManagerCompat compat = NotificationManagerCompat.from(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            compat.notify(33333, not);
        }
    }

    //    private void sendNotification(String from, String body) {
//Log.i("Notice-It",from);
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), "body", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
    private void sendNotification(String messageTitle, String messageBody) {

    }
}
