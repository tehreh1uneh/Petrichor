package com.tehreh1uneh.petrichor.model;

import android.util.Log;

import com.tehreh1uneh.petrichor.model.preferences.StorageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class Utils {

    private static final String TAG = "###" + Utils.class.getSimpleName();

    private Utils() {
    }

    public static void saveTextToFile(File file, String text) {
        if (!StorageHelper.isExternalStorageWritable()) {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(file, false);
            out.write(text.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "saveTextToFile: ", e);
        }
    }

    public static String getDirection(double degrees, String[] directions) {

        int amountOfDirections = directions.length;

        double max = 360;
        double step = max / amountOfDirections;
        double halfStep = step / 2;
        double left = halfStep;
        double right = left + step;
        int position = 0;

        for (; position < amountOfDirections; position++) {

            if (position == 0) {
                if (degrees >= max - halfStep || degrees < halfStep) {
                    break;
                }
            }

            if (degrees >= left && degrees < right) {
                position++;
                break;
            }

            left += step;
            right += step;
        }

        return directions[position];
    }

}
