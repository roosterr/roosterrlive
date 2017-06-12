package com.roosterr;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class SchedulingService extends IntentService {
    public static final String TAG = "SMSScheduler";

    public SchedulingService() {
        super("SchedulingService");
    }

    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
