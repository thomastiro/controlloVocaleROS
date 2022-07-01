package it.unibas.progetto.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import it.unibas.progetto.R;

public class ActivityJoystick extends AppCompatActivity {

    private final String TAG = ActivityJoystick.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate inizio");
        setContentView(R.layout.activity_joystick);
        Log.e(TAG, "onCreate fine");
    }
}
