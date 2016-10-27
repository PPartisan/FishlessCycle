package com.github.ppartisan.fishlesscycle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.ppartisan.fishlesscycle.util.ConversionUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getClass().getName(), "Test Dosage: " + ConversionUtils.getAmmoniaDosage(46,5,10));

        if(getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }

    }

}
