package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue;

import java.util.Locale;

public final class LinguaIT extends ALinguaParteComuneA {

    @Override
    public Locale getLingua() {
        return Locale.ITALY;
    }

    @Override
    public String getMeno(){
        return "meno";
    }

    @Override
    protected String[] getMetri() {
        return new String[]{"metri", "metro"};
    }

    @Override
    protected String[] getSecondi() {
        return new String[]{"secondi", "secondo"};
    }

    @Override
    protected String[] getGradi() {
        return new String[]{"gradi", "grado"};
    }

    @Override
    public String[] getCoordinataX() {
        return new String[]{"x", "ics"};
    }

    @Override
    public String[] getCoordinataY() {
        return new String[]{"y", "ipsilon","ypsilon"};
    }

    @Override
    public String[] getCoordinataW() {
        return new String[]{"w", "orientamento", "omega"};
    }

    @Override
    public String[] getChiaviAvanti() {
        return new String[]{"avanti", "cristmovt"};
    }

    @Override
    public String[] getChiaviIndietro() {
        return new String[]{"indietro"};
    }

    @Override
    public String[] getChiaviDestra() {
        return new String[]{"destra"};
    }

    @Override
    public String[] getChiaviSinistra() {
        return new String[]{"sinistra"};
    }

    @Override
    public String[] getChiaveAngoloZero() {
        return new String[]{"raddrizza", "raddrizzare", "raddrizzati", "raddrizzarti", "raddrizzarsi"};
    }

    @Override
    public String[] getChiaviAngolo() {
        return new String[]{"angolo"};
    }

    @Override
    public String[] getChiaviFermo() {
        return new String[]{"fermo", "fermati", "stop", "fermarti"};
    }

    @Override
    public String[] getChiaviRiposizione() {
        return new String[]{"riposizionare","riposiziona", "riposizionati", "riposizionamento", "ricollocare", "ricollocati", "riposizionarti"};
    }

    @Override
    protected String getRegexVaiSalvataIncompleto() { //(in|da|al|all|allo|alla)
        return "(vai|andare|recati|recarsi) (in|da|al(l(a|o)?)?) (posizione )?";
    }

    @Override
    public String getRegexPosizioneDaSalvareIncompleto() {
        return "(salva|salvare|memorizza|memorizzare) ((la|questa) )?(posizione) (come|con il nome) ";
    }
}

