package it.unibas.progetto.modello;

import java.util.Map;

import it.unibas.progetto.modello.utilita.Target;

public class DatiPermanenti {
    /*costretto a fare questa classe a causa del sistema del ModelloPersistente
      dove non posso salvare direttamente una mappa perché inserendo map.class ci sarebbe un errore di
      cast.
      -guardando il bicchiere mezzo pieno, potrei in futuro anche salvare altro.
    */
    private Map<String, Target> targets = null;

    public Map<String, Target> getTargets() {
        return targets;
    }

    public void setTargets(Map<String, Target> targets) {
        this.targets = targets;
    }
}
