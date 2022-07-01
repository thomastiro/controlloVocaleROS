package it.unibas.progetto.controllo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.activity.ActivityPrincipale;
import it.unibas.progetto.modello.DatiPermanenti;

public class ControlloPrincipale {
    private final String TAG = ControlloPrincipale.class.getName();
    private AzioneParla azioneParla = new AzioneParla();
    private AzioneDialogoPosizioniSalvate azioneDialogoPosizioniSalvate = new AzioneDialogoPosizioniSalvate();

    public AzioneParla getAzioneParla() {
        return azioneParla;
    }

    public AzioneDialogoPosizioniSalvate getAzioneDialogoPosizioniSalvate() {
        return azioneDialogoPosizioniSalvate;
    }

    public AzioneDialogoTempiTimer getAzioneDialogoTempiTimer(){
        return new AzioneDialogoTempiTimer();
    }

    private class AzioneParla implements View.OnClickListener {
        private Intent speechRecognize;

        public AzioneParla(){
            speechRecognize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }

        @Override
        public void onClick(View v) {
            getSpeechInput();
        }

        public void getSpeechInput() {
            Log.e(TAG, "inzio azione RecognizerIntent");
            Activity activity = Applicazione.getInstance().getCurrentActivity();
            String linguaPaese = Locale.getDefault().getLanguage();
            if(Costanti.IT.equals(linguaPaese)){
                speechRecognize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Costanti.IT);
            } else if(Costanti.ES.equals(linguaPaese)){
                speechRecognize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Costanti.ES);
            } else{
                speechRecognize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Costanti.EN);
            }
            try{
                activity.startActivityForResult(speechRecognize, Costanti.MAST_ROCC_REQUEST_CODE);
                Log.e(TAG, "avvio corretto intent RecognizerIntent");
            } catch (ActivityNotFoundException e){
                Log.e(TAG, "non è possibile risolvere RecognizerIntent");
                Applicazione.getInstance().toastSuActivityCorrente(Applicazione.getInstance().getLinguaDinamica().getProblemaInputVocale());
            }
            //if (intent.resolveActivity(activity.getPackageManager()) != null) {
        }


        /*public void getSpeechInput2() {
            ActivityPrincipale activity = (ActivityPrincipale)Applicazione.getInstance().getCurrentActivity();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //non ho capito bene come funzioni quest'activity
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()); //setto la lingua di defaul dello smartphone
            activity.startActivity(intent);
        }*/
    }

    private class AzioneDialogoPosizioniSalvate implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(DialogInterface.BUTTON_NEGATIVE == which){
                Applicazione.getInstance().getModelloPersistente().saveBean(Costanti.DATI_PERMANENTI, new DatiPermanenti());
                Log.e(TAG,"target eliminati");
            }
        }
    }

    private class AzioneDialogoTempiTimer implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(DialogInterface.BUTTON_NEGATIVE == which){
                Activity activity = Applicazione.getInstance().getCurrentActivity();
                if(activity instanceof ActivityPrincipale){
                    ActivityPrincipale activityPrincipale = (ActivityPrincipale)activity;
                    activityPrincipale.getVistaPrincipale().getNodoComandoVocale().azzeraTempiPerTimer();
                    Log.e(TAG, "elementi movimento cancellati");
                }
            }
        }
    }
}
