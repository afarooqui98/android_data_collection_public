package com.example.android.activitymonitor_android;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Observable;

public class MainActivity extends AppCompatActivity{
    Intent mServiceIntent;
    private DataCollector mDataCollector;
    private final int MY_PERMISSION_REQUEST_PACKAGE_USAGE_STATS=1;

    Context ctx;

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        //lines that are commented are supposed to check for permissions, DO NOT WORK
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.PACKAGE_USAGE_STATS},
//                    MY_PERMISSION_REQUEST_PACKAGE_USAGE_STATS);
//            // Permission is not granted
//        } else{
//            mDataCollector = new DataCollector("Monitor");
//            mServiceIntent = new Intent(getCtx(), mDataCollector.getClass());
//            Log.i("MAINACT", "onCreate, Service about to start");
//            if (!isMyServiceRunning(DataCollector.class)) {
//                startService(mServiceIntent);
//            }
//        }
        if (!isUsageStatsGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            //TODO: pause here till intent is closed
        }

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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_PACKAGE_USAGE_STATS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);

                        mDataCollector = new DataCollector("Monitor");
                        mServiceIntent = new Intent(getCtx(), mDataCollector.getClass());
                        Log.i("MAINACT", "onCreate, Service about to start");
                        if (!isMyServiceRunning(DataCollector.class)) {
                            startService(mServiceIntent);
                        }
                    // permission was granted, Do the
                    //  task you need to do.
                } else {
                    // Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
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

    private boolean isUsageStatsGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}