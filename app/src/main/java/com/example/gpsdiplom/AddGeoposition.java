package com.example.gpsdiplom;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AddGeoposition extends Service {
    private Thread mThread;
    ScheduledExecutorService worker;
    HelperDB helper;
//    private static final int SYNC_TIME = 20 * 60 * 1000; // 20 minute
    private static final int SYNC_TIME = 1 * 30 * 1000; // 30 секунд
    SQLiteDatabase gpsDB;

    public AddGeoposition() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new HelperDB(this);
        gpsDB = helper.getWritableDatabase();
        Log.d("QQQQQQQQQQQQQQQQQQ", "onCreate: ");
        Toast.makeText(this, "Служба создана",
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Служба запущена",
                Toast.LENGTH_SHORT).show();
        startSyncService();
        Log.d("QQQQQQQQQQQQ", "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopThread() {
        Log.d("QQQQQQQQQQQQQQQ", "stopThread: ");
        worker = null;
        if (mThread != null && mThread.isAlive()) mThread.interrupt();
    }


    private void startSyncService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (worker == null) worker = Executors.newSingleThreadScheduledExecutor();

        if (mThread == null || !mThread.isAlive()) {
            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    workDB();

                    if (worker != null) {
                        worker.schedule(this, SYNC_TIME, TimeUnit.MILLISECONDS);
                    }
                }
            });
            mThread.start();
        }
    }

    private void workDB() {
        LocationManager locationManager = null;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.d("AAAAAAA", "workDB: locationManager create");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values.put("datetime", dateFormat.format(new Date()));
            values.put("latitude", String.valueOf(location.getLatitude()));
            values.put("longitude", String.valueOf(location.getLongitude()));
            gpsDB.insert("gpsdiplom2", null, values);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
    }

}