package com.tehreh1uneh.petrichor.ui.currentweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tehreh1uneh.petrichor.R;
import com.tehreh1uneh.petrichor.model.restclient.RestClient;
import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.API_KEY_OPEN_WEATHER;
import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.LANGUAGE_DESCRIPTION;
import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.UNITS_FORMAT;
import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.CURRENT_GMT;

public class CurrentWeatherFragment extends Fragment {

    private EditText cityEditText;
    private TextView descriptionTextView;
    private TextView dayOfWeekTextView;
    private TextView currentTemperatureTextView;
    private TextView todayTextView;
    private TextView minTemperatureTextView;
    private TextView maxTemperatureTextView;
    private TextView windTextView;
    private TextView pressureTextView;

    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View currentWeatherView = inflater.inflate(R.layout.fragment_weather_current, container, false);
        initializeViews(currentWeatherView);

        dateFormat = new SimpleDateFormat("EEEE");
        dateFormat.setTimeZone(TimeZone.getTimeZone(CURRENT_GMT));

        return currentWeatherView;
    }

    private void initializeViews(View currentView) {
        cityEditText = currentView.findViewById(R.id.petrichor_edit_text_city);
        cityEditText.setOnEditorActionListener(this::onChangeFieldCity);

        descriptionTextView = currentView.findViewById(R.id.petrichor_text_view_description);
        dayOfWeekTextView = currentView.findViewById(R.id.petrichor_text_view_day_of_week);

        currentTemperatureTextView = currentView.findViewById(R.id.petrichor_text_view_current_temperature);
        todayTextView = currentView.findViewById(R.id.petrichor_text_view_today);

        minTemperatureTextView = currentView.findViewById(R.id.petrichor_text_view_temperature_min);
        maxTemperatureTextView = currentView.findViewById(R.id.petrichor_text_view_temperature_max);
        windTextView = currentView.findViewById(R.id.petrichor_text_view_wind);
        pressureTextView = currentView.findViewById(R.id.petrichor_text_view_pressure);
    }

    private boolean onChangeFieldCity(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (!event.isShiftPressed()) {
                updateCurrentWeather();
                return true;
            }
        }
        return false;
    }

    private void updateCurrentWeather() {
        RestClient.getApi().getCurrentWeatherByCity(cityEditText.getText().toString(), UNITS_FORMAT, LANGUAGE_DESCRIPTION, API_KEY_OPEN_WEATHER).enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherModel receivedModel = response.body();
                    fillData(receivedModel);
                } else {

                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {

            }
        });
    }

    private void fillData(CurrentWeatherModel model) {
        String city = String.format("%s,%s", model.getName(), model.getSys().getCountry());
        cityEditText.setText(city);

        descriptionTextView.setText(model.getWeather().get(0).getMain());
        dayOfWeekTextView.setText(dateFormat.format(new Date()));

        String temp = String.format("%s%s", model.getMain().getTemperature(), "°");
        currentTemperatureTextView.setText(temp);

        todayTextView.setText(R.string.petrichor_today);

        String minTemp = model.getMain().getTempMin().toString();
        minTemperatureTextView.setText(minTemp);

        String maxTemp = model.getMain().getTempMax().toString();
        maxTemperatureTextView.setText(maxTemp);

        String direction = direction(model.getWind().getDegrees());
        String windSpeed = model.getWind().getSpeed().toString();
        String windDescription = String.format("%s %s %s", direction.toUpperCase(), windSpeed, "m/s");
        windTextView.setText(windDescription);

        String pressure = String.format("%s %s", model.getMain().getPressure(), "hPa");
        pressureTextView.setText(pressure);

    }

    private String direction(double degrees) {

        String[] directions = getResources().getStringArray(R.array.directions);
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
            } else {
                if (degrees >= left && degrees < right) {
                    break;
                }
            }

            left += step;
            right += step;
        }

        return directions[position];
    }


}
