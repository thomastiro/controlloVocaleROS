package it.unibas.progetto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import it.unibas.progetto.R;

public class ActivityInfo extends AppCompatActivity {
    private static final String TAG = ActivityInfo.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.vista_informazioni_vecchio);
        setContentView(R.layout.vista_informazioni);
        Log.e(TAG, "onCreate fine");
        //addPreferencesFromResource(R.layout.vista_informazioni_vecchio);
    }
}
