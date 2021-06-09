package com.example.gpsdiplom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.media.app.NotificationCompat;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MainActivitydistant extends AppCompatActivity implements LocationListener {

    protected TextView tw_long;
    protected TextView tw_lat;
    protected TextView tw_bar;
    protected TextView tw_proideno;
    protected TextView tw_alt;
    protected TextView tw_acc;
    protected TextView tw_provider;
    protected String providerSelection;
    protected LocationManager locationManager;
    private ProgressBar pb;
    private int total_dist;
    private int rest_dist;
    private int distantse;
    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;
    private NotificationManager notificationManager;
    private static final int Notify_ID = 1;
    private static final String Channel_ID = "progress_Notify";
    private Location lastLocation;
    protected TextView tw_total, type_move;
    Button bt_maps, bt_home, bt_distant;
    int pop;
    int pll;
    String pl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitydistant);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);




        bt_maps = findViewById(R.id.bt_maps_d);
        bt_home = findViewById(R.id.bt_home_m);
        bt_distant = findViewById(R.id.bt_distant_m);
        tw_long = findViewById(R.id.tw_long);
        tw_lat = findViewById(R.id.tw_lat);
        tw_alt = findViewById(R.id.tw_alt);
        tw_acc = findViewById(R.id.tw_acc);
        tw_provider = findViewById(R.id.tw_provider);
        pb = findViewById(R.id.progressBar);
        tw_bar = findViewById(R.id. tw_progress_bar);
        tw_total = findViewById(R.id.tw_total);
        tw_proideno = findViewById(R.id.tw_proideno);
        type_move = findViewById(R.id.type_move);


        pb.setMax(100000);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(MainActivitydistant.this, permissions, 1);
        }

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        providerSelection = "gps";
        tw_provider.setText("Источник Сигнала: GPS");
        locationManager.requestLocationUpdates(providerSelection, 1000, 5, this);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }
    public void setDistant(String distant){
        pb.setMax(Integer.parseInt(distant));
        rest_dist = Integer.parseInt(distant);
        distantse = Integer.parseInt(distant);
        tw_bar.setText(distant);

    }
    private void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Dialog_title_bar);
        ConstraintLayout cl = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_bar_layout, null);
        builder.setNeutralButton("сбросить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pb.setProgress(0);
                total_dist = 0;
                rest_dist = 0;
                distantse = 0;
                tw_total.setText("0");
                tw_bar.setText("0");

            }
        });
        builder.setNegativeButton(R.string.Dialog_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.Dialog_button_bar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                EditText ed = alertDialog.findViewById(R.id.ed_bar_title);
                pb.setProgress(0);
                if (ed != null){
                    if (!ed.getText().toString().equals(""))setDistant(ed.getText().toString());
                }
                tw_total.setVisibility(View.VISIBLE);
                tw_proideno.setVisibility(View.VISIBLE);
            }
        });
        builder.setView(cl);

        builder.show();
    }

    public void OnClickprogressbar(View view){
        ShowDialog();
    }

    @SuppressLint("ResourceAsColor")
    private void updateDistance(Location location){
        if(location.hasSpeed() && lastLocation != null){

            if(distantse > total_dist) total_dist += lastLocation.distanceTo(location);
            if(rest_dist > 0){ rest_dist -= lastLocation.distanceTo(location);}
                    else{
                tw_bar.setText("Нажми чтобы ввести расстояние");
            }
                  // pl = tw_bar.getText();
                   //pll = Integer.parseInt(pl);

                    //if (pll == 0){
                      //  tw_bar.setText("Нажми чтобы ввести расстояние");
                    

            pb.setProgress(total_dist);

            boolean isSwitch_sound = mSettings.getBoolean("sound_notify",false);
            boolean isSwitch_vibrate = mSettings.getBoolean("vibrate_notify",false);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            int fullpb = pb.getProgress();


                  if(distantse > 1){  pop = fullpb / distantse * 100;}



            if (pop == 100) {
                Intent intent = new Intent(getApplicationContext(), MainActivitydistant.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(getApplicationContext(), Channel_ID);
                builder.setAutoCancel(false);
                builder.setWhen(System.currentTimeMillis());
                builder.setContentIntent(pendingIntent);
                builder.setContentTitle("Дистанция");
                builder.setContentText("Вы прошли заданную дистанцию");

               if ( isSwitch_sound == true){
                   builder.setSound(defaultSoundUri);
               }

               if ( isSwitch_vibrate == true){
                  builder.setVibrate(new long[] { 1000, 1000});
               }
                builder.setPriority(PRIORITY_HIGH);
                builder.setSmallIcon(R.mipmap.ic_launcher_round);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.finish));

                createNotification(notificationManager);
                notificationManager.notify(Notify_ID, builder.build());


            }

        }
        lastLocation = location;
        tw_total.setText(String.valueOf(total_dist));
        tw_bar.setText(String.valueOf(rest_dist));
    }

    public static void createNotification(NotificationManager manager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Channel_ID, Channel_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        tw_long.setText("");
        tw_lat.setText("");
        tw_alt.setText("");
        tw_acc.setText("");
        locationManager.removeUpdates(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(MainActivitydistant.this, permissions, 1);
        }
        locationManager.requestLocationUpdates(providerSelection, 1000, 5, this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (grantResult[0] < 0 && grantResult[1] < 0) {
            tw_lat.setText("Нет GPS");
            tw_long.setText("");
            tw_alt.setText("");
            tw_acc.setText("");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        float longitude = (float) location.getLongitude();
        float latitude = (float) location.getLatitude();
        float altitude = (float) location.getAltitude();
        float acceleration = (float) (location.getSpeed() * 3.6);
        tw_lat.setText(String.format("%.6f°", latitude));
        tw_long.setText(String.format("%.6f°", longitude));
        tw_alt.setText(String.format("%.1f m", altitude));
        tw_acc.setText(String.format("%.1f km/h", acceleration));
        updateDistance(location);
        typeMoveSpeed(location);
    }


    public void typeMoveSpeed(Location location){
        int switchVariable = 0;
        float move = (float) (location.getSpeed() * 3.6);

        if(move == 0) switchVariable = 0;
        else if (move >= 1 && move <= 5) switchVariable = 1;
        else if (move >= 6 && move <= 9) switchVariable = 2;
        else if (move >= 10 && move <= 29) switchVariable = 3;
        else if (move >= 30 && move <= 299) switchVariable = 4;
        else if (move >= 300) switchVariable = 5;



        switch (switchVariable){
            case 0:
                type_move.setText(R.string.move_1);
                break;
            case 1:
                type_move.setText(R.string.move_2);
                break;
            case 2:
                type_move.setText(R.string.move_3);
                break;
            case 3:
                type_move.setText(R.string.move_4);
                break;
            case 4:
                type_move.setText(R.string.move_5);
                break;
            case 5:
                type_move.setText(R.string.move_6);
                break;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        tw_lat.setText("Нет GPS");
        tw_long.setText("");
        tw_alt.setText("");
        tw_acc.setText("");
        locationManager.removeUpdates(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(MainActivitydistant.this, permissions, 1);
        }
        locationManager.requestLocationUpdates(providerSelection, 1000, 5, this);
    }

    public void onclickhom (View view){
        startActivity(new Intent(MainActivitydistant.this, MainActivity.class));
    }

    public void onclickmap (View view){
        startActivity(new Intent(MainActivitydistant.this, MapsActivity.class));
    }
}