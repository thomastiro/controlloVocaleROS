package it.unibas.progetto.test.comandi_vocali.sinistra_destra.svo.lingue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import it.unibas.progetto.Costanti;
import it.unibas.progetto.modello.DatiPermanenti;
import it.unibas.progetto.modello.comando_vocale.ComandoVel;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.ComandiVocali;
import it.unibas.progetto.modello.utilita.Target;
import it.unibas.progetto.test.comandi_vocali.sinistra_destra.svo.ComandiVocaliTest;

public class CVTestIT extends ComandiVocaliTest {

    @Override
    protected ComandiVocali inizializzaComandiVocali() {
        DatiPermanenti datiPermanenti = new DatiPermanenti();
        datiPermanenti.setTargets(new HashMap<String, Target>());
        Map<String, Target> mappa = datiPermanenti.getTargets();
        mappa.put("donato", new Target(15.1, 15.1, 15.1, "donato"));
        mappa.put("rocco", new Target(16.1, 16.1, 16.1, "rocco"));
        mappa.put("bagno", new Target(17.1, 17.1, 17.1, "bagno"));
        mappa.put("bagno 1", new Target(18.1, 18.1, 18.1, "bagno 1"));
        mappa.put("target 1", new Target(19.1, 19.1, 19.1, "target 1"));
        mappa.put("ingresso", new Target(20.1, 20.1, 19.1, "ingresso"));
        ComandiVocali comandiVocali = new ComandiVocali();
        comandiVocali.setDatiPermanenti(datiPermanenti);
        comandiVocali.cambiaLingua(Locale.ITALIAN);
        return comandiVocali;
    }

    @Test
    public void testNessunRisultato(){
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate1 = new ArrayList<>();
        frasiSbagliate1.add(null);
        verificaNessunRisultato(frasiSbagliate1);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate2 = new ArrayList<>();
        frasiSbagliate2.add(null);
        frasiSbagliate2.add("avanti");
        frasiSbagliate2.add("salva la posizione come donato");
        verificaNessunRisultato(frasiSbagliate2);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate3 = new ArrayList<>();
        frasiSbagliate3.add("avantiindietrovaidestra sinistrafermo");
        verificaNessunRisultato(frasiSbagliate3);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate4 = new ArrayList<>();
        frasiSbagliate4.add("per favore potresti salva la posizione come avanti");
        verificaNessunRisultato(frasiSbagliate4);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate5 = new ArrayList<>();
        frasiSbagliate5.add("salva la posizione come avanti 1");
        verificaNessunRisultato(frasiSbagliate5);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate6 = new ArrayList<>();
        frasiSbagliate6.add("salva la posizione con il nome avanti 1 derebe");
        verificaNessunRisultato(frasiSbagliate6);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate7 = new ArrayList<>();
        frasiSbagliate7.add("avanti per meno 10 metri");
        verificaNessunRisultato(frasiSbagliate7);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate8 = new ArrayList<>();
        frasiSbagliate8.add("destra di meno 10°");
        verificaNessunRisultato(frasiSbagliate8);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate9 = new ArrayList<>();
        frasiSbagliate9.add("salva la posizione come salva");
        verificaNessunRisultato(frasiSbagliate9);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate10 = new ArrayList<>();
        frasiSbagliate10.add("memorizza la posizione con il nome salva 1");
        verificaNessunRisultato(frasiSbagliate10);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate11 = new ArrayList<>();
        frasiSbagliate11.add("salva la posizione come x");
        verificaNessunRisultato(frasiSbagliate11);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate12 = new ArrayList<>();
        frasiSbagliate12.add("potresti salvare la posizione come y 1");
        verificaNessunRisultato(frasiSbagliate12);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate13 = new ArrayList<>();
        frasiSbagliate13.add("ciao salva la posizione come salva la posizione come avanti");
        verificaNessunRisultato(frasiSbagliate13);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate14 = new ArrayList<>();
        frasiSbagliate14.add("salva la posizione come fermo");
        verificaNessunRisultato(frasiSbagliate14);
        //------------------ INCOMPLETE -----------------------//
        ArrayList<String> incomplete1 = new ArrayList<>();
        incomplete1.add("potresti andare al target grazie");
        verificaNessunRisultato(incomplete1);

        //System.out.println(comandiVocali.getDoubleStringaNumero("erbbt135.").toString());
        /*
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate = new ArrayList<>();
        frasiSbagliate.add("avanti");
        verificaNessunRisultato(frasiSbagliate);
        */
    }

    @Test
    public void testComandoVelocita(){
        //------------------------------------------//
        ArrayList<String> frasi1 = new ArrayList<>();
        frasi1.add("ciao bello come stai?");
        frasi1.add("avanti");
        verificaRisultatoComando(frasi1, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("fermo");
        frasi2.add("avanti");
        verificaRisultatoComando(frasi2, 0, ComandoVel.FERMO, null, null);
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("ciao bello come stai?");
        frasi3.add("cristmovt chi te stramuort di 10 metri");
        verificaRisultatoComando(frasi3, ComandoVel.METRI, ComandoVel.AVANTI, 10.0, 10.0);
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("potresti andare indietro avanti indietro fermo");
        verificaRisultatoComando(frasi4, 0, ComandoVel.FERMO, null, null);
        //----------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("ciao bello destra come stai?");
        verificaRisultatoComando(frasi5, ComandoVel.GRADI_A_RADIANTI, ComandoVel.DESTRA, null, Math.PI/2);
        //----------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("vai avanti per 10 metrio");
        verificaRisultatoComando(frasi6, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //----------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("gira all'angolo -134,5°");
        double rad7= -134.5* Costanti.DA_GRADI_A_RADIANTI;
        double posRad7= Math.PI*2+rad7;
        verificaRisultatoComando(frasi7, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, posRad7, posRad7);
        //------------------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("potresti andare indietro avanti indietro fermo per 10 metri");
        verificaRisultatoComando(frasi8, 0, ComandoVel.FERMO, null, null);
        //----------------------------------------//
        ArrayList<String> frasi9 = new ArrayList<>();
        frasi9.add("raddrizzare");
        verificaRisultatoComando(frasi9, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, null, 0.0);
        //----------------------------------------//
        ArrayList<String> frasi9_1 = new ArrayList<>();
        frasi9_1.add("raddrizzati");
        verificaRisultatoComando(frasi9_1, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, null, 0.0);
        //----------------------------------------//
        ArrayList<String> frasi9_2 = new ArrayList<>();
        frasi9_2.add("raddrizzarti");
        verificaRisultatoComando(frasi9_2, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, null, 0.0);
        //----------------------------------------//
        ArrayList<String> frasi10 = new ArrayList<>();
        frasi10.add("andresti avanti per 10 secondi");
        verificaRisultatoComando(frasi10, ComandoVel.SECONDI, ComandoVel.AVANTI, 10.0, 10.0);
        //----------------------------------------//
        ArrayList<String> frasi11 = new ArrayList<>();
        frasi11.add("potresti fermarti");
        verificaRisultatoComando(frasi11, 0, ComandoVel.FERMO, null, null);
        //-----------------DIFFICILI ------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("potresti avanti in posizione x 2 y 3 salva la posizione come stop");
        verificaRisultatoComando(difficili1, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //----------------------------------------//
        ArrayList<String> difficili2 = new ArrayList<>();
        difficili2.add("avanti memorizza la posizione salva la posizione come salva la posizione come indietro");
        verificaRisultatoComando(difficili2, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //----------------------------------------//
        ArrayList<String> difficili3 = new ArrayList<>();
        difficili3.add("sinistra salva la posizione come salva la posizione come avanti 1");
        verificaRisultatoComando(difficili3, ComandoVel.GRADI_A_RADIANTI, ComandoVel.SINISTRA, null, Math.PI/2);
        //----------------------------------------//
        ArrayList<String> difficili4 = new ArrayList<>();
        difficili4.add("sinistra 375.5 gradi salva la posizione come fermo salva la posizione come avanti per 1 metro");
        double radD4= 15.5* Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(difficili4, ComandoVel.GRADI_A_RADIANTI, ComandoVel.SINISTRA, radD4, radD4);
        //----------------------------------------//
        ArrayList<String> difficili5 = new ArrayList<>();
        difficili5.add("sinistra 15 gradi salva la posizione come riposiziona salva la posizione come avanti per 1 metro");
        double radD5= 15*Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(difficili5, ComandoVel.GRADI_A_RADIANTI, ComandoVel.SINISTRA, radD5, radD5);
        //----------------------------------------//
        ArrayList<String> difficili6 = new ArrayList<>();
        difficili6.add("salva avanti per 1 metro grazie");
        verificaRisultatoComando(difficili6, ComandoVel.METRI, ComandoVel.AVANTI, 1.0, 1.0);
        /*
        //----------------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("gira all'angolo -134,5° ");
        verificaRisultatoComando(frasi);
        */
    }

    @Test
    public void testComandoPosizione(){
        //------------------------------------------//
        ArrayList<String> frasi1 = new ArrayList<>();
        frasi1.add("ciao bello come stai?");
        frasi1.add("potresti andare in posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(frasi1, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("potresti ANDARE in POSIZIONE 12.2 13.3 e 96.57 400 gradi grazie");
        verificaRisultatoTargetPosizione(frasi2, new Target(96.57,12.2,13.3 , null));
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("potresti andare in posizione x 12 y 13 e 400.2 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(frasi3, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("potresti andare in posizione y 13 x 12 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(frasi4, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("potresti andare in posizione 15 y 13 e orientamento 96 x 12 33.2 gradi grazie");
        verificaRisultatoTargetPosizione(frasi5, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("vai in posizione x 12 16.33 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(frasi6, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("potresti andare in posizione x 12 16,33 y 13 gradi grazie");
        verificaRisultatoTargetPosizione(frasi7, new Target(16.33,12,13 , null));
        //------------------- DIFFICILI ----------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("potresti andare da rocco x 1 posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(difficili1, new Target(16.1,16.1, 16.1, "rocco"));
        //------------------------------------------//
        ArrayList<String> difficili2 = new ArrayList<>();
        difficili2.add("recarsi in bagno 1 rocco");
        verificaRisultatoTargetPosizione(difficili2, new Target(18.1,18.1,18.1 , "bagno 1"));
        //------------------------------------------//
        ArrayList<String> difficili3 = new ArrayList<>();
        difficili3.add("potresti andare al bagno grazie");
        verificaRisultatoTargetPosizione(difficili3,  new Target(17.1,17.1,17.1 , "bagno"));
        //------------------------------------------//
        ArrayList<String> difficili4 = new ArrayList<>();
        difficili4.add("potresti andare all'ingresso grazie salva posizione come avanti");
        verificaRisultatoTargetPosizione(difficili4, new Target(20.1, 20.1,19.1 , "ingresso"));
        //------------------ FRASI XML ---------------//
        ArrayList<String> frasiXML1 = new ArrayList<>();
        frasiXML1.add("vai in posizione x -3,4 y 2.3 orientamento -15 gradi");
        verificaRisultatoTargetPosizione(frasiXML1, new Target(-15, -3.4, 2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML2 = new ArrayList<>();
        frasiXML2.add("vai alla posizione orientamento -90° la y -3,2 e 4");
        verificaRisultatoTargetPosizione(frasiXML2, new Target(-90,4.0,-3.2 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML3 = new ArrayList<>();
        frasiXML3.add("recati in y -4,1 poi y -3,2 4,5 x 1.0 y 2.2");
        verificaRisultatoTargetPosizione(frasiXML3, new Target(4.5,1.0, -4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML4 = new ArrayList<>();
        frasiXML4.add("vai in y -4,1 poi y -3,2 x 1.0 y 2.2"); //{1.0, -4.1, 0.0}
        verificaRisultatoTargetPosizione(frasiXML4, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML5 = new ArrayList<>();
        frasiXML5.add("vai in posizione y -4,1 poi y -3,2 1.0 y 2.2"); //{1.0, -4.1, 0.0}
        verificaRisultatoTargetPosizione(frasiXML5, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML6 = new ArrayList<>();
        frasiXML6.add("potresti andare in posizione 3,0 -2.3 grazie"); //{3.0, -2.3, 0.0}
        verificaRisultatoTargetPosizione(frasiXML6, new Target(0.0,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML7 = new ArrayList<>();
        frasiXML7.add("andare in 3,0 -2.3 560");
        verificaRisultatoTargetPosizione(frasiXML7, new Target(560,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML8 = new ArrayList<>();
        frasiXML8.add("vai in x tavolo y -4,1 poi x y -3,2 4,5 orientamento 1.0 y 2.2");//{4.5, -4.1, 1.0}
        verificaRisultatoTargetPosizione(frasiXML8, new Target(1.0,4.5,-4.1 , null));
        /*
        //------------------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("potresti andare in posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(frasi, new Target(96,12,13 , null));
        */
    }

    @Test
    public void testComandoRiposiziona(){
        //------------------------------------------//
        ArrayList<String> frasi1 = new ArrayList<>();
        frasi1.add("riposiziona");
        verificaRisultatoTargetRiposiziona(frasi1, null);
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("potresti riposizionarti");
        verificaRisultatoTargetRiposiziona(frasi2, null);
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("senti, riposizionati in y 1 x 3 e orientamento 12 gradi grazie");
        verificaRisultatoTargetRiposiziona(frasi3, new Target(12, 3,1 , null));
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("per favore riposizionati in y 4 e x 2");
        verificaRisultatoTargetRiposiziona(frasi4, new Target(0, 2,4 , null));
        //------------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("vai avanti per 10 metri riposizionati salva la posizione come avanti");
        verificaRisultatoTargetRiposiziona(frasi5, null);
        //------------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("vai avanti per 10 metri riposizionati in x 2 salva la posizione come avanti");
        verificaRisultatoTargetRiposiziona(frasi6, null);
        //------------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("vai avanti per 10 metri riposizionati in x 2 e orientamento 90 gradi salva la posizione come avanti");
        verificaRisultatoTargetRiposiziona(frasi7, null);
        //------------------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("vai avanti per 10 metri riposizionati in x 2 poi y 8 e orientamento 90 gradi salva la posizione come avanti");
        verificaRisultatoTargetRiposiziona(frasi8, new Target(90, 2,8 , null));

        /*
        //------------------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("riposiziona");
        verificaRisultatoTargetRiposiziona(frasi, new Target(20.1, 20.1,19.1 , null));
        */
    }

    @Test
    public void testComandoSalva(){
        //------------------------------------------//
        ArrayList<String> frasi1 = new ArrayList<>();
        frasi1.add("potresti salvare la posizione con il nome antonio");
        verificaRisultatoSalva(frasi1, "antonio");
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("memorizza posizione come antonio");
        verificaRisultatoSalva(frasi2, "antonio");
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("potresti salvare la posizione con il nome roccflicantoij grazie");
        verificaRisultatoSalva(frasi3, "roccflicantoij");
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("avanti per 10 m potresti salvare la posizione con il nome roccflicantoij 1 3 3 grazie");
        verificaRisultatoSalva(frasi4, "roccflicantoij 1");
        //------------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("salva la posizione come antonio 2 okay salva la posizione come avanti 33");
        verificaRisultatoSalva(frasi5, "antonio 2");
        //------------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("salva questa posizione come antonio 2 okay salva la posizione come avanti 33");
        verificaRisultatoSalva(frasi6, "antonio 2");
        //-----------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("salva la posizione come salirò");
        verificaRisultatoSalva(frasi7, "salirò");
        //------------------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("salva la posizione come 1 1 1");
        verificaRisultatoSalva(frasi8, "1 1");
        //------------------------------------------//
        ArrayList<String> frasi9 = new ArrayList<>();
        frasi9.add("salva la posizione con il nome è1 1 grazie bello");
        verificaRisultatoSalva(frasi9, "è1 1");
        //------------------------------------------//
        ArrayList<String> frasi10 = new ArrayList<>();
        frasi10.add("salva la posizione con il nome è1.0 1 grazie bello");
        verificaRisultatoSalva(frasi10, "è1");
        //------------------------------------------//
        ArrayList<String> frasi11 = new ArrayList<>();
        frasi11.add("potresti memorizzare la posizione con il nome tavolo 1rocco grazie");
        verificaRisultatoSalva(frasi11, "tavolo 1");
        //------------------------------------------//
        ArrayList<String> frasi12 = new ArrayList<>();
        frasi12.add("memorizza questa posizione come tavolo rocco1");
        verificaRisultatoSalva(frasi12, "tavolo");
        //------------------------------------------//
        ArrayList<String> frasi13 = new ArrayList<>();
        frasi13.add("memorizza questa posizione come posizione 2 rocco1");
        verificaRisultatoSalva(frasi13, "posizione 2");

        /*
        //------------------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("potresti andare in posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoSalva(frasi, "antonio");
        */
    }
}
