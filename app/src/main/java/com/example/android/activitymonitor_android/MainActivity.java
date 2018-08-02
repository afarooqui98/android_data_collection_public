package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    Intent mServiceIntent;
    private DataCollector mDataCollector;
    BaseApp myBase;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        mDataCollector = new DataCollector(getCtx());
        mServiceIntent = new Intent(getCtx(), mDataCollector.getClass());

        myBase = (BaseApp) getApplication();
        myBase.getObserver().addObserver(this);

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

    public void update(Observable observable, Object data){
        Log.e("Notification", myBase.getObserver().getValue());
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
        myBase.getObserver().setValue("After Value Changed!");
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent); // Service must restart itself with broadcast
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
