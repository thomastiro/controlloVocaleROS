package it.unibas.progetto.controllo;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.slider.Slider;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.R;
import it.unibas.progetto.activity.ActivityImpostazioni;
import it.unibas.progetto.modello.Modello;
import it.unibas.progetto.modello.layer_personali.GrigliaLayer;
import it.unibas.progetto.modello.utilita.Movimento;
import it.unibas.progetto.modello.utilita.TempiPerTimer;
import it.unibas.progetto.vista.VistaImpostazioni;
import it.unibas.progetto.modello.comando_vocale.ComandoVel;

public class ControlloImpostazioni {
    private final String TAG = ControlloImpostazioni.class.getName();
    private Integer tabellaSelezionata;
    private AzioneSpinnerMovimento azioneSpinnerMovimento = new AzioneSpinnerMovimento();
    private AzioneToccoSliderGriglia azioneToccoSliderGriglia = new AzioneToccoSliderGriglia();
    private AzionePulsanteElimina azionePulsanteElimina = new AzionePulsanteElimina();

    public AzioneSpinnerMovimento getAzioneSpinnerMovimento() {
        return azioneSpinnerMovimento;
    }

    public AzioneToccoSliderGriglia getAzioneToccoSliderGriglia() {
        return azioneToccoSliderGriglia;
    }

    public AzionePulsanteElimina getAzionePulsanteElimina() {
        return azionePulsanteElimina;
    }

    private class AzioneSpinnerMovimento implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Activity activity = Applicazione.getInstance().getCurrentActivity();
            String nomeTabella = (String)parent.getItemAtPosition(position);
            tabellaSelezionata = null;
            if(activity instanceof ActivityImpostazioni){
                ActivityImpostazioni activityImpostazioni = (ActivityImpostazioni)activity;
                TempiPerTimer tempiPerTimer = (TempiPerTimer)Applicazione.getInstance().getModello().getBean(Costanti.TEMPI_PER_TIMER);
                if(tempiPerTimer == null){
                    Log.e(TAG, "tempiPerTimer nullo");
                    Applicazione.getInstance().toastSuActivityCorrente(activityImpostazioni.getString(R.string.nodoNCM));
                    return;
                }
                Log.e(TAG, nomeTabella);
                Movimento movimento = null;
                boolean inMetri = true;
                if(nomeTabella.equalsIgnoreCase(activityImpostazioni.getString(R.string.avanti))){  // la ricerca è sul nome associato alla tabella
                    movimento = tempiPerTimer.getAvanti();
                    tabellaSelezionata = ComandoVel.AVANTI;
                }else if(nomeTabella.equalsIgnoreCase(activityImpostazioni.getString(R.string.indietro))){
                    movimento = tempiPerTimer.getIndietro();
                    tabellaSelezionata = ComandoVel.INDIETRO;
                }else if(nomeTabella.equalsIgnoreCase(activityImpostazioni.getString(R.string.destra))){
                    movimento = tempiPerTimer.getDestra();
                    inMetri = false;
                    tabellaSelezionata = ComandoVel.DESTRA;
                }else if(nomeTabella.equalsIgnoreCase(activityImpostazioni.getString(R.string.sinistra))){
                    movimento = tempiPerTimer.getSinistra();
                    inMetri = false;
                    tabellaSelezionata = ComandoVel.SINISTRA;
                }
                activityImpostazioni.getVistaImpostazioni().aggiornaLayoutInfoMovimento(movimento, inMetri);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }

    private class AzionePulsanteElimina implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            TempiPerTimer tempiPerTimer = (TempiPerTimer)Applicazione.getInstance().getModello().getBean(Costanti.TEMPI_PER_TIMER);
            Activity activityCorrente = Applicazione.getInstance().getCurrentActivity();
            if(activityCorrente instanceof ActivityImpostazioni && tempiPerTimer != null){
                VistaImpostazioni vistaImpostazioni = ((ActivityImpostazioni) activityCorrente).getVistaImpostazioni();
                if(tabellaSelezionata != null && cancellaMovimentoSelezionato(tabellaSelezionata, tempiPerTimer)){
                    Log.e(TAG, "tabella cancellata correttamente");
                    Applicazione.getInstance().toastSuActivityCorrente(vistaImpostazioni.getString(R.string.tabEliminata));
                } else {
                    Log.e(TAG, "tabella non cancellata");
                    Applicazione.getInstance().toastSuActivityCorrente(vistaImpostazioni.getString(R.string.tabNoEliminata));
                }
                vistaImpostazioni.aggiornaLayoutInfoMovimento(null, true);
            }
            tabellaSelezionata = null;
        }

        private boolean cancellaMovimentoSelezionato(int val, TempiPerTimer tempiPerTimer){
            if(val == ComandoVel.AVANTI) return !tempiPerTimer.creazioneMovimentoAvanti(null);
            if(val == ComandoVel.INDIETRO) return !tempiPerTimer.creazioneMovimentoIndietro(null);
            if(val == ComandoVel.DESTRA) return !tempiPerTimer.creazioneMovimentoDestra(null);
            if(val == ComandoVel.SINISTRA) return !tempiPerTimer.creazioneMovimentoSinistra(null);
            return false;
        }

    }

    private class AzioneToccoSliderGriglia implements Slider.OnSliderTouchListener{

        @Override
        public void onStartTrackingTouch(Slider slider) {}

        @Override
        public void onStopTrackingTouch(Slider slider) {
            Modello modello = Applicazione.getInstance().getModello();
            GrigliaLayer grigliaLayer = (GrigliaLayer)modello.getBean(Costanti.LAYER_GRIGLIA);
            if(grigliaLayer == null){
                return;
            }
            switch (slider.getId()) {
                case R.id.sliderX:
                    grigliaLayer.aggiornaDimensioniGriglia((int)slider.getValue(),null);
                    Log.e(TAG,"dimensioni griglia x:"+ slider.getValue());
                    break;
                case R.id.sliderY:
                    grigliaLayer.aggiornaDimensioniGriglia(null, (int)slider.getValue());
                    Log.e(TAG,"dimensioni griglia y:"+ slider.getValue());
                    break;
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////
}
