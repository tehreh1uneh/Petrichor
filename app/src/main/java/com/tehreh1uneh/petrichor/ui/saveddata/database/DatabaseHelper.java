package com.tehreh1uneh.petrichor.ui.saveddata.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weatherbase.db";
    private static final int DATABASE_VERSION = 1;
    static final String TABLE_WEATHER_HISTORY = "weatherhistory";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_CITY = "city";
    static final String COLUMN_TEMPERATURE = "temperature";
    static final String COLUMN_PRESSURE = "pressure";
    static final String COLUMN_WIND_SPEED = "windspeed";

    public static final SimpleDateFormat dateFormatDatabase = new SimpleDateFormat("yyyy-MM-dd");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String creationQuery = String.format(
                "CREATE TABLE IF NOT EXISTS [%s] ( " +
                        "[%s] INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "[%s] TEXT," +
                        "[%s] TEXT," +
                        "[%s] REAL," +
                        "[%s] REAL," +
                        "[%s] REAL" +
                        ");",
                TABLE_WEATHER_HISTORY, COLUMN_ID, COLUMN_DATE,
                COLUMN_CITY, COLUMN_TEMPERATURE, COLUMN_PRESSURE,
                COLUMN_WIND_SPEED);

        db.execSQL(creationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
