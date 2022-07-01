package it.unibas.progetto.modello.comando_vocale;

import it.unibas.progetto.Costanti;
import it.unibas.progetto.persistenza.DAOException;

public final class ComandoVel {
    private static final double METRI_DEFAULT = 1.0;
    private static final double ANGOLO_DEFAULT = 90;
    private static final double ANGOLO_ZERO = 0;
    private static final double SECONDI_DEFAULT = 1.0;
    //valore
    public static final int METRI = 30;
    public static final int GRADI_A_RADIANTI = 31;
    public static final int SECONDI = 32;
    //dove
    public static final int FERMO = 10;
    public static final int AVANTI = 11;
    public static final int INDIETRO = 12;
    public static final int DESTRA = 13;
    public static final int SINISTRA = 14;
    public static final int ANGOLO_DA_RAGGIUNGERE = 15;

    private int tipo;
    private int dove;
    private Double valore;
    private Double valoreSecondi;
    private Double valoreDefaultSecondi;
    private Double valoreDefault;

    public ComandoVel(Double valore, int tipo, int dove){
        this.dove = dove;
        if(dove == ANGOLO_DA_RAGGIUNGERE){
            inizializzaComandoAngoloDaRaggungere(valore, tipo);
        } else if(dove == AVANTI || dove == INDIETRO){
            inizializzaComandoAvantiIndietro(valore, tipo);
        } else if(dove == DESTRA || dove == SINISTRA){
            inizializzaComandoDestraSinistra(valore, tipo);
        } else if(dove != FERMO){
            throw new DAOException("dove sbagliato");
        }
    }

    private void inizializzaComandoAngoloDaRaggungere(Double valore, int tipo){
        if(tipo != GRADI_A_RADIANTI){
            throw new DAOException("il tipo del valore è sbagliato");
        }
        Double valoreDefault;
        if(valore != null){
            valore = conversioneAngoloDaRaggiungere(valore);
            valoreDefault = valore;
        } else {
            valoreDefault = conversioneAngoloDaRaggiungere(ANGOLO_ZERO);
        }
        inizializza(valore, valoreDefault, null, null, tipo);
    }

    private void inizializza(Double valore, Double valoreDefault, Double valoreSecondi, Double valoreDefaultSecondi, int tipo){
        this.valore = valore;
        this.valoreDefault = valoreDefault;
        this.valoreSecondi = valoreSecondi;
        this.valoreDefaultSecondi = valoreDefaultSecondi;
        this.tipo = tipo;
    }

    private void inizializzaComandoAvantiIndietro(Double valore, int tipo){
        Double valoreDefaultSecondi;
        Double valoreSecondi;
        Double valoreDefault;
        if(tipo == METRI){
            if(valore != null){
                valoreDefault = valore;
                valoreDefaultSecondi = valoreSecondi = valore/Costanti.VELOCITA_LINEARE_MAX;
            } else { //parte default//
                valoreSecondi = null;
                valoreDefault = METRI_DEFAULT;
                valoreDefaultSecondi = METRI_DEFAULT/Costanti.VELOCITA_LINEARE_MAX;
            }
        } else if(tipo == SECONDI){
            valoreSecondi = valore;
            if(valore != null){
                valoreDefault = valoreDefaultSecondi = valore;
            } else {
                valoreDefault = valoreDefaultSecondi = SECONDI_DEFAULT;
            }
        } else {
            throw new DAOException("il tipo del valore è sbagliato");
        }
        inizializza(valore, valoreDefault, valoreSecondi, valoreDefaultSecondi, tipo);
    }

    private void inizializzaComandoDestraSinistra(Double valore, int tipo){
        Double valoreDefaultSecondi;
        Double valoreSecondi;
        Double valoreDefault;
        if(tipo == GRADI_A_RADIANTI){
            if(valore != null){
                valore = valoreDefault = conversioneAngoloDaRaggiungere(valore);
                valoreSecondi = valoreDefaultSecondi = valore/Costanti.VELOCITA_ANGOLARE_MAX;
            } else {
                valoreSecondi = null;
                valoreDefault = conversioneAngoloDaRaggiungere(ANGOLO_DEFAULT);
                valoreDefaultSecondi = valoreDefault/Costanti.VELOCITA_ANGOLARE_MAX;
            }
        } else if(tipo == SECONDI){
            valoreSecondi = valore;
            if(valore != null){
                valoreDefault = valoreDefaultSecondi = valore;
            } else {
                valoreDefault = valoreDefaultSecondi = SECONDI_DEFAULT;
            }
        } else {
            throw new DAOException("il tipo del valore è sbagliato");
        }
        inizializza(valore, valoreDefault, valoreSecondi, valoreDefaultSecondi, tipo);
    }

    private double conversioneAngoloDaRaggiungere(double valore){
        valore = valore%360;
        if(dove == ComandoVel.ANGOLO_DA_RAGGIUNGERE && valore < 0.0){ //agg. 22/03
            valore = 360+valore;
        }
        return valore*Costanti.DA_GRADI_A_RADIANTI;
    }

    public int getTipo() {
        return tipo;
    }

    public int getDove() {
        return dove;
    }

    public Double getValore() {
        return valore;
    }

    public Double getValoreDefault() {
        return valoreDefault;
    }

    public Double getValoreInSecondi() {
        return valoreSecondi;
    }

    //getValoreInSecondiDefault
    public Double getValoreInSecondiDefault() {
        return valoreDefaultSecondi;
    }
}
