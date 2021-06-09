package com.example.gpsdiplom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Save_road_maps extends AppCompatActivity {

    Button btn_save, btn_clear;
    Spinner sets;
    int[] minDist;
    private boolean permissionGranted;
    private static String FILE_NAME = "content.txt";
    private ListView recyclerView;
    SimpleCursorAdapter adapter;
    ListView listView;
    HelperDB helper;
    SQLiteDatabase gpsDB;
    Cursor infoDB;
    private boolean isWatched = false;

    public void onPermission()
    {
// Permision can add more at your convinient
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]
                            {
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.BLUETOOTH,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.CALL_PHONE
                            },
                    0
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_road_maps);

//        recyclerView = findViewById(R.id.lv_log_latlong);
        btn_save = findViewById(R.id.btn_save);
        btn_clear = findViewById(R.id.btn_clear);

        helper = new HelperDB(this);
        gpsDB = helper.getWritableDatabase();
        listView = findViewById(R.id.lv_log_latlong);
        showDataByDB();
    }

    public void onclicksave(View view) throws IOException {

        onPermission();
        KMLController.downloadKmlFile();

        if(minDist != null && !permissionGranted) {
            FileOutputStream fos = null;
            try {
                String line = Arrays.toString(minDist).replace("[", "").replace("]", "");
                line = line.replace(" ", "");
                FILE_NAME = sets.getSelectedItem().toString() + ".csv";
                fos = new FileOutputStream(getExternalPath());
                Toast.makeText(this, new File(String.valueOf(Environment.getExternalStorageDirectory()))+ "", Toast.LENGTH_SHORT).show();
                fos.write(line.getBytes());
            }
            catch(IOException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finally{
                try{
                    if(fos!=null)
                        fos.close();
                }
                catch(IOException ex){

                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


            Toast.makeText(this, "File '" + sets.getSelectedItem().toString() +"' saved successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private File getExternalPath() {
        return(new File(Environment.getExternalStorageDirectory(), FILE_NAME));
    }

    public void onclickclear(View view){
        helper.onUpgrade(gpsDB, 1, 2);
        showDataByDB();
    }

    public void onclickhom (View view){
        startActivity(new Intent(Save_road_maps.this, MainActivity.class));
    }

    public void onclickmap (View view){
        startActivity(new Intent(Save_road_maps.this, MapsActivity.class));
    }

    public void onclickhomemaps (View view){
        startActivity(new Intent(Save_road_maps.this, MainActivitydistant.class));
    }

    public void showDataByDB() {
        infoDB = gpsDB.rawQuery("SELECT * FROM gpsdiplom2", null);

        String[] track_fields = infoDB.getColumnNames();
        int[] views = {
                R.id.room_id,
                R.id.room_date,
                R.id.room_latitude,
                R.id.room_longitude
        };

        adapter = new SimpleCursorAdapter(this, R.layout.item_room_list, infoDB, track_fields, views, 0);
        listView.setAdapter(adapter);
    }

    public void startTrack(View view) {
        isWatched = !isWatched;
        Button btn = findViewById(R.id.button2);
        btn.setText(isWatched ? "Прекратить отслеживать" : "Начать отслеживать");
        if (isWatched) {
            startService(new Intent(this, AddGeoposition.class));
        } else {
            stopService(new Intent(this, AddGeoposition.class));
        }

    }
}