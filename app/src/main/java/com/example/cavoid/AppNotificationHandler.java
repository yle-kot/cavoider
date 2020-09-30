package com.example.cavoid;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AppNotificationHandler {


    private static final int NOTIFICATION_ID = 0;

    //Notification channel ID
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    public static void deliverNotification(Context context, String title, String message){
        Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_trend_up)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
