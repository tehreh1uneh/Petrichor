package com.tehreh1uneh.petrichor.ui.currentweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tehreh1uneh.petrichor.R;
import com.tehreh1uneh.petrichor.model.restclient.RestClient;
import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tehreh1uneh.petrichor.model.restclient.config.Config.API_KEY_OPEN_WEATHER;

public class CurrentWeatherFragment extends Fragment {

    private EditText cityEditText;
    private Button sendButton;
    private TextView descriptionTextView;
    CurrentWeatherModel model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View currentWeatherView = inflater.inflate(R.layout.fragment_current_weather, container, false);

        cityEditText = currentWeatherView.findViewById(R.id.petrichor_edit_text_city);
        sendButton = currentWeatherView.findViewById(R.id.petrichor_button_send);
        descriptionTextView = currentWeatherView.findViewById(R.id.petrichor_text_view_description);

        sendButton.setOnClickListener(this::onClickButtonSend);

        model = new CurrentWeatherModel();

        return currentWeatherView;
    }

    private void onClickButtonSend(View v) {

        RestClient.getApi().getCurrentWeatherByCity(cityEditText.getText().toString(), API_KEY_OPEN_WEATHER).enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                if (response.isSuccessful()) {
                    descriptionTextView.setText(response.body().getMain().getTemperature().toString());
                } else {

                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {

            }
        });
    }
}
