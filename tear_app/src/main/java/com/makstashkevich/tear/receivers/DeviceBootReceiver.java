package com.makstashkevich.tear.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.makstashkevich.tear.utils.Alarm;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) Alarm.send(context);
    }
}