package it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto;

import android.util.Log;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import it.unibas.progetto.modello.DatiPermanenti;
import it.unibas.progetto.modello.comando_vocale.ComandoVel;
import it.unibas.progetto.modello.utilita.ChiaveIndici;
import it.unibas.progetto.modello.comando_vocale.ComandoUniversale;
import it.unibas.progetto.modello.utilita.Target;

public final class ComandiVocali {
    private final String TAG = ComandiVocali.class.getName();
    private static final String REGEX_PER_PULIZIA = "[^-0-9.]";
    public static final String REGEX_FORMATO_NOME_SALVABILE = "[a-z_\u00DF-\u00FF_0-9]+(( [0-9]+)?)"; // "[^ .]+(( [0-9]+)?)"; // "[a-z_0-9]+(( [0-9]+)?)"; //
    public static final String REGEX_DOUBLE_SOLO_POSITIVI = "[0-9]+[.,]?([0-9]+)?";
    private static final int NESSUNO = 1;
    private static final int POSIZIONE = 2;
    private static final int SALVA_POSIZIONE = 3;
    private static final int RIPOSIZIONA = 4;
    private static final int FERMO = 5;
    private static final int AVANTI = 6;
    private static final int INDIETRO = 7;
    private static final int DESTRA = 8;
    private static final int SINISTRA = 9;
    private static final int ANGOLO = 10;

    private LinguaGenerico lG;
    private char separatoreDecimale;
    private final Pattern patternNomeSalvabile;
    private Comparator<String> comparatoreStringhe;
    private DatiPermanenti datiPermanentiN;

    public ComandiVocali(){
        this.comparatoreStringhe = null; //se è il valore è null comparerà in ordine alfabetico 1 .. 9.. a .. z
        this.lG = new LinguaGenerico(comparatoreStringhe);
        this.separatoreDecimale = new DecimalFormatSymbols().getDecimalSeparator();
        this.patternNomeSalvabile = Pattern.compile(REGEX_FORMATO_NOME_SALVABILE);
    }

    public void setDatiPermanenti(DatiPermanenti datiPermanenti){
        this.datiPermanentiN = datiPermanenti;
    }

    public DatiPermanenti getDatiPermanenti(){
        return datiPermanentiN;
    }

    public void cambiaLingua(){
        lG.cambioLingua();
    }

    public void cambiaLingua(Locale locale){
        lG.cambioLingua(locale);
    }

    public ComandoUniversale convertiInComandoUniversale(ArrayList<String> frasi){
        if(frasiSonoCorrette(frasi)){
            for(String frase: pulisciConvertiInMinuscolo(frasi)){
                ComandoUniversale risultato = interno(frase);
                if(risultato != null) return risultato;
            }
        }
        return new ComandoUniversale();
    }

    private ComandoUniversale interno(String frase){
        int n = 0;
        int z = frase.length();
        int inizio = z+1;
        ChiaveIndici[] chiavi = new ChiaveIndici[2];
        String[] split = frase.split(" ");
        for(int i = split.length-1; i >= 0; i--){
            String attuale = split[i];
            int fine = inizio - 1;
            inizio = fine-attuale.length();
            int tipologia = tipoligiaChiave(attuale);
            if(tipologia != NESSUNO){
                chiavi[n] = new ChiaveIndici(tipologia, inizio, fine, z);
                z = inizio;
                n++;
            }
            if(n == 2){
                if(chiavi[1].getTipologiaChiave() == SALVA_POSIZIONE){
                    StringBuffer sB = new StringBuffer(frase.substring(chiavi[1].getInizio(), chiavi[0].getFine()));
                    primiRisultatiPatternsEliminandoAnalogie(sB, new Pattern[]{lG.getPatternPosizioneDaSalvare()}, true);
                    if(sB.toString().isEmpty()){
                        n = 0; // scarto entrambe.
                    }
                }
                if(n == 2){
                    ComandoUniversale cU = gestioneStringaChiave(chiavi[0], frase);
                    if(cU != null) {
                        return cU;
                    }
                    chiavi[0] = chiavi[1];
                    n = 1;
                }
            }
        }
        if(n == 1){
            return gestioneStringaChiave(chiavi[0], frase);
        }
        return null;
    }

    private int tipoligiaChiave(String s){
        if(ugualeAUnaChiave(s, lG.getChiaviFermo())) return FERMO;
        if(ugualeAUnaChiave(s, lG.getChiaviAvanti())) return AVANTI;
        if(ugualeAUnaChiave(s, lG.getChiaviIndietro())) return INDIETRO;
        if(ugualeAUnaChiave(s, lG.getChiaviDestra())) return DESTRA;
        if(ugualeAUnaChiave(s, lG.getChiaviSinistra())) return SINISTRA;
        if(ugualeAUnaChiave(s, lG.getChiaviAngolo())) return ANGOLO;
        if(ugualeAUnaChiave(s, lG.getChiaviVai())) return POSIZIONE;
        if(ugualeAUnaChiave(s, lG.getChiaviRiposizione())) return RIPOSIZIONA;
        if(ugualeAUnaChiave(s, lG.getChiaviSalva())) return SALVA_POSIZIONE;
        return NESSUNO;
    }

    private ComandoUniversale gestioneStringaChiave(ChiaveIndici chiave, String frase){
        int tipologia = chiave.getTipologiaChiave();
        if(tipoComandoVelocita(tipologia)){
            String parolaChiave = frase.substring(chiave.getInizio(), chiave.getFine());
            String sottoStringa = frase.substring(chiave.getFine(), chiave.getZ());
            return trovaComandoVelocita(sottoStringa, tipologia, parolaChiave);
        }
        if(tipologia == POSIZIONE) {
            Target target = trovaPosizione(frase.substring(chiave.getInizio(), chiave.getZ()));
            if(target != null){
                return new ComandoUniversale(ComandoUniversale.RISULTATO_POSIZIONE, target);
            }
        }
        if(tipologia == SALVA_POSIZIONE) {
            String nomeDaSalvare = trovaSalvaPosizione(frase.substring(chiave.getInizio(), chiave.getZ()));
            if(nomeDaSalvare != null){
                return new ComandoUniversale(nomeDaSalvare);
            }
        }
        if(tipologia == RIPOSIZIONA){
            Double[] valoriTrovati = convertitoreFraseInPosizione(frase.substring(chiave.getFine(), chiave.getZ()));
            Target risultato = nuovoTarget(valoriTrovati, null);
            return new ComandoUniversale(ComandoUniversale.RISULTATO_RIPOSIZIONA, risultato);
        }
        return null;
    }

    private String trovaSalvaPosizione(String frase){
        StringBuffer sB = new StringBuffer(frase);
        Pattern[] pattern = new Pattern[]{lG.getPatternPosizioneDaSalvareIncompleto()};
        primiRisultatiPatternsEliminandoAnalogie(sB, pattern, true);
        String restante = sB.toString();
        if(!restante.trim().isEmpty()){
            String risultato = getPrimaStringaRispettaPattern(patternNomeSalvabile, restante);
            if(risultato != null){
                String[] splitRisultato = risultato.split(" ");    //al max sono i primi 2 elem.
                if(!ugualeAUnaChiave(splitRisultato[0], lG.getParoleVietate())){  //da confrontare con parole vietate.
                    return risultato;
                }
            }
        }
        return null;
    }

    private boolean tipoComandoVelocita(int t){
        if(t == FERMO) return true;
        if(t == AVANTI) return true;
        if(t == INDIETRO) return true;
        if(t == DESTRA) return true;
        if(t == SINISTRA) return true;
        return t == ANGOLO;
    }

    private ComandoUniversale trovaComandoVelocita(String sK, int tipologia, String chiave) {
        ComandoVel c = trovaDurataComando(sK, tipologia, chiave);
        if (c == null || (c.getValoreInSecondi() != null && c.getValoreInSecondi() <= 0)) {
            //se è stato espresso un comando spiritoso es. vai avanti per -5 secondi ==> ritorna false
            //Log.e(TAG, "non fare lo spiritoso oppur mbart a parla. ");
            return null;
        }
        return new ComandoUniversale(c);
    }
    //potrei unirli//
    private ComandoVel trovaDurataComando(String sK, int tipologiaLocale, String chiave){
        int dove = convertiTipologiaPerTipologiaComandoVelocità(tipologiaLocale);
        //L'ordine è IMPORTANTISSIMO!!!!
        if(dove == ComandoVel.FERMO) {
            return new ComandoVel(null,-1, dove);
        }
        sK = sK+" ";
        // [gradi] angolo preciso
        if(dove == ComandoVel.ANGOLO_DA_RAGGIUNGERE){
            String risultato = getPrimaStringaRispettaPattern(lG.getPatternGradi(), sK);
            if(risultato == null && !ugualeAUnaChiave(chiave, lG.getAngoloZero())){//!chiave.equals(lG.getAngoloZero())){
                return null;
            }
            return new ComandoVel(getDoubleStringaNumero(risultato), ComandoVel.GRADI_A_RADIANTI, dove);
        }
        // [secondi] universale solo secondi tranne ANGOLO
        String secondi = getPrimaStringaRispettaPattern(lG.getPatternSecondi(), sK);
        if(secondi != null){
            return new ComandoVel(getDoubleStringaNumero(secondi), ComandoVel.SECONDI, dove);
        }
        // [metri] solo per comandi che siano avanti o indietro
        if(dove == ComandoVel.AVANTI || dove == ComandoVel.INDIETRO){
            return new ComandoVel(getDoubleStringaNumero(getPrimaStringaRispettaPattern(lG.getPatternMetri(), sK)), ComandoVel.METRI, dove);
        }
        // [gradi] solo per comandi che siano destra o sinistra
        if(dove == ComandoVel.SINISTRA || dove == ComandoVel.DESTRA){
            return new ComandoVel(getDoubleStringaNumero(getPrimaStringaRispettaPattern(lG.getPatternGradi(), sK)), ComandoVel.GRADI_A_RADIANTI, dove);
        }
        return null;
    }

    private Target trovaPosizione(String frase){
        Target target = ricercaTargetTraDatiPermanenti(frase);
        if(target != null){
            return target;
        }
        ///////////////////////// ricerca coordinate  ///////////////////////////
        Double[] valoriTrovati = convertitoreFraseInPosizione(frase);
        return nuovoTarget(valoriTrovati, null);
    }

    private Target ricercaTargetTraDatiPermanenti(String frase){
        //DatiPermanenti datiPermanentiN = (DatiPermanenti) Applicazione.getInstance().getModelloPersistente().getPersistentBean(Costanti.DATI_PERMANENTI, DatiPermanenti.class);
        if(datiPermanentiN != null){
            Map<String, Target> targets = datiPermanentiN.getTargets();
            if(targets != null){ //cerca tra i valori salvati se è presente//
                String risultato = getPrimaStringaRispettaPattern(lG.getPatternVaiSalvata(), frase);
                if(risultato != null){
                    String[] split = risultato.split(" ");
                    if(split.length >= 2){
                        String n1 = split[split.length-2];
                        String n2 = split[split.length-1];
                        Target target = targets.get(n1+" "+n2);
                        if(target != null) return target;
                        target = targets.get(n2);
                        if(target != null) return target;
                        target = targets.get(n1);
                        if(target != null) return target;
                    }
                }
            }
        }
        return null;
    }

    private Double[] convertitoreFraseInPosizione(String frase){
        StringBuffer fraMod = new StringBuffer(frase);
        //Log.e(TAG, "fraMod prima:<" + fraMod.toString() + ">");
        String [] coordinateStringa = primiRisultatiPatternsEliminandoAnalogie(fraMod, lG.getPatternsXYW(), true);
        Double [] valoriTrovati = new Double[coordinateStringa.length];
        //Log.e(TAG, "fraMod dopo:<"+ fraMod.toString() + ">");
        Matcher matcher = lG.getPatternDoubleCompleto().matcher(fraMod);
        for(int i = 0; i < coordinateStringa.length; i++){
            if(coordinateStringa[i] != null){
                //Log.e(TAG, "valore già era stato trovato: " + coordinateStringa[i]);
                Double valore = getDoubleStringaNumero(coordinateStringa[i]);
                valoriTrovati[i] = valore;
                //Log.e(TAG, "valore trovato convertito: " + valore.toString());
            } else if(matcher.find()) {
                String trovata = matcher.group();
                //Log.e(TAG, "valore trovato: " + trovata);
                Double valore = getDoubleStringaNumero(trovata);
                valoriTrovati[i] = valore;
                //Log.e(TAG, "valore trovato convertito: " + valore.toString());
            }
        }
        return valoriTrovati;
    }

    private String[] primiRisultatiPatternsEliminandoAnalogie(StringBuffer sB, Pattern[] patterns, final boolean lePrimeOccorrenze){
        String[] risultati = new String[patterns.length];
        for(int i = 0; i < patterns.length; i++){
            String risultato = null;
            Matcher matcher = patterns[i].matcher(sB.toString());
            while(matcher.find()){
                String trovato = matcher.group();
                if(risultato == null) risultato = trovato;
                if(!lePrimeOccorrenze) risultato = trovato;
                String fraseRipulita = sB.toString().replaceFirst(trovato, "");
                sB.setLength(0);
                sB.append(fraseRipulita);
            }
            if(risultato != null){
                risultati[i] = risultato;
            }
            //Log.e(TAG, "risultato pattern n. "+ i+ " : "+ risultato);
        }
        return risultati;
    }

    private String getPrimaStringaRispettaPattern(Pattern pattern, String stringa){
        Matcher matcher = pattern.matcher(stringa);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

    private boolean frasiSonoCorrette(ArrayList<String> frasi){
        if(frasi == null || frasi.isEmpty()) return false;
        for(String f:frasi) if(f == null) return false;
        return true;
    }

    private ArrayList<String> pulisciConvertiInMinuscolo(ArrayList<String> frasi){
        ArrayList<String> nuovo = new ArrayList<>();
        for(String s: frasi){
            s = s.replaceAll("'"," ");
            nuovo.add(s.trim().toLowerCase());
        }
        return nuovo;
    }

    private Target nuovoTarget(Double[] valoriXYW, String nome){
        if(valoriXYW != null && valoriXYW[0] != null && valoriXYW[1] != null) {
            if(valoriXYW[2] == null){
                valoriXYW[2] = 0.0;
            }
            return new Target(valoriXYW[2], valoriXYW[0], valoriXYW[1], nome);
        }
        return null;
    }

    private boolean ugualeAUnaChiave(String s, String[] arrayOrdinato){
        return Arrays.binarySearch(arrayOrdinato, s, comparatoreStringhe) >= 0;
        /*
        int l = s.length();
        for(String k: chiaviOrdinateDecrescenti){  // giovanni {fermati, fermo, stop}
            if(l > k.length()) return false; //evito di dover continuare a cercare
            if(s.equals(k))  return true;
            //dato che so per certo che l'oggetto che andrò a confrontare è una stringa ed ha un altro OID
            // posso riscrivere l'equals dove EVITO di fare il confronto OID e poi istanceOf e cast a string!
        }
        return false;*/
    }

    //per eliminarlo DEVO garantire che siano le stesse e che non siano in conflitto con le altre costanti di questa classe//
    private int convertiTipologiaPerTipologiaComandoVelocità(int tipologiaLocale){
        switch (tipologiaLocale){ //ha prestazioni maggiori degli if (dalla Bibbia Stackoverflow)
            case FERMO: return ComandoVel.FERMO;
            case AVANTI: return ComandoVel.AVANTI;
            case INDIETRO: return ComandoVel.INDIETRO;
            case DESTRA: return ComandoVel.DESTRA;
            case SINISTRA: return ComandoVel.SINISTRA;
            case ANGOLO: return ComandoVel.ANGOLO_DA_RAGGIUNGERE;
        }
        return -1;
    }

    private Double getDoubleStringaNumero(String stringaConNumero){
        if(stringaConNumero == null){
            return null;
        }
        String soloNumero = stringaConNumero;
        soloNumero = soloNumero.replace(lG.getMeno()+" ","-");
        soloNumero = soloNumero.replace(lG.getMeno(),"-");
        if(String.valueOf(separatoreDecimale).equals(",")) {
            soloNumero = soloNumero.replace(",", ".");
            //Log.e(TAG, "sostituito la virgola con il punto");
        }
        soloNumero = soloNumero.replaceAll(REGEX_PER_PULIZIA, "");
        try {
            return Double.parseDouble(soloNumero);
        } catch (ArithmeticException | NumberFormatException e) {
            //Log.e(TAG, "ERRORE conversione", e);
            return null;
        }
    }

    public String[] getParoleVietate(){
        return lG.getParoleVietate().clone();
    }

    private String getStringaRipulita(String[] arrayString){
        StringBuilder rexChiavi = new StringBuilder(arrayString[0]);
        for (int i = 1; i < arrayString.length; i++) {
            rexChiavi.append(" ").append(arrayString[i]);
        }
        return rexChiavi.toString();
    }

    private boolean controlloDuplicati(ArrayList<String[]> tutti){
        Set<String> Set = new HashSet<>();
        for(String[] strings: tutti) {
            for (String element : strings) {
                if (!Set.add(element)) {
                    Log.e(TAG, element);
                    return true;
                }
            }
        }
        return false;
    }
}
