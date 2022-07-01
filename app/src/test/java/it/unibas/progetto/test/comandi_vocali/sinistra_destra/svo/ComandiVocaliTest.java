package it.unibas.progetto.test.comandi_vocali.sinistra_destra.svo;

import org.junit.Assert;

import java.util.ArrayList;

import it.unibas.progetto.modello.comando_vocale.ComandoVel;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.ComandiVocali;
import it.unibas.progetto.modello.comando_vocale.ComandoUniversale;
import it.unibas.progetto.modello.utilita.Target;

public abstract class ComandiVocaliTest {
    private ComandiVocali comandiVocali = inizializzaComandiVocali();

    protected abstract ComandiVocali inizializzaComandiVocali();

    protected final void verificaRisultatoTargetPosizione(ArrayList<String> frasi, Target targetAttesso){
        System.out.println("-----------------------------------------------");
        System.out.println(frasi.toString());
        ComandoUniversale risultato = comandiVocali.convertiInComandoUniversale(frasi);
        Assert.assertEquals(ComandoUniversale.RISULTATO_POSIZIONE, risultato.getGenereRisultato());
        Assert.assertTrue(risultato.getRisultato() instanceof Target);
        Target target = (Target) risultato.getRisultato();
        Assert.assertEquals(targetAttesso.getX(), target.getX(), 0);
        Assert.assertEquals(targetAttesso.getY(), target.getY(), 0);
        Assert.assertEquals(targetAttesso.getAlfa(), target.getAlfa(), 0);
        Assert.assertEquals(targetAttesso.getNome(), target.getNome());
    }

    protected final void verificaRisultatoTargetRiposiziona(ArrayList<String> frasi, Target targetAttesso){
        System.out.println("-----------------------------------------------");
        System.out.println(frasi.toString());
        ComandoUniversale risultato = comandiVocali.convertiInComandoUniversale(frasi);
        Assert.assertEquals(ComandoUniversale.RISULTATO_RIPOSIZIONA, risultato.getGenereRisultato());
        if(targetAttesso == null){
            Assert.assertNull(risultato.getRisultato());
        } else {
            Assert.assertTrue(risultato.getRisultato() instanceof Target);
            Target target = (Target) risultato.getRisultato();
            Assert.assertEquals(targetAttesso.getX(), target.getX(), 0);
            Assert.assertEquals(targetAttesso.getY(), target.getY(), 0);
            Assert.assertEquals(targetAttesso.getAlfa(), target.getAlfa(), 0);
            Assert.assertEquals(targetAttesso.getNome(), target.getNome());
        }
    }

    protected final void verificaRisultatoSalva(ArrayList<String> frasi, String nomeAtteso){
        System.out.println("-----------------------------------------------");
        System.out.println(frasi.toString());
        ComandoUniversale cU = comandiVocali.convertiInComandoUniversale(frasi);
        Assert.assertEquals(ComandoUniversale.RISULTATO_SALVA_POSIZIONE, cU.getGenereRisultato());
        System.out.println("atteso:<"+nomeAtteso+">     risultato:<"+cU.getRisultato()+">");
        Assert.assertEquals(nomeAtteso, cU.getRisultato());
    }

    protected final void verificaNessunRisultato(ArrayList<String> frasi){
        System.out.println("-----------------------------------------------");
        System.out.println(frasi.toString());
        ComandoUniversale risultato = comandiVocali.convertiInComandoUniversale(frasi);
        Assert.assertEquals(ComandoUniversale.RISULTATO_NESSUNO, risultato.getGenereRisultato());
        Assert.assertTrue(risultato.getRisultato() == null);
    }

    protected final void verificaRisultatoComando(ArrayList<String> frasi, int tipoAtteso, int doveAtteso, Double valoreAtteso, Double valoreAttesoDiDefault){
        System.out.println("-----------------------------------------------");
        System.out.println(frasi.toString());
        ComandoUniversale risultato = comandiVocali.convertiInComandoUniversale(frasi);
        Assert.assertEquals(ComandoUniversale.RISULTATO_COMANDO_VELOCITA, risultato.getGenereRisultato());
        Assert.assertTrue(risultato.getRisultato() instanceof ComandoVel);
        ComandoVel comando = (ComandoVel) risultato.getRisultato();
        Assert.assertEquals(doveAtteso, comando.getDove());
        Assert.assertEquals(tipoAtteso, comando.getTipo());
        Assert.assertEquals(valoreAtteso, comando.getValore());
        Assert.assertEquals(valoreAttesoDiDefault, comando.getValoreDefault());
    }
}
