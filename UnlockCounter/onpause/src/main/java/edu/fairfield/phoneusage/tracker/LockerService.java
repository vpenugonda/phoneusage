package edu.fairfield.phoneusage.tracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockerService extends Service {
    public static final String LOG_TAG = "UC_SERVICE";
    Notification note = new Notification();
    private NotificationManager nm;

    public IBinder onBind(Intent paramIntent) {
        return null;
    }


    public void onCreate() {
        super.onCreate();
        Log.d("UC_SERVICE", "Service started");
        IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_PRESENT");
        localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(new BroadReceiver(), localIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Service", "onStartCommand");
        //return 1;
        //return Service.START_STICKY;
        //this.startForeground(10, note);
        return Service.START_REDELIVER_INTENT;
        //	return Service.START_CONTINUATION_MASK;
        //return Service.START_STICKY_COMPATIBILITY;
        //return Service.START_NOT_STICKY;
    }


    public void onDestroy() {
        super.onDestroy();
        Log.d("UC_SERVICE", "Service exited");

    }
}
