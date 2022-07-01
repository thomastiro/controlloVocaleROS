package it.unibas.progetto.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import it.unibas.progetto.R;
import it.unibas.progetto.vista.VistaImpostazioni;
import it.unibas.progetto.vista.VistaPrincipale;

public class ActivityImpostazioni extends AppCompatActivity {
    private static final String TAG = ActivityImpostazioni.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);
        Log.e(TAG, "onCreate fine");
    }

    public VistaImpostazioni getVistaImpostazioni() {
        return (VistaImpostazioni)this.getFragmentManager().findFragmentById(R.id.vistaImpostazioni);
        //return this.getSupportFragmentManager().findFragmentById(R.id.vistaImpostazioni); //purtroppo non posso usarlo
    }
}
