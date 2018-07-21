package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE); // activity service: get global system state
        String mpackageName;
        if(android.os.Build.VERSION.SDK_INT > 20){
            mpackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        }
        else{
            mpackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        Log.e("Foremost Application:", mpackageName);
    }
}
