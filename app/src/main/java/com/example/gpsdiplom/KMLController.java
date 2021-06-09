package com.example.gpsdiplom;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class KMLController {

    private static String fileName = "hello.kml";

    public static String getPatternKml(String fileName) throws IOException {
//        "patternKmlFile.txt"
        Context context = App.getAppContext();
        InputStream stream = context.getResources().getAssets().open(fileName);

        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();
        String tContents = new String(buffer);

        return tContents;
    }

    private static String getStringPlacemark(String str, String name, String description, String lat, String lng) {
        return String.format(str, name, description, lat, lng);
    }

    public static String getStringPlasemarks() throws IOException {
        String str = getPatternKml("patternKmlPlacemark.txt");

        HelperDB helper = new HelperDB(App.getAppContext());
        Cursor infoDB = helper.getCursor();
        String ans = "";
        if (!infoDB.moveToFirst()) {
            return "";
        }
        int dbLat = infoDB.getColumnIndex("latitude"),
                dbLong = infoDB.getColumnIndex("longitude"),
                dbName = infoDB.getColumnIndex("datetime");
        do {
            String latitude = infoDB.getString(dbLat);
            String longitude = infoDB.getString(dbLong);
            String datetime = infoDB.getString(dbName);

            ans += getStringPlacemark(str, datetime, "", latitude, longitude);

        } while (infoDB.moveToNext());

        return ans;
    }

    public static void downloadKmlFile() throws IOException {
        String pattern = getPatternKml("patternKmlFile.txt");
        String marks = getStringPlasemarks();

        String allText = String.format(pattern, marks);


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(App.getAppContext().openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(allText);
            outputStreamWriter.close();


        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        ClipboardManager clipboard = (ClipboardManager) App.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", allText);
        clipboard.setPrimaryClip(clip);


        Toast.makeText(App.getAppContext(), "KML скопирован в буфер обмена",
                Toast.LENGTH_SHORT).show();
//        Log.d("AAAAA", "downloadKmlFile: " + allText);


    }

}
