package com.android.fighterdroid2p;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Controller2PService extends Service {
    private long clock = SystemClock.uptimeMillis();
    //private long previousClock = SystemClock.uptimeMillis();
    private FileInputStream fileInputStream = null;

    private int idDevice = -1;


    private Class<?> inputManagerClass;
    private Method getInstanceMethod;
    private Object inputManager;
    private Method injectInputEventMethod;

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

        // The injectInputEvent cannot be accessed normally, so we use Reflection to do it
        // see https://www.pocketmagic.net/injecting-events-programatically-on-android/
        try {
            // Get the InputManager class
            inputManagerClass = Class.forName("android.hardware.input.InputManager");
            // Get the getInstance method of InputManager
            getInstanceMethod = inputManagerClass.getMethod("getInstance");
            // Invoke the getInstance method to get an instance of InputManager
            inputManager = getInstanceMethod.invoke(null);
            // Get the injectInputEvent method
            injectInputEventMethod = inputManagerClass.getMethod("injectInputEvent", InputEvent.class, int.class);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.d("Bremen79", "Service created");
        startMyOwnForeground();
    }


    // https://androidwave.com/foreground-service-android-example/
    private void startMyOwnForeground() {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FighterDroid2P "+BuildConfig.VERSION_NAME+" is running in background")
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

    private int findInputDeviceIdByName(String deviceName) {
        InputManager inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);

        int[] deviceIds = inputManager.getInputDeviceIds();

        for (int deviceId : deviceIds) {
            String name = inputManager.getInputDevice(deviceId).getName();

            if (name.equals(deviceName)) {
                return deviceId;
            }
        }

        // If the device with the given name is not found, return -1 or handle as needed
        return -1;
    }

    private void readUmidoKeys() {
        if (new File("/dev/umidokey").exists()) {
            Log.d("Bremen79", "umidokey device exists");
            try {
                Log.d("Bremen79", "Trying to open umidokey device");
                if (fileInputStream == null)
                    fileInputStream = new FileInputStream("/dev/umidokey");
                Log.d("Bremen79", "umidokey device opened");

                idDevice = findInputDeviceIdByName(BuildConfig.DEVICE_NAME);
                Log.d("Bremen79", String.valueOf(idDevice));

                byte[] bArr = {0, 0, 0, 0};
                while (true) {
                    fileInputStream.read(bArr, 0, 4);
                    // no need to read Player 1, let's skip it to save time
                    //int i = ((bArr[1] & 255) << 8) | (bArr[0] & 255);
                    clock = SystemClock.uptimeMillis();
                    int i2 = ((bArr[3] & 255) << 8) | (bArr[2] & 255);
                    //Log.d("Bremen79", String.format("i2: %d, polling time: %d", i2, clock-previousClock));
                    //previousClock=clock;
                    if ((i2 & UP) != 0) {
                        if (!holdUP) {
                            pressKeyEvent(BuildConfig.UP_KEY);
                            holdUP = true;
                        }
                    } else {
                        if (holdUP) {
                            releaseKeyEvent(BuildConfig.UP_KEY);
                            holdUP = false;
                        }
                        if ((i2 & DOWN) != 0) {
                            if (!holdDOWN) {
                                pressKeyEvent(BuildConfig.DOWN_KEY);
                                holdDOWN = true;
                            }
                        } else if (holdDOWN) {
                            releaseKeyEvent(BuildConfig.DOWN_KEY);
                            holdDOWN = false;
                        }
                    }
                    if ((i2 & LEFT) != 0) {
                        if (!holdLEFT) {
                            pressKeyEvent(BuildConfig.LEFT_KEY);
                            holdLEFT = true;
                        }
                    } else {
                        if (holdLEFT) {
                            releaseKeyEvent(BuildConfig.LEFT_KEY);
                            holdLEFT = false;
                        }
                        if ((i2 & RIGHT) != 0) {
                            if (!holdRIGHT) {
                                pressKeyEvent(BuildConfig.RIGHT_KEY);
                                holdRIGHT = true;
                            }
                        } else if (holdRIGHT) {
                            releaseKeyEvent(BuildConfig.RIGHT_KEY);
                            holdRIGHT = false;
                        }
                    }
                    if ((i2 & P1) != 0) {
                        if (!holdP1) {
                            pressKeyEvent(BuildConfig.P1_KEY);
                            holdP1 = true;
                        }
                    } else if (holdP1) {
                        releaseKeyEvent(BuildConfig.P1_KEY);
                        holdP1 = false;
                    }
                    if ((i2 & P2) != 0) {
                        if (!holdP2) {
                            pressKeyEvent(BuildConfig.P2_KEY);
                            holdP2 = true;
                        }
                    } else if (holdP2) {
                        releaseKeyEvent(BuildConfig.P2_KEY);
                        holdP2 = false;
                    }
                    if ((i2 & P3) != 0) {
                        if (!holdP3) {
                            pressKeyEvent(BuildConfig.P3_KEY);
                            holdP3 = true;
                        }
                    } else if (holdP3) {
                        releaseKeyEvent(BuildConfig.P3_KEY);
                        holdP3 = false;
                    }
                    if ((i2 & P4) != 0) {
                        if (!holdP4) {
                            pressKeyEvent(BuildConfig.P4_KEY);
                            holdP4 = true;
                        }
                    } else if (holdP4) {
                        releaseKeyEvent(BuildConfig.P4_KEY);
                        holdP4 = false;
                    }
                    if ((i2 & P5) != 0) {
                        if (!holdP5) {
                            pressKeyEvent(BuildConfig.P5_KEY);
                            holdP5 = true;
                        }
                    } else if (holdP5) {
                        releaseKeyEvent(BuildConfig.P5_KEY);
                        holdP5 = false;
                    }
                    if ((i2 & P6) != 0) {
                        if (!holdP6) {
                            pressKeyEvent(BuildConfig.P6_KEY);
                            holdP6 = true;
                        }
                    } else if (holdP6) {
                        releaseKeyEvent(BuildConfig.P6_KEY);
                        holdP6 = false;
                    }
                    if ((i2 & START) != 0) {
                        if (!holdSTART) {
                            pressKeyEvent(BuildConfig.START_KEY);
                            holdSTART = true;
                        }
                    } else if (holdSTART) {
                        releaseKeyEvent(BuildConfig.START_KEY);
                        holdSTART = false;
                    }

                    // We are aiming at polling the state of the joystick every 16ms, that is at 62.5Hz
                    // So, if the processing took more than 16ms, we skip the sleep.
                    long timeSleep = 16L-(SystemClock.uptimeMillis() - clock);
                    //Log.d("Bremen79", String.format("Sleep time: %d", timeSleep));
                    if (timeSleep>0)
                        Thread.sleep(timeSleep);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
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

    @Override // android.app.Service
    public final void onDestroy() {
        Log.d("Bremen79", "Service onDestroy");
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
                Log.d("Bremen79", "Closed umidokey");
            } catch (Exception ignored) {
            }
            fileInputStream = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

    // Should I implement an on task removed method?
    // see https://robertohuertas.com/2019/06/29/android_foreground_services/

    private void pressKeyEvent(int keyCode) {
        try {
            injectInputEventMethod.invoke(inputManager, new KeyEvent(clock, clock, KeyEvent.ACTION_DOWN, keyCode, 0, 0,
                            idDevice, 0, 0, 1),
                    0);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d("Bremen79", String.format("Keypress down sent, response time: %d ms", SystemClock.uptimeMillis() - clock));
    }

    private void releaseKeyEvent(int keyCode) {
        try {
            injectInputEventMethod.invoke(inputManager,new KeyEvent(clock, clock, KeyEvent.ACTION_UP, keyCode, 0, 0,
                            idDevice, 0, 0, 1),
                        0);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d("Bremen79", String.format("Keypress up sent, response time: %d ms", SystemClock.uptimeMillis() - clock));
    }
}