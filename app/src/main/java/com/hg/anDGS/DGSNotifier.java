package com.hg.anDGS;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

public class DGSNotifier extends Service {
    public static final long DEFNOTIFIERINTERVAL = 63;  // seconds
    public static final long MINNOTIFIERINTERVAL = 33;  // seconds
    private final long second = 1000;
    private final long DELAY_INTERVAL = 0;  // seconds
    private final long DELAY_INTERVAL_MILLI = DELAY_INTERVAL*second;
    private final int JOINWAIT = 5;  // milliseconds
    public static String CHAN_ID;
    public static int NOTIFIER_ID = R.string.DGSClient;
    private long update_interval;
    private Timer timer = new Timer();
    String make_sound;
    private DGSNotifierThread mThread = null;
    private boolean startIt = true;
    private boolean restartIt = true;
    private ErrorHistory errHist = ErrorHistory.getInstance();

    public DGSNotifier() {
    }

    /*
        Context context;
        public DGSNotifier() {
            super();
        }
        public DGSNotifier(Context applicationContext) {
            super();
            context = applicationContext;
        }
    */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // NotificationManager mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
        String DGSUser = prefs.getString("com.hg.anDGS.DGSUser", "none");
        String DGSPass = prefs.getString("com.hg.anDGS.DGSPass", "none");
        String serverURL = prefs.getString("com.hg.anDGS.ServerURL", PrefsDGS.DGS_URL);
        update_interval = prefs.getLong("com.hg.anDGS.Interval", DEFNOTIFIERINTERVAL);
        make_sound = prefs.getString("com.hg.anDGS.Sound", PrefsDGS.NOSOUND);
        boolean notifierVibrate = prefs.getBoolean("com.hg.anDGS.Vibrate", false);
        boolean notifyAllDetails = prefs.getBoolean("com.hg.anDGS.NotifyFailure", false);
        CHAN_ID = getString(R.string.DGSClient);

        if (MainDGS.dbgNotifier) {
            errHist.writeErrorHistory("DGSNotifier.onCreate, Update Interval: " + update_interval);
        }

        createNotificationChannel();
        // init the service here
        mThread = new DGSNotifierThread(this, serverURL, DGSUser, DGSPass, make_sound, notifierVibrate, notifyAllDetails, update_interval);
        String nText = mThread.notificationText(-1, -1, -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = mThread.makeNotification(this, nText, mThread.notify_waiting, "startForeground", make_sound, false);
            if (notification == null) {
                errHist.writeErrorHistory("DGSNotifier.onCreate, failed to make notification ");
            } else {
                startForeground(NOTIFIER_ID, notification);
            }
        } else {
            mThread.showNotification(this, nText, mThread.notify_waiting, "firstNotification");
        }
        mThread.start();

        m_startService();
    }

    private void m_startService() {
        final long c_UPDATE_INTERVAL = update_interval*second;
        final long c_DELAY_INTERVAL = DELAY_INTERVAL_MILLI;
        timer.schedule(
                new TimerTask() {
                    public void run() {
                        m_getDGSStatus();
                    }
                },
                c_DELAY_INTERVAL,
                c_UPDATE_INTERVAL);
    }

    private void m_getDGSStatus() {
        if (mThread != null) {
            mThread.getStatus();
        } else {
            errHist.writeErrorHistory("DGSNotifier, poll timer, no Notifier Thread for status");
        }
    }

    private void m_shutdownService() {
        if (timer != null) timer.cancel();
        if (mThread != null)
            try {
                mThread.stopThread();
                mThread.join(JOINWAIT);
            } catch (InterruptedException ignored) {
            }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();
        boolean resetIt = false;
        if (extras != null) {
            startIt = extras.getBoolean("Start", true);
            restartIt = extras.getBoolean("Restart", true);
            resetIt = extras.getBoolean("Reset", false);
        }
        if (MainDGS.dbgNotifier) {
            errHist.writeErrorHistory("DGSNotifier.onStartCommand, Restart: " + restartIt
                    + ", Start: " + startIt
                    + ", Reset: " + resetIt);
        }
        if (startIt) {
            if (resetIt) {
                if (mThread != null) {
                    mThread.resetStatus();
                } else {
                    errHist.writeErrorHistory("DGSNotifier.onStart, no NotifierThread to reset");
                }
            }
        } else {  // startIt is false  == shutdown
            m_shutdownService();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
                if (MainDGS.dbgNotifier) {
                    errHist.writeErrorHistory("DGSNotifier.onStart, stopForeground");
                }
                stopSelf();
            } else {
                mThread.removeNotification();
                if (MainDGS.dbgNotifier) {
                    errHist.writeErrorHistory("DGSNotifier.onStart, removeNotification");
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m_shutdownService();
        if (MainDGS.dbgNotifier) {
            errHist.writeErrorHistory("DGSNotifier.onDestroy, Restart: " + restartIt);
        }
        if (restartIt) {
            Intent broadcastIntent = new Intent(this, DGSNotifierRestart.class);
            sendBroadcast(broadcastIntent);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.DGSClient);
            String description = getString(R.string.DGSClient);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHAN_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                errHist.writeErrorHistory("DGSNotifier, createNotificationChannel failed: notificationManager is null");
            }
        }
    }
}
