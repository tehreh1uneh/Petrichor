package com.tehreh1uneh.petrichor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.tehreh1uneh.petrichor.restclient.RestClient;
import com.tehreh1uneh.petrichor.restclient.jsonmodel.CurrentWeatherModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tehreh1uneh.petrichor.restclient.config.Config.API_KEY_OPEN_WEATHER;

public class MainActivity extends AppCompatActivity {

    TextView textViewSample;
    CurrentWeatherModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewSample = findViewById(R.id.tv_sample);

        RestClient.getApi().getCurrentWeather("London", API_KEY_OPEN_WEATHER).enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                model = response.body();
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_LONG).show();
            }
        });


    }
}
