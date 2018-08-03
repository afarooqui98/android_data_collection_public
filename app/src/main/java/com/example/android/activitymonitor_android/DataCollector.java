package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DataCollector extends IntentService {

    public DataCollector(String name) {
        super(name);
    }

    public DataCollector() {
        super("Default");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Create/allocate space for the dictionary for storing application runtimes.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
       // onHandleIntent(intent);
        return START_STICKY; //Will re-create after process is killed

    }

    @Override
    public void onDestroy() {
        Log.i("DataCollector", "onDestroy");
        Intent broadcastIntent = new Intent("com.example.android.activitymonitor_android.Restart_DataCollector");
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    //TODO: implement observer
        //Possible use get context or refresh to receive app data
    protected void onHandleIntent(Intent workIntent) {
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

        do{

            ActivityManager newActMan = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            // introduce some type of time delay here
            if (android.os.Build.VERSION.SDK_INT < 20) {
                ActivityManager.RunningTaskInfo foreTask = newActMan.getRunningTasks(1).get(0);
                newTaskPackageName = foreTask.topActivity.getPackageName();
            }
            else {
                ActivityManager.RunningAppProcessInfo foreTask = newActMan.getRunningAppProcesses().get(0);
                newTaskPackageName = foreTask.processName;
            }
            Log.i("DataCollector",  newTaskPackageName);
            // TODO: sleep for 10 secs. Do not use thread.sleep. Try: ScheduledExecutorService and either scheduleAtFixedRate or scheduleWithFixedDelay.
            // Will change later, just for debugging purposes -> May also be ok to use thread.sleep since we are running in a different thread now?
            try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                //do stuff
            }
        }while(newTaskPackageName.equals(foregroundTaskPackageName));

        // This is where we "return" since the foreground app has now changed
        Log.e("DataCollector", "foreground app changed");
    }
}
