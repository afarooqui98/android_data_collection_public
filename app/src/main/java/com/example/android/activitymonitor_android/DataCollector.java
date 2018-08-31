package com.example.android.activitymonitor_android;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageStats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
    public final String[] HISTORY_PROJECTION = new String[]{
            "_id", // 0
            "url", // 1
            "visits", // 2
            "date", // 3
            "bookmark", // 4
            "title", // 5
            "favicon", // 6
            "thumbnail", // 7
            "touch_icon", // 8
            "user_entered", // 9
    };

    public final int HISTORY_PROJECTION_TITLE_INDEX = 5;
    public final int HISTORY_PROJECTION_URL_INDEX = 1;
    public Handler handler = null;
    public static Runnable runnable = null;
    Map<String,Long> foregroundDict = null; //<foregroundTask, time spent>
    final int delay = 5000; // Delay between checks to foregroundTask

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
        foregroundDict = new HashMap<String,Long>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                //printForegroundTask();
                // TODO: Detect when the foreground app changes and update the time of the app we just left
                Map<String,Long> lastTwo =  getLastTwoForegroundTasks();
                //printing Last Two activities
                for (Map.Entry<String,Long> entry : lastTwo.entrySet()) {
                    String key = entry.getKey();
                    long value = entry.getValue();

                    if(key.equals("com.android.chrome")){
                        String chromeUrl = getLastUrl();
                    }
                    Log.e("display lastTwo" , key + ", time spent: " + Long.toString(value));
                }

                // Write previously ended activity to dictionary
                String prevTask = "" + lastTwo.keySet().toArray()[1];
                Long prevTime = lastTwo.get(prevTask);

                // String foregroundTask = printForegroundTask();

                if (foregroundDict.containsKey(prevTask)) {
                    //increment time spent
                    foregroundDict.put(prevTask, prevTime);
                }
                else {
                    //create key
                    foregroundDict.put(prevTask, prevTime);
                }//do something
                handler.postDelayed(this, delay);
            }
        }, delay);

        return START_STICKY; //Will re-create after process is killed
    }

    @Override
    public void onDestroy() {
        Log.e("DataCollector", "onDestroy. Printing dictionary contents:");
        //Display contents of dictionary after app is killed to ensure proper storage in dictionary
        for (Map.Entry<String,Long> entry : foregroundDict.entrySet()) {
            String key = entry.getKey();
            long value = entry.getValue();
            Log.e("display foregroundDict" , key + ", time spent: " + Long.toString(value));
        }
        MapToFile();
        Intent broadcastIntent = new Intent("com.example.android.activitymonitor_android.Restart_DataCollector");
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    // Returns a dictionary with current app first and then the previous foreground app along with their usage times
    private Map<String,Long> getLastTwoForegroundTasks() {
        String currentApp = null;
        Long timespent = null;
        Map<String,Long> previousTwoApps = new LinkedHashMap<String,Long>();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager)this.getSystemService("usagestats");
            long time = System.currentTimeMillis(); //attempting to find total foreground time for the application in question
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    timespent = mySortedMap.get(mySortedMap.lastKey()).getTotalTimeInForeground();
                    Log.e("adapter", "Current App in foreground is: " + currentApp + " " + timespent);
                    SortedMap<Long, UsageStats> tailless = mySortedMap.headMap(mySortedMap.lastKey());
                    String previousApp = tailless.get(tailless.lastKey()).getPackageName();
                    long previoustimespent = tailless.get(tailless.lastKey()).getTotalTimeInForeground();
                    Log.e("adapter", "Previous app in foreground was: " + previousApp + " " + previoustimespent);
                    previousTwoApps.put(currentApp,timespent);
                    previousTwoApps.put(previousApp,previoustimespent);
                    return previousTwoApps;
                }
            }
        } else {
            //Todo: get a dictionary instead of just the processName
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        return previousTwoApps;
       // return currentApp;
    }

    protected void onHandleIntent(Intent workIntent) {

        /* check if usage stats is enabled. Make new function for this? */
        //TODO: only open settings if usage is NOT ENABLED. Above method is currently not working.
    }

    protected void MapToFile(){
        File file = new File(getFilesDir() + "/map.ser");
        try {
            if(!file.exists()){
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(foregroundDict);
                oos.close();
                Log.e("1", "MapToFile: file created first time");
                // add something to the log
            }
            else {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Map map_to_update = (Map)ois.readObject();
                ois.close();
                // iterate through and combine the two maps then write an updated map
                for (Map.Entry<String, Long> entry : foregroundDict.entrySet()) {
                    String temp_key = entry.getKey();
                    Long  temp_val = entry.getValue();
                    Integer to_add = (Integer) map_to_update.get(temp_key);
                    if (to_add == null) {to_add = 0;}
                    map_to_update.put(temp_key,temp_val + to_add);
                    Log.d("key", temp_key);
                    Log.d("temp", String.valueOf(temp_val));
                    Log.d("add", String.valueOf(to_add));

                }
                FileOutputStream fos = new FileOutputStream(file,false); //look into this: should allow us to write over the existing file
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(map_to_update);
                oos.close();
                Log.e("2", "MapToFile: file updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLastUrl(){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Log.d("getLastUrl", "fetching url");
            //TODO: Find something that works for API > 22
        } else{
            Cursor cursor = this.getContentResolver().query(BOOKMARKS_URI,
                    HISTORY_PROJECTION, null, null,
                    HISTORY_PROJECTION[3] + " DESC");
            cursor.moveToNext();
            String url = cursor.getString(HISTORY_PROJECTION_URL_INDEX);
            cursor.close();
            Log.d("getLastURL", "" + url);
            return url;
        }
        return null;
    }
}
