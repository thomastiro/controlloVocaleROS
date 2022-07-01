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

public class CVTestEN extends ComandiVocaliTest {

    @Override
    protected ComandiVocali inizializzaComandiVocali() {
        DatiPermanenti datiPermanenti = new DatiPermanenti();
        datiPermanenti.setTargets(new HashMap<String, Target>());
        Map<String, Target> mappa = datiPermanenti.getTargets();
        mappa.put("donato", new Target(1.1, 1.1, 1.1, "donato"));
        mappa.put("rocco", new Target(2.1, 2.1, 2.1, "rocco"));
        mappa.put("bagno", new Target(3.1, 3.1, 3.1, "bagno"));
        mappa.put("bagno 1", new Target(3.1, 3.1, 3.1, "bagno 1"));
        mappa.put("target 1", new Target(4.1, 4.1, 4.1, "target 1"));
        mappa.put("ingresso", new Target(0.1, 0.1, 10, "ingresso"));
        ComandiVocali comandiVocali = new ComandiVocali();
        comandiVocali.setDatiPermanenti(datiPermanenti);
        comandiVocali.cambiaLingua(Locale.ENGLISH);
        return comandiVocali;
    }

    @Test
    public void testNessunRisultato(){
        //-------------------------------------------------//
        ArrayList<String> sbagliate1 = new ArrayList<>();
        sbagliate1.add(null);
        verificaNessunRisultato(sbagliate1);
        //-------------------------------------------------//
        ArrayList<String> sbagliate2 = new ArrayList<>();
        sbagliate2.add(null);  //<-----
        sbagliate2.add("back");
        sbagliate2.add("save the position with name target 1");
        verificaNessunRisultato(sbagliate2);
        ////////////////////////////////////////////
        //-------------------------------------------------//
        ArrayList<String> sbagliate3 = new ArrayList<>();
        sbagliate3.add("go in position 2,3");
        verificaNessunRisultato(sbagliate3);
        //-------------------------------------------------//
        ArrayList<String> sbagliate4 = new ArrayList<>();
        sbagliate4.add("could you please go to position 3,0 orientation -60.3");
        verificaNessunRisultato(sbagliate4);
        //-------------------------------------------------//
        ArrayList<String> sbagliate5 = new ArrayList<>();
        sbagliate5.add("please go to location x rocco y -4.1 then x y -3,2 y 4,5 orientation 1.0 y 2.2 thanks");
        verificaNessunRisultato(sbagliate5);
        //-------------------------------------------------//
        ArrayList<String> sbagliate6 = new ArrayList<>();
        sbagliate6.add("go in x 2.0 x 3.0 x 4.0");
        verificaNessunRisultato(sbagliate6);
        //------------------------------------------//
        ArrayList<String> sbagliate7 = new ArrayList<>();
        sbagliate7.add("you could memorize the position as forward 1rocco thanks");
        verificaNessunRisultato(sbagliate7);
        //-------------------------------------------------//
        ArrayList<String> sbagliate8 = new ArrayList<>();
        sbagliate8.add("save position as X thanks");
        verificaNessunRisultato(sbagliate8);
        //-------------------------------------------------//
        ArrayList<String> sbagliate9 = new ArrayList<>();
        sbagliate9.add("go forward for -10 m");
        verificaNessunRisultato(sbagliate9);
        //-------------------------------------------------//
        ArrayList<String> sbagliate10 = new ArrayList<>();
        sbagliate10.add("left -90°");
        verificaNessunRisultato(sbagliate10);

        /*
        //-------------------------------------------------//
        ArrayList<String> sbagliate = new ArrayList<>();
        sbagliate.add("");
        verificaNessunRisultato(sbagliate);
        */

    }

    @Test
    public void testComandoVelocita(){
        ArrayList<String> frasi1 = new ArrayList<>();
        frasi1.add("hello");
        frasi1.add("on");
        verificaRisultatoComando(frasi1, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //--------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("you could go on for 2 meters"); //prende la versione più accurata
        frasi2.add("you could go on for 10 meters");
        verificaRisultatoComando(frasi2, ComandoVel.METRI, ComandoVel.AVANTI, 2.0, 2.0);
        //--------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("hello go back 1.53 m tks");
        verificaRisultatoComando(frasi3, ComandoVel.METRI, ComandoVel.INDIETRO, 1.53, 1.53);
        //--------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("go ahead for 5.4 seconds please");
        verificaRisultatoComando(frasi4, ComandoVel.SECONDI, ComandoVel.AVANTI, 5.4, 5.4);
        //--------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("turn right 270 degrees");
        double rad5= 270* Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(frasi5, ComandoVel.GRADI_A_RADIANTI, ComandoVel.DESTRA, rad5, rad5);
        //--------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("you could turn left 15°");
        double rad6= 15* Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(frasi6, ComandoVel.GRADI_A_RADIANTI, ComandoVel.SINISTRA, rad6, rad6);
        //--------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("go right 3 seconds please");
        verificaRisultatoComando(frasi7, ComandoVel.SECONDI, ComandoVel.DESTRA, 3.0, 3.0);
        //--------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("go to the angle of -134,5 degrees thanks");
        double rad8= -134.5* Costanti.DA_GRADI_A_RADIANTI;
        double posRad8= Math.PI*2+rad8;
        verificaRisultatoComando(frasi8, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, posRad8, posRad8);
        //--------------------------------//
        ArrayList<String> frasi9 = new ArrayList<>();
        frasi9.add("you could go to the angle 450 degrees");
        double rad9= (450%360)*Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(frasi9, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, rad9, rad9);
        //--------------------------------//
        ArrayList<String> frasi10 = new ArrayList<>();
        frasi10.add("angle 1,0 degree");
        double rad10 = Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(frasi10, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, rad10, rad10);
        //--------------------------------//
        ArrayList<String> frasi11 = new ArrayList<>();
        frasi11.add("could you straighten thanks?");
        verificaRisultatoComando(frasi11, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, null, 0.0 );
        //--------------------------------//
        ArrayList<String> frasi12 = new ArrayList<>();
        frasi12.add("you might stop");
        verificaRisultatoComando(frasi12, 0, ComandoVel.FERMO, null, null);
        //--------------------------------//
        ArrayList<String> frasi13 = new ArrayList<>();
        frasi13.add("ahead");
        verificaRisultatoComando(frasi13, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        /////////////// DIFFICILI /////////////////

        /*
        //--------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("hello");
        verificaRisultatoComando(frasi, Comando.METRI, Comando.AVANTI, null, 1.0);
        */
    }

    @Test
    public void testRisultatoPosizione(){
        //------------------------------------------//
        ArrayList<String> posizione1 = new ArrayList<>();
        posizione1.add("go to the position x -3,4 y 2.3 orientation -15 degrees");
        verificaRisultatoTargetPosizione(posizione1, new Target(-15.0,-3.4,2.3, null));
        //------------------------------------------//
        ArrayList<String> posizione2 = new ArrayList<>();
        posizione2.add("you could go to position x 2, y 9.3 and orientation 90,3 degrees");
        verificaRisultatoTargetPosizione(posizione2, new Target(90.3,2,9.3 , null));
        //------------------------------------------//
        ArrayList<String> posizione3 = new ArrayList<>();
        posizione3.add("go to position orientation -90 °, y 3,2 and 4");
        verificaRisultatoTargetPosizione(posizione3, new Target(-90,4,3.2 , null));
        //------------------------------------------//
        ArrayList<String> posizione5 = new ArrayList<>();
        posizione5.add("Hi! go in y -4,1 then y -3,2 x 1.0 y 2.2 thanks");
        verificaRisultatoTargetPosizione(posizione5, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> posizione6 = new ArrayList<>();
        posizione6.add("go in location 3,0 minus 2.3");
        verificaRisultatoTargetPosizione(posizione6, new Target(0.0,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> posizione7 = new ArrayList<>();
        posizione7.add("go in 3,0 -2.3 560");
        verificaRisultatoTargetPosizione(posizione7, new Target(560,3.0,-2.3, null));
        //------------------------------------------//
        ArrayList<String> posizione8 = new ArrayList<>();
        posizione8.add("go to ingresso go go go tonino!");
        verificaRisultatoTargetPosizione(posizione8, new Target(0.1, 0.1, 10, "ingresso"));
        //------------------------------------------//
        ArrayList<String> posizione9 = new ArrayList<>();
        posizione9.add("could you please go to position target 1 thanks");
        verificaRisultatoTargetPosizione(posizione9, new Target(4.1, 4.1, 4.1, "target 1"));


        //-----------------DIFFICILI----------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("forward go in y -4,1 then y -3,2 x 1.0 y 2.2 thanks forward for -3.1 m");
        verificaRisultatoTargetPosizione(difficili1, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> difficili2 = new ArrayList<>();
        difficili2.add("hello could you please go to position y -4,1 then y -3,2 4,5 x 1.0 y 2.2 thanks bye");
        verificaRisultatoTargetPosizione(difficili2, new Target(4.5,1.0, -4.1 , null));
        //------------------------------------------//
        ArrayList<String> difficili3 = new ArrayList<>();
        difficili3.add("could you please go to position target 1 forward for -3.1 m");
        verificaRisultatoTargetPosizione(difficili3, new Target(4.1, 4.1, 4.1, "target 1"));
        //------------------ FRASI XML ---------------//
        ArrayList<String> frasiXML1 = new ArrayList<>();
        frasiXML1.add("go to position x -3.4 y 2.3 orientation -15 degrees");
        verificaRisultatoTargetPosizione(frasiXML1, new Target(-15, -3.4, 2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML2 = new ArrayList<>();
        frasiXML2.add("go to position orientation -90° la y -3,2 and 4");
        verificaRisultatoTargetPosizione(frasiXML2, new Target(-90,4.0,-3.2 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML3 = new ArrayList<>();
        frasiXML3.add("go to y -4.1 then y -3.2 4.5 x 1.0 y 2.2");
        verificaRisultatoTargetPosizione(frasiXML3, new Target(4.5,1.0, -4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML4 = new ArrayList<>();
        frasiXML4.add("go to y -4.1 then y -3.2 x 1.0 y 2.2"); //{1.0, -4.1, 0.0}
        verificaRisultatoTargetPosizione(frasiXML4, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML5 = new ArrayList<>();
        frasiXML5.add("go to position y -4.1 then y -3.2 1.0 y 2.2"); //{1.0, -4.1, 0.0}
        verificaRisultatoTargetPosizione(frasiXML5, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML6 = new ArrayList<>();
        frasiXML6.add("you could go in position 3.0 -2.3 thanks"); //{3.0, -2.3, 0.0}
        verificaRisultatoTargetPosizione(frasiXML6, new Target(0.0,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML7 = new ArrayList<>();
        frasiXML7.add("go to 3.0 -2.3 560");
        verificaRisultatoTargetPosizione(frasiXML7, new Target(560,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML8 = new ArrayList<>();
        frasiXML8.add("go to x table y -4.1 then x y -3.2 4.5 orientation 1.0 y 2.2");//{4.5, -4.1, 1.0}
        verificaRisultatoTargetPosizione(frasiXML8, new Target(1.0,4.5,-4.1 , null));
        /*
        //------------------------------------------//
        ArrayList<String> posizione = new ArrayList<>();
        posizione.add("potresti andare in posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoTargetPosizione(posizione, new Target(96,12,13 , null));
        */
    }

    @Test
    public void testRisultatoRiposiziona(){
        //------------------------------------------//
        ArrayList<String> riposiziona1 = new ArrayList<>();
        riposiziona1.add("reposition");
        verificaRisultatoTargetRiposiziona(riposiziona1, null);
        //------------------------------------------//
        ArrayList<String> riposiziona2 = new ArrayList<>();
        riposiziona2.add("relocate");
        verificaRisultatoTargetRiposiziona(riposiziona2, null);
        //------------------------------------------//
        ArrayList<String> riposiziona3 = new ArrayList<>();
        riposiziona3.add("repositioned in x 1.2 orientation 0.0");
        verificaRisultatoTargetRiposiziona(riposiziona3, null);
        //------------------------------------------//
        ArrayList<String> riposiziona4 = new ArrayList<>();
        riposiziona4.add("please could you reposition yourself in position x 12 y 3");
        verificaRisultatoTargetRiposiziona(riposiziona4, new Target(0, 12, 3, null));

        //----------------DIFFICILI---------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("stop please could you reposition yourself in position x 12 y 3 forward for minus 10 m");
        verificaRisultatoTargetRiposiziona(difficili1, new Target(0.0, 12, 3, null));
        //------------------------------------------//
        ArrayList<String> difficili2 = new ArrayList<>();
        difficili2.add("left of 90° reposition forward for minus 10 m");
        verificaRisultatoTargetRiposiziona(difficili2, null);

        //------------- FRASI XML-----------------//
        ArrayList<String> riposizionaXML1 = new ArrayList<>();
        riposizionaXML1.add("you can reposition yourself in x 1 y -1,2 and orientation 15.0 thanks");
        verificaRisultatoTargetRiposiziona(riposizionaXML1, new Target(15.0, 1.0, -1.2, null));
        //------------------------------------------//
        ArrayList<String> riposizionaXML2 = new ArrayList<>();
        riposizionaXML2.add("reposition yourself in x 1 y minus 1,2");
        verificaRisultatoTargetRiposiziona(riposizionaXML2, new Target(0.0, 1.0, -1.2, null));
        //------------------------------------------//
        ArrayList<String> riposizionaXML3 = new ArrayList<>();
        riposizionaXML3.add("reposition");
        verificaRisultatoTargetRiposiziona(riposizionaXML3, null);
        //------------------------------------------//
        ArrayList<String> riposizionaXML4 = new ArrayList<>();
        riposizionaXML4.add("you can reposition yourself in x 1.2 and orientation 0.0");
        verificaRisultatoTargetRiposiziona(riposizionaXML4, null);
        /*
        //------------------------------------------//
        ArrayList<String> riposiziona = new ArrayList<>();
        riposiziona.add("please could you reposition yourself in position x 12 y 3 ");
        verificaRisultatoTargetRiposiziona(riposiziona, new Target(0.0, 12, 3, null));
        */
    }

    @Test
    public void testComandoSalva(){
        //------------------------------------------//
        ArrayList<String> salva1 = new ArrayList<>();
        salva1.add("could you save the location as origin thanks");
        verificaRisultatoSalva(salva1, "origin");
        //------------------------------------------//
        ArrayList<String> salva2 = new ArrayList<>();
        salva2.add("hi! save position as table 1vfrt thanks");
        verificaRisultatoSalva(salva2, "table 1");
        //------------------------------------------//
        ArrayList<String> salva3 = new ArrayList<>();
        salva3.add("save position as table 4.4 thanks");
        verificaRisultatoSalva(salva3, "table 4");
        //------------------------------------------//
        ArrayList<String> salva4 = new ArrayList<>();
        salva4.add("store the position as table rocco1 thanks");
        verificaRisultatoSalva(salva4, "table");
        //------------------------------------------//
        ArrayList<String> salva5 = new ArrayList<>();
        salva5.add("store the position as rocco1.0 table thanks\"");
        verificaRisultatoSalva(salva5, "rocco1");
        //------------------------------------------//
        ArrayList<String> salva6 = new ArrayList<>();
        salva6.add("you could memorize the position as table 1rocco thanks");
        verificaRisultatoSalva(salva6, "table 1");
        //------------------------------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("hi! save position as table 4.4, you could memorize the position as on 1 thanks");
        verificaRisultatoSalva(difficili1, "table 4");

        //---------- FRASI XML-----------------//
        ArrayList<String> salvaXML1 = new ArrayList<>();
        salvaXML1.add("you could save the position with the name origin thanks");
        verificaRisultatoSalva(salvaXML1, "origin");
        //------------------------------------------//
        ArrayList<String> salvaXML2 = new ArrayList<>();
        salvaXML2.add("save position as table 1");
        verificaRisultatoSalva(salvaXML2, "table 1");
        //------------------------------------------//
        ArrayList<String> salvaXML3 = new ArrayList<>();
        salvaXML3.add("hello save position as table 4.4 thanks");
        verificaRisultatoSalva(salvaXML3, "table 4");
        //------------------------------------------//
        ArrayList<String> salvaXML4 = new ArrayList<>();
        salvaXML4.add("stores this position as table dd1");
        verificaRisultatoSalva(salvaXML4, "table");
        //------------------------------------------//
        ArrayList<String> salvaXML5 = new ArrayList<>();
        salvaXML5.add("stores this position as position 2");
        verificaRisultatoSalva(salvaXML5, "position 2");
        //------------------------------------------//
        ArrayList<String> salvaXML6 = new ArrayList<>();
        salvaXML6.add("you could memorize the position with the name table 1rocco thanks");
        verificaRisultatoSalva(salvaXML6, "table 1");
        /*
        //------------------------------------------//
        ArrayList<String> salvaXML = new ArrayList<>();
        salvaXML.add("stores this position as position 2");
        verificaRisultatoSalva(salvaXML, "table 4");
        */
    }
}
