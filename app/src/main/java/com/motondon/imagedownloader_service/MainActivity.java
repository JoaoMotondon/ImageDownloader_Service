package com.motondon.imagedownloader_service;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);

        if (fragment == null) {
            fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container_id, fragment, MainFragment.TAG).commit();
        }
    }
}
