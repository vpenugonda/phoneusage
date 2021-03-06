package edu.fairfield.phoneusage.models.data_sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.fairfield.phoneusage.models.classes.UnlockLockEvent;
import edu.fairfield.phoneusage.models.sql.UnlockLockEventDbHelper;

/**
 * Created by SujayBusam on 3/2/16.
 * <p>
 * Use this class to call the public CRUD operation methods!
 * <p>
 * Defines a data source class for UnlockLockEvent objects. This is a singleton class.
 * This interfaces with the ExerciseEntryDbHelper, and is a wrapper around it.
 * <p>
 * There is no need to open and close the database; this class takes care of it.
 */
public class UnlockLockEventDataSource extends BaseDataSource {

    private static final String TAG = "SVB-LockDataSource";
    private static UnlockLockEventDataSource instance;

    private UnlockLockEventDbHelper mDbHelper;

    /**
     * Constructor is private to enforce singleton property of this class.
     */
    private UnlockLockEventDataSource(Context context) {
        this.mDbHelper = UnlockLockEventDbHelper.getInstance(context);
    }

    /**
     * Public static instance retriever that enforces singleton property of this class.
     */
    public static synchronized UnlockLockEventDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new UnlockLockEventDataSource(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        mDbHelper.close();
    }


    // **************************** Public CRUD operation methods ********************************//

    /**
     * Asynchronously save an UnlockLockEvent to the local database. The completion handler will be
     * called with the saved UnlockLockEvent.
     */
    public void saveUnlockLockEvent(UnlockLockEvent event,
                                    CompletionHandler<UnlockLockEvent> completionHandler) {
        SaveEntryTask task = new SaveEntryTask(completionHandler);
        task.execute(event);
    }

    /**
     * Asynchronously fetch all UnlockLockEvents between the two given date times in milliseconds,
     * inclusive. The completion handler will be called with the List of fetched UnlockLockEvents.
     */
    public void fetchUnlockLockEventsBetweenDateTimes(long startDateTimeMS, long endDateTimeMS,
                                                      CompletionHandler<List<UnlockLockEvent>> completionHandler) {
        FetchEntryTask task = new FetchEntryTask(startDateTimeMS, endDateTimeMS, completionHandler);
        task.execute();
    }

    // ********************************** Private Async Tasks ************************************//

    /**
     * Create and return an UnlockLockEvent object from the given Cursor.
     */
    private UnlockLockEvent cursorToUnlockLockEvent(Cursor cursor) throws SQLException {
        if (cursor.getColumnCount() != UnlockLockEventDbHelper.ALL_COLUMNS.length) {
            Log.e(TAG, "Invalid cursor passed in. Columns: " + cursor.getColumnCount());
            throw new SQLException();
        }

        return new UnlockLockEvent(
                cursor.getLong(0), cursor.getLong(1), cursor.getLong(2)
        );
    }

    /**
     * Task to save an UnlockLockEvent in the database asynchronously.
     */
    private class SaveEntryTask extends BaseEntryAsyncTask<UnlockLockEvent, UnlockLockEvent> {

        public SaveEntryTask(CompletionHandler<UnlockLockEvent> completionHandler) {
            super(completionHandler);
        }

        @Override
        protected UnlockLockEvent doInBackground(UnlockLockEvent... params) {
            UnlockLockEvent unlockLockEvent = params[0]; // Only one being saved

            try {
                // Populate the ContentValues
                ContentValues values = new ContentValues();
                values.put(UnlockLockEventDbHelper.COLUMN_UNLOCK_TIME_MS, unlockLockEvent.getUnlockTimeMS());
                values.put(UnlockLockEventDbHelper.COLUMN_LOCK_TIME_MS, unlockLockEvent.getLockTimeMS());

                // Insert into db and get ID
                open();
                long insertId = mDb.insert(UnlockLockEventDbHelper.TABLE_NAME, null, values);
                Log.d(TAG, "Inserted UnlockLockEvent into db. Id: " + unlockLockEvent.getId());

                // Get the newly saved UnlockLockEvent and return it.
                Cursor cursor = mDb.query(UnlockLockEventDbHelper.TABLE_NAME,
                        UnlockLockEventDbHelper.ALL_COLUMNS,
                        UnlockLockEventDbHelper.COLUMN_ID + " = " + insertId,
                        null, null, null, null);
                cursor.moveToFirst();
                UnlockLockEvent newUnlockLockEvent = cursorToUnlockLockEvent(cursor);
                cursor.close();

                Log.d(TAG, "Saved and retrieved UnlockLockEvent: " + newUnlockLockEvent);
                return newUnlockLockEvent;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to insert UnlockLockEvent with id: " + unlockLockEvent.getId());
                return null;
            }
        }
    }

    // **************************** Private helper functions *************************************//

    /**
     * Task to fetch all UnlockLockEvents between the given datetime (milliseconds) asynchronously.
     */
    private class FetchEntryTask extends BaseEntryAsyncTask<Void, List<UnlockLockEvent>> {
        long mStartDateTimeMS;
        long mEndDateTimeMS;

        public FetchEntryTask(long startDateTimeMS, long endDateTimeMS,
                              CompletionHandler<List<UnlockLockEvent>> completionHandler) {
            super(completionHandler);
            this.mStartDateTimeMS = startDateTimeMS;
            this.mEndDateTimeMS = endDateTimeMS;
        }

        @Override
        protected List<UnlockLockEvent> doInBackground(Void... params) {
            List<UnlockLockEvent> queriedEvents = new ArrayList<>();

            // Query the database for UnlockLockEvents between the start and end datetime milliseconds.
            try {
                open();
                Cursor cursor = mDb.query(UnlockLockEventDbHelper.TABLE_NAME,
                        UnlockLockEventDbHelper.ALL_COLUMNS,
                        UnlockLockEventDbHelper.COLUMN_LOCK_TIME_MS + " BETWEEN ? AND ?",
                        new String[]{String.valueOf(mStartDateTimeMS), String.valueOf(mEndDateTimeMS)},
                        null, null, null, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    queriedEvents.add(cursorToUnlockLockEvent(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
                return queriedEvents;
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d(TAG, String.format("Failed to fetch UnlockLockEvents between dates %d and %d",
                        mStartDateTimeMS, mEndDateTimeMS));
                return null;
            }
        }
    }

}
