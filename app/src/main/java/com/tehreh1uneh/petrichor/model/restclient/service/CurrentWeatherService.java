package com.tehreh1uneh.petrichor.model.restclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.tehreh1uneh.petrichor.model.restclient.RestClient;

public class CurrentWeatherService extends Service {

    private final IBinder currentWeatherServiceBinder = new CurrentWeatherServiceBinder();
    private IRetrofitEventListener listener;

    public CurrentWeatherService() {
    }

    public void setListener(IRetrofitEventListener listener) {
        this.listener = listener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return currentWeatherServiceBinder;
    }

    public void getCurrentWeather(String cityName, String unitsFormat, String descriptionLanguage, String apiKey) {
        if (listener == null) {
            throw new RuntimeException("Callback listener for service was not set");
        }
        CurrentWeatherCallback callback = new CurrentWeatherCallback(listener);
        RestClient.getApi().getCurrentWeatherByCity(cityName, unitsFormat, descriptionLanguage, apiKey).enqueue(callback);
    }

    public class CurrentWeatherServiceBinder extends Binder {
        public CurrentWeatherService getService() {
            return CurrentWeatherService.this;
        }
    }
}
