package it.unibas.progetto.modello.utilita;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import it.unibas.progetto.persistenza.DAOException;
import nav_msgs.Odometry;

public final class TempiPerTimer {
    private final String TAG = TempiPerTimer.class.getName();
    //private final int MAX_VEL_NEGATIVE_CONSECUTIVE = 5;
    //private volatile boolean lock = false;
                                //-1.2251969058696682E-6    0.000001
                                //2.2007121707693565E-6     0.0000022
                                //8.831923823426987E-6      0.000008
                                //8.695695739306786E-6      0.0000086 A f
                                //9.799363657126204E-6      0.000009 A p

                                //1.087003846179642E-5      0.0000108
                                //1.738148082388565E-5      0.000017

                                //4.55120776011873E-5       0.000045
                                //5.3808179573548655E-5     0.0000538

    public static final double VALORE_DA_CONSIDERARE_ZERO = 0.00002; // velocità in valore assoluto inferiori sono sostituite con 0
    public static final double DELTA_VELOCITA = 0.0002; //0.0001
    public static final int MAX_VEL_UGUALI_CONSECUTIVE = 6;

    private Movimento avanti;
    private Movimento indietro;
    private Movimento destra;
    private Movimento sinistra;

    public boolean creazioneMovimentoAvanti(List<Odometry> listaValoriOdom){
        this.avanti = creaMovimentoSicuro(listaValoriOdom, true);
        return avanti != null;
    }

    public boolean creazioneMovimentoIndietro(List<Odometry> listaValoriOdom){
        this.indietro = creaMovimentoSicuro(listaValoriOdom, true);
        return indietro != null;
    }

    public boolean creazioneMovimentoDestra(List<Odometry> listaValoriOdom){
        this.destra = creaMovimentoSicuro(listaValoriOdom, false);
        return destra != null;
    }

    public boolean creazioneMovimentoSinistra(List<Odometry> listaValoriOdom){
        this.sinistra = creaMovimentoSicuro(listaValoriOdom, false);
        return sinistra != null;
    }

    public Movimento getAvanti() {
        return avanti;
    }

    public Movimento getIndietro() {
        return indietro;
    }

    public Movimento getDestra() {
        return destra;
    }

    public Movimento getSinistra() {
        return sinistra;
    }

    private Movimento creaMovimentoSicuro(List<Odometry> listaValoriOdom, boolean isVelLineare){
        try{
            Log.e(TAG,"------------------");
            for(Odometry i:listaValoriOdom){
                String s = "id: "+i.getHeader().getSeq()+ " tempo: "+ i.getHeader().getStamp().totalNsecs();
                if(isVelLineare){
                    s = s  +" velL:"+i.getTwist().getTwist().getLinear().getX();
                }else{
                    s = s  + " velA:"+i.getTwist().getTwist().getAngular().getZ();
                }
                Log.e(TAG,s);
            }
            Log.e(TAG,"------------------");
            return inizializzaMovimento(listaValoriOdom, isVelLineare);
        } catch (DAOException | NullPointerException e){
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /** public perché usato per i test*/
    public Movimento inizializzaMovimento(List<Odometry> lista, final boolean isVelLineare) throws DAOException{
        List<ElementoMovimento> tabA = new ArrayList<>();
        List<ElementoMovimento> tabF = new ArrayList<>();
        List<ElementoMovimento> tabRegime = new ArrayList<>(); //solo per trovare la media ponderata delle velocità a regime
        int fase = 1;
        Odometry prec = lista.get(0);
        int seqP = prec.getHeader().getSeq();
        long tempP = prec.getHeader().getStamp().totalNsecs();
        double velP ;
        if(isVelLineare){
            velP = prec.getTwist().getTwist().getLinear().getX();
        } else {
            velP = prec.getTwist().getTwist().getAngular().getZ();
        }
        for(int i = 1; i < lista.size(); i++){
            Odometry corrente = lista.get(i);
            int seqC = corrente.getHeader().getSeq();
            long tempC = corrente.getHeader().getStamp().totalNsecs();
            double velC;
            if(isVelLineare){
                velC = corrente.getTwist().getTwist().getLinear().getX();
            } else {
                velC = corrente.getTwist().getTwist().getAngular().getZ();
            }
            //System.out.println("seqP:"+seqP+ "  seqC:"+ seqC + "  velP:"+ velP + "  velC:"+ velC);
            if(fase == 1){       //trova la prima variazione
                //System.out.println("fase 1");
                if(Math.abs(velC) > VALORE_DA_CONSIDERARE_ZERO && Math.abs(velP) <= VALORE_DA_CONSIDERARE_ZERO && (seqC - seqP) == 1){
                    tabA.clear();
                    tabA.add(new ElementoMovimento(tempP, 0));
                    tabA.add(new ElementoMovimento(tempC, velC));
                    fase = 2;
                }
            } else if(fase == 2){//aggiungo elementi finché non
                //System.out.println("fase 2");
                if((seqC - seqP) == 1){
                    ElementoMovimento n = new ElementoMovimento(tempC, velC);
                    tabA.add(n);
                    double possibileVelRegime = velP;
                    if(!tabRegime.isEmpty()){
                        possibileVelRegime = tabRegime.get(0).getVelocità();
                    }
                    if(Math.abs(velC - possibileVelRegime) < DELTA_VELOCITA){
                        if(tabRegime.isEmpty()){
                            tabRegime.add(tabA.get(tabA.size()-2)); //(new ElementoMovimento(tempP, velP));
                        }
                        tabRegime.add(n);
                        if(tabRegime.size() > MAX_VEL_UGUALI_CONSECUTIVE){
                            int j = tabA.size() - MAX_VEL_UGUALI_CONSECUTIVE;
                            tabA = tabA.subList(0, j);
                            fase = 3;
                        }
                    } else {
                        tabRegime.clear();
                    }
                } else {
                    fase = 1;
                }
            } else if(fase == 3){ // in questa fase non controllo seq//
                //System.out.println("fase 3");
                if(Math.abs(velC - tabRegime.get(0).getVelocità()) < DELTA_VELOCITA){
                    tabRegime.add(new ElementoMovimento(tempC, velC));
                } else if((seqC - seqP) == 1){
                    double mediaPon = mediaPonderataVel(tabRegime);
                    if(Math.abs(mediaPon) > Math.abs(velC)){
                        ElementoMovimento sostituire = tabA.get(tabA.size() - 1);
                        tabA.remove(tabA.size() - 1);
                        tabA.add(new ElementoMovimento(sostituire.getTempo(), mediaPon));
                        tabF.clear();
                        if(Math.abs(velC) <= VALORE_DA_CONSIDERARE_ZERO){
                            //System.out.println("fase 4 - caso particolare");
                            tabF.add(new ElementoMovimento(tempP, mediaPon));
                            tabF.add(new ElementoMovimento(tempC, 0));
                            return new Movimento(tabA.toArray(new ElementoMovimento[0]), tabF.toArray(new ElementoMovimento[0]));
                        }
                        tabF.add(new ElementoMovimento(tempP, mediaPon));
                        tabF.add(new ElementoMovimento(tempC, velC));
                        velC = mediaPon;
                        fase = 4;
                    } else {
                        fase = 1;
                    }
                } else {
                    fase = 1;
                }
            } else {
                //System.out.println("fase 4");
                if((seqC - seqP) == 1 && Math.abs(velP) > Math.abs(velC)){
                    if(Math.abs(velC) <= VALORE_DA_CONSIDERARE_ZERO){
                        tabF.add(new ElementoMovimento(tempC, 0));
                        return new Movimento(tabA.toArray(new ElementoMovimento[0]), tabF.toArray(new ElementoMovimento[0]));
                    }
                    tabF.add(new ElementoMovimento(tempC, velC));
                } else {
                    fase = 1;
                }
            }
            seqP = seqC;
            tempP = tempC;
            velP = velC;
        }
        return null;
    }

    private double mediaPonderataVel(List<ElementoMovimento> lista){
        double metriTotali = 0;
        ElementoMovimento p = lista.get(0);
        for(int i = 1; i < lista.size(); i++){
            ElementoMovimento a = lista.get(i);
            metriTotali = metriTotali + Segmento.metriPercosi(p.getTempo(), p.getVelocità(), a.getTempo(), a.getVelocità());
            p = a;
        }
        return metriTotali/(p.getTempo() - lista.get(0).getTempo());
    }

    public boolean èPresenteUnMovimento(){
        return avanti != null || indietro != null || destra != null || sinistra != null;
    }

    public double getSpazioFrenataLineare(double velocita) {
        //non comprimere le condizioni. Attenzione.
        if(velocita > 0){
            if(avanti != null) return avanti.ricercaSpazioFrenata(velocita);
        } else {
            if(indietro != null) return indietro.ricercaSpazioFrenata(Math.abs(velocita));
        }
        return 0;
    }

    // attualmente inutile
    public double getSpazioFrenataAngolare(double velocità){
        if(velocità > 0){
            if(sinistra != null){
                return sinistra.ricercaSpazioFrenata(velocità);
            }
        } else {
            if(destra != null){
                return destra.ricercaSpazioFrenata(-velocità);
            }
        }
        return 0;
    }
}
