package it.unibas.progetto.modello;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

import it.unibas.progetto.R;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue.LinguaEN;

public class RiferimentiDirettiStringhe implements ILinguaDinamica {
    private String posizione;
    private String velocità;
    private String configurazioneAMCL_OK;
    private String configurazioneAMCL_NO;
    private String erroreServiceAMCL;
    private String comandoFallito;
    private String comandoFallitoAMCL;
    private String obbiettivoRaggiuntoSi;
    private String obbiettivoRaggiuntoNo;
    private String obbiettivoSuperato;
    private String velAngRidotta;
    private String velLinRidotta;
    private String timerNuovo;
    private String timerComandoNonDisponibile;
    private String problemaInputVocale;
    private String posizioneSalvata;
    private String problemaTimeout;
    private String noLaser;
    private String ostacoloRilevato;
    private String erroreRiposizioneAMCL;
    private String okRiposizione;
    private String tabellaCreata;
    private String erroreCreazioneTabella;
    private String errRecuperoDaOdometria;
    private String noTabella;
    private String tentRecuperoDaTopic;

    private String nodoNonConnessoMaster;
    private String nodoInizializzato;
    private String nodoNonInizializzato;
    private String erroreMaster;
    private String nodoSpento;
    private String nodoNonSpento;

    /** creata per evitare di ricercare i rifermenti delle
     * stringhe contenute negli XML ogniqualvolta servissero*/
    public RiferimentiDirettiStringhe(@NonNull Resources resources){
        posizione = resources.getString(R.string.posizione);
        velocità = resources.getString(R.string.velocità);
        configurazioneAMCL_OK = resources.getString(R.string.confAMCL_OK);
        configurazioneAMCL_NO = resources.getString(R.string.confAMCL_NO);
        erroreServiceAMCL = resources.getString(R.string.erroreServiceAMCL);
        comandoFallito = resources.getString(R.string.cmdFallito);
        comandoFallitoAMCL = resources.getString(R.string.cmdFallitoNO_AMCL);
        obbiettivoRaggiuntoSi = resources.getString(R.string.obbRagSi);
        obbiettivoRaggiuntoNo = resources.getString(R.string.obbRagNo);
        obbiettivoSuperato = resources.getString(R.string.obbSuperato);
        velAngRidotta = resources.getString(R.string.velAngR);
        velLinRidotta = resources.getString(R.string.velLinR);
        timerNuovo = resources.getString(R.string.timerNuovo);
        timerComandoNonDisponibile = resources.getString(R.string.timerComandoNo);
        problemaInputVocale = resources.getString(R.string.problemaInputVocale);
        nodoNonConnessoMaster = resources.getString(R.string.nodoNCM);
        nodoInizializzato = resources.getString(R.string.nodoI);
        nodoNonInizializzato = resources.getString(R.string.nodoNI);
        erroreMaster = resources.getString(R.string.errMaster);
        nodoSpento = resources.getString(R.string.nodoSpento);
        nodoNonSpento = resources.getString(R.string.nodoNSpento);
        posizioneSalvata = resources.getString(R.string.posizioneSalvata);
        problemaTimeout = resources.getString(R.string.problemaTimeout);
        noLaser = resources.getString(R.string.noLaser);
        ostacoloRilevato = resources.getString(R.string.ostacoloRilevato);
        erroreRiposizioneAMCL = resources.getString(R.string.erroreRiposizioneAMCL);
        okRiposizione = resources.getString(R.string.okRiposizione);
        tabellaCreata = resources.getString(R.string.tabellaCreata);
        erroreCreazioneTabella = resources.getString(R.string.errCreazioneTabella);
        noTabella = resources.getString(R.string.noTabella);
        errRecuperoDaOdometria = resources.getString(R.string.errRecuperoDaOdometria);
        tentRecuperoDaTopic = resources.getString(R.string.tentRecuperoDaTopic);
    }

    @Override
    public String getPosizione() {
        return posizione;
    }

    @Override
    public String getConfigurazioneAMCL_OK() {
        return configurazioneAMCL_OK;
    }

    @Override
    public String getConfigurazioneAMCL_NO() {
        return configurazioneAMCL_NO;
    }

    @Override
    public String getErroreServiceAMCL() {
        return erroreServiceAMCL;
    }

    @Override
    public String getComandoFallito() {
        return comandoFallito;
    }

    @Override
    public String getComandoFallitoAMCL() {
        return comandoFallitoAMCL;
    }

    @Override
    public String getObbiettivoRaggiuntoSi() {
        return obbiettivoRaggiuntoSi;
    }

    @Override
    public String getObbiettivoRaggiuntoNo() {
        return obbiettivoRaggiuntoNo;
    }

    @Override
    public String getObbiettivoSuperato() {
        return obbiettivoSuperato;
    }

    @Override
    public String getVelAngRidotta() {
        return velAngRidotta;
    }

    @Override
    public String getVelLinRidotta() {
        return velLinRidotta;
    }

    @Override
    public String getTimerNuovo() {
        return timerNuovo;
    }

    @Override
    public String getPosizioneSalvata() {
        return posizioneSalvata;
    }

    @Override
    public String getTimerComandoNonDisponibile() {
        return timerComandoNonDisponibile;
    }

    @Override
    public String getProblemaInputVocale() {
        return problemaInputVocale;
    }

    @Override
    public String getProblemaTimeout(){
        return problemaTimeout;
    }

    @Override
    public String getVelocità() {
        return velocità;
    }

    @Override
    public String getNodoNonConnessoMaster() {
        return nodoNonConnessoMaster;
    }

    @Override
    public String getNodoInizializzato() {
        return nodoInizializzato;
    }

    @Override
    public String getNodoNonInizializzato() {
        return nodoNonInizializzato;
    }

    @Override
    public String getErroreMaster() {
        return erroreMaster;
    }

    @Override
    public String getNodoSpento() {
        return nodoSpento;
    }

    @Override
    public String getNodoNonSpento() {
        return nodoNonSpento;
    }

    @Override
    public String getNoLaser() {
        return noLaser;
    }

    @Override
    public String getOstacoloRilevato() {
        return ostacoloRilevato;
    }

    @Override
    public String getErroreRiposizioneAMCL() {
        return erroreRiposizioneAMCL;
    }

    @Override
    public String getOkRiposizione() {
        return okRiposizione;
    }

    @Override
    public String getTabellaCreata() {
        return tabellaCreata;
    }

    @Override
    public String getErroreCreazioneTabella() {
        return erroreCreazioneTabella;
    }

    @Override
    public String getErrRecuperoDaOdometria() {
        return errRecuperoDaOdometria;
    }

    @Override
    public String getNoTabella() {
        return noTabella;
    }

    @Override
    public String getTentRecuperoDaTopic() {
        return tentRecuperoDaTopic;
    }

}
