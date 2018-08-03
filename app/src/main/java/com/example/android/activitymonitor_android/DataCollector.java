package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DataCollector extends Service implements Observer{
    BaseApp myBase;

    public DataCollector(Context appContext) {
        super();
        Log.i("DataCollector", "obj created");
    }
    public DataCollector() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void update(Observable observable, Object data){
        Log.e("Notification", myBase.getObserver().getValue());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        myBase = (BaseApp) getApplication();
        myBase.getObserver().addObserver(this);
        onHandleWork(intent);
        return START_STICKY; //Will re-create after process is killed

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DataCollector", "onDestroy");
        Intent broadcastIntent = new Intent("Restart_DataCollector");
        sendBroadcast(broadcastIntent);
    }

    //TODO: track foreground applications here
        //Possible use get context or refresh to receive app data
    protected void onHandleWork(Intent workIntent) {
        Log.e("test", "reached");
//        String foregroundTaskPackageName;
//        String newTaskPackageName;
//        ActivityManager actMan = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT < 20) {
//            ActivityManager.RunningTaskInfo foreTask = actMan.getRunningTasks(1).get(0);
//            foregroundTaskPackageName = foreTask.topActivity.getPackageName();
//        }
//        else {
//            ActivityManager.RunningAppProcessInfo foreTask = actMan.getRunningAppProcesses().get(0);
//            foregroundTaskPackageName = foreTask.processName;
//        }
//        // this is the polling implementation: couldn't find a viable way of doing it without polling
//
//        do{
//            // introduce some type of time delay here
//
//            if (android.os.Build.VERSION.SDK_INT < 20) {
//                ActivityManager.RunningTaskInfo foreTask = actMan.getRunningTasks(1).get(0);
//                newTaskPackageName = foreTask.topActivity.getPackageName();
//            }
//            else {
//                ActivityManager.RunningAppProcessInfo foreTask = actMan.getRunningAppProcesses().get(0);
//                newTaskPackageName = foreTask.processName;
//            }
//            Log.i("DataCollector", "went through loop");
//        }while(!newTaskPackageName.equals(foregroundTaskPackageName));


        myBase.getObserver().setValue("After Value Changed!");
        myBase.getObserver().addObserver(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ActivityManager actvityManager = (ActivityManager) myBase.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
                for(ActivityManager.RunningAppProcessInfo runningProInfo:procInfos){
                    Log.d("Running Processes", "()()"+runningProInfo.processName);
                }
            }
        }, 10000);

        // This is where we "return" since the foreground app has now changed
        Log.i("DataCollector", "foreground app changed");
    }
}

