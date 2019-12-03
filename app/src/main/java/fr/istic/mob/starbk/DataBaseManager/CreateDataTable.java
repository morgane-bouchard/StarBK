package fr.istic.mob.starbk.DataBaseManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static fr.istic.mob.starbk.DataBaseManager.myDB.*;

public class CreateDataTable extends SQLiteOpenHelper {
    private static final String TAG = CreateDataTable.class.getSimpleName();

    public CreateDataTable(Context context) {
        super(context, DbName, null, DbVersion);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    public void createTables(SQLiteDatabase db) {
        createDataVersionTable(db);
        createBusRoutesTable(db);
        createTripsTable(db);
        createStopsTable(db);
        createStopTimesTable(db);
        createCalendarTable(db);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    public void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DataVersions.content_path);
        db.execSQL("DROP TABLE IF EXISTS " + BusRoutes.CONTENT_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + Trips.CONTENT_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + Stops.CONTENT_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + StopTimes.CONTENT_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + Calendar.CONTENT_PATH);
    }

    private void createDataVersionTable(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT NOT NULL)",
                DataVersions.content_path,
                DataVersions.DataVersionColumns._ID,
                DataVersions.DataVersionColumns.filename,
                DataVersions.DataVersionColumns.fileVersion
        );
        db.execSQL(sql);
        Log.d(TAG, "Versions table created");
    }

    private void createBusRoutesTable(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY, " +
                        "%s TEXT NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s TEXT NOT NULL" +
                        ")",
                BusRoutes.CONTENT_PATH,
                BusRoutes.BusRouteColumns._ID,
                BusRoutes.BusRouteColumns.SHORT_NAME,
                BusRoutes.BusRouteColumns.LONG_NAME,
                BusRoutes.BusRouteColumns.DESCRIPTION,
                BusRoutes.BusRouteColumns.TYPE,
                BusRoutes.BusRouteColumns.COLOR,
                BusRoutes.BusRouteColumns.TEXT_COLOR
        );
        db.execSQL(sql);
        Log.d(TAG, "Bus Routes table created");
    }

    private void createTripsTable(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY, " +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        ")",
                Trips.CONTENT_PATH,
                Trips.TripColumns._ID,
                Trips.TripColumns.ROUTE_ID,
                Trips.TripColumns.SERVICE_ID,
                Trips.TripColumns.HEADSIGN,
                Trips.TripColumns.DIRECTION_ID,
                Trips.TripColumns.BLOCK_ID,
                Trips.TripColumns.WHEELCHAIR_ACCESSIBLE
        );
        db.execSQL(sql);
        Log.d(TAG, "Trips table created");
    }

    private void createStopsTable(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY, " +
                        "%s TEXT NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s TEXT NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        ")",
                Stops.CONTENT_PATH,
                Stops.StopColumns._ID,
                Stops.StopColumns.NAME,
                Stops.StopColumns.DESCRIPTION,
                Stops.StopColumns.LATITUDE,
                Stops.StopColumns.LONGITUDE,
                Stops.StopColumns.WHEELCHAIR_BOARDING
        );
        db.execSQL(sql);
        Log.d(TAG, "Stops table created");
    }

    private void createStopTimesTable(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY, " +
                        "%s INTEGER NOT NULL" +
                        "%s DATETIME NOT NULL" +
                        "%s DATETIME NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s TEXT NOT NULL" +
                        ")",
                StopTimes.CONTENT_PATH,
                StopTimes.StopTimeColumns._ID,
                StopTimes.StopTimeColumns.TRIP_ID,
                StopTimes.StopTimeColumns.ARRIVAL_TIME,
                StopTimes.StopTimeColumns.DEPARTURE_TIME,
                StopTimes.StopTimeColumns.STOP_ID,
                StopTimes.StopTimeColumns.STOP_SEQUENCE
        );
        db.execSQL(sql);
        Log.d(TAG, "Stop Times table created");
    }

    private void createCalendarTable(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY, " +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s INTEGER NOT NULL" +
                        "%s DATETIME NOT NULL" +
                        "%s DATETIME NOT NULL" +
                        ")",
                Calendar.CONTENT_PATH,
                Calendar.CalendarColumns._ID,
                Calendar.CalendarColumns.MONDAY,
                Calendar.CalendarColumns.TUESDAY,
                Calendar.CalendarColumns.WEDNESDAY,
                Calendar.CalendarColumns.THURSDAY,
                Calendar.CalendarColumns.FRIDAY,
                Calendar.CalendarColumns.SATURDAY,
                Calendar.CalendarColumns.SUNDAY,
                Calendar.CalendarColumns.START_DATE,
                Calendar.CalendarColumns.END_DATE
        );
        db.execSQL(sql);
        Log.d(TAG, "Calendar table created");
    }
}
