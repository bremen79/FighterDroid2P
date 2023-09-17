package com.android.fighterdroid2p;

import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller2PService extends Service {
    //private final IBinder mBinder = new Binder();
    private long clock = SystemClock.uptimeMillis();
    private FileInputStream fileInputStream = null;

    private static final int UP = 4;
    private static final int DOWN = 8;
    private static final int LEFT = 2;
    private static final int RIGHT = 1;
    private static final int P1 = 16384;
    private static final int P2 = 512;
    private static final int P3 = 256;
    private static final int P4 = 64;
    private static final int P5 = 128;
    private static final int P6 = 32768;
    private static final int START = 32;

    private boolean holdUP = false;
    private boolean holdDOWN = false;
    private boolean holdLEFT = false;
    private boolean holdRIGHT = false;
    private boolean holdP1 = false;
    private boolean holdP2 = false;
    private boolean holdP3 = false;
    private boolean holdP4 = false;
    private boolean holdP5 = false;
    private boolean holdP6 = false;
    private boolean holdSTART = false;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d("Bremen79", "Service created");
        startMyOwnForeground();
    }


    // https://androidwave.com/foreground-service-android-example/
    private void startMyOwnForeground() {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FighterDroid2P is running in background")
                .setPriority(1)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_NONE
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    // We map the joystick and buttons to numeric keypad keys. The reason is that we don't want to map to any key already used by retroarch and mame.
    // See the following for the retroarch accepted keyboard keys:
    // https://gist.github.com/Monroe88/0f7aa02156af6ae2a0e728852dcbfc90
    private void readUmidoKeys() {
        if (new File("/dev/umidokey").exists()) {
            Log.d("Bremen79", "umidokey device exists");
            try {
                Log.d("Bremen79", "Trying to open umidokey device");
                if (fileInputStream == null)
                    fileInputStream = new FileInputStream("/dev/umidokey");
                Log.d("Bremen79", "umidokey device opened");
                byte[] bArr = {0, 0, 0, 0};
                while (true) {
                    fileInputStream.read(bArr, 0, 4);
                    // no need to read Player 1, let's skip it to save time
                    //int i = ((bArr[1] & 255) << 8) | (bArr[0] & 255);
                    clock = SystemClock.uptimeMillis();
                    int i2 = ((bArr[3] & 255) << 8) | (bArr[2] & 255);
                    Log.d("Bremen79", String.format("i2: %d", i2));
                    if ((i2 & UP) != 0) {
                        if (!holdUP) {
                            pressKeyEvent(152);
                            holdUP = true;
                        }
                    } else {
                        if (holdUP) {
                            releaseKeyEvent(152);
                            holdUP = false;
                        }
                        if ((i2 & DOWN) != 0) {
                            if (!holdDOWN) {
                                pressKeyEvent(146);
                                holdDOWN = true;
                            }
                        } else if (holdDOWN) {
                            releaseKeyEvent(146);
                            holdDOWN = false;
                        }
                    }
                    if ((i2 & LEFT) != 0) {
                        if (!holdLEFT) {
                            pressKeyEvent(148);
                            holdLEFT = true;
                        }
                    } else {
                        if (holdLEFT) {
                            releaseKeyEvent(148);
                            holdLEFT = false;
                        }
                        if ((i2 & RIGHT) != 0) {
                            if (!holdRIGHT) {
                                pressKeyEvent(150);
                                holdRIGHT = true;
                            }
                        } else if (holdRIGHT) {
                            releaseKeyEvent(150);
                            holdRIGHT = false;
                        }
                    }
                    if ((i2 & P1) != 0) {
                        if (!holdP1) {
                            pressKeyEvent(151);
                            holdP1 = true;
                        }
                    } else if (holdP1) {
                        releaseKeyEvent(151);
                        holdP1 = false;
                    }
                    if ((i2 & P2) != 0) {
                        if (!holdP2) {
                            pressKeyEvent(153);
                            holdP2 = true;
                        }
                    } else if (holdP2) {
                        releaseKeyEvent(153);
                        holdP2 = false;
                    }
                    if ((i2 & P3) != 0) {
                        if (!holdP3) {
                            pressKeyEvent(149);
                            holdP3 = true;
                        }
                    } else if (holdP3) {
                        releaseKeyEvent(149);
                        holdP3 = false;
                    }
                    if ((i2 & P4) != 0) {
                        if (!holdP4) {
                            pressKeyEvent(145);
                            holdP4 = true;
                        }
                    } else if (holdP4) {
                        releaseKeyEvent(145);
                        holdP4 = false;
                    }
                    if ((i2 & P5) != 0) {
                        if (!holdP5) {
                            pressKeyEvent(147);
                            holdP5 = true;
                        }
                    } else if (holdP5) {
                        releaseKeyEvent(147);
                        holdP5 = false;
                    }
                    if ((i2 & P6) != 0) {
                        if (!holdP6) {
                            pressKeyEvent(161);
                            holdP6 = true;
                        }
                    } else if (holdP6) {
                        releaseKeyEvent(161);
                        holdP6 = false;
                    }
                    if ((i2 & START) != 0) {
                        if (!holdSTART) {
                            pressKeyEvent(158);
                            holdSTART = true;
                        }
                    } else if (holdSTART) {
                        releaseKeyEvent(158);
                        holdSTART = false;
                    }

                    // Here I am not sure what is the best number
                    // I took this from Team Encoder code?
                    Thread.sleep(16L);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        } else {
            Log.d("Bremen79", "no umidokey device found");
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("Bremen79", "Start command received");
        new Thread(this::readUmidoKeys).start();
        return START_STICKY;
    }

    // This is the easiest way I found to inject key events
    // see https://www.pocketmagic.net/injecting-events-programatically-on-android/
    private void injectKeyEvent(KeyEvent event) {
        Instrumentation inst = new Instrumentation();
        inst.sendKeySync(event);
    }

    @Override // android.app.Service
    public final void onDestroy() {
        Log.d("Bremen79", "Service onDestroy");
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
                Log.d("Bremen79", "Closed umidokey");
            } catch (Exception unused) {
            }
            fileInputStream = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

    // Should I implement an on task removed method?
    // see https://robertohuertas.com/2019/06/29/android_foreground_services/

    private void pressKeyEvent(int keyCode) {
        injectKeyEvent(new KeyEvent(clock, clock, KeyEvent.ACTION_DOWN, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
        Log.d("Bremen79", String.format("Keypress down sent, processing time: %d ms", SystemClock.uptimeMillis() - clock));
    }

    private void releaseKeyEvent(int keyCode) {
        injectKeyEvent(new KeyEvent(clock, clock, KeyEvent.ACTION_UP, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
        Log.d("Bremen79", String.format("Keypress up sent, processing time: %d ms", SystemClock.uptimeMillis() - clock));
    }
}