package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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

    public void onPause(){
        super.onPause();
        mDataCollector = new DataCollector(getCtx());
        mServiceIntent = new Intent(getCtx(), mDataCollector.getClass());


        Log.i("MAINACT", "onPause, Service about to start");
        if (!isMyServiceRunning(DataCollector.class)) {
            startService(mServiceIntent);
        }
//
//        myBase.getObserver().setValue("After Value Changed!");
//        myBase.getObserver().addObserver(this);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                ActivityManager actvityManager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
//                List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
//                for(ActivityManager.RunningAppProcessInfo runningProInfo:procInfos){
//                    Log.d("Running Processes", "()()"+runningProInfo.processName);
//                }
//            }
//        }, 10000);
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent); // Service must restart itself with broadcast
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
