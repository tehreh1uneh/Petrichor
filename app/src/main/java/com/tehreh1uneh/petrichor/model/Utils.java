package com.tehreh1uneh.petrichor.model;

public final class Utils {

    private static final String TAG = "###" + Utils.class.getSimpleName();

    private Utils() {
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
