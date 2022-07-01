package it.unibas.progetto.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.ros.address.InetAddressFactory;
import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.time.NtpTimeProvider;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.R;
import it.unibas.progetto.modello.DatiPermanenti;
import it.unibas.progetto.modello.Modello;
import it.unibas.progetto.modello.ModelloPersistente;
import it.unibas.progetto.modello.NodoComandoVocale;
import it.unibas.progetto.modello.utilita.TempiPerTimer;
import it.unibas.progetto.vista.VistaPrincipale;

public class ActivityPrincipale extends AppCompatRosActivity {
    private final String TAG = ActivityPrincipale.class.getName();
    private AlertDialog alert;

    public ActivityPrincipale() {
        super("ros comandi vocali", "comandi vocali ros");
        Log.e(TAG, "COSTRUTTORE");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "ON CREATE");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_principale);
        gestioneDatiPermanenti();
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     *
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "Eseguo activity pricipale- menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principale, menu);
        MenuItem menuItemAvviaJoystick = menu.findItem(R.id.menu_avvia_joystick);
        menuItemAvviaJoystick.setOnMenuItemClickListener(Applicazione.getInstance().getControlloMenu().getAzioneJoystick());
        MenuItem menuItemAvviaImpostazioni = menu.findItem(R.id.menu_avvia_impostazioni);
        menuItemAvviaImpostazioni.setOnMenuItemClickListener(Applicazione.getInstance().getControlloMenu().getAzioneImpostazioni());
        MenuItem menuItemInformazioni = menu.findItem(R.id.menu_informazioni);
        menuItemInformazioni.setOnMenuItemClickListener(Applicazione.getInstance().getControlloMenu().getAzioneInfo());
        return true;
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        Log.e(TAG, "INIT");
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());
        //memorizzo nel modello i due importanti oggetti che mi permetteranno di comunicare con il nodo master al di fuori del ActivityPrincipale
        //utilizzarli per altri nodi
        Applicazione.getInstance().getModello().putBean(Costanti.NODE_CONFIGURATION, nodeConfiguration);
        Applicazione.getInstance().getModello().putBean(Costanti.NODE_MAIN_EXECUTOR, nodeMainExecutor);
        try{
            //gli serve un server ntp , cioè che, permettetemi il termine, risponda con messaggi che corrispondano al protocollo ntp
            //es.ntp1.inrim.it	  193.204.114.232	  NTP (RFC 5905)
            NtpTimeProvider ntpTimeProvider = new NtpTimeProvider(InetAddressFactory.newFromHostString(Costanti.IP_SERVER_NTP), nodeMainExecutor.getScheduledExecutorService());
            ntpTimeProvider.startPeriodicUpdates(1, TimeUnit.MINUTES);
            nodeConfiguration.setTimeProvider(ntpTimeProvider);
            Log.e(TAG,"sincronizzato con ntp provider con ip:"+Costanti.IP_SERVER_NTP);
        }catch(Exception e){
            Log.e(TAG,"time di default-non è stato possibile sincronizzarsi con ntp provider:" + e.getMessage());
        }
        Log.e(TAG,"FINE INIT: "+"nodeC: "+ nodeConfiguration + "  nodeM:"+ nodeMainExecutor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG,"RESULT: "+ "RequestCode="+ requestCode + " ResultCode="+resultCode );
        if(requestCode == MASTER_CHOOSER_REQUEST_CODE){//altrimenti verrebbe chiamato lo shutdown completo
            super.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == Costanti.MAST_ROCC_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                ArrayList<String> risultatoRecognizerIntent = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //getVistaPrincipale().getNodoComandoVocale().abilitaLaGestioneDiUnNuovoComando(risultatoRecognizerIntent); con timer
                boolean presaInCarico = getVistaPrincipale().getNodoComandoVocale().eseguiComando(risultatoRecognizerIntent);
                if(presaInCarico){
                    getVistaPrincipale().aggiornaTestoTextView(risultatoRecognizerIntent.get(0));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NodoComandoVocale nodoComandoVocale = getVistaPrincipale().getNodoComandoVocale();
        switch (item.getItemId()) {
            case R.id.check_amcl:
                if(!item.isChecked()){
                    item.setChecked(true);
                    nodoComandoVocale.setVoglioUsareAMCL(true);
                } else {
                    item.setChecked(false);
                    nodoComandoVocale.setVoglioUsareAMCL(false);
                }
                return true;
            case R.id.check_rileva_ostacoli:
                if(!item.isChecked()){
                    item.setChecked(true);
                    nodoComandoVocale.setVoglioRilevareGliOstacoli(true);
                } else {
                    item.setChecked(false);
                    nodoComandoVocale.setVoglioRilevareGliOstacoli(false);
                }
                return true;
            case R.id.check_usa_dati_odom_per_timer:
                if(!item.isChecked()){
                    item.setChecked(true);
                    nodoComandoVocale.setVoglioUsareOdom(true);
                } else {
                    item.setChecked(false);
                    nodoComandoVocale.setVoglioUsareOdom(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void schermoActivityR() {
        Intent intent = new Intent(getApplicationContext(), ActivityJoystick.class);
        startActivity(intent);
    }

    public void schermoActivityImpostazioni() {
        Intent intent = new Intent(getApplicationContext(), ActivityImpostazioni.class);
        startActivity(intent);
    }

    public void schermoActivityInfo(){
        Intent intent = new Intent(getApplicationContext(), ActivityInfo.class);
        startActivity(intent);
    }

    public VistaPrincipale getVistaPrincipale() {
        return (VistaPrincipale)this.getFragmentManager().findFragmentById(R.id.vistaPrincipale);
        // this.getSupportFragmentManager().findFragmentById(R.id.vistaPrincipale); purtroppo non posso usarlo
    }
    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     *
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "Ac.Principale resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "lingua app:"+ Locale.getDefault().getLanguage());
        Log.e(TAG, "Ac.Principale start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "Ac.Principale restart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Ac.Principale stop");
    }

    @Override
    protected void onDestroy() {
        nodeMainExecutorService.onDestroy();    //effettua quello in sicurezza
        nodeMainExecutorService.forceShutdown();
        Modello modello = Applicazione.getInstance().getModello();
        modello.putBean(Costanti.NODE_CONFIGURATION, null);
        modello.putBean(Costanti.NODE_MAIN_EXECUTOR, null);
        if(alert != null){
            alert.dismiss();
            alert = null;
        }
        Log.e(TAG, "Ac.Principale distrutta e anche il servizio");
        super.onDestroy();
    }

    private void rfee(){
        this.getPackageManager();
    }

    private void gestioneDatiPermanenti(){
        ModelloPersistente modelloPersistente = Applicazione.getInstance().getModelloPersistente();
        DatiPermanenti datiPermanenti = (DatiPermanenti)modelloPersistente.getPersistentBean(Costanti.DATI_PERMANENTI, DatiPermanenti.class);
        if(datiPermanenti != null){
            if(datiPermanenti.getTargets() != null){
                DialogInterface.OnClickListener azione = Applicazione.getInstance().getControlloPrincipale().getAzioneDialogoPosizioniSalvate();
                mostraDialogo(getString(R.string.dialTitPosSalvate), getString(R.string.dialMesPosSalvate), azione);
            }
        } else {
            Applicazione.getInstance().getModelloPersistente().saveBean(Costanti.DATI_PERMANENTI, new DatiPermanenti());
        }
        TempiPerTimer tempiPerTimer= (TempiPerTimer)modelloPersistente.getPersistentBean(Costanti.TEMPI_PER_TIMER, TempiPerTimer.class);
        if(tempiPerTimer != null && tempiPerTimer.èPresenteUnMovimento()){
            DialogInterface.OnClickListener azione = Applicazione.getInstance().getControlloPrincipale().getAzioneDialogoTempiTimer();
            mostraDialogo(getString(R.string.dialTitTempi), getString(R.string.dialMesTempi), azione);
        }
    }

    private void mostraDialogo(String titolo, String messaggio, DialogInterface.OnClickListener azione){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titolo);
        builder.setMessage(messaggio);
        builder.setPositiveButton(getString(R.string.si), azione);
        builder.setNegativeButton(getString(R.string.eliminare),azione);
        alert = builder.create();
        alert.show();   //modificare tema?
    }
}
