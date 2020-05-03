package com.hg.anDGS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DGSNotifierRestart extends BroadcastReceiver {
    private CommonStuff commonStuff = new CommonStuff();
    @Override
    public void onReceive(Context context, Intent intent) {
        commonStuff.startStopNotifier(true, context, true, false);
    }
}
