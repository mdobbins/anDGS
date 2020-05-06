package com.hg.anDGS;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class DGSNotifierThread extends Thread {
    private final int DO_NOTHING = 0;
    private final int DO_GET_STATUS = 1;
    private final long slow_poll = 729;  // in seconds 9x9x9, Target above 12:09 minutes
    private final long idle_limit = 123;  //in seconds, close connection if idle for more than 2 minutes
    private final long second = 1000;
    private final long idle_limit_millis = idle_limit*second;  //close connection if idle for more than 2 minutes
    private long targetTime;
    private int doEvt = DO_NOTHING;
    private OkHttpClient okHTTPclient = null;
    private Map<String,String> HTTPparams = new HashMap<>();
    private Map<String,String> QuickStatusParams = new HashMap<String, String>();
    private String dgsURL;
    private String loginURL;
    private String qsURL;
    private String sgfURL;
    private String qdURL;
    private String mUser;
    private String mPass;
    private String make_sound;
    private boolean notifierVibrate;
    private boolean notifyAllDetails;
    private TextHelper th = new TextHelper();
    private volatile boolean mStopped = false;
    private volatile boolean mLoggedOn = false;
    private Context ctxt;
    //private NotificationManager mNM;
    private NotificationManagerCompat mNM;
    private StoredMoves storMov = StoredMoves.getInstance();
    private ErrorHistory errHist = ErrorHistory.getInstance();
    private CommonStuff commonStuff = new CommonStuff();

    private int prev_games = -1;
    private int prev_msgs = -1;
    private long update_interval;  // Seconds
    private long current_interval;  // Seonds
    private boolean notifierBusy = false;
    private int skipped_status_count;
    final int notify_none = -1;
    final int notify_waiting = 0;
    final int notify_nothing_waiting = 1;
    final int notify_responded = 2;
    final int notify_stuff_to_do = 3;
    final int notify_failure = 4;
    final int notify_debug = 5;
    private int last_notify = notify_none;

    DGSNotifierThread(Context ctx, String serverURL, String user, String pass, String m_sound, boolean m_vibrate, boolean m_notifyAllDetails, long m_update_interval) {
        ctxt = ctx;
        mUser = user;
        mPass = pass;
        dgsURL = serverURL;
        QuickStatusParams.clear();
        QuickStatusParams.put("version","2");
        QuickStatusParams.put("no_cache","0");
        QuickStatusParams.put("order","0");
        QuickStatusParams.put("userid",commonStuff.encodeIt(mUser));
        QuickStatusParams.put("passwd",commonStuff.encodeIt(mPass));
        loginURL = dgsURL + "login.php";
        qsURL = dgsURL + "quick_status.php";
        sgfURL = dgsURL + "sgf.php";
        qdURL = dgsURL + "quick_do.php";
        make_sound = m_sound;
        notifierVibrate = m_vibrate;
        notifyAllDetails = m_notifyAllDetails;
        current_interval = update_interval = m_update_interval; // in seconds
        skipped_status_count = 0;
        targetTime = System.currentTimeMillis();  // Don't wait, let notifier tell
        //mNM = ctxt.getSystemService(NotificationManager.class);
        mNM = NotificationManagerCompat.from(ctxt);
    }

    public void run() {
        Date dt;
        String prevStatusDate;
        if (MainDGS.dbgNotifier) {
            dt = new Date(MainDGS.lastStatusTime);
            prevStatusDate = dt.toLocaleString();
            errHist.writeErrorHistory("DGSNotifierThread started: "
                    + "prevStatusTime: " + prevStatusDate);
        }
        while (!mStopped) {
            switch (doEvt) {
                case DO_NOTHING:
                    try {
                        sleep(idle_limit_millis);  // 2 minutes
                    } catch (InterruptedException e) {
                        break; // some action
                    }
                    mLoggedOn = false;  // 2 minutes idle, force logon again
                    break;
                case DO_GET_STATUS:
                    doEvt = DO_NOTHING;
                    if (!mLoggedOn) {
                        if (!logonToDGS(mUser, mPass)) {
                            break;
                        }
                    }
                    HTTPparams.clear();
                    HTTPparams.putAll(QuickStatusParams);
                    String s = doHTTPreq(qsURL);
                    long prevStatusTime = MainDGS.lastStatusTime;
                    dt = new Date(prevStatusTime);
                    MainDGS.lastStatusTime = System.currentTimeMillis();
                    if (MainDGS.dbgStatus) {
                        prevStatusDate = dt.toLocaleString();
                        errHist.writeErrorHistory("DGSNotifierThread, sent get status: "
                                + "prevStatusTime: " + prevStatusDate
                                + ", interval: " + commonStuff.timeDiff(MainDGS.lastStatusTime,prevStatusTime));
                    }
                    if (s.startsWith("#Failed")) {
                        showNotification(ctxt, "#Failed", notify_failure, s);
                        mLoggedOn = false;
                        updateDelay();
                        break;
                    }
                    if (MainDGS.dbgStatus) {
                        if (s.contains("excessive_usage")) {
                                errHist.writeErrorHistory(s);
                        }
                    }

                    String [] statusList = s.split("\n");
                    int n_games = countGames(statusList);
                    int n_responded = respondToGames(statusList);
                    if (n_responded < 0) {
                        errHist.writeErrorHistory("DGSNotifierThread, bad responded count: "
                                + n_responded);
                        updateDelay();
                        break;
                    }
                    int n_msgs = countMsgs(statusList);
                    n_games -= n_responded;

                    String nText = notificationText(n_games, n_msgs, n_responded);
                    dt = new Date(MainDGS.lastStatusTime);
                    prevStatusDate = dt.toLocaleString();
                    String logmsg = ", current_interval: " + current_interval
                                + ", skipped_status_count: " + skipped_status_count
                            + ", prevStatusTime: " + prevStatusDate;
                    if (n_games != prev_games || n_msgs != prev_msgs || n_responded>0) {
                        if (n_games == 0 && n_msgs == 0 && n_responded == 0) {
                            showNotification(ctxt, nText, notify_nothing_waiting, logmsg);
                        } else if (n_games == 0 && n_msgs == 0) {  // n_responded > 0
                            showNotification(ctxt, nText, notify_responded, logmsg);
                        } else if (n_games != prev_games || n_msgs != prev_msgs) {
                            showNotification(ctxt, nText, notify_stuff_to_do, logmsg);
                        } else if (MainDGS.dbgNotifier) {
                            errHist.writeErrorHistory("DGSNotifierThread, Changed, no notification: "
                                    + nText
                                    + ", " + logmsg);
                        }
                    } else {
                        if (MainDGS.dbgNotifier) {
                            errHist.writeErrorHistory("DGSNotifierThread, No Change, no notification: "
                                    + nText
                                    + ", " + logmsg);
                        }
                    }
                    prev_games = n_games;
                    prev_msgs = n_msgs;
                    updateDelay();
                    if (current_interval > idle_limit) {
                        mLoggedOn = false;
                    }
                    break;
                default:
                    doEvt = DO_NOTHING;
            }
            notifierBusy = false;
        }

        removeNotification();
        mLoggedOn = false;
        if (MainDGS.dbgNotifier) {
            dt = new Date(MainDGS.lastStatusTime);
            prevStatusDate = dt.toLocaleString();
            errHist.writeErrorHistory("DGSNotifierThread ended: "
                    + "prevStatusTime: " + prevStatusDate);
        }
    }

    private String doHTTPreq(String baseURL) {
        String rsp = commonStuff.executeHTTPreq(baseURL, HTTPparams);
        if (rsp.startsWith("#Failed: ")) {
            errHist.writeErrorHistory("DGSNotifierThread, " + rsp);
        }
        return rsp;
    }

    private boolean logonToDGS(String DGSUser, String DGSPass) {
        String rslt = commonStuff.executeLogonToDGS(DGSUser, DGSPass);
        if (rslt.contains("Ok")) {
            mLoggedOn = true;
        } else {
            showNotification(ctxt, ctxt.getString(R.string.LogonFailed), notify_failure, rslt);
            mLoggedOn = false;
            updateDelay();
        }
        return mLoggedOn;
    }

    void getStatus() {
        long nowTime = System.currentTimeMillis();
        if (notifierBusy || nowTime < targetTime
                || nowTime < MainDGS.lastStatusTime + (update_interval * second)) {
            skipped_status_count++;
        } else {
            notifierBusy = true;
            doEvt = DO_GET_STATUS;
            this.interrupt();
        }
    }

    void resetStatus() {
        current_interval = update_interval;
        targetTime = System.currentTimeMillis() + current_interval * second;
        skipped_status_count = 0;
        prev_games = -1;
        prev_msgs = -1;
        showNotification(ctxt, notificationText(-1, -1, -1), notify_waiting, "Reset Status");
    }

    private void updateDelay() {
        if (current_interval < slow_poll) { // limit for back off
            //current_interval += update_interval; // linear
            current_interval += current_interval; // binary
            if (current_interval > slow_poll) {
                current_interval = slow_poll;
            }
        }
        skipped_status_count = 0;
        targetTime = System.currentTimeMillis() + current_interval*second;
    }

    void removeNotification() {
        mNM.cancel(DGSNotifier.NOTIFIER_ID);
    }

    void stopThread() {
        /* As we've written this method, calling it from multiple threads would
		 * be problematic. */
        if (mStopped)
            return;

		/* Too late! */
        if (!isAlive())
            return;

		/* Flag to instruct the thread to halt at the next
		 * opportunity. */
        mStopped = true;

		/* Interrupt the blocking thread.  This won't break out of a blocking
		 * I/O request, but will break out of a wait or sleep call.  While in
		 * this case we know that no such condition is possible, it is always a
		 * good idea to include an interrupt to avoid assumptions about the
		 * thread in question. */
        this.interrupt();
    }

    private int respondToGames(String [] statusList) {
        int count = 0;
        if (!storMov.areStoredMovesLoaded()) {
            storMov.loadStoredMoves();
        }
        for (int i=0; i<statusList.length; i++) {
            if (statusList[i].startsWith("G")) {
                String [] elements;
                elements = statusList[i].split(",");
                if (elements.length > 9) {
                    String current_color;
                    String gameId = elements[1].trim();
                    String player_color = elements[3].trim();  // B | W = current color of player to move
                    String game_action = elements[6].trim();   // 2 = play next move
                    String game_status = elements[7].trim();   // PLAY
                    String moveId = elements[8].trim();
                    if (player_color.contentEquals("B")) {  // player to move
                        current_color="W";
                    } else {
                        current_color = "B";
                    }
                    if (!gameId.contentEquals("0") && game_action.contentEquals("2") && game_status.contentEquals("PLAY")) {
                        if (storMov.isStoredMove(gameId, moveId, current_color, "")) {
                            HTTPparams.clear();
                            HTTPparams.put("gid",gameId);
                            HTTPparams.put("owned_comments","1");
                            HTTPparams.put("quick_mode","1");
                            String sgf = doHTTPreq(sgfURL);

                            String [] nextMov = storMov.findTakeStoredMove(gameId, moveId, sgf);
                            if (nextMov != null) {
                                // (nextMov[0].contentEquals(cMovID) && nextMov[1].contentEquals(cClr) && nextMov[2].contentEquals(cMov)
                                HTTPparams.clear();
                                HTTPparams.put("obj","game");
                                HTTPparams.put("cmd","move");
                                HTTPparams.put("gid",gameId);
                                HTTPparams.put("move_id",nextMov[0]);
                                HTTPparams.put("move",nextMov[2]);
                                String rslt = doHTTPreq(qdURL);
                                if (MainDGS.dbgStdMov) {
                                    errHist.writeErrorHistory("DGSNotifierThread.respondToGames: " + rslt);
                                }

                                String errString;
                                try {
                                    JSONObject joMovRslt = new JSONObject(rslt);
                                    errString = joMovRslt.getString("error");
                                } catch (JSONException err) {
                                    errString = "";
                                }
                                if (errString.contentEquals("")) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
        storMov.checkSaveStoredMoves();
        return count;
    }

    private int countGames(String[] statusList) {
        int count = 0;
        for (int i=0; i<statusList.length; i++) {
            if (statusList[i].startsWith("G")) {
                count++;
            }
        }
        return count;
    }

    private int countMsgs(String[] statusList) {
        int count = 0;
        for (int i=0; i<statusList.length; i++) {
            if (statusList[i].startsWith("M")) {
                count++;
            }
        }
        return count;
    }

    String notificationText(int n_games, int n_msgs, int n_responded) {
        String rslt = "";
        if (n_responded > 0) {
            rslt = n_responded + " " + "Responded" + ". ";
        }
        if (n_games > 0) {
            rslt = rslt + n_games + " " + ctxt.getString(R.string.Games) + " ";
        }
        if (n_msgs > 0) {
            rslt = rslt + n_msgs + " " + ctxt.getString(R.string.Messages) + " ";
        }
        if (n_games == 0 && n_msgs == 0) {
            rslt = rslt + ctxt.getString(R.string.Nothing) + " ";
        }
        if (!notifyAllDetails && n_games < 0 && n_msgs < 0) {  // not all details combine waiting and nothing waiting
            rslt = rslt + ctxt.getString(R.string.Nothing) + " ";
        }
        rslt = rslt + ctxt.getString(R.string.Waiting);
        return rslt;
    }

    void showNotification(Context ctx, String notify_txt, int notify_type, String log_txt) {
        Notification notification = makeNotification(ctx, false, notify_txt, notify_type, log_txt, make_sound, notifierVibrate);
        // Send the notification.
        // We use a unique number.  We use it later to cancel.
        if (notification != null) {
            mNM.notify(DGSNotifier.NOTIFIER_ID, notification);
        }
    }

    Notification makeNotification(Context ctx, boolean forceIt, String notify_txt, int notify_type, String log_txt, String make_sound, boolean notifierVibrate) {
        if (MainDGS.dbgNotifier) {
            errHist.writeErrorHistory("DGSNotifierThread.makeNotification: " + notify_txt
                    + ", " + notify_type
                    + ", " + log_txt);
        }

        boolean showIt;
        if (notifyAllDetails) {  // see if we need to show the notification
            showIt = true;
        } else {
            switch (notify_type) {
                case notify_waiting:
                case notify_nothing_waiting:
                    showIt = !(last_notify == notify_waiting || last_notify == notify_nothing_waiting);
                    notify_type = notify_nothing_waiting;   // combine these 2 states
                    break;
                case notify_responded:
                case notify_stuff_to_do:
                    showIt = true;
                    break;
                case notify_failure:
                case notify_debug:
                default:
                    showIt = false;
                    break;
            }
        }
        if (!showIt && !forceIt) return null;

        // In this sample, we'll use the same text for the ticker and the expanded notification
        //  | Notification.FLAG_ONLY_ALERT_ONCE
        CharSequence title = ctx.getText(R.string.DGSClient);
        int ndefaults = 0;

        // Set the icon, scrolling text and timestamp
        Intent myIntent = new Intent(ctx, MainDGS.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra("DGSNOTIFIERSTART", true);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                myIntent, PendingIntent.FLAG_ONE_SHOT);   // TODO verify this setting
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(ctx, ctx.getString(R.string.DGSClient));
        int smallIcon;

        if (log_txt!=null) {
            if (!log_txt.contentEquals("")) {
                // TODO save the log text to a file
            }
        }

        switch (notify_type) {
            case notify_waiting:
                last_notify = notify_waiting;
                smallIcon=R.drawable.bc;    // black circle
                break;
            case notify_nothing_waiting:
                last_notify = notify_nothing_waiting;
                smallIcon=R.drawable.bs;    // black square
                break;
            case notify_responded:
                last_notify = notify_responded;
                smallIcon=R.drawable.bt;    // black triangle .setLargeIcon(R.drawable.bt)
                break;
            case notify_stuff_to_do:
                last_notify = notify_stuff_to_do;
                smallIcon=R.drawable.y;    // yin/yang
                if (make_sound.contentEquals(PrefsDGS.DEFAULTSOUND)) {
                    ndefaults |= Notification.DEFAULT_SOUND;
                } else if (make_sound.contentEquals(PrefsDGS.STONESOUND)) {
                    notifyBuilder.setSound(Uri.parse("android.resource://com.hg.anDGS/raw/stone"),AudioManager.STREAM_NOTIFICATION);
                }
                if (notifierVibrate) {
                    ndefaults |= Notification.DEFAULT_VIBRATE;
                }
                /* note for Notification class -
                This field was deprecated in API level 26. use
                NotificationChannel.getSound() and
                NotificationChannel.shouldShowLights() and
                NotificationChannel.shouldVibrate().
                 */
                break;
            case notify_failure:
                last_notify = notify_failure;
                smallIcon=R.drawable.wx;    // white X
                break;
            case notify_debug:
            default:
                last_notify = notify_debug;
                smallIcon=R.drawable.wt;    // white triangle
                break;
        }

        notifyBuilder.setDefaults(ndefaults)
                .setContentTitle(title)
                .setContentText(notify_txt)
                .setSmallIcon(smallIcon)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
        ;
        notifyBuilder.setChannelId(DGSNotifier.CHAN_ID);
        // build notification
        Notification notification = notifyBuilder.build();
        int nflags = 0; //Notification.FLAG_AUTO_CANCEL;   FLAG_ONLY_ALERT_ONCE if new
        nflags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notification.flags = nflags;

        return notification;
    }
}
