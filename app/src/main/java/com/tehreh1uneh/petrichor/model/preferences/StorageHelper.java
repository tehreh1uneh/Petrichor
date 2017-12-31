package com.tehreh1uneh.petrichor.model.preferences;


import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.MAX_WEATHER_DESCRIPTION_LENGTH_BYTES;

public class StorageHelper {

    private static final String TAG = "###" + StorageHelper.class.getSimpleName();

    private StorageHelper() {
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String loadTextFromExternalStorage(Activity activity, String fileName) {
        File file = getFileFromExternalStorage(activity, fileName);
        return loadTextFromFile(file);
    }

    public static File getFileFromInternalStorage(Activity activity, String fileName) {
        File directory = new File(activity.getExternalFilesDir(null).getPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(directory, fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    public static File getFileFromExternalStorage(Activity activity, String fileName) {
        return new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
    }

    private static String loadTextFromFile(File file) {

        if (!file.exists()) {
            return "";
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] textBytes = new byte[MAX_WEATHER_DESCRIPTION_LENGTH_BYTES];
            inputStream.read(textBytes);

            return new String(textBytes);
        } catch (IOException e) {
            Log.e(TAG, "loadTextFromExternalOrInternalStorage: ", e);
            return "";
        }
    }

    public static String loadTextFromInternalStorage(Activity activity, String fileName) {
        File file = getFileFromInternalStorage(activity, fileName);
        return loadTextFromFile(file);
    }


}
