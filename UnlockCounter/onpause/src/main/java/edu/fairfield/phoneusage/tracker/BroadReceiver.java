package edu.fairfield.phoneusage.tracker;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import edu.fairfield.phoneusage.R;


public class BroadReceiver
        extends BroadcastReceiver {
    //	 DBAdapter myDB = new DBAdapter(this);
    public static final String LOG_TAG = "UC_SCREENEVENT";
    int k = 0;

    @SuppressLint("NewApi")
    public void onReceive(Context paramContext, Intent paramIntent) {

        paramContext.startService(new Intent(paramContext, LockerService.class));
        if (paramIntent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            Log.d("UC_SCREENEVENT", "Screen locked");
        }
        while (!paramIntent.getAction().equals("android.intent.action.USER_PRESENT")) {
            Log.d("UC_SCREENEVENT", "user Still Present");
            return;
        }
        DBAdapter myDB = new DBAdapter(paramContext);

        //openDB();
        myDB.open();
        Calendar cal = Calendar.getInstance();

        //Adding records to the databaseadd_log();
        Long localLong = Long.valueOf(Calendar.getInstance().getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy hh:mm a");
        String test = sdf.format(cal.getTime());

        Log.d("UC_SCREENEVENT", test);
        myDB.insertRow(test);

        Log.d("UC_SCREENEVENT", "Screen unlock");


        //Notification code Starts
        Cursor cursor = myDB.getAllRows();
        k = 0;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd:MM:yyyy");
        String test1 = sdf1.format(cal.getTime());
        if (cursor.moveToFirst()) {
            do {
                String dateInstance = cursor.getString(1);
                StringTokenizer tokens = new StringTokenizer(dateInstance, " ");
                String test2 = tokens.nextToken();
                //Toast.makeText(paramContext.getApplicationContext(), test1, Toast.LENGTH_SHORT).show();
                if (test2.equals(test1)) {
                    //String[] columnNames = cursor.getColumnNames();
                    //System.out.println(""+columnNames.length);
                    k++;
                }
                //Toast.makeText(paramContext.getApplicationContext(), Integer.toString(k), Toast.LENGTH_SHORT).show();

            } while (cursor.moveToNext());
            cursor.close();

            if (k == 50) {
                Style biggText = new NotificationCompat.BigTextStyle();
                biggText = ((BigTextStyle) biggText).bigText("You have crossed " + k + " unlocks and counting");
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(paramContext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Unlock Tracker")
                                .setAutoCancel(true)
                                .setStyle(biggText)
                                .setContentText("You have crossed " + k + " unlocks and counting");

                Intent resultIntent = new Intent(paramContext, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(paramContext);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) paramContext.getSystemService(Context.NOTIFICATION_SERVICE);


                // mId allows you to update the notification later on.
                mNotificationManager.notify(1025, mBuilder.build());
            }
            if (k == 75) {
                Style biggText = new NotificationCompat.BigTextStyle();
                biggText = ((BigTextStyle) biggText).bigText("You have crossed " + k + " unlocks, Remember mobile isin't life");
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(paramContext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Unlock Tracker")
                                .setAutoCancel(true)
                                .setStyle(biggText)
                                .setContentText("You have crossed " + k + " unlocks, Remember mobile isin't life");

                Intent resultIntent = new Intent(paramContext, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(paramContext);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) paramContext.getSystemService(Context.NOTIFICATION_SERVICE);


                // mId allows you to update the notification later on.
                mNotificationManager.notify(1025, mBuilder.build());
            }

            if (k == 100) {
                Style biggText = new NotificationCompat.BigTextStyle();
                biggText = ((BigTextStyle) biggText).bigText("You have crossed " + k + " unlocks, keep you mobile aside and have life");
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(paramContext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Unlock Tracker")
                                .setAutoCancel(true)
                                .setStyle(biggText)
                                .setContentText("You have crossed " + k + " unlocks, keep you mobile aside and have life");

                Intent resultIntent = new Intent(paramContext, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(paramContext);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) paramContext.getSystemService(Context.NOTIFICATION_SERVICE);


                // mId allows you to update the notification later on.
                mNotificationManager.notify(1025, mBuilder.build());
            }
            //Notification code ends
            myDB.close();
        }


    }
}