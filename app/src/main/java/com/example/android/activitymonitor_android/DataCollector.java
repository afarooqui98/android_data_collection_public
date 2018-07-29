package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class DataCollector extends Service {
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //onHandleWork(intent);
        return START_STICKY; //Will re-create after process is killed

    }

    @Override
    public void onDestroy() {
        Log.i("DataCollector", "onDestroy");
        Intent broadcastIntent = new Intent("com.example.android.activitymonitor_android.Restart_DataCollector");
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    //TODO: track foreground applications here
        //Possible use get context or refresh to receive app data
    protected void onHandleWork(Intent workIntent) {
        Log.e("test", "reached");
        String foregroundTaskPackageName;
        String newTaskPackageName;
        ActivityManager actMan = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT < 20) {
            ActivityManager.RunningTaskInfo foreTask = actMan.getRunningTasks(1).get(0);
            foregroundTaskPackageName = foreTask.topActivity.getPackageName();
        }
        else {
            ActivityManager.RunningAppProcessInfo foreTask = actMan.getRunningAppProcesses().get(0);
            foregroundTaskPackageName = foreTask.processName;
        }
        // this is the polling implementation: couldn't find a viable way of doing it without polling

        //TODO: this needs to be in a separate thread, or application close will not be detected.
        do{
            // introduce some type of time delay here
            if (android.os.Build.VERSION.SDK_INT < 20) {
                ActivityManager.RunningTaskInfo foreTask = actMan.getRunningTasks(1).get(0);
                newTaskPackageName = foreTask.topActivity.getPackageName();
            }
            else {
                ActivityManager.RunningAppProcessInfo foreTask = actMan.getRunningAppProcesses().get(0);
                newTaskPackageName = foreTask.processName;
            }
            // TODO: print out the foreground process name instead of "went through loop"
            Log.i("DataCollector", "went through loop");
            // TODO: sleep for 10 secs. Do not use thread.sleep. Try: ScheduledExecutorService and either scheduleAtFixedRate or scheduleWithFixedDelay.
        }while(newTaskPackageName.equals(foregroundTaskPackageName));

        // This is where we "return" since the foreground app has now changed
        Log.e("DataCollector", "foreground app changed");
    }
}
