package com.tehreh1uneh.petrichor.ui.currentweather;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.tehreh1uneh.petrichor.R;
import com.tehreh1uneh.petrichor.model.Utils;
import com.tehreh1uneh.petrichor.model.database.DatabaseHelper;
import com.tehreh1uneh.petrichor.model.database.WeatherHistorySource;
import com.tehreh1uneh.petrichor.model.preferences.SharedPreferencesHelper;
import com.tehreh1uneh.petrichor.model.preferences.StorageHelper;
import com.tehreh1uneh.petrichor.model.restclient.response.CurrentWeatherModel;
import com.tehreh1uneh.petrichor.model.restclient.service.CurrentWeatherService;
import com.tehreh1uneh.petrichor.model.restclient.service.IRetrofitEventListener;
import com.tehreh1uneh.petrichor.ui.base.IOnBackListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.API_KEY_OPEN_WEATHER;
import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.LANGUAGE_DESCRIPTION;
import static com.tehreh1uneh.petrichor.model.restclient.config.ConfigRestClient.UNITS_FORMAT;
import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.CURRENT_GMT;
import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.DEFAULT_CITY;
import static com.tehreh1uneh.petrichor.ui.config.ConfigUi.FILE_NAME_LAST_WEATHER_DESCRIPTION;

public class CurrentWeatherFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, IOnBackListener, IRetrofitEventListener {

    public static final String TAG = "###" + CurrentWeatherFragment.class.getSimpleName();
    boolean bound = false;
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
    private SimpleDateFormat dateFormatDayOfWeek = new SimpleDateFormat("EEEE");
    private String lastReceivedCity;
    private WeatherHistorySource dbSource;
    private CurrentWeatherService weatherService;
    private ServiceConnection serviceConnection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        currentWeatherView = inflater.inflate(R.layout.fragment_weather_current, container, false);
        initializeViews(currentWeatherView);
        loadTextFromInternalOrExternalStorage();
        loadSettingsFromPreferences();
        initializeDatabase();

        dateFormatDayOfWeek.setTimeZone(TimeZone.getTimeZone(CURRENT_GMT));

        return currentWeatherView;
    }

    private void initializeViews(View currentView) {
        Log.d(TAG, "initializeViews");

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
        Log.d(TAG, "loadSettingsFromPreferences");
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        lastReceivedCity = preferences.getString(getString(R.string.petrichor_key_last_received_city), getLastReceivedCity());
        cityEditText.setText(lastReceivedCity);
    }

    private void initializeDatabase() {
        Log.d(TAG, "initializeDatabase");
        dbSource = new WeatherHistorySource(this.getContext());
        dbSource.open();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected");

                CurrentWeatherService.CurrentWeatherServiceBinder binder = (CurrentWeatherService.CurrentWeatherServiceBinder) service;
                weatherService = binder.getService();
                weatherService.setListener(CurrentWeatherFragment.this);
                bound = true;
                updateCurrentWeather();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
                bound = false;
            }
        };

        Intent intent = new Intent(getContext(), CurrentWeatherService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (bound) {
            getActivity().unbindService(serviceConnection);
            bound = false;
            Log.d(TAG, "onStop: service unbidden");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings_weather_current, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        SharedPreferencesHelper.saveToSharedPreferences(getActivity(), getString(R.string.petrichor_key_last_received_city), getLastReceivedCity());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        dbSource.close();
        super.onDestroyView();
    }

    private void onClickToolbarNavigationButton(View v) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void onClickFloatingButtonSend(View v) {
        sendCurrentWeatherInfo();
    }

    private void sendCurrentWeatherInfo() {

        String message = String.format("%s : %s, temperature: %s, pressure: %s, wind: %s",
                getLastReceivedCity(), descriptionTextView.getText().toString().toLowerCase(), currentTemperatureTextView.getText(),
                pressureTextView.getText(), windTextView.getText());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
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
        Log.d(TAG, "updateCurrentWeather");
        String cityName = cityEditText.getText().toString();
        setWaitingMode(true);

        if (bound) {
            weatherService.getCurrentWeather(cityName, UNITS_FORMAT, LANGUAGE_DESCRIPTION, API_KEY_OPEN_WEATHER);
        }
    }

    @Override
    public void onResponseCurrentWeatherModel(CurrentWeatherModel model) {
        updateUI(model);
        setWaitingMode(false);
    }

    private void updateUI(CurrentWeatherModel receivedModel) {
        if (receivedModel != null) {
            String cityName = String.format("%s,%s", receivedModel.getName(), receivedModel.getSys().getCountry());
            fillData(receivedModel);
            lastReceivedCity = cityName;
            saveWeatherDescriptionToInternalAndExternalStorage();
            addWeatherHistory(receivedModel);
        } else {
            cityEditText.setText(lastReceivedCity);
            makeErrorToast();
        }
    }

    private void makeErrorToast() {
        Toast.makeText(getContext(), R.string.petrichor_error_message, Toast.LENGTH_LONG).show();
    }

    private String getLastReceivedCity() {
        if (lastReceivedCity == null || lastReceivedCity.isEmpty()) {
            return DEFAULT_CITY;
        }
        return lastReceivedCity;
    }

    private void fillData(CurrentWeatherModel model) {
        String city = String.format("%s,%s", model.getName(), model.getSys().getCountry());
        cityEditText.setText(city);

        descriptionTextView.setText(model.getWeather().get(0).getMain());
        dayOfWeekTextView.setText(dateFormatDayOfWeek.format(new Date()));

        String temp = String.format("%s%s", model.getMain().getTemperature(), "°");
        currentTemperatureTextView.setText(temp);

        todayTextView.setText(R.string.petrichor_today);

        String minTemp = model.getMain().getTempMin().toString();
        minTemperatureTextView.setText(minTemp);

        String maxTemp = model.getMain().getTempMax().toString();
        maxTemperatureTextView.setText(maxTemp);

        String direction = Utils.getDirection(model.getWind().getDegrees(), getResources().getStringArray(R.array.directions));
        String windSpeed = model.getWind().getSpeed().toString();
        String windDescription = String.format("%s %s %s", direction.toUpperCase(), windSpeed, "m/s");
        windTextView.setText(windDescription);

        String pressure = String.format("%s %s", model.getMain().getPressure(), "hPa");
        pressureTextView.setText(pressure);
    }

    private void addWeatherHistory(CurrentWeatherModel receivedModel) {
        Date date = new Date(receivedModel.getUnixTimeStamp() * 1000);
        String formattedDate = DatabaseHelper.dateFormatDatabase.format(date);
        String city = cityEditText.getText().toString();
        float temperature = receivedModel.getMain().getTemperature().floatValue();
        float pressure = receivedModel.getMain().getPressure().floatValue();
        float windSpeed = receivedModel.getWind().getSpeed().floatValue();

        if (!dbSource.hasInfo(city, formattedDate)) {
            dbSource.add(city, formattedDate, temperature, pressure, windSpeed);
        }
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
        Utils.saveTextToFile(file, descriptionTextView.getText().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveWeatherDescriptionToExternalStorage() {
        File file = StorageHelper.getFileFromExternalStorage(getActivity(), FILE_NAME_LAST_WEATHER_DESCRIPTION);
        Utils.saveTextToFile(file, descriptionTextView.getText().toString());
    }

    private void setWaitingMode(boolean showProgressBar) {
        int progressBarVisibility = showProgressBar ? View.VISIBLE : View.INVISIBLE;
        int elementsVisibility = !showProgressBar ? View.VISIBLE : View.INVISIBLE;

        currentWeatherView.findViewById(R.id.petrichor_progress_bar).setVisibility(progressBarVisibility);
        descriptionTextView.setVisibility(elementsVisibility);
        currentTemperatureTextView.setVisibility(elementsVisibility);
        dayOfWeekTextView.setVisibility(elementsVisibility);
        todayTextView.setVisibility(elementsVisibility);
        minTemperatureTextView.setVisibility(elementsVisibility);
        maxTemperatureTextView.setVisibility(elementsVisibility);
        currentWeatherView.findViewById(R.id.petrichor_header_wind).setVisibility(elementsVisibility);
        windTextView.setVisibility(elementsVisibility);
        currentWeatherView.findViewById(R.id.petrichor_header_pressure).setVisibility(elementsVisibility);
        pressureTextView.setVisibility(elementsVisibility);
        currentWeatherView.findViewById(R.id.petrichor_view_horizontal_line).setVisibility(elementsVisibility);
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
            // TODO add settings activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            // TODO add implicit intent for social networks
        } else if (id == R.id.nav_send) {
            sendCurrentWeatherInfo();
        }

        DrawerLayout drawer = currentWeatherView.findViewById(R.id.layout_drawer_weather_current);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
