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

public class CVTestES extends ComandiVocaliTest {

    @Override
    protected ComandiVocali inizializzaComandiVocali() {
        DatiPermanenti datiPermanenti = new DatiPermanenti();
        datiPermanenti.setTargets(new HashMap<String, Target>());
        Map<String, Target> mappa = datiPermanenti.getTargets();
        mappa.put("donato", new Target(15.1, 15.1, 15.1, "donato"));
        mappa.put("rocco", new Target(16.1, 16.1, 16.1, "rocco"));
        mappa.put("baño", new Target(17.1, 17.1, 17.1, "baño"));
        mappa.put("baño 1", new Target(18.1, 18.1, 18.1, "baño 1"));
        mappa.put("target 1", new Target(19.1, 19.1, 19.1, "target 1"));
        mappa.put("ingresso", new Target(20.1, 20.1, 19.1, "ingresso"));
        ComandiVocali comandiVocali = new ComandiVocali();
        comandiVocali.setDatiPermanenti(datiPermanenti);
        comandiVocali.cambiaLingua(new Locale("es","ES"));
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
        frasiSbagliate2.add("adelante");
        frasiSbagliate2.add("podrías guardar la ubicación como origen gracias");
        verificaNessunRisultato(frasiSbagliate2);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate3 = new ArrayList<>();
        frasiSbagliate3.add("avantiindietrovaidestra sinistrafermo");
        verificaNessunRisultato(frasiSbagliate3);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate4 = new ArrayList<>();
        frasiSbagliate4.add("¿podría guardar la posición como continuar grazias?");
        verificaNessunRisultato(frasiSbagliate4);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate5 = new ArrayList<>();
        frasiSbagliate5.add("guardar posición como avanzar 1 ");
        verificaNessunRisultato(frasiSbagliate5);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate6 = new ArrayList<>();
        frasiSbagliate6.add("guardar la ubicación con el nombre izquierda 1 erebtdvd ");
        verificaNessunRisultato(frasiSbagliate6);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate7 = new ArrayList<>();
        frasiSbagliate7.add("adelante por menos 10 metros ");
        verificaNessunRisultato(frasiSbagliate7);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate8 = new ArrayList<>();
        frasiSbagliate8.add("derecha por menos 10°");
        verificaNessunRisultato(frasiSbagliate8);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate9 = new ArrayList<>();
        frasiSbagliate9.add("guardar ubicación como guardar");
        verificaNessunRisultato(frasiSbagliate9);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate10 = new ArrayList<>();
        frasiSbagliate10.add("memorizar posición con nombre guardar 1");
        verificaNessunRisultato(frasiSbagliate10);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate11 = new ArrayList<>();
        frasiSbagliate11.add("guardar posición como x");
        verificaNessunRisultato(frasiSbagliate11);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate12 = new ArrayList<>();
        frasiSbagliate12.add("podría guardar la posición como y 1");
        verificaNessunRisultato(frasiSbagliate12);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate13 = new ArrayList<>();
        frasiSbagliate13.add("hola guardar posición como guardar posición como reposicionar");
        verificaNessunRisultato(frasiSbagliate13);
        //-------------------------------------------------//
        ArrayList<String> frasiSbagliate14 = new ArrayList<>();
        frasiSbagliate14.add("guardar la posición como stop");
        verificaNessunRisultato(frasiSbagliate14);
        //------------------ INCOMPLETE -----------------------//
        ArrayList<String> incomplete1 = new ArrayList<>();
        incomplete1.add("podrías ir al target gracias");
        verificaNessunRisultato(incomplete1);


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
        frasi1.add("¿Hola hermosa Cómo estás? ");
        frasi1.add("adelante");
        verificaRisultatoComando(frasi1, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("detenido");
        frasi2.add("adelante");
        verificaRisultatoComando(frasi2, 0, ComandoVel.FERMO, null, null);
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("¿Hola hermosa Cómo estás? ");
        frasi3.add("cristmovt chi te stramuort de 10 metros");
        verificaRisultatoComando(frasi3, ComandoVel.METRI, ComandoVel.AVANTI, 10.0, 10.0);
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("podrías retroceder adelante atrás detener");
        verificaRisultatoComando(frasi4, 0, ComandoVel.FERMO, null, null);
        //----------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("hola linda derecha como estas?");
        verificaRisultatoComando(frasi5, ComandoVel.GRADI_A_RADIANTI, ComandoVel.DESTRA, null, Math.PI/2);
        //----------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("adelante 10 metross");
        verificaRisultatoComando(frasi6, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //----------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("gire al ángulo -134.5°");
        double rad7= -134.5* Costanti.DA_GRADI_A_RADIANTI;
        double posRad7= Math.PI*2+rad7;
        verificaRisultatoComando(frasi7, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, posRad7, posRad7);
        //------------------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("podrías retroceder hacia adelante hacia atrás, detenerte durante 10 metros ");
        verificaRisultatoComando(frasi8, 0, ComandoVel.FERMO, null, null);
        //----------------------------------------//
        ArrayList<String> frasi9 = new ArrayList<>();
        frasi9.add("enderezado");
        verificaRisultatoComando(frasi9, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE, null, 0.0 );
        //----------------------------------------//
        ArrayList<String> fermo1 = new ArrayList<>();
        fermo1.add("deténgase");
        verificaRisultatoComando(fermo1,0, ComandoVel.FERMO, null, null);
        //----------------------------------------//
        ArrayList<String> fermo2 = new ArrayList<>();
        fermo2.add("detenido");
        verificaRisultatoComando(fermo2,0, ComandoVel.FERMO, null, null);

        //-----------------DIFFICILI ------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("podría adelante en la posición x 2 y 3 guardar la posición como deténgase");
        verificaRisultatoComando(difficili1, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //----------------------------------------//
        ArrayList<String> difficili2 = new ArrayList<>();
        difficili2.add("adelante guardar posición guardar posición cómo guardar posición como deténgase");
        verificaRisultatoComando(difficili2, ComandoVel.METRI, ComandoVel.AVANTI, null, 1.0);
        //----------------------------------------//
        ArrayList<String> difficili3 = new ArrayList<>();
        difficili3.add("izquierda guardar posición como guardar posición como adelante 1");
        verificaRisultatoComando(difficili3, ComandoVel.GRADI_A_RADIANTI, ComandoVel.SINISTRA, null, Math.PI/2);
        //----------------------------------------//
        ArrayList<String> difficili5 = new ArrayList<>();
        difficili5.add("gire a la izquierda 15 grados guarde la posición como reposicione guarde la posición como adelante por 1 metro");
        double radD5= 15*Costanti.DA_GRADI_A_RADIANTI;
        verificaRisultatoComando(difficili5, ComandoVel.GRADI_A_RADIANTI, ComandoVel.SINISTRA, radD5, radD5);
        //----------------------------------------//
        ArrayList<String> difficili6 = new ArrayList<>();
        difficili6.add("guardar adelante para 1 metro gracias");
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
        frasi1.add("podrias ir a la posicion x 12 y 13 y orientacion 96 grados gracias ");
        verificaRisultatoTargetPosizione(frasi1, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("podrías IR a la POSICIÓN 12.2 13.3 e 96.57 400 grados gracias");
        verificaRisultatoTargetPosizione(frasi2, new Target(96.57,12.2,13.3 , null));
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("podrias ir a la posicion x 12 y 13 y 400.2 y orientacion 96 grados gracias");
        verificaRisultatoTargetPosizione(frasi3, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("podrias ir a la posicion y 13 x 12 y orientacion 96 grados gracias");
        verificaRisultatoTargetPosizione(frasi4, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("podrias ir a la posicion 15 y 13 orientación 96 x 12 33.2 grados gracias");
        verificaRisultatoTargetPosizione(frasi5, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("ir a posicion x 12 16.33 y 13 y orientación 96 grados gracias");
        verificaRisultatoTargetPosizione(frasi6, new Target(96,12,13 , null));
        //------------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>();
        frasi7.add("podrias ir a la posicion x 12 16.33 y 13 grados gracias");
        verificaRisultatoTargetPosizione(frasi7, new Target(16.33,12,13 , null));
        //------------------- DIFFICILI ----------------------//
        ArrayList<String> difficili1 = new ArrayList<>();
        difficili1.add("podrias ir a rocco x 1 posicion x 12 y 13 y orientacion 96 grados gracias ");
        verificaRisultatoTargetPosizione(difficili1, new Target(16.1,16.1, 16.1, "rocco"));
        //------------------------------------------//
        ArrayList<String> difficili2 = new ArrayList<>();
        difficili2.add("ir al baño 1 rocco");
        verificaRisultatoTargetPosizione(difficili2, new Target(18.1,18.1,18.1 , "baño 1"));
        //------------------------------------------//
        ArrayList<String> difficili3 = new ArrayList<>();
        difficili3.add("podrias ir al baño gracias");
        verificaRisultatoTargetPosizione(difficili3,  new Target(17.1,17.1,17.1 , "baño"));
        //------------------------------------------//
        ArrayList<String> difficili4 = new ArrayList<>();
        difficili4.add("podrías ir a la ingresso gracias guardar posición como ir");
        verificaRisultatoTargetPosizione(difficili4, new Target(20.1, 20.1,19.1 , "ingresso"));



        //------------------ FRASI XML ---------------//
        ArrayList<String> frasiXML1 = new ArrayList<>();
        frasiXML1.add("ir a posición x -3.4 y 2.3 orientación -15 grados");
        verificaRisultatoTargetPosizione(frasiXML1, new Target(-15, -3.4, 2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML2 = new ArrayList<>();
        frasiXML2.add("ir a posición orientación -90° la y -3.2 4");
        verificaRisultatoTargetPosizione(frasiXML2, new Target(-90,4.0,-3.2 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML3 = new ArrayList<>();
        frasiXML3.add("irías a y -4.1 luego y -3.2 4.5 x 1.0 y 2.2 ");
        verificaRisultatoTargetPosizione(frasiXML3, new Target(4.5,1.0, -4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML4 = new ArrayList<>();
        frasiXML4.add("ir a y -4.1 luego y -3.2 x 1.0 y 2.2"); //{1.0, -4.1, 0.0}
        verificaRisultatoTargetPosizione(frasiXML4, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML5 = new ArrayList<>();
        frasiXML5.add("ir a la posición y -4.1 luego y -3.2 1.0 y 2.2"); //{1.0, -4.1, 0.0}
        verificaRisultatoTargetPosizione(frasiXML5, new Target(0.0,1.0,-4.1 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML6 = new ArrayList<>();
        frasiXML6.add("podrias ir en la posicion 3.0 -2.3 gracias"); //{3.0, -2.3, 0.0}
        verificaRisultatoTargetPosizione(frasiXML6, new Target(0.0,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML7 = new ArrayList<>();
        frasiXML7.add("ir a 3.0 -2.3 560");
        verificaRisultatoTargetPosizione(frasiXML7, new Target(560,3.0,-2.3 , null));
        //------------------------------------------//
        ArrayList<String> frasiXML8 = new ArrayList<>();
        frasiXML8.add("ir a la tabla x y -4.1 luego x y -3.2 4.5 orientación 1.0 y 2.2 ");//{4.5, -4.1, 1.0}
        verificaRisultatoTargetPosizione(frasiXML8, new Target(1.0,4.5,-4.1 , null));

        /*
        //------------------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("potresti andare in posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoTarget(frasi, new Target(96,12,13 , null));
        */
    }

    @Test
    public void testComandoRiposiziona(){
        //------------------------------------------//
        ArrayList<String> frasi1 = new ArrayList<>();
        frasi1.add("reposicionar");
        verificaRisultatoTargetRiposiziona(frasi1, null);
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("podrías reposicionarte");
        verificaRisultatoTargetRiposiziona(frasi2, null);
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("escucha, reposicionado en y 1 x 3 y orientación 12 grados gracias");
        verificaRisultatoTargetRiposiziona(frasi3, new Target(12, 3,1 , null));
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("por favor, reposicione a y 4 y x 2");
        verificaRisultatoTargetRiposiziona(frasi4, new Target(0, 2,4 , null));
        //------------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("avanzar 10 metros reposicionar guardar la posición como adelante");
        verificaRisultatoTargetRiposiziona(frasi5, null);
        //------------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("avanzar 10 metros reposicionar en x 2 guardar la posición como adelante");
        verificaRisultatoTargetRiposiziona(frasi6, null);
        //------------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>(); //questa è maligna
        frasi7.add("avance 10 metros reposicionado en x 2 y orientación 90 grados guarde la posición como adelante");
        verificaRisultatoTargetRiposiziona(frasi7, null);
        //------------------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("avanzar 10 metros reposicionado en x 2 luego y 8 y orientación 90 grados guarde la posición como adelante");
        verificaRisultatoTargetRiposiziona(frasi8, new Target(90, 2,8 , null));

        //------------- FRASI XML-----------------//
        ArrayList<String> riposizionaXML1 = new ArrayList<>();
        riposizionaXML1.add("puedes reposicionarte en x 1 y -1,2 y orientacion 15.0 gracias");
        verificaRisultatoTargetRiposiziona(riposizionaXML1, new Target(15.0, 1.0, -1.2, null));
        //------------------------------------------//
        ArrayList<String> riposizionaXML2 = new ArrayList<>();
        riposizionaXML2.add("podrías reposicionarte en x 1 y -1,2");
        verificaRisultatoTargetRiposiziona(riposizionaXML2, new Target(0.0, 1.0, -1.2, null));
        //------------------------------------------//
        ArrayList<String> riposizionaXML3 = new ArrayList<>();
        riposizionaXML3.add("reposicionar");
        verificaRisultatoTargetRiposiziona(riposizionaXML3, null);
        //------------------------------------------//
        ArrayList<String> riposizionaXML4 = new ArrayList<>();
        riposizionaXML4.add("puedes reposicionarte en x 1.2 orientación 0.0");
        verificaRisultatoTargetRiposiziona(riposizionaXML4, null);
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
        frasi1.add("podrías guardar la ubicación como origen gracias");
        verificaRisultatoSalva(frasi1, "origen");
        //------------------------------------------//
        ArrayList<String> frasi2 = new ArrayList<>();
        frasi2.add("memorizar posición como antonio");
        verificaRisultatoSalva(frasi2, "antonio");
        //------------------------------------------//
        ArrayList<String> frasi3 = new ArrayList<>();
        frasi3.add("podrías guardar la posición con el nombre roccflicantoij gracias");
        verificaRisultatoSalva(frasi3, "roccflicantoij");
        //------------------------------------------//
        ArrayList<String> frasi4 = new ArrayList<>();
        frasi4.add("adelante por 10 m podrías guardar la posición con el nombre roccflicantoij 1 3 3 gracias");
        verificaRisultatoSalva(frasi4, "roccflicantoij 1");
        //------------------------------------------//
        ArrayList<String> frasi5 = new ArrayList<>();
        frasi5.add("guardar posición como antonio 2 ok guardar posición como ir 33");
        verificaRisultatoSalva(frasi5, "antonio 2");
        //------------------------------------------//
        ArrayList<String> frasi6 = new ArrayList<>();
        frasi6.add("guarde esta posición como entrada 2 ok guarde la posición como adelante 33");
        verificaRisultatoSalva(frasi6, "entrada 2");
        //-----------------------------------------//
        ArrayList<String> frasi7 = new ArrayList<>(); //test carattere speciale
        frasi7.add("guardar posición como pequeño");
        verificaRisultatoSalva(frasi7, "pequeño");
        //------------------------------------------//
        ArrayList<String> frasi8 = new ArrayList<>();
        frasi8.add("guardar ubicación como 1 1 1 ");
        verificaRisultatoSalva(frasi8, "1 1");
        //------------------------------------------//
        ArrayList<String> frasi9 = new ArrayList<>();
        frasi9.add("guarde la posición con el nombre è1 1 gracias bonito");
        verificaRisultatoSalva(frasi9, "è1 1");
        //------------------------------------------//
        ArrayList<String> frasi10 = new ArrayList<>();
        frasi10.add("guarde la ubicación con el nombre è1.0 1 gracias agradable");
        verificaRisultatoSalva(frasi10, "è1");

        //---------- FRASI XML-----------------//
        ArrayList<String> salvaXML1 = new ArrayList<>();
        salvaXML1.add("podrías guardar la posición con el nombre origen gracias");
        verificaRisultatoSalva(salvaXML1, "origen");
        //------------------------------------------//
        ArrayList<String> salvaXML2 = new ArrayList<>();
        salvaXML2.add("guardar posición como tabla 1");
        verificaRisultatoSalva(salvaXML2, "tabla 1");
        //------------------------------------------//
        ArrayList<String> salvaXML3 = new ArrayList<>();
        salvaXML3.add("hola guardar posición como tabla 4.4 gracias");
        verificaRisultatoSalva(salvaXML3, "tabla 4");
        //------------------------------------------//
        ArrayList<String> salvaXML4 = new ArrayList<>();
        salvaXML4.add("almacena esta posición como tabla dd1");
        verificaRisultatoSalva(salvaXML4, "tabla");
        //------------------------------------------//
        ArrayList<String> salvaXML5 = new ArrayList<>();
        salvaXML5.add("almacena esta posición como posición 2");
        verificaRisultatoSalva(salvaXML5, "posición 2");
        //------------------------------------------//
        ArrayList<String> salvaXML6 = new ArrayList<>();
        salvaXML6.add("podrías memorizar la posición con el nombre tabla 1rocco gracias");
        verificaRisultatoSalva(salvaXML6, "tabla 1");
        /*
        //------------------------------------------//
        ArrayList<String> frasi = new ArrayList<>();
        frasi.add("potresti andare in posizione x 12 y 13 e orientamento 96 gradi grazie");
        verificaRisultatoSalva(frasi, "antonio");
        */
    }
}
