package it.unibas.progetto.vista;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.R;
import it.unibas.progetto.controllo.ControlloImpostazioni;
import it.unibas.progetto.modello.Modello;
import it.unibas.progetto.modello.layer_personali.GrigliaLayer;
import it.unibas.progetto.modello.utilita.Movimento;

public class VistaImpostazioni extends Fragment {
    private final String TAG = VistaImpostazioni.class.getName();
    private Slider sliderX;
    private Slider sliderY;
    private CheckBox checkMostraGriglia;
    private Spinner spinnerResetTabelle;
    private Button pulsanteEliminaTabelle;
    //dettagli movimento
    private LinearLayout layoutInfo;
    private TextView textVelocita;
    private TextView textValoreVelocita;
    private TextView textValoreSecAcc;
    private TextView textPercosiAcc;
    private TextView textValorePercorsiAcc;
    private TextView textValoreSecFre;
    private TextView textPercorsiFre;
    private TextView textValorePercorsiFre;
    private TextView textValoreOdomMem;

    public VistaImpostazioni() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View vistaImpostazioni = inflater.inflate(R.layout.vista_impostazioni, container, false);
        return vistaImpostazioni;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderX = view.findViewById(R.id.sliderX);
        sliderY = view.findViewById(R.id.sliderY);
        checkMostraGriglia = view.findViewById(R.id.check_mostra_griglia);
        spinnerResetTabelle = view.findViewById(R.id.spinner_tabelle_movimento);
        pulsanteEliminaTabelle = view.findViewById(R.id.bottone_elimina_tabella);
        inizializzaTextInfo(view);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.string_spinner_tabelle,android.R.layout.simple_spinner_item);
        //spinnerResetTabelle.setAdapter(adapter);
        ControlloImpostazioni controlloImpostazioni = Applicazione.getInstance().getControlloImpostazioni();
        spinnerResetTabelle.setOnItemSelectedListener(controlloImpostazioni.getAzioneSpinnerMovimento());//setOnItemSelectedListener(this);
        pulsanteEliminaTabelle.setOnClickListener(controlloImpostazioni.getAzionePulsanteElimina());
        sliderX.addOnSliderTouchListener(controlloImpostazioni.getAzioneToccoSliderGriglia());
        sliderY.addOnSliderTouchListener(controlloImpostazioni.getAzioneToccoSliderGriglia());
    }

    private void inizializzaTextInfo(View view){
        layoutInfo = view.findViewById(R.id.layout_info_selezionato);
        textVelocita = view.findViewById(R.id.textVelocità);
        textValoreVelocita = view.findViewById(R.id.textValoreVelocita);
        textValoreSecAcc = view.findViewById(R.id.textValoreSecondiAcc);
        textPercosiAcc = view.findViewById(R.id.textPercosiAcc);
        textValorePercorsiAcc = view.findViewById(R.id.textValorePercorsiAcc);
        textValoreSecFre = view.findViewById(R.id.textValoreSecondiFre);
        textPercorsiFre = view.findViewById(R.id.textPercosiFre);
        textValorePercorsiFre = view.findViewById(R.id.textValorePercosiFre);
        textValoreOdomMem = view.findViewById(R.id.textValoreOdomMemorizzati);
    }

    @SuppressLint("SetTextI18n")
    public void aggiornaLayoutInfoMovimento(Movimento movimento, final boolean inMetri){
        pulsanteEliminaTabelle.setEnabled(false);
        if(movimento == null){
            layoutInfo.setVisibility(View.GONE);
            Applicazione.getInstance().toastSuActivityCorrente(getString(R.string.noTabella));
            return;
        }
        if(inMetri){
            textVelocita.setText(getString(R.string.velocità)+" (m/s)");
            textPercosiAcc.setText(getString(R.string.testoAccMetri));
            textPercorsiFre.setText(getString(R.string.testoFreMetri));
        }else{
            textVelocita.setText(getString(R.string.velocità)+" (rad/s)");
            textPercosiAcc.setText(getString(R.string.testoAccRadianti));
            textPercorsiFre.setText(getString(R.string.testoFreRadianti));
        }
        textValoreVelocita.setText(""+movimento.getVelRegime());
        textValoreSecAcc.setText(""+movimento.getSecondiTotaliAccelerazione());
        textValorePercorsiAcc.setText(""+movimento.getPercorsiTotaliAccelerazione());
        textValoreSecFre.setText(""+movimento.getSecondiTotaliFrenata());
        textValorePercorsiFre.setText(""+movimento.getPercorsiTotaliFrenata());
        int dim = movimento.getDimensioneTabAccelerazione() + movimento.getDimensioneTabFrenata();
        textValoreOdomMem.setText(""+dim);
        layoutInfo.setVisibility(View.VISIBLE);
        pulsanteEliminaTabelle.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Modello modello= Applicazione.getInstance().getModello();
        GrigliaLayer grigliaLayer= (GrigliaLayer)modello.getBean(Costanti.LAYER_GRIGLIA);
        if(grigliaLayer != null){
            sliderX.setValue(grigliaLayer.getCelleAltezza());
            sliderY.setValue(grigliaLayer.getCelleLarghezza());
        }
        Boolean mostraGriglia =(Boolean) modello.getBean(Costanti.MOSTRA_GRIGLIA);
        if(mostraGriglia!=null){
            checkMostraGriglia.setChecked(mostraGriglia);
        }
    }

    @Override
    public void onPause() {
        Applicazione.getInstance().getModello().putBean(Costanti.MOSTRA_GRIGLIA,checkMostraGriglia.isChecked());
        super.onPause();
    }
}
