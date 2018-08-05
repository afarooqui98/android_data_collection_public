package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TreeMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataCollector extends IntentService {
    public Handler handler = null;
    public static Runnable runnable = null;
    Map<String,String> dictionary = new HashMap<String,String>();
    private int index = 0;

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
        //Track foreground here
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                printForegroundTask();
                dictionary.put(String.format("%d", index++),printForegroundTask());
                handler.postDelayed(runnable, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);

        //onHandleIntent(intent);
        return START_STICKY; //Will re-create after process is killed

    }

    @Override
    public void onDestroy() {

        //Display contents of dictionary after app is killed to ensure proper storage in dictionary
        for (Map.Entry<String,String> entry : dictionary.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.e(key, value);
            // do stuff
        }
        Log.i("DataCollector", "onDestroy");
        Intent broadcastIntent = new Intent("com.example.android.activitymonitor_android.Restart_DataCollector");
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    //TODO (low priority): implement observer
        //Possible use get context or refresh to receive app data

    //TODO (low priority): cleaner way to do this?
    private String printForegroundTask() {
        String currentApp = null;
        long timeInForeGround = 500;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager)this.getSystemService("usagestats");
            long time = System.currentTimeMillis(); //attempting to find total foreground time for the application in question
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    timeInForeGround = usageStats.getTotalTimeInForeground();
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.e("adapter", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    protected void onHandleIntent(Intent workIntent) {

        /* check if usage stats is enabled. Make new function for this? */

        try {
            PackageManager packageManager = this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            Log.e("usagestats", "is enabled");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("usagestats", "is not enabled");
        }
        //TODO: only open settings if usage is NOT ENABLED. Above method is currently not working.

        //TODO: investigate any shortcomings with infinite while loop
//        while(true) {
//            printForegroundTask();
//            try {
//                //TODO: write to dictionary on cycle. Maybe use a timer instead of sleep, or query the system time.
//                Thread.sleep(5000);
//            }
//            catch(Exception n) {}
//        }
    }
}
