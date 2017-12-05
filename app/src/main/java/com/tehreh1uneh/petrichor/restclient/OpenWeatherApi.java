package com.tehreh1uneh.petrichor.restclient;


import com.tehreh1uneh.petrichor.restclient.jsonmodel.CurrentWeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.tehreh1uneh.petrichor.restclient.config.Config.GET_URL;

public interface OpenWeatherApi {
    @GET(GET_URL)
    Call<CurrentWeatherModel>getCurrentWeather(@Query("q")String city, @Query("APPID")String key);
}
