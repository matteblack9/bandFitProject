package com.bandfitproject.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.bandfitproject.FirstActivity;
import com.bandfitproject.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static String TAG = "FCM_MESSAGE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            final String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Body: " + body);

            Intent sendIntent = new Intent("com.bandfit.SEND_BROAD_CAST");
            sendIntent.putExtra("isBoolean", true);
            sendIntent.putExtra("sendInteger", 123);
            sendIntent.putExtra("sendString", "Intent String");
            sendBroadcast(sendIntent);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.notification) // 알림 영역에 노출 될 아이콘.
                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용

            /*Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 300);
                    toast.show();
                }
            });*/

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0x1001, notificationBuilder.build());
        }
    }

}
