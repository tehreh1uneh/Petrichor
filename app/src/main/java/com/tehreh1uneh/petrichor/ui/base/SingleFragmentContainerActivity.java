package com.tehreh1uneh.petrichor.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tehreh1uneh.petrichor.R;

abstract public class SingleFragmentContainerActivity extends AppCompatActivity {

    abstract protected Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container_fragment_single);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.petrichor_fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.petrichor_fragment_container, fragment)
                    .commit();
        }
    }
}
