package com.tehreh1uneh.petrichor.model.restclient.service;

import android.util.Log;

import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentWeatherCallback implements Callback<CurrentWeatherModel> {

    private static final String TAG = "###" + CurrentWeatherCallback.class.getSimpleName();
    private IRetrofitEventListener listener;

    public CurrentWeatherCallback(IRetrofitEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
        if (response.isSuccessful()) {
            listener.onResponseCurrentWeatherModel(response.body());
        } else {
            listener.onResponseCurrentWeatherModel(null);
            Log.d(TAG, "onResponse: [not successful]" + response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {
        listener.onResponseCurrentWeatherModel(null);
        Log.d(TAG, "onFailure");
    }
}
