package com.example.android.activitymonitor_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastRec_DataCollector extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BroadcastReceiver", "Restarting DataCollector Service");
        context.startService(new Intent(context, DataCollector.class));
    }
}
