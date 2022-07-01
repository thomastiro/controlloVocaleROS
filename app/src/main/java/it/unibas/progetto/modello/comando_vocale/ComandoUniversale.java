package it.unibas.progetto.modello.comando_vocale;

import it.unibas.progetto.modello.utilita.Target;
import it.unibas.progetto.persistenza.DAOException;

public final class ComandoUniversale{
    public static final int RISULTATO_NESSUNO = 100;
    public static final int RISULTATO_POSIZIONE = 101;
    public static final int RISULTATO_COMANDO_VELOCITA = 102;
    public static final int RISULTATO_SALVA_POSIZIONE = 103;
    public static final int RISULTATO_RIPOSIZIONA = 104;

    private int genereRisultato;
    private Object risultato;

    public ComandoUniversale(){
        inizializza(RISULTATO_NESSUNO, null);
    }

    public ComandoUniversale(int genereRisultato, Target target){
        if((genereRisultato == RISULTATO_POSIZIONE && target != null) || genereRisultato == RISULTATO_RIPOSIZIONA){
            inizializza(genereRisultato, target);
        } else {
            throw new DAOException("valori comando universale sbagliati");
        }
    }

    public ComandoUniversale(ComandoVel comando){
        if(comando != null){
            inizializza(RISULTATO_COMANDO_VELOCITA, comando);
        } else {
            throw new DAOException("valori comando universale sbagliati");
        }
    }

    public ComandoUniversale(String nomeDaSalvare){
        if(nomeDaSalvare != null){
            inizializza(RISULTATO_SALVA_POSIZIONE, nomeDaSalvare);
        } else {
            throw new DAOException("valori comando universale sbagliati");
        }
    }

    private void inizializza(int genereRisultato, Object risultato){
        this.genereRisultato = genereRisultato;
        this.risultato = risultato;
    }

    public int getGenereRisultato() {
        return genereRisultato;
    }

    public Object getRisultato() {
        return risultato;
    }
}
