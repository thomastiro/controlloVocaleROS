package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue;

import java.util.Locale;

public class LinguaES extends ALinguaParteComuneA {
    @Override
    public Locale getLingua() {
        return new Locale("es", "ES");
    }

    @Override
    public String getMeno(){
        return "menos";
    }

    @Override
    protected String[] getMetri() {
        return new String[]{"metros", "metro"};
    }

    @Override
    protected String[] getSecondi() {
        return new String[]{"segundos", "segundo"};
    }

    @Override
    protected String[] getGradi() {
        return new String[]{"grados", "grado"};
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
        return new String[]{"w", "orientación", "omega"};
    }

    @Override
    public String[] getChiaviAvanti() {
        return new String[]{"adelante", "continuar", "avanzar", "cristmovt"};
    }

    @Override
    public String[] getChiaviIndietro() {
        return new String[]{"retrocede", "retroceder", "traseros", "atrás"};
    }

    @Override
    public String[] getChiaviDestra() {
        return new String[]{"derecha"};
    }

    @Override
    public String[] getChiaviSinistra() {
        return new String[]{"izquierda", "izquierdo"};
    }

    @Override
    public String[] getChiaveAngoloZero() {
        return new String[]{"enderezado", "enderezarte", "enderezar"};
    }

    @Override
    public String[] getChiaviAngolo() {
        return new String[]{"ángulo"};
    }

    @Override
    public String[] getChiaviFermo() {
        return new String[]{"detener", "detenido", "parar", "detiene", "deténgase", "stop", "detenerte"};
    }

    @Override
    public String[] getChiaviRiposizione() {
        return new String[]{"reposicionar", "reposicione", "reposicionarte" , "reposicionado", "reposicionamiento", "trasladarse", "reubicado"};
    }

    @Override//podrías ir a la posición x -10 luego y 15 y orientación 90 grados
    protected String getRegexVaiSalvataIncompleto() { //(in|da|al|all|allo|alla)
        return "(ir|ve|irías) (a( la)?|al) (posición )?";
    }

    @Override//podrías guardar la ubicación como origen gracias
    public String getRegexPosizioneDaSalvareIncompleto() {
        return "(guardar|guarde|memorizar|almacenar|almacena) ((la|el|esta) )?(posición|puesto|ubicación) (como|con (el )?nombre) ";
    }
}
