package com.ilyaeremin.pubnubchatexample;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ereminilya on 13/3/17.
 */

public class PushNotificationsListener extends FirebaseMessagingService {

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.d("PUSH LISTENER", "PUSH RECEIVED");
        Log.d("PUSH LISTENER", "Title is: " + title);
        Log.d("PUSH LISTENER", "Body : " + body);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_man_suka)
            .setContentTitle(title)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setColor(this.getResources().getColor(R.color.colorPrimary))
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setVibrate(new long[]{0, 500})
            .setContentIntent(pendingIntent);

        android.app.NotificationManager notificationManager = (android.app.NotificationManager)
            this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}