package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue;

import java.util.Locale;

public interface ILingua {
    Locale getLingua();

    String getMeno();

    String[] getChiaveAngoloZero();

    String[] getChiaviAvanti();

    String[] getChiaviIndietro();

    String[] getChiaviDestra();

    String[] getChiaviSinistra();

    String[] getChiaviAngolo();

    String[] getChiaviFermo();

    String[] getCoordinataX();

    String[] getCoordinataY();

    String[] getCoordinataW();

    String[] getChiaviVai();    // ricavabili, da pattern

    String[] getChiaviSalva();

    String[] getChiaviRiposizione();

    String getRegexDoubleCompleto(); // pattern

    String getRegexMetri();

    String getRegexSecondi();

    String getRegexGradi();

    String getRegexVaiSalvata();

    String getRegexX();

    String getRegexY();

    String getRegexW();

    String getRegexPosizioneDaSalvare();

    String getRegexPosizioneDaSalvareIncompleto();
}
