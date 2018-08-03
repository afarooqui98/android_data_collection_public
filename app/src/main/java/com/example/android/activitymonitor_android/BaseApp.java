package com.example.android.activitymonitor_android;

import android.app.Application;

public class BaseApp extends Application{
    Test mTest;

    public void onCreate(){
        super.onCreate();

        mTest = new Test();
    }

    public Test getObserver(){
        return mTest;
    }
}
