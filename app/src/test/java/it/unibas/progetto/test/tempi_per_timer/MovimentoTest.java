package it.unibas.progetto.test.tempi_per_timer;
import org.junit.Test;
import org.junit.Assert;
import it.unibas.progetto.modello.utilita.ElementoMovimento;
import it.unibas.progetto.modello.utilita.Movimento;
//import it.unibas.progetto.modello.utilita.TempiPerTimer;
import it.unibas.progetto.persistenza.DAOException;

public class MovimentoTest{
    private double precisione =  0.000001; //grado di precisione per secondi
    /*
    * Usando i vecchi metodi di tempi per timer ottengo un tempo di esecuzione di circa 5 volte superiore alle nuove versioni.
    * 650ms vs 165ms
    * NOTA (per replicarli): utilizzare i metodi
    * - TempiPerTimer ==> 'double tempoPerTimerGenerale(double richiesto, movimento mov)'
    * - il vecchio stampaEAssert di questa classe
    */

    @Test
    public void testCondizioniSbagliate1(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 1: valori nulli");
        stampaAssertCondizioniSbagliate(null, null);                                //tabelle nulle
        stampaAssertCondizioniSbagliate(new ElementoMovimento[5], new ElementoMovimento[10]);  //valori nulli
    }

    @Test
    public void testCondizioniSbagliate2(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 2: dimensioni");
        //dimensioni
        ElementoMovimento[] tabA = new ElementoMovimento[2];
        ElementoMovimento[] tabF = new ElementoMovimento[1];
        stampaAssertCondizioniSbagliate(tabA, tabF);
        //dimensioni
        tabA[0] = new ElementoMovimento(1.0,0);//, 0);
        tabA[1] = new ElementoMovimento(2.0,3);//, 0);
        tabF[0] = new ElementoMovimento(5.0,3);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabA = new ElementoMovimento[1];
        tabF = new ElementoMovimento[2];
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate3(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 3: tempi negativi");
        ElementoMovimento[] tabA = new ElementoMovimento[2];
        ElementoMovimento[] tabF = new ElementoMovimento[2];
        // tempo negativo tabA
        tabA[0] = new ElementoMovimento(-1.0,0);//, 0);
        tabA[1] = new ElementoMovimento(2.0,6);//, 0);
        tabF[0] = new ElementoMovimento(3.0,6);//, 0);
        tabF[1] = new ElementoMovimento(4.0,0);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        // tempo negativo tabF
        tabA[0] = new ElementoMovimento(1.0,0);//, 0);
        tabF[0] = new ElementoMovimento(-1.0,6);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate4(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 4: valori inaccettabili");
        ElementoMovimento[] tabA = new ElementoMovimento[4];
        tabA[0] = new ElementoMovimento(1.0,6);//, 0); //<-
        tabA[1] = new ElementoMovimento(2.0,3);//, 0);
        tabA[2] = new ElementoMovimento(3.0,5);//, 0);
        tabA[3] = new ElementoMovimento(4.0,6);//, 0);
        ElementoMovimento[] tabF = new ElementoMovimento[3];
        tabF[0] = new ElementoMovimento(5.0,7);//, 0); //<-
        tabF[1] = new ElementoMovimento(6.0,5);//, 0);
        tabF[2] = new ElementoMovimento(7.0,7);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabA[0] = new ElementoMovimento(1.0,0);//, 0); //<- sistemato questo, l'errore è in tabF
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate5(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 5: valori limite sbagliati");
        ElementoMovimento[] tabA = new ElementoMovimento[2];
        ElementoMovimento[] tabF = new ElementoMovimento[2];
        //// inferiori diversi da zero
        tabA[0] = new ElementoMovimento(1.0,2);//, 0);
        tabA[1] = new ElementoMovimento(2.0,6);//, 0);
        tabF[0] = new ElementoMovimento(3.0,6);//, 0);
        tabF[1] = new ElementoMovimento(4.0,2);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        ///// valori di velocità negativi
        tabA[0] = new ElementoMovimento(1.0,-2);//, 0);
        tabF[0] = new ElementoMovimento(1.0,6);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        //superiori non uguali
        tabA[0] = new ElementoMovimento(1.0,0);//, 0);
        tabA[1] = new ElementoMovimento(2.0,7);//, 0);
        tabF[0] = new ElementoMovimento(3.0,6);//, 0);
        tabF[1] = new ElementoMovimento(4.0,0);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate6(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 6: valori tempo non crescenti");
        ElementoMovimento[] tabA = new ElementoMovimento[4];
        tabA[0] = new ElementoMovimento(0.0,0);//, 0);
        tabA[1] = new ElementoMovimento(2.0,3);//, 0);
        tabA[2] = new ElementoMovimento(2.0,5);//, 0);
        tabA[3] = new ElementoMovimento(4.0,6);//, 0);
        ElementoMovimento[] tabF = new ElementoMovimento[3];
        tabF[0] = new ElementoMovimento(5.0,6);//, 0);
        tabF[1] = new ElementoMovimento(6.0,5);//, 0);
        tabF[2] = new ElementoMovimento(7.0,0);//, 0);
        //errore su tabA
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabA[2] = new ElementoMovimento(5.0,5);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabA[2] = new ElementoMovimento(-5.0,5);//, 0);
        //errore su tabF
        tabA[2] = new ElementoMovimento(3.0,5);//, 0);
        tabF[1] = new ElementoMovimento(-5.0,5);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabF[1] = new ElementoMovimento(5.0,5);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate7(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 7: velocità tab Accelerazione non compresa tra gli estremi");
        ElementoMovimento[] tabA = new ElementoMovimento[4];
        tabA[0] = new ElementoMovimento(0.0,0);//, 0);
        tabA[1] = new ElementoMovimento(2.0,8);//, 0); //<--
        tabA[2] = new ElementoMovimento(3.0,5);//, 0);
        tabA[3] = new ElementoMovimento(4.0,6);//, 0);
        ElementoMovimento[] tabF = new ElementoMovimento[2];
        tabF[0] = new ElementoMovimento(5.0,6);//, 0);
        tabF[1] = new ElementoMovimento(7.0,0);//, 0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabA[1] = new ElementoMovimento(2.0,-1);//, 0); //<--
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate8(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 8: velocità tab Accelerazione non compresa tra gli estremi");
        ElementoMovimento[] tabA = new ElementoMovimento[2];
        tabA[0] = new ElementoMovimento(0.0,0);
        tabA[1] = new ElementoMovimento(2.0,8);
        ElementoMovimento[] tabF = new ElementoMovimento[5];
        tabF[0] = new ElementoMovimento(5.0,8);
        tabF[1] = new ElementoMovimento(7.0,7);
        tabF[2] = new ElementoMovimento(7.1,7);     //<--
        tabF[3] = new ElementoMovimento(8.0,5);
        tabF[4] = new ElementoMovimento(9.0,0);
        stampaAssertCondizioniSbagliate(tabA, tabF);
        tabF[2] = new ElementoMovimento(7.5,7.5);   //<--
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void testCondizioniSbagliate9(){
        System.out.println("TEST CONDIZIONI SBAGLIATE 9:  ^v-^  (caso particolare), esiste un tratto dove mA(v) < mF(v) ");
        ElementoMovimento[] tabA= new ElementoMovimento[7];
        ElementoMovimento[] tabF= new ElementoMovimento[3];
        tabA[0] = new ElementoMovimento(0.0,0);
        tabA[1] = new ElementoMovimento(3.0,3);
        tabA[2] = new ElementoMovimento(4.0,5);
        tabA[3] = new ElementoMovimento(4.5,2);
        tabA[4] = new ElementoMovimento(5.0,4);
        tabA[5] = new ElementoMovimento(7.0,4);
        tabA[6] = new ElementoMovimento(8.0,6);

        tabF[0]= new ElementoMovimento(11.0,6.0);//, 0);
        tabF[1]= new ElementoMovimento(13.0,4.0);//, 10);
        tabF[2]= new ElementoMovimento(14.0,0);//, 2);
        stampaAssertCondizioniSbagliate(tabA, tabF);
    }

    @Test
    public void test0() {
        System.out.println("TEST 0: ^ ");
        ElementoMovimento[] tabA = new ElementoMovimento[3];
        ElementoMovimento[] tabF = new ElementoMovimento[2];

        tabA[0]= new ElementoMovimento(15000000000L,0);//, 0);
        tabA[1]= new ElementoMovimento(17000000000L,3);//, 3);
        tabA[2]= new ElementoMovimento(18000000000L,6);//, 4.5);

        tabF[0]= new ElementoMovimento(0.0,6);//, 0);
        tabF[1]= new ElementoMovimento(5.0,0);//, 15);

        Movimento avanti = new Movimento(tabA, tabF);
        stampaEAssertTimerGenerale(0,0, avanti);
        stampaEAssertTimerGenerale(6.75, 2, avanti);
        stampaEAssertTimerGenerale(13.3125, 2.5, avanti);
    }

    @Test
    public void test1(){
        System.out.println("TEST 1: ^ ");
        ElementoMovimento[] tabA= new ElementoMovimento[2];
        ElementoMovimento[] tabF= new ElementoMovimento[2];
        tabA[0]= new ElementoMovimento(0000000000L,0);//, 0);
        tabA[1]= new ElementoMovimento(3000000000L,10);//, 15);
        tabF[0]= new ElementoMovimento(5000000000L,10);//, 0);
        tabF[1]= new ElementoMovimento(7000000000L,0);//, 10);
        Movimento avanti = new Movimento(tabA, tabF);
        //casi limite
        //stampaEAssert(-5, 0, avanti); //controllare conseguenze ==> Quello vecchio ritornava il risultato a 5 (norma). Il nuovo 0.
        stampaEAssertTimerGenerale(0, 0, avanti);
        stampaEAssertTimerGenerale(25, 3, avanti);
        stampaEAssertTimerGenerale(26, 3.1, avanti);
        stampaEAssertTimerGenerale(30, 3.5, avanti);

        //casi intermedi
        stampaEAssertTimerGenerale(6.25, 1.5, avanti);
        stampaEAssertTimerGenerale(12.25, 2.1, avanti);
    }

    @Test
    public void test2() {
        System.out.println("TEST 2: ^ , + frenata");
        ElementoMovimento[] tabA = new ElementoMovimento[7];
        ElementoMovimento[] tabF = new ElementoMovimento[5];
        long i = -3000000000L;
        tabA[0]= new ElementoMovimento(3000000000L+i,0);//, 0);
        tabA[1]= new ElementoMovimento(4000000000L+i,1);//, 0.5);
        tabA[2]= new ElementoMovimento(5000000000L+i,2);//, 1.5);
        tabA[3]= new ElementoMovimento(6000000000L+i,3);//, 2.5);
        tabA[4]= new ElementoMovimento(7000000000L+i,4);//, 3.5);
        tabA[5]= new ElementoMovimento(8000000000L+i,5);//, 4.5);
        tabA[6]= new ElementoMovimento(9000000000L+i,6);//, 5.5);

        tabF[0]= new ElementoMovimento(15000000000L+i,6);//, 0);
        tabF[1]= new ElementoMovimento(16000000000L+i,4);//, 5);
        tabF[2]= new ElementoMovimento(17000000000L+i,3);//, 3.5);
        tabF[3]= new ElementoMovimento(18000000000L+i,1);//, 2);
        tabF[4]= new ElementoMovimento(19000000000L+i,0);//, 0.5);


        Movimento avanti = new Movimento(tabA, tabF);
        stampaEAssertTimerGenerale(17.1875, 4.5,avanti);
        stampaEAssertTimerGenerale(20.75, 5.0,avanti);
        stampaEAssertTimerGenerale(0, 0,avanti);
        stampaEAssertTimerGenerale(1, 1,avanti);
        stampaEAssertTimerGenerale(29, 6,avanti);

        stampaEAssertSpazioFrenata(6, 11, avanti);
        stampaEAssertSpazioFrenata(0, 0, avanti);
        stampaEAssertSpazioFrenata(1, 0.5, avanti);
        stampaEAssertSpazioFrenata(2, 1.25, avanti);
    }

    @Test
    public void test3() {
        int RIPETI_TEST = 1000000;
        System.out.println("TEST 3: ^, valutazione prestazioni, ripetuto per:"+ RIPETI_TEST);
        ElementoMovimento[] tabA = new ElementoMovimento[2];
        ElementoMovimento[] tabF = new ElementoMovimento[3];
        tabA[0] = new ElementoMovimento(0.0,0);//, 0);
        tabA[1] = new ElementoMovimento(5.0,6);//, 15);

        tabF[0] = new ElementoMovimento(15000000000L,6);//, 0);
        tabF[1] = new ElementoMovimento(16000000000L,3);//, 4.5);
        tabF[2] = new ElementoMovimento(18000000000L,0);//, 3);
        Movimento avanti = new Movimento(tabA, tabF);

        stampaEAssertTimerGenerale(65D/6, 10D/3, avanti);    //vel 4
        for(int i = 0; i < RIPETI_TEST; i++){
            // limite
            stampaEAssertTimerGenerale(0, 0, avanti);
            stampaEAssertTimerGenerale(6.75, 2.5, avanti);
            stampaEAssertTimerGenerale(1.6875, 1.25, avanti);
            //intermedi
            stampaEAssertTimerGenerale(8.24984023, 2.8347, avanti);
            stampaEAssertTimerGenerale(16.08333333, 4.16666667, avanti); //alla quinta cifra sballa
            stampaEAssertTimerGenerale(17.578125, 4.375, avanti); //
            stampaEAssertTimerGenerale(2.041875, 1.375, avanti); //vel 1.65
            stampaEAssertTimerGenerale(65D/6, 10D/3, avanti);    //vel 4
        }
    }

    @Test
    public void test4(){
        System.out.println("TEST 4: /-^ , con tratto a velocità costante");
        ElementoMovimento[] tabA= new ElementoMovimento[4];
        ElementoMovimento[] tabF= new ElementoMovimento[3];
        double i = 16.0;
        tabA[0]= new ElementoMovimento(0.0+i,0);
        tabA[1]= new ElementoMovimento(3.0+i,8);
        tabA[2]= new ElementoMovimento(7.0+i,8);
        tabA[3]= new ElementoMovimento(9.0+i,16);

        tabF[0]= new ElementoMovimento(11.0+i,16);
        tabF[1]= new ElementoMovimento(13.0+i,10);
        tabF[2]= new ElementoMovimento(14.0+i,0);
        Movimento mov = new Movimento(tabA, tabF);
        //soluzione presente nella parte superiore ai due punti.
        stampaEAssertTimerGenerale(199.0/3,8,mov); //v=12
        stampaEAssertTimerGenerale(47.2,7,mov); //caso limite superiore a v=8

        //soluzione presente nella parte inferiore ai due punti.
        stampaEAssertTimerGenerale(8.55,2.25,mov); //v=6
        stampaEAssertTimerGenerale(15.2,3,mov); //caso limite inferiore a v=8

        //souluzione presente tra i due punti, sempre a v=8
        stampaEAssertTimerGenerale(31.2,5,mov);
        stampaEAssertTimerGenerale(39.2,6,mov);
        //soluzione nei casi limite
        stampaEAssertTimerGenerale(0,0,mov);
        stampaEAssertTimerGenerale(99,9,mov);
        stampaEAssertTimerGenerale(100, 9 + 1/16.0,mov);
    }

    @Test
    public void test5(){
        System.out.println("TEST 5: ^-^ , negativa e con tratto a velocità costante(casi particolare)");
        ElementoMovimento[] tabA= new ElementoMovimento[6];
        ElementoMovimento[] tabF= new ElementoMovimento[3];
        tabA[0]= new ElementoMovimento(0.0,0);//, 0);
        tabA[1]= new ElementoMovimento(3.0,3);//, 4.5);
        tabA[2]= new ElementoMovimento(4.0,5);//, 4.0);
        tabA[3]= new ElementoMovimento(5.0,4);//, 4.5);
        tabA[4]= new ElementoMovimento(7.0,4);//, 8.0);
        tabA[5]= new ElementoMovimento(8.0,6);//, 5.0);

        tabF[0]= new ElementoMovimento(11.0,6.0);//, 0);
        tabF[1]= new ElementoMovimento(13.0,4.0);//, 10);
        tabF[2]= new ElementoMovimento(14.0,0);//, 2);
        Movimento mov = new Movimento(tabA, tabF);

        //caso limite inf e sup
        stampaEAssertTimerGenerale(-5, 0, mov);      //vel 0
        stampaEAssertTimerGenerale(0, 0, mov);       //vel 0
        stampaEAssertTimerGenerale(38, 8, mov);      //vel 6
        stampaEAssertTimerGenerale(50, 10, mov);     //vel 6

        //casi intermedi classici / \
        stampaEAssertTimerGenerale(1.40625, 1.5, mov); //vel 1.5
        stampaEAssertTimerGenerale(5.625, 3, mov);     //vel 3
        stampaEAssertTimerGenerale(6.84375, 3.25, mov);//vel 3.5
        stampaEAssertTimerGenerale(8.25, 3.5, mov);    //vel 4
        stampaEAssertTimerGenerale(26.1875, 7.25, mov); //vel 4.5 (terza intersezione)
        //CASI STRANI  \ \  -\
        stampaEAssertTimerGenerale(15, 4.0, mov); //(caso moolto particolare) dove ci potrebbero essere infinite soluzioni
        //Es. potrebbe dare lo stesso risultato a vel 4.5 (ma della seconda funzione che interseca)
        //ovviamente, dato che questa stessa funzione ha lo stesso coefficiente angolare della rispettiva
        //nella tabella di frenata: questo tratto è come se non ci fosse; in quanto lo spazio totale del precedente è lo stesso.
        stampaEAssertTimerGenerale(19, 6.0, mov); //    -\
    }

    @Test
    public void test6(){
        ElementoMovimento[] tabA= new ElementoMovimento[8];
        ElementoMovimento[] tabF= new ElementoMovimento[3];
        tabA[0] = new ElementoMovimento(0.0,0.0);
        tabA[1] = new ElementoMovimento(3.0,3.0);
        tabA[2] = new ElementoMovimento(4.0,5.0);
        tabA[3] = new ElementoMovimento(6.0,4.0);
        tabA[4] = new ElementoMovimento(8.0,4.0);
        tabA[5] = new ElementoMovimento(9.0,3.5);
        tabA[6] = new ElementoMovimento(10.5,2.0);
        tabA[7] = new ElementoMovimento(11.0,6.0);

        tabF[0]= new ElementoMovimento(13.0,6.0);
        tabF[1]= new ElementoMovimento(15.0,4.0);
        tabF[2]= new ElementoMovimento(16.0,0.0);
        Movimento mov = new Movimento(tabA, tabF);
        //caso limite inf e sup
        stampaEAssertTimerGenerale(-5, 0, mov);                 //vel 0
        stampaEAssertTimerGenerale(0, 0, mov);                  //vel 0
        stampaEAssertTimerGenerale(47.375, 11.0, mov);          //vel 6
        // durante la fase di regime
        stampaEAssertTimerGenerale(53.375, 12.0, mov);          //vel 6
        //casi intermedi classici / \
        stampaEAssertTimerGenerale(1.40625, 1.5, mov);          //vel 1.5
        stampaEAssertTimerGenerale(5.625, 3, mov);              //vel 3
        stampaEAssertTimerGenerale(6.84375, 3.25, mov);         //vel 3.5
        stampaEAssertTimerGenerale(8.25, 3.5, mov);             //vel 4
        stampaEAssertTimerGenerale(34.296875, 10.5625, mov);    //vel 2.5 (terza intersezione)
        stampaEAssertTimerGenerale(36.125, 10.75, mov);         //vel 4   (quarta intersezione)
        stampaEAssertTimerGenerale(41.1875, 10.875, mov);       //vel 5   (seconda intersezione)
        // casi particolari \ \
        stampaEAssertTimerGenerale(18.255, 5.4, mov);           //vel 4.3 (seconda intersezione)
        stampaEAssertTimerGenerale(33.03125, 10.0, mov);        //vel 2.5 (seconda intersezione)
        stampaEAssertTimerGenerale(29.1953125, 8.5, mov);         //vel 3.75 (seconda intersezione)
        stampaEAssertTimerGenerale(30.78125, 9.0, mov);         //vel 3.5 (seconda intersezione)
        // -\
        stampaEAssertTimerGenerale(23.5, 7.0, mov);             //vel 4.0 (nel tratto costante)
        stampaEAssertTimerGenerale(25.5, 7.5, mov);             // //
        stampaEAssertTimerGenerale(27.5, 8.0, mov);             // //
    }

    @Test
    public void test7(){
        System.out.println("TEST 7: FINAL DESTINATION. Dovrebbe compredere tutti i casi strani e limite.\nLa velocità di nessun punto di tabA coincide con la velictà di un punto di tabF come i precedenti test");
        ElementoMovimento[] tabA = new ElementoMovimento[12];
        ElementoMovimento[] tabF = new ElementoMovimento[3];
        tabA[0] = new ElementoMovimento(1.0,0.0);
        tabA[1] = new ElementoMovimento(4.0,3.0);
        tabA[2] = new ElementoMovimento(5.0,5.0);
        tabA[3] = new ElementoMovimento(7.0,4.0);
        tabA[4] = new ElementoMovimento(8.0,4.0);
        tabA[5] = new ElementoMovimento(8.5,4.0);
        tabA[6] = new ElementoMovimento(9.0,4.0);
        tabA[7] = new ElementoMovimento(10.0,3.5);
        tabA[8] = new ElementoMovimento(10.5,2.0);
        tabA[9] = new ElementoMovimento(10.75,2.0);
        tabA[10] = new ElementoMovimento(11.0,2.0);
        tabA[11] = new ElementoMovimento(12.0,6.0);

        tabF[0] = new ElementoMovimento(15.0, 6.0);
        tabF[1] = new ElementoMovimento(16.0, 4.5);
        tabF[2] = new ElementoMovimento(17.5, 0.0);

        Movimento mov = new Movimento(tabA, tabF);
        //casi limite
        stampaEAssertTimerGenerale(-5.0, 0.0, mov);
        stampaEAssertTimerGenerale(0.0, 0.0, mov);
        stampaEAssertTimerGenerale(44.25, 11, mov);
        //regime per 1 secondo
        stampaEAssertTimerGenerale(50.25, 12.0, mov);               //vel 6   (regime)
        //casi classici /\
        stampaEAssertTimerGenerale(2.66666667, 2.0, mov);           //vel 2   (prima intersezione)
        stampaEAssertTimerGenerale(6.0, 3.0, mov);                  //vel 3   (prima intersezione)
        stampaEAssertTimerGenerale(10.6875, 3.75, mov);             //vel 4.5 (prima intersezione)
        stampaEAssertTimerGenerale(13.45833332, 4.0, mov);          //vel 5   (prima intersezione)
        //caso \\
        stampaEAssertTimerGenerale(16.625, 5.0, mov);               //vel 4.5 (seconda intersezione)
        //casi -\
        stampaEAssertTimerGenerale(20.166666667, 6.0, mov);         //vel 4.0 (seconda intersezione)
        stampaEAssertTimerGenerale(24.166666667, 7.0, mov);         //vel 4.0 (seconda intersezione)
        stampaEAssertTimerGenerale(26.166666667, 7.5, mov);         //vel 4.0 (seconda intersezione)
        stampaEAssertTimerGenerale(28.166666667, 8.0, mov);         //vel 4.0 (seconda intersezione)
        //caso \\
        stampaEAssertTimerGenerale(29.78125, 8.5, mov);             //vel 3.75 (seconda intersezione)
        stampaEAssertTimerGenerale(31.2916666667, 9.5, mov);        //vel 3.5 (seconda intersezione) //caso improbabilissimo. inf sol
        //stampaEAssertTimerGenerale(31.2916666667, 10.5, mov);                          //altra soluzione per stessa richiesta
        //casi -\
        stampaEAssertTimerGenerale(31.5416666667, 9.625, mov);      //vel 2.0
        stampaEAssertTimerGenerale(31.7916666667, 9.75, mov);
        stampaEAssertTimerGenerale(32.2916666667, 10.0, mov);
        //casi /\
        stampaEAssertTimerGenerale(35.791666667, 10.5, mov);        //vel 4.0 (terza intersezione)
        stampaEAssertTimerGenerale(37.03125, 10.625, mov);          //vel 4.5 (terza intersezione)
        stampaEAssertTimerGenerale(39.208333333, 10.75, mov);       //vel 5.0 (seconda intersezione)
        stampaEAssertTimerGenerale(41.61458333, 10.875, mov);       //vel 5.5
        stampaEAssertTimerGenerale(44.25, 11, mov);                 //cielo azzurro sopra Berlino
    }

    @Test
    public void test8(){
        System.out.println("TEST 7: ^ , valori tipici direzione avanti Turtebot 3");
        ElementoMovimento[] tabA = new ElementoMovimento[10];
        ElementoMovimento[] tabF = new ElementoMovimento[8];

        tabA[0] = new ElementoMovimento(3269.6670000000004, 0.0);//, 0.0);
        tabA[1] = new ElementoMovimento(3269.7000000000003, 0.026088122440483263);//, 4.304540202666926E-4);
        tabA[2] = new ElementoMovimento(3269.7340000000004, 0.053018268883903485);//, 0.0013448086525187477);
        tabA[3] = new ElementoMovimento(3269.7670000000003, 0.07915900755557756);//, 0.0021809250612449455);
        tabA[4] = new ElementoMovimento(3269.8, 0.105264023537738);//, 0.0030429800130306493);
        tabA[5] = new ElementoMovimento(3269.8340000000003, 0.1321084532165257);//, 0.004035332104835004);
        tabA[6] = new ElementoMovimento(3269.867, 0.15806887793320207);//, 0.004787925963956257);
        tabA[7] = new ElementoMovimento(3269.9, 0.1838589630640703);//, 0.005641809376438201);
        tabA[8] = new ElementoMovimento(3269.934, 0.21045083147173402);//, 0.006703266507129473);
        tabA[9] = new ElementoMovimento(3269.967, 0.22000333311546388);//, 0.007102493715667624);

        tabF[0] = new ElementoMovimento(3274.2000000000003, 0.22000333311546388);//, 0.0);
        tabF[1] = new ElementoMovimento(3274.2340000000004, 0.1863408435381083);//, 0.006907851003132162);
        tabF[2] = new ElementoMovimento(3274.2670000000003, 0.1537187318993417);//, 0.005610982994701224);
        tabF[3] = new ElementoMovimento(3274.3, 0.12112007708640521);//, 0.004534840348251326);
        tabF[4] = new ElementoMovimento(3274.3340000000003, 0.08757692699016711);//, 0.0035478490693127383);
        tabF[5] = new ElementoMovimento(3274.367, 0.05503779364052329);//, 0.002353142890399388);
        tabF[6] = new ElementoMovimento(3274.4, 0.022510648814048975);//, 0.0012795493004966337);
        tabF[7] = new ElementoMovimento(3274.434, 0.0);//, 3.8268102984002005E-4);

        Movimento avanti = new Movimento(tabA, tabF);
        stampaEAssertTimerGenerale(13, 59.11780482, avanti);    // a regime //
        stampaEAssertTimerGenerale(0, 0, avanti);
        //stampaEAssert(0.01, 0.117628, avanti);
        //0.1176 51 90852338492 nuovo
        //0.1176 51 59399919867 vecchio
        stampaEAssertSpazioFrenata(10, 0.024616896636133492, avanti);
    }

    @Test
    public void testSpazioFrenata(){
        System.out.println("TEST SPAZIO FRENATA");
        ElementoMovimento[] tabA = new ElementoMovimento[2];
        ElementoMovimento[] tabF = new ElementoMovimento[13];
        //accelerazione
        tabA[0]= new ElementoMovimento(00000000000L,0);//, 0);
        tabA[1]= new ElementoMovimento(10000000000L,12);//, 6);
        //frenata
        tabF[0]= new ElementoMovimento(0000000000L,12);//, 0);
        tabF[1]= new ElementoMovimento(1000000000L,11);//, 11.5);
        tabF[2]= new ElementoMovimento(2000000000L,10);//, 10.5);
        tabF[3]= new ElementoMovimento(3000000000L,9);//, 9.5);
        tabF[4]= new ElementoMovimento(4000000000L,8);//, 8.5);
        tabF[5]= new ElementoMovimento(5000000000L,7);//, 7.5);
        tabF[6]= new ElementoMovimento(6000000000L,6);//, 6.5);
        tabF[7]= new ElementoMovimento(7000000000L,5);//, 5.5);
        tabF[8]= new ElementoMovimento(8000000000L,4);//, 4.5);
        tabF[9]= new ElementoMovimento(9000000000L,3);//, 3.5);
        tabF[10]= new ElementoMovimento(10000000000L,2);//, 2.5);
        tabF[11]= new ElementoMovimento(11000000000L,1);//, 1.5);
        tabF[12]= new ElementoMovimento(12000000000L,0);//, 0.5);

        Movimento avanti = new Movimento(tabA, tabF);
        stampaEAssertSpazioFrenata(12, 72, avanti);
        stampaEAssertSpazioFrenata(11.3, 63.845, avanti);
        stampaEAssertSpazioFrenata(8.9, 39.605, avanti);
        stampaEAssertSpazioFrenata(9.3, 43.245, avanti);
        stampaEAssertSpazioFrenata(0.0001, 5E-9, avanti);
        stampaEAssertSpazioFrenata(0.000015, 0, avanti);
    }

    @Test
    public void testRadianti() {
        System.out.println("TEST RADIANTI: ^");
        ElementoMovimento[] tabA = new ElementoMovimento[4];
        ElementoMovimento[] tabF = new ElementoMovimento[3];
        tabA[0] = new ElementoMovimento(1000000000L, 0);//, 0);
        tabA[1] = new ElementoMovimento(2000000000L, 0.5);//, 0.25);
        tabA[2] = new ElementoMovimento(4000000000L, 0.6);//, 1.1);
        tabA[3] = new ElementoMovimento(5000000000L, 0.7);//, 0.65);

        tabF[0] = new ElementoMovimento(10000000000L, 0.7);//, 0);
        tabF[1] = new ElementoMovimento(12000000000L, 0.3);//, 1);
        tabF[2] = new ElementoMovimento(13000000000L, 0);//, 0.15);

        Movimento rotazione = new Movimento(tabA, tabF);
        stampaEAssertTimerGenerale(3.15, 4.0, rotazione);
        stampaEAssertTimerGenerale(2.175, 3.0, rotazione);
        stampaEAssertTimerGenerale(Math.PI/2, 2.1653698, rotazione);
        //stampaEAssert(Math.PI/2, 2.16537, rotazione); vecchio
        //2.165369 9874877925   vecchio
        //2.165369 785969326    nuovo
        //2.165369 8            calcolatrice algeo
        stampaEAssertTimerGenerale(0, 0, rotazione);
    }

    private void stampaAssertCondizioniSbagliate(ElementoMovimento[] tabA, ElementoMovimento[] tabF){
        DAOException daoException = null;
        Movimento movimento = null;
        try {
            movimento = new Movimento(tabA, tabF);
        } catch (DAOException e){
            daoException = e;
        }
        Assert.assertNull(movimento);
        Assert.assertNotNull(daoException);
        System.out.println(daoException.getMessage());
    }

    private void stampaEAssertTimerGenerale(double richiesti, double risultatoAtteso, Movimento movimento){
        double risultato = movimento.tempoPerTimerGenerale(richiesti);
        //System.out.println("SPAZIO RICHIESTO:"+richiesti+" secondi attesi:"+risultatoAtteso +" risultato:"+risultato);
        Assert.assertEquals(risultatoAtteso, risultato, precisione);
    }


    private void stampaEAssertSpazioFrenata(double velocità, double risultatoAtteso, Movimento movimento){
        //System.out.println("###########   SPAZIO FRENATA VELOCITÀ:"+ velocità+ "   ############");
        double risultato = movimento.ricercaSpazioFrenata(velocità);
        //System.out.println("Risultato atteso: "+ risultatoAtteso + " Risultato: "+risultato);
        Assert.assertEquals(risultatoAtteso, risultato, precisione);
    }

    //vecchio stampaEAssertTimerGenerale
    /*private void stampaEAssertTimerGenerale(){
        //System.out.println("#################################### RICHIESTI "+richiesti+" ######################################");
        //double risultato = tempiPerTimer.tempoPerTimerGenerale(richiesti, movimento);
        double risultato = tempiPerTimer.risultatoDaTabella(new ComandoVel(richiesti, ComandoVel.METRI, ComandoVel.AVANTI), movimento);
        //System.out.println("RISULTATO ATTESO:"+risultatoAtteso+" RISULTATO:"+risultato);
        Assert.assertEquals(risultatoAtteso, risultato, precisione);
    }*/
    //vecchio stampaEAssertSpazioFrenata
    /*private void stampaEAssertSpazioFrenata(double velocità, double risultatoAtteso, Movimento movimento){
        System.out.println("###########   SPAZIO FRENATA VELOCITÀ:"+ velocità+ "   ############");
        double risultato = tempiPerTimer.ricercaSpazioFrenata(movimento, velocità);
        System.out.println("Risultato atteso: "+ risultatoAtteso + " Risultato: "+risultato);
        Assert.assertEquals(risultatoAtteso, risultato, precisione);
    }*/
}
