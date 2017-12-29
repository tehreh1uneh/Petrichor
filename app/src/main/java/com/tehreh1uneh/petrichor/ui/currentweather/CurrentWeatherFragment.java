package com.tehreh1uneh.petrichor.ui.currentweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tehreh1uneh.petrichor.R;
import com.tehreh1uneh.petrichor.model.restclient.RestClient;
import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;
import com.tehreh1uneh.petrichor.ui.base.IOnBackListener;
import com.tehreh1uneh.petrichor.ui.saveddata.SharedPreferencesHelper;
import com.tehreh1uneh.petrichor.ui.saveddata.StorageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.DEFAULT_CITY;
import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.FILE_NAME_LAST_WEATHER_DESCRIPTION;

public class CurrentWeatherFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, IOnBackListener {

    public static final String TAG = "###" + CurrentWeatherFragment.class.getSimpleName();

    private View currentWeatherView;

    private EditText cityEditText;
    private TextView descriptionTextView;
    private TextView dayOfWeekTextView;
    private TextView currentTemperatureTextView;
    private TextView todayTextView;
    private TextView minTemperatureTextView;
    private TextView maxTemperatureTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private DrawerLayout drawer;

    private SimpleDateFormat dateFormat;
    private String lastReceivedCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        currentWeatherView = inflater.inflate(R.layout.fragment_weather_current, container, false);
        initializeViews(currentWeatherView);
        loadTextFromInternalOrExternalStorage();
        loadSettingsFromPreferences();

        dateFormat = new SimpleDateFormat("EEEE");
        dateFormat.setTimeZone(TimeZone.getTimeZone(CURRENT_GMT));

        return currentWeatherView;
    }

    private void initializeViews(View currentView) {

        Toolbar toolbar = currentWeatherView.findViewById(R.id.toolbar);
        AppCompatActivity currentActivity = (CurrentWeatherActivity) getActivity();
        currentActivity.setSupportActionBar(toolbar);

        drawer = currentWeatherView.findViewById(R.id.layout_drawer_weather_current);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(currentActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_humberger_24dp);
        toggle.setToolbarNavigationClickListener(this::onClickToolbarNavigationButton);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton floatingButtonSend = currentWeatherView.findViewById(R.id.floating_button_send);
        floatingButtonSend.setOnClickListener(this::onClickFloatingButtonSend);
        NavigationView navigationView = currentWeatherView.findViewById(R.id.navigation_view_weather_current);
        navigationView.setNavigationItemSelectedListener(this);

        setHasOptionsMenu(true);

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

    private void loadSettingsFromPreferences() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        lastReceivedCity = preferences.getString(getString(R.string.petrichor_key_last_received_city), getLastReceivedCity());
        cityEditText.setText(lastReceivedCity);
        updateCurrentWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings_weather_current, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferencesHelper.saveToSharedPreferences(getActivity(), getString(R.string.petrichor_key_last_received_city), getLastReceivedCity());
        super.onSaveInstanceState(outState);
    }

    private void onClickToolbarNavigationButton(View v) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void onClickFloatingButtonSend(View v) {
        Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
        String cityName = cityEditText.getText().toString();

        RestClient.getApi().getCurrentWeatherByCity(cityName, UNITS_FORMAT, LANGUAGE_DESCRIPTION, API_KEY_OPEN_WEATHER).enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherModel receivedModel = response.body();
                    fillData(receivedModel);
                    lastReceivedCity = cityName;
                    saveWeatherDescriptionToInternalAndExternalStorage();
                } else {
                    // TODO message like 'something is going wrong'
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {

            }
        });
    }

    private String getLastReceivedCity() {
        if (lastReceivedCity == null || lastReceivedCity.isEmpty()) {
            return DEFAULT_CITY;
        }
        return lastReceivedCity;
    }

    private void loadTextFromInternalOrExternalStorage() {
        if (!StorageHelper.isExternalStorageReadable()) {
            return;
        }

        String description;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            description = StorageHelper.loadTextFromExternalStorage(getActivity(), FILE_NAME_LAST_WEATHER_DESCRIPTION);
        } else {
            description = StorageHelper.loadTextFromInternalStorage(getActivity(), FILE_NAME_LAST_WEATHER_DESCRIPTION);
        }

        descriptionTextView.setText(description);
    }

    private void saveWeatherDescriptionToInternalAndExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            saveWeatherDescriptionToExternalStorage();
        }
        saveWeatherDescriptionToInternalStorage();
    }

    private void saveWeatherDescriptionToInternalStorage() {
        File file = StorageHelper.getFileFromInternalStorage(getActivity(), FILE_NAME_LAST_WEATHER_DESCRIPTION);
        saveTextToFile(file, descriptionTextView.getText().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveWeatherDescriptionToExternalStorage() {
        File file = StorageHelper.getFileFromExternalStorage(getActivity(), FILE_NAME_LAST_WEATHER_DESCRIPTION);
        saveTextToFile(file, descriptionTextView.getText().toString());
    }

    private void saveTextToFile(File file, String text) {
        if (!StorageHelper.isExternalStorageWritable()) {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(file, false);
            out.write(text.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "saveTextToFile: ", e);
        }
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

        String direction = getDirection(model.getWind().getDegrees());
        String windSpeed = model.getWind().getSpeed().toString();
        String windDescription = String.format("%s %s %s", direction.toUpperCase(), windSpeed, "m/s");
        windTextView.setText(windDescription);

        String pressure = String.format("%s %s", model.getMain().getPressure(), "hPa");
        pressureTextView.setText(pressure);

    }

    private String getDirection(double degrees) {

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
            }

            if (degrees >= left && degrees < right) {
                position++;
                break;
            }

            left += step;
            right += step;
        }

        return directions[position];
    }

    @Override
    public boolean onBackPressed() {
        DrawerLayout drawer = currentWeatherView.findViewById(R.id.layout_drawer_weather_current);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.petrichor_action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = currentWeatherView.findViewById(R.id.layout_drawer_weather_current);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
