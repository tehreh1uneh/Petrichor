package com.tehreh1uneh.petrichor.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.tehreh1uneh.petrichor.R;
import com.tehreh1uneh.petrichor.model.database.WeatherHistorySource;
import com.tehreh1uneh.petrichor.model.restclient.RestClient;
import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;
import com.tehreh1uneh.petrichor.model.restclient.response.Weather;
import com.tehreh1uneh.petrichor.model.restclient.service.CurrentWeatherCallback;
import com.tehreh1uneh.petrichor.model.restclient.service.IRetrofitEventListener;

import java.util.List;

import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.API_KEY_OPEN_WEATHER;
import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.LANGUAGE_DESCRIPTION;
import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.UNITS_FORMAT;

public class CurrentWeatherWidget extends AppWidgetProvider implements IRetrofitEventListener {

    private Context context;
    private AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;
        this.context = context;
        getCurrentWeatherInfo(context);
    }

    private void getCurrentWeatherInfo(Context context) {
        String city = getLastCity(context);

        if (city == null) {
            return;
        }

        CurrentWeatherCallback callback = new CurrentWeatherCallback(this);
        RestClient.getApi().getCurrentWeatherByCity(city, UNITS_FORMAT, LANGUAGE_DESCRIPTION, API_KEY_OPEN_WEATHER).enqueue(callback);
    }

    private String getLastCity(Context context) {
        WeatherHistorySource db = new WeatherHistorySource(context);

        db.open();
        String lastCity = db.getLastCity();
        db.close();

        return lastCity;
    }

    @Override
    public void onResponseCurrentWeatherModel(CurrentWeatherModel model) {

        List<Weather> list = model.getWeather();

        if (list.size() != 0) {
            Weather currentWeather = list.get(0);
            int imageId = getImageId(currentWeather.getIcon());
            String description = model.getMain().getTemperature().toString() + "°";

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, imageId, description);
            }
        }
        free();
    }

    private int getImageId(String apiId) {
        switch (apiId) {
            case "01d":
            case "01n":
                return R.drawable.ic_clear_sky;
            case "02d":
            case "02n":
                return R.drawable.ic_few_clouds;
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                return R.drawable.ic_scattered_clouds;
            case "09d":
            case "09n":
                return R.drawable.ic_shower_rain;
            case "10d":
            case "10n":
                return R.drawable.ic_rain;
            case "11d":
            case "11n":
                return R.drawable.ic_thunderstorm;
            case "13d":
            case "13n":
                return R.drawable.ic_snow;
            case "50d":
            case "50n":
                return R.drawable.ic_mist;
            default:
                return R.drawable.ic_clear_sky;
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int imageId, CharSequence description) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.current_weather_widget);
        views.setImageViewResource(R.id.petrichor_widget_image_view, imageId);
        views.setTextViewText(R.id.petrichor_widget_description, description);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void free() {
        context = null;
        appWidgetManager = null;
        appWidgetIds = null;
    }
}

