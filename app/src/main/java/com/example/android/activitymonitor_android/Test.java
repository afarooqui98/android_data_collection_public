package com.example.android.activitymonitor_android;

import java.util.Observable;

public class Test extends Observable {
    private String name = "first test";

    public String getValue(){
        return name;
    }

    public void setValue(String name){
        this.name = name;
        setChanged();
        notifyObservers();
    }

}
