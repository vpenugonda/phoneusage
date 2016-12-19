package edu.fairfield.phoneusage.tracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import edu.fairfield.phoneusage.R;

public class HistoryActivity extends AppCompatActivity {

    DBAdapter myDB = new DBAdapter(this);


    int k = 0, j = 0;

    String formatedDate;

    protected void onCreate(Bundle savedInstanceState) {

        Calendar cal = Calendar.getInstance();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        openDB();
        Cursor cur = myDB.getAllRows();

        //FINDING COUNT

        j = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
        String test = sdf.format(cal.getTime());
        if (cur.moveToFirst()) {
            do {
                String dateInstance = cur.getString(1);
                StringTokenizer tokens = new StringTokenizer(dateInstance, " ");
                String test2 = tokens.nextToken();
                if (test2.equals(test)) {
                    //String[] columnNames = cursor.getColumnNames();
                    //System.out.println(""+columnNames.length);
                    j++;
                }
                int id = cur.getInt(0);
                String timestamp = cur.getString(1);
            } while (cur.moveToNext());
        }
        cur.close();
        //END OF FINDING COUNT
//			MainActivity obj = new MainActivity();
//			j= obj.displayRecords(cur);
        //cur.close();
        ((TextView) findViewById(R.id.text_id2)).setText("Select Date");
        ((TextView) findViewById(R.id.text_id3)).setText("Number of times unlocked:");
        ((TextView) findViewById(R.id.text_id4)).setText(Integer.toString(j));
        //((TextView)findViewById(R.id.text_id3)).setText(j);
        //findViewById(R.id.text_id3).setVisibility(View.GONE);
        //findViewById(R.id.text_id4).setVisibility(View.GONE);
        //layone.setVisibility(View.VISIBLE);
        //final Cursor cursor=myDB.getAllRows();
        DatePicker datePicker;
        datePicker = (DatePicker) findViewById(R.id.datePicker1);
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new OnDateChangedListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                findViewById(R.id.text_id3).setVisibility(View.VISIBLE);
                findViewById(R.id.text_id4).setVisibility(View.VISIBLE);
                // TODO Auto-generated method stub
                int year = arg1;
                int month = arg2;
                int day = arg3;
                // ((TextView)findViewById(R.id.text_id3)).setText(Integer.toString(year));
                Cursor cursor = myDB.getAllRows();
                SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
                formatedDate = sdf.format(new Date(year - 1900, month, day));
                //((TextView)findViewById(R.id.text_id3)).setText(formatedDate);
                k = 0;
                if (cursor.moveToFirst()) {
                    do {
                        String dateInstance = cursor.getString(1);
                        StringTokenizer tokens = new StringTokenizer(dateInstance, " ");
                        String dateInstance1 = tokens.nextToken();
                        if (dateInstance1.equals(formatedDate)) {
                            //String[] columnNames = cursor.getColumnNames();
                            //System.out.println(""+columnNames.length);
                            k++;

                        }

                        //cursor.moveToNext();
                        int id = cursor.getInt(0);
                        String timestamp = cursor.getString(1);
                    } while (cursor.moveToNext());

                    int j = cursor.getCount();
                    ((TextView) findViewById(R.id.text_id3)).setText("Number of times unlocked:");
                    ((TextView) findViewById(R.id.text_id4)).setText(Integer.toString(k));

                } else
                    //((TextView)findViewById(R.id.text_id)).setText("If not executing");
                    cursor.close();

            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_history) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void openDB() {

        //DBAdapter myDB = new DBAdapter(this);
        myDB.open();
        return;
    }

    private void closeDB() {
        myDB.close();
    }
}
