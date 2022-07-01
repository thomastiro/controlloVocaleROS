package it.unibas.progetto.modello.utilita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unibas.progetto.persistenza.DAOException;

public final class Movimento {
    private final static String TAG = "movimento";
    public static final double VALORE_DA_CONSIDERARE_ZERO = 0.000001;// 0.00000015;

    private ElementoMovimento[] tabAcc;
    private ElementoMovimento[] tabFre;
    private Elemento[] tabellaCompleta;
    private double[] sommatoriaAccelerazione;
    private double[] sommatoriaFrenata;
    //ricavarli anche di volta in volta//
    private double secondiAccelerazione;
    private double secondiFrenata;
    private double velRegime;

    public Movimento(ElementoMovimento[] tabellaAccelerazione, ElementoMovimento[] tabellaFrenata) throws DAOException {
        controlloGenericoTab(tabellaAccelerazione, tabellaFrenata);
        this.tabellaCompleta = inizializzaTabCompleta(tabAcc, tabFre);
        this.velRegime = tabAcc[tabAcc.length-1].getVelocità();
        this.sommatoriaAccelerazione = spazioPercorsoAccelerazione(tabAcc);
        this.sommatoriaFrenata = spazioPercorsoFrenata(tabFre);
        this.secondiAccelerazione = tabAcc[tabAcc.length-1].getTempo() - tabAcc[0].getTempo();
        this.secondiFrenata = tabFre[tabFre.length-1].getTempo() - tabFre[0].getTempo();
        // Alcuni sono anche ricavabili di volta in volta. Ma non voglio.
        //stampaTab(tabAcc);
        //stampaTab(tabFre);
        //stampaElementi();
    }

    private void controlloGenericoTab(ElementoMovimento[] tabA, ElementoMovimento[] tabF){
        try{
            if(tabA.length < 2) throw new DAOException("Errore. Dimensioni tabA minori di 2");
            if(tabF.length < 2) throw new DAOException("Errore. Dimensioni tabF minori di 2");
            tabA = tabA.clone();
            tabF = tabF.clone();
            // nel try
            if(tabA[0].getTempo() < 0) throw new DAOException("Errore. Primo valore tabA tempo negativo");
            if(tabF[0].getTempo() < 0) throw new DAOException("Errore. Primo valore tabF tempo negativo");
            ElementoMovimento ultimoA = tabA[tabA.length-1];
            ElementoMovimento ultimoF = tabF[tabF.length-1];
            if(tabA[0].getVelocità() != 0) throw new DAOException("Errore. Velocità limite inferiore tabA diverse da 0");
            if(ultimoF.getVelocità() != 0) throw new DAOException("Errore. Velocità limite inferiore tabF diverse da 0");
            if(ultimoA.getVelocità() != tabF[0].getVelocità()) throw new DAOException("Errore. Velocità limite superiore diverse");
            if(ultimoA.getVelocità() == 0) throw new DAOException("Errore. Velocità limite superiore uguale a 0");
            boolean positive = true; //valido per entrambe le tabelle
            if(ultimoA.getVelocità() < 0){
                positive = false;
            }
            double tP = tabA[0].getTempo();
            for(int i = 1; i < tabA.length; i++){
                ElementoMovimento s = tabA[i];
                if(s.getTempo() <= tP) throw new DAOException("Errore tabella accelerazione. Valori tempo solo crescenti");
                if(positive){
                    if(s.getVelocità() > ultimoA.getVelocità() || s.getVelocità() < 0) throw new DAOException("Errore tabA. Velocità non comprese tra i due estremi.");
                } else {
                    if(s.getVelocità() < ultimoA.getVelocità() || s.getVelocità() > 0) throw new DAOException("Errore tabA. Velocità non comprese tra i due estremi.");
                    tabA[i] = new ElementoMovimento(s.getTempo(), Math.abs(s.getVelocità())); //se la rendo classe interna posso evitarlo//
                }
                tP = s.getTempo();
            }
            ElementoMovimento p = tabF[tabF.length-1];
            for(int i = tabF.length-2; i > -1; i--){
                ElementoMovimento a = tabF[i];
                if(a.getTempo() >= p.getTempo()) throw new DAOException("Errore tabella frenata. Valori tempo solo crescenti");
                if(positive){
                    if(a.getVelocità() <= p.getVelocità()) throw new DAOException("Errore tabella frenata. Valori velocità solo decrescenti");
                } else {
                    if(a.getVelocità() >= p.getVelocità()) throw new DAOException("Errore tabella frenata. Valori velocità solo crecrescenti");
                    tabF[i] = new ElementoMovimento(a.getTempo(), Math.abs(a.getVelocità())); //se la rendo classe interna posso evitarlo//
                }
                p = a;
            }
            this.tabAcc = tabA;
            this.tabFre = tabF;
        } catch (NullPointerException n){
            throw new DAOException("Errore. Valori nulli");
        }
    }

    private void stampaTab(ElementoMovimento[] tab){
        System.out.println("-----------------------");
        for(ElementoMovimento e:tab){
            System.out.println("tempo: "+ e.getTempo() + " velocità: "+ e.getVelocità());
        }
    }

    private void stampaElementi(){
        for(Elemento e: tabellaCompleta){
            String s = "metriT:" + e.metriTotali;
            s = s + "\tvel:" + e.velocità;
            s = s + "\ttA:" + e.tA;
            s = s + "\ttF:" + e.tF;
            if(e.coefficientiA != null){
                s = s + "\tmA:" + e.coefficientiA.getM() + "\tqA:"+ e.coefficientiA.getQ();
            }
            if(e.coefficientiF != null){
                s = s + "\tmF:" + e.coefficientiF.getM() + "\tqF:"+ e.coefficientiF.getQ();
            }
            s = s + "\tspazioA:" + e.metriA;
            s = s + "\tspazioF:" + e.metriF;
            //vel, ta, tf, cA, cF, metriA, metriF, metriT
            System.out.println(s);
        }
    }

    private double[] spazioPercorsoAccelerazione(ElementoMovimento[] tabAcc){
        double array[] = new double[tabAcc.length];
        array[0] = 0;
        for(int i = 0; i < tabAcc.length-1; i++){
            int iS = i+1;
            double traPuntoEPunto = Segmento.metriPercosi(tabAcc[i].getTempo(), tabAcc[i].getVelocità(), tabAcc[iS].getTempo(), tabAcc[iS].getVelocità());
            array[iS] = array[i] + traPuntoEPunto;
        }
        return array;
    }

    private double[] spazioPercorsoFrenata(ElementoMovimento[] tabFre){
        double array[] = new double[tabFre.length];
        array[tabFre.length-1] = 0;
        for(int i = tabFre.length-1; i > 0; i--){
            int iS = i-1;
            double traPuntoEPunto = Segmento.metriPercosi(tabFre[iS].getTempo(), tabFre[iS].getVelocità(), tabFre[i].getTempo(), tabFre[i].getVelocità());
            array[iS] = array[i] + traPuntoEPunto;
        }
        return array;
    }

    public int getDimensioneTabAccelerazione(){
        return tabAcc.length;
    }

    public int getDimensioneTabFrenata(){
        return tabFre.length;
    }

    public double getPercorsiTotaliAccelerazione() {
        return sommatoriaAccelerazione[sommatoriaAccelerazione.length-1];
    }

    public double getPercorsiTotaliFrenata() {
        return sommatoriaFrenata[0];
    }

    public double getVelRegime() {
        return velRegime;
    }

    public double getSecondiTotaliAccelerazione() {
        return secondiAccelerazione;
    }

    public double getSecondiTotaliFrenata() {
        return secondiFrenata;
    }

    /** viene garantito che la velocità in ogni elemento di tabF sia inveriore al successivo;
     *  inoltre è garantito che ogni elemento di tabellaCompleta ha la mA >= mF*/
    public double tempoPerTimerGenerale(double spazio){
        if(spazio > 0){
            int min = 0;
            int max = tabellaCompleta.length-1;
            Elemento eleMax = tabellaCompleta[max];
            if(spazio >= eleMax.metriTotali){
                spazio = spazio - eleMax.metriTotali;
                return spazio/eleMax.velocità + secondiAccelerazione;//+ eleMax.tA- tabellaCompleta[min].tA;
            }
            while(min < max){
                int med = (max + min)/2;
                if(tabellaCompleta[med].metriTotali > spazio){
                    max = med;
                } else if(tabellaCompleta[med+1].metriTotali < spazio){
                    min = med;
                } else {
                    Elemento pre = tabellaCompleta[med];
                    Elemento suc = tabellaCompleta[med+1];
                    if(suc.coefficientiA.getM() > 0){ //caso normale: dove l'accelerazione risulta maggiore di 0
                        double spazioR = spazio - pre.metriTotali;
                        double a1 = suc.coefficientiA.getM();
                        double a2 = suc.coefficientiF.getM();
                        double vo = pre.velocità;
                        double v = Math.sqrt(((2*spazioR*a1*a2)/(a2-a1)) + vo*vo);
                        return Segmento.xDaEquazioneRetta(v, suc.coefficientiA) - tabellaCompleta[0].tA;
                    } else if(suc.coefficientiA.getM() == 0){ //caso strano (improbabile): dove accelerazione risulta uguale a 0.
                        double spazioR = spazio - pre.metriTotali;
                        return spazioR/suc.velocità + pre.tA - tabellaCompleta[0].tA;
                    } else { //caso ancora più strano (molto improbabile nella realtà): dove accelerazione è negativa.
                        double a1 = suc.coefficientiA.getM();
                        double a2 = suc.coefficientiF.getM();
                        if(a1 == a2){
                            return pre.tA - tabellaCompleta[0].tA; //limite dove sono addirittura uguali. (infinite soluzioni ==> tutti i possibili valori di tempo compresi nell'intervallo)
                        }
                        double spazioR = spazio - (pre.metriA + suc.metriF);
                        double vPre = pre.velocità;
                        double vSuc = suc.velocità;
                        double v = Math.sqrt((a1*(2*spazioR*a2 - vSuc*vSuc) + vPre*vPre*a2)/(a2 - a1));
                        return Segmento.xDaEquazioneRetta(v, suc.coefficientiA) - tabellaCompleta[0].tA;
                    }
                }
            }
        }
        return 0;
    }

    /** è garantito che i valori delle velocità presenti nella fase di frenata utilizzata sono decrescenti */
    public double ricercaSpazioFrenata(double velocita){
        if(velocita > velRegime - 0.002) return getPercorsiTotaliFrenata(); //oppure trovarlo sulla funzione associata alla velocità più alta
        if(velocita > VALORE_DA_CONSIDERARE_ZERO){
            int iMin = 0;
            int iMax = tabFre.length-1;
            while(iMin <= iMax){
                int iMed = (iMax+iMin)/2;
                if(tabFre[iMed].getVelocità() < velocita){
                    iMax = iMed;
                } else if (tabFre[iMed+1].getVelocità() > velocita){
                    iMin = iMed;
                } else {
                    //dalla formula V^2 = Vo^2 + 2*acc*spazio , dove acc = (v2-v1)/(t2-t2)
                    // ==> spazio = (V^2 - Vo^2)/2*acc , ora per evitare un ulteriore divisione
                    // spazio = (V^2 - Vo^2)*0.5*acc^-1
                    int iMedPiù1 = iMed+1;
                    double v2 = tabFre[iMedPiù1].getVelocità();
                    double t2 = tabFre[iMedPiù1].getTempo();
                    double unoSuAcc = (t2 - tabFre[iMed].getTempo()) / (v2 - tabFre[iMed].getVelocità());
                    double spazioParziale = (v2*v2 - velocita*velocita)*0.5*unoSuAcc;
                    return sommatoriaFrenata[iMedPiù1] + spazioParziale;
                }
            }
        }
        return 0;
    }

    private Elemento[] inizializzaTabCompleta(ElementoMovimento[] tabA, ElementoMovimento[] tabF){
        // in questo metodo do per scontato che l'equazioni alla retta (funzioni del tipo y = mx + q) tra i vari punti presenti
        // nella tabella di frenata (tabF) hanno tutte come valore del coefficiente angolare m, un valore negativo.
        // E per ora: che non vi sia una funzione tra due punti nella tabella di accelarazione (tabA) la cui corrispettiva in tabF
        // abbia un coefficiente m maggiore.
        int i = 1;
        int j = tabF.length-2;
        //boolean casoParticolare = false;
        ElementoMovimento elePA = tabA[0];
        ElementoMovimento eleA = tabA[i];
        ElementoMovimento elePF = tabF[tabF.length-1];
        ElementoMovimento eleF = tabF[j];
        CoefficientiRetta cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
        CoefficientiRetta cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
        ArrayList<Elemento> lista = new ArrayList<>();
        lista.add(new Elemento(0, elePA.getTempo(), elePF.getTempo(), null, null, 0, 0, 0));
        ////
        while(i < tabA.length && j >= 0){
            Elemento elePrecedente = lista.get(lista.size()-1);
            if(cA.getM() > 0){
                lista.add(creaElementoEleA_m_positiva(eleA, eleF, elePrecedente, cA, cF));
                if(eleA.getVelocità() < eleF.getVelocità()){
                    i++;
                    elePA = eleA;
                    eleA = tabA[i];
                    cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
                } else if(eleA.getVelocità() > eleF.getVelocità()){
                    j--;
                    elePF = eleF;
                    eleF = tabF[j];
                    cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                } else {
                    //casoParticolare = true;
                    i++;
                    if(i == tabA.length){
                        break; // avasta accussì. S no, t pui fa mal.
                    }
                    elePA = eleA;
                    eleA = tabA[i];
                    cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
                    if(cA.getM() > 0){
                        j--;
                        elePF = eleF;
                        eleF = tabF[j];
                        cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                    } else if(cA.getM() < 0) {
                        j++;
                        eleF = elePF;
                        elePF = tabF[j+1];
                        cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                    }
                }
            } else if(cA.getM() < 0){ //attenzione a elePF
                lista.add(creaElementoEleA_m_negativa(eleA, elePF, elePrecedente, cA, cF));
                if(eleA.getVelocità() > elePF.getVelocità()){
                    i++;
                    elePA = eleA;
                    eleA = tabA[i];
                    cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
                } else if(eleA.getVelocità() < elePF.getVelocità()){
                    j++; //sembra strano, ma è così.
                    eleF = elePF;
                    elePF = tabF[j+1];
                    cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                } else {
                    //casoParticolare = true;
                    i++;
                    elePA = eleA;
                    eleA = tabA[i];
                    cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
                    if(cA.getM() > 0){
                        j--;
                        elePF = eleF;
                        eleF = tabF[j];
                        cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                    } else if(cA.getM() < 0) {
                        j++;
                        eleF = elePF;
                        elePF = tabF[j+1];
                        cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                    }
                }
            } else { //nel caso m == 0
                //cA.q corrisponde, come previsto, alla velocità dell'elemento precedente. VERIFICATO cA.getQ() == elePrecedente.velocità
                double metriA = elePrecedente.metriA + elePrecedente.velocità*(eleA.getTempo()-elePA.getTempo());//q corrisponte a elePrecedente.velocità.
                lista.add(new Elemento(elePrecedente.velocità, eleA.getTempo(), elePrecedente.tF, cA, cF, metriA, elePrecedente.metriF, metriA+elePrecedente.metriF));
                i++;
                elePA = eleA;
                eleA = tabA[i];
                cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
                if(elePA.getVelocità() == eleF.getVelocità() && cA.getM() > 0){
                    j--;
                    elePF = eleF;
                    eleF = tabF[j];
                    cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                }
            }
            /*
            if(casoParticolare){
                i++;
                if(i == tabA.length){
                    break; // avasta accussi. S no, t fach mal.
                }
                elePA = eleA;
                eleA = tabA[i];
                cA = new CoefficientiRetta(elePA.getTempo(), elePA.getVelocità(), eleA.getTempo(), eleA.getVelocità());
                if(cA.getM() > 0){
                    j--;
                    elePF = eleF;
                    eleF = tabF[j];
                    cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                } else if(cA.getM() < 0) {
                    j++;
                    eleF = elePF;
                    elePF = tabF[j+1];
                    cF = new CoefficientiRetta(eleF.getTempo(), eleF.getVelocità(), elePF.getTempo(), elePF.getVelocità());
                }
                casoParticolare = false;
            }*/
        }
        Elemento[] risultato = lista.toArray(new Elemento[0]);
        controlloArrayMovimento(risultato);
        return risultato;
    }

    private void controlloArrayMovimento(Elemento[] tabellaCompleta){
        Elemento p = tabellaCompleta[0];
        for(int i = 1; i < tabellaCompleta.length; i++){
            Elemento a = tabellaCompleta[i];
            if(p.metriTotali > a.metriTotali) throw new DAOException("Errore su Elemento[]. ( esiste mA(v) < mF(v)) --> non si può usare ricerca binaria");
            p = a;
        }
    }

    private Elemento creaElementoEleA_m_positiva(ElementoMovimento eleA, ElementoMovimento eleF, Elemento elePrecedente, CoefficientiRetta cA, CoefficientiRetta cF){
        //do per scontato che la valecità inferiorie sia uguale//
        double velRiferimento = eleA.getVelocità();
        double tA = eleA.getTempo();
        double tF = eleF.getTempo();
        if(eleA.getVelocità() < eleF.getVelocità()){
            //velRiferimento = eleA.getVelocità();
            //tA = eleA.getTempo();
            tF = Segmento.xDaEquazioneRetta(velRiferimento, cF);
        }else if(eleA.getVelocità() > eleF.getVelocità()){
            velRiferimento = eleF.getVelocità();
            tA = Segmento.xDaEquazioneRetta(velRiferimento, cA);
            tF = eleF.getTempo();
        }
        double metriA = Segmento.metriPercosi(elePrecedente.tA, elePrecedente.velocità, tA, velRiferimento);
        double metriF = Segmento.metriPercosi(tF, velRiferimento, elePrecedente.tF, elePrecedente.velocità);
        metriA = metriA + elePrecedente.metriA;
        metriF = metriF + elePrecedente.metriF;
        return new Elemento(velRiferimento, tA, tF, cA, cF, metriA, metriF, metriA + metriF);
    }

    private Elemento creaElementoEleA_m_negativa(ElementoMovimento eleA, ElementoMovimento eleF, Elemento elePrecedente, CoefficientiRetta cA, CoefficientiRetta cF){
        double velRiferimento = eleA.getVelocità();
        double tA = eleA.getTempo();
        double tF = eleF.getTempo();//segmento.xDaEquazioneRetta(velRiferimento, cF);
        if(eleA.getVelocità() > eleF.getVelocità()){
            //velRiferimento = eleA.getVelocità();
            //tA = eleA.getTempo();
            tF = Segmento.xDaEquazioneRetta(velRiferimento, cF);
        } else if(eleA.getVelocità() < eleF.getVelocità()){
            velRiferimento = eleF.getVelocità();
            tA = Segmento.xDaEquazioneRetta(velRiferimento, cA);
            tF = eleF.getTempo();
        }
        double metriA = Segmento.metriPercosi(elePrecedente.tA, elePrecedente.velocità, tA, velRiferimento);
        double metriF = Segmento.metriPercosi(elePrecedente.tF, elePrecedente.velocità, tF, velRiferimento);
        metriA = metriA + elePrecedente.metriA;
        metriF = elePrecedente.metriF - metriF;
        return new Elemento(velRiferimento, tA, tF, cA, cF, metriA, metriF, metriA + metriF);
    }

    /*
    //NON SONO PIÙ NECESSARI PERCHÉ LE PRESTAZIONI SONO 'DECISAMENTE' MIGLIORATE
    private Map<Double, Double> mappaRisultati = new HashMap<>();

    private Double getValoreCalcolato(Double valoreRichiesto){
        return this.mappaRisultati.get(valoreRichiesto);
    }

    private void salvaRisultato(Double valoreRichiesto, Double risultato){
        this.mappaRisultati.put(valoreRichiesto,risultato);
    }
    */

    private class Elemento {
        private double velocità;
        private double tA;
        private double tF;
        private CoefficientiRetta coefficientiA;
        private CoefficientiRetta coefficientiF;
        private double metriA;
        private double metriF;
        private double metriTotali;
        public Elemento(double velocità, double tA, double tF, CoefficientiRetta coefficientiA, CoefficientiRetta coefficientiF, double metriA, double metriF, double metriTotali) {
            this.velocità = velocità;
            this.tA = tA;
            this.tF = tF;
            this.coefficientiA = coefficientiA;
            this.coefficientiF = coefficientiF;
            this.metriA = metriA;
            this.metriF = metriF;
            this.metriTotali = metriTotali;
        }
    }
}
