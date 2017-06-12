package com.roosterr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSBootReceiver extends BroadcastReceiver {
    ScheduleAlaramReceiver alarm;

    public SMSBootReceiver() {
        this.alarm = new ScheduleAlaramReceiver();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            this.alarm.resetAllAlarms(context, 0);
        }
    }
}
