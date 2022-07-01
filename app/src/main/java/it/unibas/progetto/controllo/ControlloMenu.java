package it.unibas.progetto.controllo;

import android.util.Log;
import android.view.MenuItem;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.activity.ActivityPrincipale;

public class ControlloMenu {
    private final String TAG = ControlloMenu.class.getName();
    private AzioneJoystick azioneJoystick = new AzioneJoystick();
    private AzioneImpostazioni azioneImpostazioni = new AzioneImpostazioni();
    private AzioneInfo azioneInfo = new AzioneInfo();

    public AzioneJoystick getAzioneJoystick() {
        return azioneJoystick;
    }

    public AzioneImpostazioni getAzioneImpostazioni() {
        return azioneImpostazioni;
    }

    public AzioneInfo getAzioneInfo() {
        return azioneInfo;
    }

    private class AzioneJoystick implements MenuItem.OnMenuItemClickListener {

        /**
         * Called when a menu item has been invoked.  This is the first code
         * that is executed; if it returns true, no other callbacks will be
         * executed.
         *
         * @param item The menu item that was invoked.
         * @return Return true to consume this click and prevent others from
         * executing.
         */
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.d(TAG, "inizio azione avvia joystick menu");
            if(!Applicazione.getInstance().getControlloConnessione().verificaConnessioneAlNodoMaster()){
                return false;
            }
            ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
            activityPrincipale.schermoActivityR();
            return true;
        }
    }

    private class AzioneImpostazioni implements MenuItem.OnMenuItemClickListener{
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
            activityPrincipale.schermoActivityImpostazioni();
            return true;
        }
    }

    private class AzioneInfo implements MenuItem.OnMenuItemClickListener{
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
            activityPrincipale.schermoActivityInfo();
            return true;
        }
    }
}
