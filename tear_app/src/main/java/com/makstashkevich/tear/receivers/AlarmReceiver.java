package com.makstashkevich.tear.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.makstashkevich.tear.R;
import com.makstashkevich.tear.activity.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_noty, 1)
                        .setColor(Color.argb(100, 50, 50, 50))
                        .setContentTitle("Первой парой сегодня")
                        .setContentText("Обработка материалов и инструментов")
                        .setSubText("Доброе утро!");*/

       /* NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Понедельник");
        inboxStyle.addLine("1-2. Обработка материалов и инструментов");
        inboxStyle.addLine("3-4. Инженерная графика");
        inboxStyle.addLine("5-6. МРС");
        inboxStyle.addLine("7-8. Электротехника");
        mBuilder.setStyle(inboxStyle);*/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo_noty, 1)
                        .setColor(Color.argb(100, 50, 50, 50))
                        .setContentTitle("Добрый день")
                        .setContentText("Проверьте расписание на сегодня!")
                        .setSubText("Напоминалка");
        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}