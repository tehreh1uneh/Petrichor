package com.tehreh1uneh.petrichor.model.restclient;

import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.GET_URL;

public interface OpenWeatherApi {
    @GET(GET_URL)
    Call<CurrentWeatherModel> getCurrentWeatherByCity(@Query("q") String city, @Query("units") String units, @Query("lang") String languageCode, @Query("APPID") String key);
}
