package com.makstashkevich.tear.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.makstashkevich.tear.R;
import com.makstashkevich.tear.receivers.AlarmReceiver;

import java.util.Calendar;

public class Alarm {
    public static void send(Context context) {
        PendingIntent pendingIntent = getPendingIntent(context);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == Calendar.SUNDAY || week == Calendar.SATURDAY) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, prefs.getInt(context.getString(R.string.hour), 7));
        calendar.set(Calendar.MINUTE, prefs.getInt(context.getString(R.string.min), 0) - 1);
        calendar.set(Calendar.SECOND, 1);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static PendingIntent getPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    }
}
