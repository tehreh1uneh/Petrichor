package com.tehreh1uneh.petrichor.ui.currentweather;

import android.support.v4.app.Fragment;

import com.tehreh1uneh.petrichor.ui.base.IOnBackListener;
import com.tehreh1uneh.petrichor.ui.base.SingleFragmentContainerActivity;

public class CurrentWeatherActivity extends SingleFragmentContainerActivity {

    IOnBackListener listener;

    @Override
    public Fragment createFragment() {
        Fragment fragment = new CurrentWeatherFragment();
        listener = (IOnBackListener) fragment;
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (listener != null && !listener.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
