package com.tehreh1uneh.petrichor.ui.currentweather;

import android.support.v4.app.Fragment;

import com.tehreh1uneh.petrichor.ui.base.SingleFragmentContainerActivity;

public class CurrentWeatherActivity extends SingleFragmentContainerActivity {

    @Override
    public Fragment createFragment() {
        return new CurrentWeatherFragment();
    }
}
