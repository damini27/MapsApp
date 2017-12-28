package com.example.user.googlemaps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class Splash extends Activity {


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        //connectivity manager checks for the internet connection
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            //display the alert dialog box if internet is not connected
            AlertDialog.Builder ad = new AlertDialog.Builder(Splash.this);
            ad.setTitle("Internet Required");
            ad.setMessage("Please turn on your Internet");
            //on click of settings option it takes you to the mobile's system settingd
            ad.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent pos = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(pos);
                }
            });

            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent mainIntent = new Intent(Splash.this, Activity1.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }
            });
            ad.setCancelable(false);
            ad.show();
        } else {
            Toast.makeText(Splash.this, "Internet is ON", Toast.LENGTH_SHORT).show();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Intent mainIntent = new Intent(Splash.this, Activity1.class);
                        Splash.this.startActivity(mainIntent);
                        Splash.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            t.start();
        }

    }

    }







