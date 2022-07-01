package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import it.unibas.progetto.Costanti;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue.ILingua;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue.LinguaEN;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue.LinguaES;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.lingue.LinguaIT;
import it.unibas.progetto.persistenza.DAOException;

final class LinguaGenerico {
    private static final String TAG = LinguaGenerico.class.getSimpleName();
    private boolean isDefault = false;
    private String linguaAttuale = "";
    private Pattern patternDoubleCompleto;
    private Pattern patternMetri;
    private Pattern patternSecondi;
    private Pattern patternGradi;
    private Pattern patternVaiSalvata;
    private Pattern[] patternsXYW;
    private Pattern patternPosizioneDaSalvare;
    private Pattern patternPosizioneDaSalvareIncompleto;
    private String meno;
    private String[] angoloZero;
    private String[] chiaviVai;
    private String[] chiaviRiposizione;
    private String[] chiaviSalva;
    private String[] chiaviAvanti;
    private String[] chiaviIndietro;
    private String[] chiaviDestra;
    private String[] chiaviSinistra;
    private String[] chiaviAngolo;
    private String[] chiaviFermo;
    private String[] paroleVietate;
    private Comparator<String> comparatoreAttuale;

    protected LinguaGenerico(Comparator<String> comparatoreAttuale){
        this.comparatoreAttuale = comparatoreAttuale;
        cambioLingua();
    }

    protected void cambioLingua(){
        cambioLingua(Locale.getDefault());
    }

    /*
    potrei usare la rilessione per cercare e creare dinamicamente
    oggetti dalle classi che sono contenute nella cartella lingua.
    Es. usando come nome "Lingua<Xx>" */
    protected void cambioLingua(Locale locale){
        String linguaPaese = locale.getLanguage();
        if(!linguaAttuale.equals(linguaPaese)){
            if(Costanti.IT.equals(linguaPaese)){
                inizializza(new LinguaIT());
            } else if(Costanti.ES.equals(linguaPaese)){
                inizializza(new LinguaES());
            } else if(!isDefault){
                inizializza(new LinguaEN());
            }
        }
        System.out.println("lingua attuale: <"+linguaAttuale+">");
        Log.e(TAG, "lingua attuale: <"+linguaAttuale+">");
    }

    /*protected void cambioLingua(Locale locale){
        if(!stessaLingua(localeAttuale, locale)){
            if(stessaLingua(Locale.ITALIAN, locale)){
                inizializza(new LinguaIT());
            } else if(!isDefault){
                inizializza(new LinguaEN());
            }
        }
        Log.e(TAG, "lingua attuale: <"+localeAttuale.getLanguage()+">");
    }

    private boolean stessaLingua(Locale l1, Locale l2){
        if(l1 == null || l2 == null) return false;
        return l1.getLanguage().equals(l2.getLanguage());
    }*/

    private void inizializza(ILingua l){
        String[] chiaviAngoloZeroP = l.getChiaveAngoloZero();
        angoloZero = getCloneOrdinato(chiaviAngoloZeroP);
        ArrayList<String> lista1 = new ArrayList<>(Arrays.asList(chiaviAngoloZeroP));
        lista1.addAll(Arrays.asList(l.getChiaviAngolo()));
        chiaviAngolo = lista1.toArray(new String[0]);
        Arrays.sort(chiaviAngolo, comparatoreAttuale);
        System.out.println("maestro");
        for(String k:chiaviAngolo){
            System.out.println(k);
        }
        chiaviAvanti = getCloneOrdinato(l.getChiaviAvanti());
        chiaviIndietro = getCloneOrdinato(l.getChiaviIndietro());
        chiaviDestra = getCloneOrdinato(l.getChiaviDestra());
        chiaviSinistra = getCloneOrdinato(l.getChiaviSinistra());
        chiaviFermo = getCloneOrdinato(l.getChiaviFermo());
        chiaviVai = getCloneOrdinato(l.getChiaviVai());
        chiaviSalva = getCloneOrdinato(l.getChiaviSalva());
        chiaviRiposizione = getCloneOrdinato(l.getChiaviRiposizione());
        paroleVietate = getCloneOrdinato(getParoleChiaveEControllo(l));
        meno = l.getMeno();
        patternDoubleCompleto = Pattern.compile(l.getRegexDoubleCompleto());
        patternSecondi = Pattern.compile(l.getRegexSecondi());
        patternMetri = Pattern.compile(l.getRegexMetri());
        patternGradi = Pattern.compile(l.getRegexGradi());
        Pattern patternX = Pattern.compile(l.getRegexX());
        Pattern patternY = Pattern.compile(l.getRegexY());
        Pattern patternW = Pattern.compile(l.getRegexW());
        patternsXYW = new Pattern[]{patternX, patternY, patternW};
        patternVaiSalvata = Pattern.compile(l.getRegexVaiSalvata());
        patternPosizioneDaSalvareIncompleto = Pattern.compile(l.getRegexPosizioneDaSalvareIncompleto());
        patternPosizioneDaSalvare = Pattern.compile(l.getRegexPosizioneDaSalvare());
        linguaAttuale = l.getLingua().getLanguage();
        if(l instanceof LinguaEN){
            isDefault = true;
        } else {
            isDefault = false;
        }
        Log.e(TAG, "inizializzati stringhe e pattern");
    }

    private String[] getCloneOrdinato(String[] strings){ //per evitare di mantenere riferimenti esterni non desiderati//
        String[] copia = strings.clone();
        Arrays.sort(copia, comparatoreAttuale);
        return copia;
    }

    private String[] getParoleChiaveEControllo(ILingua l){
        ArrayList<String[]> list = new ArrayList<>();
        list.add(l.getCoordinataX());
        list.add(l.getCoordinataY());
        list.add(l.getCoordinataW());
        list.add(chiaviAvanti);
        list.add(chiaviIndietro);
        list.add(chiaviDestra);
        list.add(chiaviSinistra);
        list.add(chiaviFermo);
        list.add(chiaviAngolo);
        list.add(chiaviSalva);
        list.add(chiaviVai);
        list.add(chiaviRiposizione);
        Set<String> set = new HashSet<>();
        for(String[] array: list) {
            if(array == null || array.length == 0) {
                throw new DAOException("ERRORE ARRAY");
            }
            for(String elemento : array) {
                if (!set.add(elemento)) {
                    throw new DAOException("ERRORE DUPLICATO: " + elemento);
                }
            }
        }
        return set.toArray(new String[0]);
    }

    public String[] getChiaviVai() {
        return chiaviVai;
    }

    public String[] getChiaviRiposizione() {
        return chiaviRiposizione;
    }

    public String[] getChiaviSalva() {
        return chiaviSalva;
    }

    public String[] getChiaviAvanti() {
        return chiaviAvanti;
    }

    public String[] getChiaviIndietro() {
        return chiaviIndietro;
    }

    public String[] getChiaviDestra() {
        return chiaviDestra;
    }

    public String[] getChiaviSinistra() {
        return chiaviSinistra;
    }

    public String[] getChiaviAngolo() {
        return chiaviAngolo;
    }

    public String[] getChiaviFermo() {
        return chiaviFermo;
    }

    public String[] getParoleVietate() {
        return paroleVietate;
    }

    public String getLinguaAttuale(){
        return linguaAttuale;
    }

    public Pattern getPatternDoubleCompleto() {
        return patternDoubleCompleto;
    }

    public Pattern getPatternMetri() {
        return patternMetri;
    }

    public Pattern getPatternSecondi() {
        return patternSecondi;
    }

    public Pattern getPatternGradi() {
        return patternGradi;
    }

    public Pattern getPatternVaiSalvata() {
        return patternVaiSalvata;
    }

    public Pattern[] getPatternsXYW() {
        return patternsXYW;
    }

    public Pattern getPatternPosizioneDaSalvare() {
        return patternPosizioneDaSalvare;
    }

    public Pattern getPatternPosizioneDaSalvareIncompleto() {
        return patternPosizioneDaSalvareIncompleto;
    }

    public String getMeno() {
        return meno;
    }

    public String[] getAngoloZero() {
        return angoloZero;
    }
}
