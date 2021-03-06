package com.nadrowski.sylwester.bookland;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by korSt on 05.12.2016.
 */

public class NotificationService extends IntentService{

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    private static String message;
    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context, String m) {
        message = m;
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_START);
        return intent;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processStartNotification() {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("New message")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_action_book);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
