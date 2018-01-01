package com.tehreh1uneh.petrichor.model.restclient.service;


import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;

public interface IRetrofitEventListener {
    void onResponseCurrentWeatherModel(CurrentWeatherModel model);
}
