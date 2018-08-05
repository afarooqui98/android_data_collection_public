package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Observable;

public class MainActivity extends AppCompatActivity{
    Intent mServiceIntent;
    private DataCollector mDataCollector;

    Context ctx;

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);

        mDataCollector = new DataCollector("Monitor");
        mServiceIntent = new Intent(getCtx(), mDataCollector.getClass());
        Log.i("MAINACT", "onCreate, Service about to start");
        if (!isMyServiceRunning(DataCollector.class)) {
            startService(mServiceIntent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!isMyServiceRunning(DataCollector.class)) {
            Log.e("Test", "service not running");
        }
        else{
            Log.e("Test", "service running");
        }
    }

    //TODO: check if Service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy");
        super.onDestroy();
    }
}