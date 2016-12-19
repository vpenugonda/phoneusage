package edu.fairfield.phoneusage.tracker;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import edu.fairfield.phoneusage.R;

public class MainActivity extends AppCompatActivity {

    private static final String Extra = "Test matter";
    int k = 0;
    DBAdapter myDB = new DBAdapter(this);
    //TextView textView;
    private Context paramContext;
    private ShareActionProvider mShareActionProvider;
//	  Cursor cursor=myDB.getAllRows();
//      Cursor cursor1=myDB.getAllRows();

    //DisplayNotification myNotif = new DisplayNotification();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("UC_LifeCycle", "Create");
        startService(new Intent(this, LockerService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_main);


        openDB();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        Intent intent = new Intent(this, LockerService.class);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //for 30 mint 60*60*1000
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                1000, pintent);
        Cursor cursor = myDB.getAllRows();
        Cursor cursor1 = myDB.getAllRows();
        displayRecords(cursor);

        displayTime(cursor1);

    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void openDB() {
        myDB.open();
        return;
    }

    private void closeDB() {
        myDB.close();
    }


    private void displayRecords(Cursor cursor) {
        Calendar cal = Calendar.getInstance();
        ((TextView) findViewById(R.id.text_id)).setText(Integer.toString(k));
        ((TextView) findViewById(R.id.text_id2)).setText("Number of times unlocked:");
        k = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
        String test = sdf.format(cal.getTime());
        if (cursor.moveToFirst()) {
            do {
                String dateInstance = cursor.getString(1);
                StringTokenizer tokens = new StringTokenizer(dateInstance, " ");
                String test2 = tokens.nextToken();
                if (test2.equals(test)) {
                    //String[] columnNames = cursor.getColumnNames();
                    //System.out.println(""+columnNames.length);
                    k++;
                }
                int id = cursor.getInt(0);
                String timestamp = cursor.getString(1);
            } while (cursor.moveToNext());


            ((TextView) findViewById(R.id.text_id)).setText(Integer.toString(k));
            ((TextView) findViewById(R.id.text_id2)).setText("Number of times unlocked: "/*+k*/);

        }

        cursor.close();


    }

    private void displayTime(Cursor cursor1) {
        ((TextView) findViewById(R.id.text_id3)).setText("Lock and unlock to see last login time.");
        findViewById(R.id.text_id4).setVisibility(View.INVISIBLE);
        //To do: As display time is displaying wrong!!! use string cutter instead of moving with string to date converter
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (cursor1.moveToLast()) {
            if (cursor1.moveToPrevious()) {
                String dateInstance = cursor1.getString(1);
                StringTokenizer tokens = new StringTokenizer(dateInstance, " ");
                String DateInstance = tokens.nextToken();
                String TimeInstance1 = tokens.nextToken();
                String TimeInstance2 = tokens.nextToken();
                ParsePosition pos = new ParsePosition(0);
                Date stringDate = sdf.parse(dateInstance, pos);
                String time = sdf.format(stringDate);
                ((TextView) findViewById(R.id.text_id3)).setText("Last Login Time "/*+TimeInstance1*/);
                findViewById(R.id.text_id4).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.text_id4)).setText(DateInstance + ", " + TimeInstance1 + TimeInstance2);

                cursor1.close();
            } else {
                ((TextView) findViewById(R.id.text_id3)).setText("Lock and unlock to see last login time.");
                findViewById(R.id.text_id4).setVisibility(View.GONE);
                cursor1.close();
            }
        }
    }


    @SuppressLint("NewApi")
//protected void DisplayNotification(int k)
//{
//	 int mId=100;
//	 
//	// BitmapDrawable bm = writeOnDrawable(100,"k");
//	  NotificationCompat.Builder mBuilder =
//              new NotificationCompat.Builder(this)
//              .setSmallIcon(R.drawable.ic_launcher)
//	  		  	.setSmallIcon(R.drawable.bm)
//              .setContentTitle("Unlock Counter")
//              .setContentText("You have Unlocked: "+k+" times.");
//	  
//      // Creates an explicit intent for an Activity in your app
//      Intent resultIntent = new Intent(this, MainActivity.class);
//      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//   // Adds the back stack for the Intent (but not the Intent itself)
//   stackBuilder.addParentStack(MainActivity.class);
//   stackBuilder.addNextIntent(resultIntent);
//   PendingIntent resultPendingIntent =
//           stackBuilder.getPendingIntent(
//               0,
//               PendingIntent.FLAG_UPDATE_CURRENT
//           );
//   mBuilder.setContentIntent(resultPendingIntent);
//   NotificationManager mNotificationManager =
//       (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//   
//   
//	// mId allows you to update the notification later on.
//   mNotificationManager.notify(mId, mBuilder.build());
//
//}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.history, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click

        int id = item.getItemId();
        if (id == R.id.action_history) {
            Intent n = new Intent(this, HistoryActivity.class).putExtra(Extra, "hello");
            startActivity(n);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
         /*Commenting to migrate
    	switch (item.getItemId()) {
       
        case R.id.action_history:
        	Intent n=new Intent(this,HistoryActivity.class).putExtra(Extra,"hello");
			startActivity(n);
        	return true;
            
        case R.id.action_rateus:
        	Rateus();
        	
        default:
            return super.onOptionsItemSelected(item);
        }*/
    }


    private void Rateus() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.Swipe.Unlock.Tracker"));
        if (!MyStartActivity(intent)) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.Swipe.Unlock.Tracker"));
            if (!MyStartActivity(intent)) {
                //Well if this also fails, we have run out of options, inform the user.
                Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void Share() {
        //displayInterstitial();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "\n" + "Hey there,I found this app useful. Download it" +
                "\n https://play.google.com/store/apps/details?id=com.Swipe.Unlock.Tracker";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Unlock Tracker");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    protected void onPause() {
        Log.d("UC_LifeCycle", "Pause");
        super.onPause();
    }

    protected void onResume() {

        super.onResume();
        Cursor cursor2 = myDB.getAllRows();
        Cursor cursor3 = myDB.getAllRows();
        displayRecords(cursor2);
        displayTime(cursor3);
        // cursor2.close();
        //cursor3.close();
        Log.d("UC_LifeCycle", "Resume");

    }

    protected void onStop() {
        super.onStop();
        Log.d("UC_LifeCycle", "Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("UC_LifeCycle", "Destroy");

        closeDB();
    }
}
