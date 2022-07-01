package it.unibas.progetto;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import it.unibas.progetto.controllo.ControlloConnessione;
import it.unibas.progetto.controllo.ControlloImpostazioni;
import it.unibas.progetto.controllo.ControlloMenu;
import it.unibas.progetto.controllo.ControlloPrincipale;
import it.unibas.progetto.modello.ILinguaDinamica;
import it.unibas.progetto.modello.Modello;
import it.unibas.progetto.modello.ModelloPersistente;
import it.unibas.progetto.modello.RiferimentiDirettiStringhe;
import it.unibas.progetto.persistenza.DAOException;

public class Applicazione extends Application {

    public static final String TAG = Applicazione.class.getSimpleName();
    private static Applicazione singleton;

    public static Applicazione getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Applicazione creata...");
        singleton = (Applicazione) getApplicationContext();
        singleton.registerActivityLifecycleCallbacks(new GestoreAttivita());
        linguaDinamica = new RiferimentiDirettiStringhe(getResources());
    }

    /////////////////////////////////////////////
    private ILinguaDinamica linguaDinamica;
    private Activity currentActivity = null;
    private Modello modello = new Modello();
    private ModelloPersistente modelloPersistente = new ModelloPersistente();
    private ControlloMenu controlloMenu = new ControlloMenu();
    private ControlloPrincipale controlloPrincipale =  new ControlloPrincipale();
    private ControlloImpostazioni controlloImpostazioni = new ControlloImpostazioni();
    private ControlloConnessione controlloConnessione = new ControlloConnessione();
    private DAOException daoException = new DAOException();

    public ControlloConnessione getControlloConnessione() {
        return controlloConnessione;
    }

    public ControlloImpostazioni getControlloImpostazioni() {
        return controlloImpostazioni;
    }

    public DAOException getDaoException() {
        return daoException;
    }

    public ControlloPrincipale getControlloPrincipale() {
        return controlloPrincipale;
    }

    public ControlloMenu getControlloMenu() {
        return controlloMenu;
    }

    public Activity getCurrentActivity() {
        return this.currentActivity;
    }

    public Modello getModello() {
        return modello;
    }

    public ModelloPersistente getModelloPersistente() {
        return modelloPersistente;
    }

    public ILinguaDinamica getLinguaDinamica() {
        return linguaDinamica;
    }

    public void toastSuActivityCorrente(final String stringaToast){
        if(stringaToast == null || stringaToast.trim().isEmpty()){
            return;
        }
        final Activity activityCorrente = getCurrentActivity();
        if(activityCorrente != null){
            activityCorrente.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activityCorrente, stringaToast, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public String getStringaRisorsaDalNome(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return getString(resId);
        }
    }
    //////////////////////////////////////////////
    //////////////////////////////////////////////
    private class GestoreAttivita implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.e(TAG, "onActivityCreated: " + activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.e(TAG, "onActivityDestroyed: " + activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.e(TAG, "onActivityStarted: " + activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.e(TAG, "la current Activity inizializzata adesso: " + activity);
            currentActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.e(TAG, "onActivityPaused: " + activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (currentActivity == activity) {
                Log.e(TAG, "current Activity stopped: " + activity);
                //currentActivity = null;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.e(TAG, "onActivitySaveInstanceState: " + activity);
        }
    }
}
