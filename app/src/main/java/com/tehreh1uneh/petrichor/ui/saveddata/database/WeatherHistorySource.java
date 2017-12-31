package com.tehreh1uneh.petrichor.ui.saveddata.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WeatherHistorySource {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CITY,
            DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_TEMPERATURE,
            DatabaseHelper.COLUMN_PRESSURE,
            DatabaseHelper.COLUMN_WIND_SPEED
    };

    public WeatherHistorySource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void add(String city, String date, float temperature, float pressure, float windSpeed) {
        ContentValues row = new ContentValues(5);
        row.put(DatabaseHelper.COLUMN_CITY, city);
        row.put(DatabaseHelper.COLUMN_DATE, date);
        row.put(DatabaseHelper.COLUMN_TEMPERATURE, temperature);
        row.put(DatabaseHelper.COLUMN_PRESSURE, pressure);
        row.put(DatabaseHelper.COLUMN_WIND_SPEED, windSpeed);

        db.insert(DatabaseHelper.TABLE_WEATHER_HISTORY, null, row);
    }

    public boolean hasInfo(String city, String date) {

        String whereCondition = String.format("%s = '%s' AND %s = '%s'",
                DatabaseHelper.COLUMN_CITY, city,
                DatabaseHelper.COLUMN_DATE, date);

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_WEATHER_HISTORY,
                columns,
                whereCondition,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        return !cursor.isAfterLast();
    }

    public List<WeatherHistoryRow> getAllHistory() {
        List<WeatherHistoryRow> history = new ArrayList<>();

        Cursor cursor = db.query(DatabaseHelper.TABLE_WEATHER_HISTORY, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WeatherHistoryRow row = getWeatherHistoryRow(cursor);
            history.add(row);

            cursor.moveToNext();
        }

        return history;
    }

    private WeatherHistoryRow getWeatherHistoryRow(Cursor cursor) {
        return new WeatherHistoryRow(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getFloat(3),
                cursor.getFloat(4),
                cursor.getFloat(5)
        );
    }

}
