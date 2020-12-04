package com.operationcodify.cavoid.utilities;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.operationcodify.cavoid.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppNotificationHandler {


    private static final int NOTIFICATION_ID = 0;

    //Notification channel ID
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    /**
     * Creates the application's default notification.
     * @param context The calling context
     * @param title The title for the notification
     * @param message The message to display to the user
     */
    public static void deliverNotification(Context context, String title, String message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

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
