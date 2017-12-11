package com.tehreh1uneh.petrichor.model.restclient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.tehreh1uneh.petrichor.model.restclient.config.Config.BASE_URL;


public class RestClient{

    private static OpenWeatherApi api;

    private static void createApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(OpenWeatherApi.class);
    }

    public static OpenWeatherApi getApi() {
        if (api == null){
            createApi();
        }
        return api;
    }

}
