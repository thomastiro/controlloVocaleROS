package it.unibas.progetto.modello.utilita;

public final class ElementoMovimento {
    private final double tempo;
    private final double velocità;
    /*
    private final double percorsiDalPrecedente;

    public ElementoMovimento(long tempoNSec, double velocita, double percorsiDalPrecedente){
        this(tempoNSec*1.0E-9D, velocita, percorsiDalPrecedente);
    }

    public ElementoMovimento(double secondi, double velocita, double percorsiDalPrecedente){
        this.tempo = secondi;
        this.velocità = velocita;
        this.percorsiDalPrecedente = percorsiDalPrecedente;
    }

    public double getPercorsiDalPrecedente() {
        return percorsiDalPrecedente;
    }
    */

    public ElementoMovimento(long tempoInNanoSecondi, double velocita){
        this(tempoInNanoSecondi*1.0E-9D, velocita);
    }

    public ElementoMovimento(double tempoInSecondi, double velocita){
        this.tempo = tempoInSecondi;
        this.velocità = velocita;
    }

    public double getTempo() {
        return tempo;
    }

    public double getVelocità() {
        return velocità;
    }

}
