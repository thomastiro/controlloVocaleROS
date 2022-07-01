package it.unibas.progetto.modello.utilita;

public class ChiaveIndici{
    private int tipologiaChiave;
    private int inizio;
    private int fine;
    private int z;

    public ChiaveIndici(int tipologiaChiave, int inizio, int fine, int z) {
        this.tipologiaChiave = tipologiaChiave;
        this.inizio = inizio;
        this.fine = fine;
        this.z = z;
    }

    public int getTipologiaChiave() {
        return tipologiaChiave;
    }

    public int getInizio() {
        return inizio;
    }

    public int getFine() {
        return fine;
    }

    public int getZ(){
        return z;
    }
}
