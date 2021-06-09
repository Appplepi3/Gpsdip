package com.example.gpsdiplom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivityhome extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;
    SharedPreferences mSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityhome);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);



        mSetting = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

       

    }
}