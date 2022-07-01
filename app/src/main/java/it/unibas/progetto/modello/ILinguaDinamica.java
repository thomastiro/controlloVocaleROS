package it.unibas.progetto.modello;

import java.util.Locale;

public interface ILinguaDinamica {

    String getPosizione(); //1 nodoComandoVocale

    String getConfigurazioneAMCL_OK(); //1 nodoComandoVocale

    String getConfigurazioneAMCL_NO(); //1 nodoComandoVocale

    String getErroreServiceAMCL(); //2 nodoComandoVocale

    String getComandoFallito(); //4 nodoComandoVocale

    String getComandoFallitoAMCL(); //1 nodoComandoVocale

    String getObbiettivoRaggiuntoSi(); //2 nodoComandoVocale

    String getObbiettivoRaggiuntoNo(); //1 nodoComandoVocale

    String getObbiettivoSuperato(); //2 nodoComandoVocale

    String getVelAngRidotta(); //1 nodoComandoVocale

    String getVelLinRidotta(); //1 nodoComandoVocale

    String getTimerNuovo(); //1 nodoComandoVocale

    String getPosizioneSalvata(); //2 nodoComandoVocale

    String getTimerComandoNonDisponibile(); //1 nodoComandoVocale

    String getProblemaInputVocale(); //1 controlloPrincipale

    String getProblemaTimeout(); //1 controlloConnessione

    String getVelocità(); //1 nodoComandoVocale

    String getNodoNonConnessoMaster(); //1 controlloConnessione

    String getNodoInizializzato(); //1 controlloConnessione

    String getNodoNonInizializzato(); //1 controlloConnessione

    String getErroreMaster();  //1 controlloConnessione

    String getNodoSpento(); //1 controlloConnessione

    String getNodoNonSpento(); //1 controlloConnessione

    String getNoLaser(); //1 nodoComandoVocale.

    String getOstacoloRilevato(); //1 nodoComandoVocale.

    String getErroreRiposizioneAMCL(); //1 nodoComandoVocale

    String getOkRiposizione(); //1 nodoComandoVocale

    String getTabellaCreata(); //1 nodoComandoVocale

    String getErroreCreazioneTabella(); //2 nodoComandoVocale

    String getErrRecuperoDaOdometria(); //1 nodoComandoVocale

    String getNoTabella(); //1 nodoComandoVocale

    String getTentRecuperoDaTopic(); //1 nodoComandoVocale
}
