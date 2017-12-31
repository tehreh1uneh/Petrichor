package com.tehreh1uneh.petrichor.ui.saveddata.database;


public class WeatherHistoryRow {

    private long columnId;
    private String columnDate;
    private String columnCity;
    private float columnTemperature;
    private float columnPressure;
    private float columnWindSpeed;

    public WeatherHistoryRow(long columnId, String columnDate, String columnCity, float columnTemperature, float columnPressure, float columnWindSpeed) {
        this.columnId = columnId;
        this.columnDate = columnDate;
        this.columnCity = columnCity;
        this.columnTemperature = columnTemperature;
        this.columnPressure = columnPressure;
        this.columnWindSpeed = columnWindSpeed;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getColumnDate() {
        return columnDate;
    }

    public void setColumnDate(String columnDate) {
        this.columnDate = columnDate;
    }

    public String getColumnCity() {
        return columnCity;
    }

    public void setColumnCity(String columnCity) {
        this.columnCity = columnCity;
    }

    public float getColumnTemperature() {
        return columnTemperature;
    }

    public void setColumnTemperature(float columnTemperature) {
        this.columnTemperature = columnTemperature;
    }

    public float getColumnPressure() {
        return columnPressure;
    }

    public void setColumnPressure(float columnPressure) {
        this.columnPressure = columnPressure;
    }

    public float getColumnWindSpeed() {
        return columnWindSpeed;
    }

    public void setColumnWindSpeed(float columnWindSpeed) {
        this.columnWindSpeed = columnWindSpeed;
    }
}
