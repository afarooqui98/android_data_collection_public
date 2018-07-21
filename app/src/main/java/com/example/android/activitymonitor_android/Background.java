package com.example.android.activitymonitor_android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import Service;

public class Background extends Service {
    public static class YourService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // do your jobs here
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
