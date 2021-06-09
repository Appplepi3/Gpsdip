package com.example.gpsdiplom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    HelperDB helper;
    SQLiteDatabase gpsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new HelperDB(this);
        gpsDB = helper.getWritableDatabase();
    }

    public void onclickHome (View view){
        startActivity(new Intent(MainActivity.this, MainActivityhome.class));
    }

    public void onclickdistant (View view){
        startActivity(new Intent(MainActivity.this, MainActivitydistant.class));
    }

    public void onclickMaps (View view){
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }

    public void omclicksavecoord(View view){
        startActivity(new Intent(MainActivity.this, Save_road_maps.class));
    }
}