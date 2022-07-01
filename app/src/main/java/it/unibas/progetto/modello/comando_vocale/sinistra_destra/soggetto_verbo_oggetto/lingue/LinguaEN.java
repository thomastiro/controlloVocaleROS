package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue;

import java.util.Locale;

public final class LinguaEN extends ALinguaParteComuneA {

    @Override
    public Locale getLingua() {
        return Locale.ENGLISH;
    }

    @Override
    protected String[] getMetri() {
        return new String[] {"meters", "meter"};
    }

    @Override
    protected String[] getSecondi() {
        return new String[] {"seconds", "second"};
    }

    @Override
    protected String[] getGradi() {
        return new String[] {"degrees", "degree"};
    }

    @Override
    public String getMeno() {
        return "minus";
    }

    @Override
    public String[] getChiaviAvanti() {
        return new String[]{"forward", "ahead", "on", "forth", "cristmovt"};
    }

    @Override
    public String[] getChiaviIndietro() {
        return new String[]{"back", "backward", "backwards"};
    }

    @Override
    public String[] getChiaviDestra() {
        return new String[]{"right"};
    }

    @Override
    public String[] getChiaviSinistra() {
        return new String[]{"left"};
    }

    @Override
    public String[] getChiaveAngoloZero() {
        return new String[]{"straighten", "straightened"};
    }

    @Override
    public String[] getChiaviAngolo() {
        return new String[]{"angle"};
    }

    @Override
    public String[] getChiaviFermo() {
        return new String[]{"stop", "still", "firm"};
    }

    @Override
    public String[] getCoordinataX() {
        return new String[]{"x", "ex"};
    }

    @Override
    public String[] getCoordinataY() {
        return new String[]{"y", "wye"};
    }

    @Override
    public String[] getCoordinataW() {
        return new String[]{"w", "orientation", "omega"};
    }

    @Override
    public String[] getChiaviRiposizione() {
        return new String[]{"reposition", "repositioned", "repositioning", "relocate"};
    }

    @Override
    protected String getRegexVaiSalvataIncompleto() {
        return "go (to|in|to the) (position )?";
    }

    @Override
    public String getRegexPosizioneDaSalvareIncompleto() {
        return "(save|store|stores|memorize) ((the|this) )?(location|position) (as|with (the )?name) ";
    }
}
