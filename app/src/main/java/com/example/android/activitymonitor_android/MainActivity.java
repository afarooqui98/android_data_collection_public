package com.example.android.activitymonitor_android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Observable;

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
        stopService(mServiceIntent); // Calls DataCollector.stopSelf().
        Log.i("MAINACT", "onDestroy");
        super.onDestroy();
    }
/*
    void addObserver(ForegroundObserver fo) {

    }
    */
}



/*
///////// GARBO
        OnClickListener {
    BaseApp myBase;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myBase = (BaseApp) getApplication();
        myBase.getObserver().addObserver(this);
        // myBase.getObserver().setValue(10);

        btn = (Button) findViewById(R.id.button1);
        btn.setText("value: " + myBase.getObserver().getValue());
        btn.setOnClickListener(this);

    }

    @Override
    public void update(Observable observable, Object data) {
        // This method is notified after data changes.
        Toast.makeText(this, "I am notified" + myBase.getObserver().getValue(),
                0).show();
        btn.setText("value: " + myBase.getObserver().getValue());

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(ObjectObserverPattern.this,
                SecondActivity.class));

    }
}
*/