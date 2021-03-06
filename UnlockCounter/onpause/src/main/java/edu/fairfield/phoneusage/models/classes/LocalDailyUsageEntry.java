package edu.fairfield.phoneusage.models.classes;

import java.util.concurrent.TimeUnit;

/**
 * Created by SujayBusam on 3/3/16.
 * <p>
 * Class that encapsulates all of the daily usage for the local user. Persisted in local db.
 * These entries from the local db will also get pushed to Parse by the midnight scheduler.
 */
public class LocalDailyUsageEntry {

    private Long id; // Not set explicitly. This is an auto-incremented field in the db

    private long dateTimeMS; // the date for the entry in milliseconds
    private int totalUnlocks;
    private long totalUsageMS; // in milliseconds
    private long goalHoursMS; // today's goal hours in milliseconds for the local user (calculated using percentile)

    /**
     * Default constructor with no fields given.
     * If you use this, you have to make sure all fields are set properly before saving to db!
     */
    public LocalDailyUsageEntry() {
        this.dateTimeMS = -1;
        this.totalUnlocks = 0;
        this.totalUsageMS = 0;
        this.goalHoursMS = 0;
    }

    /**
     * Constructor with all fields provided.
     */
    public LocalDailyUsageEntry(long id, long dateTimeMS, int totalUnlocks, long totalUsageMS,
                                long goalHoursMS) {
        this.id = id;
        this.dateTimeMS = dateTimeMS;
        this.totalUnlocks = totalUnlocks;
        this.totalUsageMS = totalUsageMS;
        this.goalHoursMS = goalHoursMS;
    }

    @Override
    public String toString() {
        return String.format("{ ID: %d, totalUnlocks: %d, totalUsageMS: %d, dateTimeMS: %d, " +
                        "goalHoursMS: %d }", getId(), getTotalUnlocks(), getTotalUsageMS(), getDateTimeMS(),
                getGoalHoursMS());
    }

    // ********************************** Public Setters ***************************************//

    public Long getId() {
        return id;
    }

    public int getTotalUnlocks() {
        return totalUnlocks;
    }

    public void setTotalUnlocks(int totalUnlocks) {
        this.totalUnlocks = totalUnlocks;
    }

    public long getTotalUsageMS() {
        return totalUsageMS;
    }

    public void setTotalUsageMS(long totalUsageMS) {
        this.totalUsageMS = totalUsageMS;
    }

    /**
     * Return the total daily usage in hours rounded to two decimal places.
     */
    public float getTotalUsageInHours() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(getTotalUsageMS());
        float hours = minutes / 60.0f;
        return Math.round(hours * 100.0f) / 100.0f;
    }

    // ********************************** Public Getters ***************************************//

    public void setTotalUsageInHours(float hours) {
        int minutes = Math.round(hours * 60);
        long usageInMS = TimeUnit.MINUTES.toMillis(minutes);
        setTotalUsageMS(usageInMS);
    }

    public long getDateTimeMS() {
        return dateTimeMS;
    }

    public void setDateTimeMS(long dateTimeMS) {
        this.dateTimeMS = dateTimeMS;
    }

    public long getGoalHoursMS() {
        return goalHoursMS;
    }

    public void setGoalHoursMS(long goalHoursMS) {
        this.goalHoursMS = goalHoursMS;
    }

    /**
     * Return the goal hours in hours rounded to two decimal places.
     */
    public float getGoalHoursInHours() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(getGoalHoursMS());
        float hours = minutes / 60.00f;
        return Math.round(hours * 100.00f) / 100.00f;
    }

    public void setGoalHoursInHours(float hours) {
        int minutes = Math.round(hours * 60);
        long goalInMS = TimeUnit.MINUTES.toMillis(minutes);
        setGoalHoursMS(goalInMS);
    }

    // ****************************** Useful Instance Methods ************************************//

}
