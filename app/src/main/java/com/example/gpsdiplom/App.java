package com.example.gpsdiplom;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App instance;
    private static Context context;
    static App getInstance(){
        return  instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        instance = this;
    }

    public static Context getAppContext() {
        return App.context;
    }

}

