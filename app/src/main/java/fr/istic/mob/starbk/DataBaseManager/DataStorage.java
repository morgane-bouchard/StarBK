package fr.istic.mob.starbk.DataBaseManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import fr.istic.mob.starbk.StarContract;

import fr.istic.mob.starbk.StarContract.BusRoutes.BusRouteColumns;
import fr.istic.mob.starbk.StarContract.Calendar.CalendarColumns;
import fr.istic.mob.starbk.StarContract.StopTimes.StopTimeColumns;
import fr.istic.mob.starbk.StarContract.Stops.StopColumns;
import fr.istic.mob.starbk.StarContract.Trips.TripColumns;

import static fr.istic.mob.starbk.DataBaseManager.myDB.DataVersions.content_path;
import static fr.istic.mob.starbk.DataBaseManager.myDB.DataVersions.DataVersionColumns;

public class DataStorage {
    private static final String TAG = DataStorage.class.getSimpleName();

    private CreateDataTable dbCreator;
    private String filesDir;
    private Context aContext;

    public DataStorage(Context context) {
        this.dbCreator = new CreateDataTable(context);
        this.aContext = context;
    }

    public CreateDataTable getdbCreator() {
        return this.dbCreator;
    }

    public void close() {
        dbCreator.close();
    }

    public SQLiteDatabase getReadableDatabase() {
        return dbCreator.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return dbCreator.getWritableDatabase();
    }

    public Cursor getBusRoutes() {
        return getReadableDatabase().query(myDB.BusRoutes.CONTENT_PATH, null, null, null, null, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getCurrentDataVersion() {
        try (SQLiteDatabase db = this.dbCreator.getReadableDatabase()) {
            try (Cursor query = db.query(content_path, new String[]{"MAX(" + DataVersionColumns.createdAt + ")", DataVersionColumns.fileVersion}, null, null, null, null, null, null)) {
                return query.moveToNext() ? query.getString(query.getColumnIndex(DataVersionColumns.fileVersion)) : null;
            }
        }
    }

    public void setFilesDir(String filesDir) {
        this.filesDir = filesDir;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateDataVersion(ContentValues values) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.insert(content_path, null, values);
        }
    }

    public void updateDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            dbCreator.dropTables(db);
            dbCreator.createTables(db);
            updateCalendarTable(db);
            updateBusRoutesTable(db);
            updateStopsTable(db);
            updateTripsTable(db);
//            updateStopTimesTable(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateStopTimesTable(SQLiteDatabase db) {
        String filename = "stop_times.txt";
        try {
            String file = stringifyFile(filename);
            String ls = System.getProperty("line.separator");
            String[] lines = file.split(ls);
            int totalLines = lines.length;
            int limit = 1000, i = 1, offset = limit;
            while (i < totalLines) {
                StringBuilder stBuilder = new StringBuilder(
                        String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES ",
                                StarContract.StopTimes.CONTENT_PATH,
                                StopTimeColumns.TRIP_ID,
                                StopTimeColumns.ARRIVAL_TIME,
                                StopTimeColumns.DEPARTURE_TIME,
                                StopTimeColumns.STOP_ID,
                                StopTimeColumns.STOP_SEQUENCE
                        )
                );

                if (i + offset > totalLines - 1)
                    offset = totalLines - 1;

                for (int k = i; k <= offset; k++) {
                    String[] splits = lines[k].split(",");
                    stBuilder.append(
                            String.format(
                                    "(%s, %s, %s, %s, %s),",
                                    splits[0], splits[1], splits[2], splits[3], splits[4]
                            )
                    );
                }

                String sql = stBuilder.substring(0, stBuilder.length() - 1);
                db.execSQL(sql);

                i += limit;
                offset += limit;
            }
            Log.e(TAG, "stop times table updated");
        } catch (IOException e) {
            Log.e(TAG, "stop times table updating failed", e);
        }
    }

    private void updateStopsTable(SQLiteDatabase db) {
        String filename = "stops.txt";
        try {
            StringBuilder stBuilder = new StringBuilder(
                    String.format("INSERT INTO %s(_id, %s, %s, %s, %s, %s) VALUES ",
                            StarContract.Stops.CONTENT_PATH,
                            StopColumns.NAME,
                            StopColumns.DESCRIPTION,
                            StopColumns.LATITUDE,
                            StopColumns.LONGITUDE,
                            StopColumns.WHEELCHAIR_BOARDING
                    )
            );

            Scanner scanner = readFile(filename);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] splits = line.split(",");
                stBuilder.append(
                        String.format(
                                "(%s, %s, %s, %s, %s, %s),",
                                splits[0], splits[2], splits[3], splits[4], splits[5], splits[10]
                        )
                );
            }

            String sql = stBuilder.substring(0, stBuilder.length() - 1);
            Log.e(TAG, "Bus stops updated");
            db.execSQL(sql);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateTripsTable(SQLiteDatabase db) {
        String filename = "trips.txt";
        try {
            StringBuilder stBuilder = new StringBuilder(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s) VALUES ",
                            StarContract.Trips.CONTENT_PATH,
                            TripColumns.ROUTE_ID,
                            TripColumns.SERVICE_ID,
                            TripColumns.HEADSIGN,
                            TripColumns.DIRECTION_ID,
                            TripColumns.BLOCK_ID,
                            TripColumns.WHEELCHAIR_ACCESSIBLE
                    )
            );

            Scanner scanner = readFile(filename);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] splits = line.split(",");
                stBuilder.append(
                        String.format(
                                "(%s,%s,%s,%s,%s,%s),",
                                splits[0], splits[1], splits[3], splits[5], splits[6], splits[8]
                        )
                );
            }

            String sql = stBuilder.substring(0, stBuilder.length() - 1);
            db.execSQL(sql);
            Log.e(TAG, "Bus trips updated");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateBusRoutesTable(SQLiteDatabase db) {
        String filename = "routes.txt";
        try {
            StringBuilder stBuilder = new StringBuilder(
                    String.format("INSERT INTO %s(_id, %s, %s, %s, %s, %s, %s) VALUES ",
                            StarContract.BusRoutes.CONTENT_PATH,
                            BusRouteColumns.SHORT_NAME,
                            BusRouteColumns.LONG_NAME,
                            BusRouteColumns.DESCRIPTION,
                            BusRouteColumns.TYPE,
                            BusRouteColumns.COLOR,
                            BusRouteColumns.TEXT_COLOR
                    )
            );

            Scanner scanner = readFile(filename);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] splits = line.split(",");
                stBuilder.append(
                        String.format(
                                "(%s,%s,%s,%s,%s,%s,%s),",
                                splits[0], splits[2], splits[3], splits[4], splits[5], splits[7], splits[8]
                        )
                );
            }

            String sql = stBuilder.substring(0, stBuilder.length() - 1);
            db.execSQL(sql);
            Log.e(TAG, "Bus routes updated");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateCalendarTable(SQLiteDatabase db) {
        String filename = "calendar.txt";
        try {
            StringBuilder stBuilder = new StringBuilder(
                    String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES ",
                            StarContract.Calendar.CONTENT_PATH,
                            CalendarColumns.MONDAY,
                            CalendarColumns.TUESDAY,
                            CalendarColumns.WEDNESDAY,
                            CalendarColumns.THURSDAY,
                            CalendarColumns.FRIDAY,
                            CalendarColumns.SATURDAY,
                            CalendarColumns.SUNDAY,
                            CalendarColumns.START_DATE,
                            CalendarColumns.END_DATE
                    )
            );

            Scanner scanner = readFile(filename);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] splits = line.split(",");
                stBuilder.append(
                        String.format(
                                "(%s, %s, %s, %s, %s, %s, %s, %s, %s),",
                                splits[1],
                                splits[2],
                                splits[3],
                                splits[4],
                                splits[5],
                                splits[6],
                                splits[7],
                                splits[8],
                                splits[9]
                        )
                );
            }

            String sql = stBuilder.substring(0, stBuilder.length() - 1);
            db.execSQL(sql);
            Log.e(TAG, "calendar updated");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Scanner readFile(String filename) throws FileNotFoundException {
        InputStream is = new FileInputStream(this.filesDir + "/" + filename);
        Scanner scanner = new Scanner(is);
        scanner.useDelimiter("\n");
        scanner.nextLine(); // skip first line

        return scanner;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String stringifyFile(String filename) throws IOException {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try (BufferedReader reader = new BufferedReader(new FileReader(this.filesDir + "/" + filename))) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        }
    }
}
