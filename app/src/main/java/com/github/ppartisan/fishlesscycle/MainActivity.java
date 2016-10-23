package com.github.ppartisan.fishlesscycle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements MainFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }

    }

    @Override
    public void setCustomActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override
    public ActionBar getCustomActionBar() {
        return getSupportActionBar();
    }

}
