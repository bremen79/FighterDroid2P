package com.android.fighterdroid2p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.Iterator;

import android.hardware.input.InputManager;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        if (!isMyServiceRunning(Controller2PService.class)) {
            Log.i("Bremen79", "Service not running");
            Intent serviceIntent = new Intent(this, Controller2PService.class);
            Log.i("Bremen79", "Starting service");
            Toast.makeText(this, "FighterDroid2P started", Toast.LENGTH_SHORT).show();
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            Log.i("Bremen79", "Service already running");
            Toast.makeText(this, "FighterDroid2P is already running", Toast.LENGTH_SHORT).show();
        }

        sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));

        finish();
    }

    // https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android/5921190#5921190
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        Iterator<ActivityManager.RunningServiceInfo> it = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE).iterator();
        while (it.hasNext()) {
            if (serviceClass.getName().equals(it.next().service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        Log.d("Bremen79", "Main Activity onDestroy");
        super.onDestroy();
    }
}
