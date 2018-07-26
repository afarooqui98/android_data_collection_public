package com.example.android.activitymonitor_android;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class DataCollection extends Service {
    public DataCollection(Context appContext) {
        super();
        Log.i("Service created")
    }
    /*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY; //Will keep running after process is killed
    }
}
