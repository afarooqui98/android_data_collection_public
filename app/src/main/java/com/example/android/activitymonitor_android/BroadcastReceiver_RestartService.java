package com.example.android.activitymonitor_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiver_RestartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BroadcastReceiver", "Restarting DataCollector Service");
        context.startService(new Intent(context, DataCollector.class));
    }
}
