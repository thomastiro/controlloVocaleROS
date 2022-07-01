package it.unibas.progetto.modello.utilita;


import android.util.Log;

import org.ros.message.MessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.modello.NodoComandoVocale;
import it.unibas.progetto.modello.comando_vocale.ComandoVel;
import nav_msgs.Odometry;

public class CoseVecchieGenerale {
    //COSTANTI
    //regex VECCHI usati precedentemente!
    /*
    private String ordinaFraseCoordinate(String frase){
        //StringUtils.remove(); //potrebbe essere utile
        String regexDoubleConCoordinataX="(( x | x| ics | ics)( meno |-| meno)?[0-9]+[.][0-9]+)|"+
                                         "(( x | x| ics | ics)( meno |-| meno)?[0-9]+[,][0-9]+)|"+
                                         "(( x | x| ics | ics)( meno |-| meno)?[0-9]+)";
        String regexDoubleConCoordinataY="(( y | y| Y | Y)( meno |-| meno)?[0-9]+[.][0-9]+)|"+
                                         "(( y | y| Y | Y)( meno |-| meno)?[0-9]+[,][0-9]+)|"+
                                         "(( y | y| Y | Y)( meno |-| meno)?[0-9]+)";
        String regexDoubleOrientamento= "(( orientamento | orientamento| omega | omega)( meno |-| meno)?[0-9]+[.][0-9]+)|"+
                                        "(( orientamento | orientamento| omega | omega)( meno |-| meno)?[0-9]+[,][0-9]+)|"+
                                        "(( orientamento | orientamento| omega | omega)( meno |-| meno)?[0-9]+)";
        return frase;
    }
    */
    /*
     public static final String REGEX_DOUBLE_FORMA_IDEALE = "((-)?[0-9]+[.]?([0-9]+)?)";                                         //ideale, ma deve avere una forma perfetta
    //public static final String REGEX_DOUBLE_SOLO_POSITIVI = "([0-9]+[.,]?([0-9]+)?)";
    //oppure "([0-9]+[.][0-9]+)|"+"([0-9]+[,][0-9]+)|"+"([0-9]+)"
    public static final String REGEX_DOUBLE_COMPLETO = "(( )?|-|meno( )?)?[0-9]+[.,]?([0-9]+)?)";                               //italiano//generico usato nel caso non sia stata trovata una coordinata precisa una  oppure "(( meno |-| meno)?[0-9]+[.][0-9]+)|" + "(( meno |-| meno)?[0-9]+[,][0-9]+)|" + "(( meno |-| meno)?[0-9]+)";//questo va bene
    public static final String REGEX_DOUBLE_PARZIALE = ")?[0-9]+[.,]?([0-9]+)?)";
    public static final String REGEX_DOUBLE_COMPLETO_X = "(( X| x| ics| ICS)( )?( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)";     //per trovare la coordinata
    public static final String REGEX_DOUBLE_COMPLETO_Y = "(( Y| y| ypsilon)( )?( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)";     //per trovare la coordinata y
    public static final String REGEX_DOUBLE_COMPLETO_ORIENTAMENTO = "(( orientamento| omega)( )?( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)";//per trovare la coordinata orientamento
    //nel caso in cui si vogliano aggiungere altre coordinate inserire il rispettivo regex qui. Non inserire un regex
    public static final String[] REGEX_DOUBLE_COMPLETO_XYW = {REGEX_DOUBLE_COMPLETO_X,
                                                              REGEX_DOUBLE_COMPLETO_Y,
                                                              REGEX_DOUBLE_COMPLETO_ORIENTAMENTO};
    */
     /* Da costanti
        String REGEX_DOUBLE_COMPLETO_X=           "(( X| x| ics| ICS)( )?( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)";    //per trovare la coordinata x
        String REGEX_DOUBLE_COMPLETO_Y=           "(( Y| y| ypsilon)( )?( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)";//per trovare la coordinata y
        String REGEX_DOUBLE_COMPLETO_ORIENTAMENTO="(( orientamento| omega)( )?( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)";//per trovare la coordinata orientamento
                        " (x|ics)( )?((meno|-)( )?)?[0-9]+[.,]?([0-9]+)?"//definitivo con upperCase
                   //   " (X|x|ics|ICS)( )?((meno|-)( )?)?[0-9]+[.,]?([0-9]+)?"//definitivo prima di UpperCase
                   //comando+" ((per|di) )?((meno|-)( )?)?[0-9]+[.,]?([0-9]+)?//definitivo?
                   //comando+" ((per|di) )?((meno|-)( )?)?([0-9]+[.,]?([0-9]+)?)( (secondi|s) )   //definitivo?
        */
    //_____________________________ COMANDO VOCALE _______________________________________________
    /////////////////// REGEX DINAMICI LINGUA ///////////////////////////
    //algoritmo di tipo greedy, prende il primo risultato che possa essere usato e lo converte e ritorna true,
    /*
    public int provaPerSwitch (ArrayList<String> frasi){
        if(frasi == null || frasi.isEmpty()){
            Log.e(TAG, "errore conversione messaggio riferimenti nulli o vuoti");
            return NESSUNO_RISULTATO;
        }
        ArrayList<String> frasiLowerCase = new ArrayList<>();  //evito di inserire nei regex i caratteri maiuscoli!!! prestazioni>>>
        for(String s: frasi){
            Log.e(TAG, "frase originale: "+ s);
            s = s.replaceAll("'"," ");
            frasiLowerCase.add(s.toLowerCase()); //da controllare questione lingua LOCALE paesi strani.
        }
        //controllo se esiste una frase che corrisponde al salvataggio della attuale posizione
        for(String frase: frasiLowerCase){
            Log.e(TAG, "elemento lower case: "+ frase);
            if(trovaFrasiSalvaPosizione(frase)){
                return FRASE_SALVA_POSIZIONE;
            }
        }
        //controllo se esiste una frase di riposizionamento del robot
        for(String frase:frasiLowerCase){
            if(trovaEConvertiFraseRiposiziona(frase)){
                return FRASE_RIPOSIZIONA_RICONOSCIUTA;
            }
        }
        //controllo se esiste una frase relativa alla posizione riconosciuta
        for(String frase:frasiLowerCase){
            if(trovaEConvertiFraseInPosizioneObbiettivo(frase)){
                return FRASE_POSIZIONE_RICONOSCIUTA;
            }
        }
        //controllo, se non esiste nessuna frase riconosciuta, una parola riconosciuta in queste frasi //es. avanti, avanti per 10.2m, gira a destra di 90°
        for(String frase:frasiLowerCase){
            if(trovaParolaInFrase(frase)){
                return COMANDO_VELOCITA_RICOSCIUTA;
            }
        }
        return NESSUNO_RISULTATO;
    }
    // DA CANCELLARE QUESTO SCHIFO
    private boolean trovaEConvertiFraseRiposiziona(String frase){
        if(sinonimiFrasiRiposiziona(frase)){
            Double[] valoriTrovati = convertitoreFraseInPosizione(frase);
            return salvaTargetPosizioneMomentaneo(valoriTrovati,true);
        }
        return false;
    }

    private boolean salvaTargetPosizioneMomentaneo(Double[] valoriXYW, boolean salvarloComunque){
        if(valoriXYW[0] != null && valoriXYW[1] != null){
            Double orientamento = valoriXYW[2];
            if(orientamento == null){
                orientamento = 0.0;
            }
            Applicazione.getInstance().getModello().putBean(Costanti.TARGET_MOMENTANEO, new Target(orientamento,valoriXYW[0],valoriXYW[1],null));
            return true;
        } else if(salvarloComunque){
            Log.e(TAG, "posizione salvata comunque anche se null");
            Applicazione.getInstance().getModello().putBean(Costanti.TARGET_MOMENTANEO,null);
            return true;
        }
        Log.e(TAG, "posizione impossibile ricavare. mancano coordinate necessarie");
        return false;
    }

    */
    /*
    private String regexMenoDinamico;                   //="(("+l.getMeno()+"|-)( )?)?";                                         //"((meno|-)( )?)?"
    private String regexPartePreposizioniDinamico;      //=" (("+l.getPropPer()+"|"+l.getPropDi()+") )?";            //" ((per|di) )?"
    private String regexDoubleCompletoDinamico;         //= regexMenoDinamico +Costanti.REGEX_DOUBLE_SOLO_POSITIVI;  //"((meno|-)( )?)?[0-9]+[.,]?([0-9]+)?"
    private String globaliS;
    private String globaliM;
    private String globaliG;
    private String rexChiaviVai;                        //=getStringOrForRegex(l.getParoleChiaveVai());
    private String rexPrepo;                            //=getStringOrForRegex(l.getLocLuogo());
    private String[] regexDoubleCompletoXYWDinamico;    //=getRegexDoubleCompletoXYWDinamico();
    private String regexSalvaPosizione;                 //="("+getStringOrForRegex(l.getParoleChiaveSalva())+") "+"("+l.getLa()+" )?"+"("+l.getPosizione1()+"|"+l.getPosizione2()+") "+l.getCome()+" "+Costanti.REGEX_FORMATO_NOME_SALVA_POSIZIONE;
    private String regexPrimaParteVaiInPosizioneSalvata;//=" ("+rexChiaviVai+") ("+rexPrepo+") ("+l.getPosizione1()+" )?(";
    */
    /*comando+" ((per|di) )?((meno|-)( )?)?([0-9]+[.,]?([0-9]+)?)( (secondi|s) )//per sicurezza
      " (X|x|ics|ICS)( )?((meno|-)( )?)?[0-9]+[.,]?([0-9]+)?"//definitivo?      //definitivo
      comando+" ((per|di) )?((meno|-)( )?)?[0-9]+[.,]?([0-9]+)?( (secondi|s) )  //definitivo
    */
    /* vecchio miglioooorati di parecchio.
    String[] regexMetriSecondi ={
        comando+" (per|per |di|di )?"+"(( meno |-| -| meno)?[0-9]+[.,]?([0-9]+)?)"+"( secondi | s )"
        comando+" (per|per |di|di )?"+Costanti.REGEX_DOUBLE_COMPLETO+"( secondi | s )",  //int. tutti i valori
        comando+" (per|per |di|di )?"+Costanti.REGEX_DOUBLE_COMPLETO+"( metri | m )",
        comando+" (per|per |di|di )?"+Costanti.REGEX_DOUBLE_COMPLETO+"( gradi | ° )"
    };
    //nuovo regex(fatto perlopiù per gli inglesi)
    // "("+regexMetriSecondiDinamico[2]+"|"+"( "+regexDoubleCompletoDinamico+" "+comando+" )"
    //angle of -90.5 degrees|90 degree angle
    */
    /*
    String[] regexMetriSecondiDinamicoVecchio = {
        comando + regexPartePreposizioniDinamico + regexDoubleCompletoDinamico +"( "+l.getSecondi2()+" |( )?s )",
        comando + regexPartePreposizioniDinamico + regexDoubleCompletoDinamico +"( "+l.getMetri2()+" |( )?m )",
        comando + regexPartePreposizioniDinamico + regexDoubleCompletoDinamico +"( "+l.getGradi2()+" |( )?° )"
    };*/
    // -----------------------------SOTTOSCRITTORE AMCL VECCHIO ----------------------------------//
    /*
        subscriber_amcl_pose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>(){
            Pose poseIniziale = null;
            double velPX; //da eliminare in prova
            Comando comando = comando;
            Double alfa;
            Double valore = comando.getValoreDefault(); //CAMBIATO PER PROVA IN DEFAULT-->

            double distanzaPrecedente = 10000000;  //da cambiare in null!
            double xIniziale;
            double yIniziale;
            double xFinale;
            double yFinale;

            Double sinDestra;       //variabile che assumerà il valore di x o il valore sin destro finali
            Double sinSinistra;     // ... valore di y o valore sin sinistro finali
            Double sinDestraR;      //sin destro riduzione
            Double sinSinitraR;     //sin sinistro riduzione

            boolean isLimitata = false;
            boolean staCorreggengo = false;
            int tentativi = 0;    //se i tentativi sono "relativamente" troppi si ferma
            int tentativiT = 0;
            @Override
            public void onNewMessage(PoseWithCovarianceStamped messaggio) {
                Log.e(TAG,"-------------------------------------------------");
                //set vista_informazioni_vecchio iniziali e finali
                if(poseIniziale == null){
                    valore = comando.getValoreDefault(); //CAMBIATO PER PROVA IN DEFAULT-->
                    tentativi = 0;
                    isLimitata = false;
                    siÈFermato = false; //in prova.
                    poseIniziale = messaggio.getPose().getPose();
                    //quaternione rappresentato in forma polare w + v e ricordo che può effettuare la rotazione solo sul asse z;
                    //alfa= getRadiantiQuaternioneAMCL(.getW(), .getZ());
                    alfa = getRadiantiQuaternioneAMCL(poseIniziale.getOrientation().getZ());
                    xIniziale = poseIniziale.getPosition().getX();
                    yIniziale = poseIniziale.getPosition().getY();
                    Log.e(TAG,"POSA INIZIALE\nx:"+ xIniziale + " y:"+yIniziale + "\nalfa in gradi: "+alfa*180/Math.PI);
                    //parte iniz. destra o sinistra
                    if(Comando.GRADI_A_RADIANTI == comando.getTipo()){
                        //TOLTO PER PROVA
                        //if(valore == null){
                        //    valore = Math.PI/2; //di default, se non specificato, è 90 gradi in radianti
                        //    if(comando.getDove() == Comando.ANGOLO_DA_RAGGIUNGERE){
                        //        valore = 0.0; //si raddrizzerà all'angolo 0;
                        //    }
                        //}
                        Log.e(TAG,"ANGOLO DA EFFETTUARE:\t-radiandi:"+valore + "\t-gradi:"+valore*180/Math.PI);
                        if(comando.getDove() == Comando.SINISTRA){
                            alfa = alfa + valore;
                            setVelocitaCorrente(0,0, Costanti.VELOCITA_ANGOLARE_MAX);
                        }else if(comando.getDove()==Comando.DESTRA){
                            alfa = alfa - valore;//destra
                            setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);
                        }else if(comando.getDove() == Comando.ANGOLO_DA_RAGGIUNGERE){ //per portarsi ad un angolo preciso ssj3 livello D: ?!?!
                            setVelocitaCorrente(0,0, Costanti.VELOCITA_ANGOLARE_MAX); //sinistra
                            if(0.0 <= valore && valore < Math.PI){
                                if(alfa > valore && alfa <= (valore + Math.PI)){
                                    setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                                }
                            } else {
                                if(alfa < valore && alfa >= (valore-Math.PI)){//resta u monn cum è
                                } else {
                                    setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                                }
                            }
                            alfa = valore;
                        }
                        alfa = alfa%(2*Math.PI);
                        Log.e(TAG,"OBBIETTIVO:\t\t\t\t-radiandi:"+alfa + "\t-gradi:"+alfa*180/Math.PI); //se alfa>90 && alfa<270-->deltaAgolare
                        sinDestra = conversioneAngoloPerAMCL(alfa-Costanti.PRECISIONE_ANGOLARE_RAD);
                        sinSinistra = conversioneAngoloPerAMCL(alfa+Costanti.PRECISIONE_ANGOLARE_RAD);
                        sinSinitraR = conversioneAngoloPerAMCL(alfa+Costanti.PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD);
                        sinDestraR = conversioneAngoloPerAMCL(alfa-Costanti.PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD);
                        ////// nel caso in cui ci siano ritardi di pacchetti
                        if(èCompresoAritmeticaModulare(sinDestraR, sinSinitraR, poseIniziale.getOrientation().getZ())){
                            setVelocitaCorrente(0,0,velocitaCorrente.getAngular().getZ()/11);
                            Log.e(TAG,"velocità già ridotta");
                        }
                    }
                    // parte iniz. avanti o indietro
                    if(Comando.METRI == comando.getTipo()) {
                        Log.e(TAG, "valore metri: " + valore);
                        if (comando.getDove() == Comando.AVANTI) {
                            xFinale = xIniziale + valore * Math.cos(alfa); // x finale
                            yFinale = yIniziale + valore * Math.sin(alfa); // y finale
                            setVelocitaCorrente(Costanti.VELOCITA_LINEARE_MAX, 0, 0);
                        } else {
                            xFinale = xIniziale - valore * Math.cos(alfa);
                            yFinale = yIniziale - valore * Math.sin(alfa);
                            setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_MAX, 0, 0);
                        }
                        aggiornaObbiettivoGriglia(new Punto2D((float)xFinale,(float)yFinale));
                        Log.e(TAG, "valore metri- x finale:" + xFinale + " y finale:"+yFinale);
                    }
                    //fine iniz. e publicazione velocità
                    publisherVelocita.publish(velocitaCorrente);
                }
                if(Comando.GRADI_A_RADIANTI == comando.getTipo()){
                    double senoAttuale=messaggio.getPose().getPose().getOrientation().getZ();
                    Log.e(TAG,"A:"+ sinDestra +" B:"+ sinSinistra +" sinM:"+messaggio.getPose().getPose().getOrientation().getZ()+" //non necessario..cosM:"+messaggio.getPose().getPose().getOrientation().getW());
                    if(èCompresoAritmeticaModulare(sinDestra, sinSinistra, senoAttuale)){
                        if(staCorreggengo){
                            setVelocitaCorrente(velPX,0,0);
                            publisherVelocita.publish(velocitaCorrente);
                            comando = comando;
                            //reinizializza queste variabili
                            distanzaPrecedente = 10000000;                                        //prova a convertire l'operazione usando un Double e null
                            staCorreggengo = false;
                            isLimitata = false;
                        } else{
                            fermarsi(l.getObbiettivoRaggiuntoSi());
                        }
                    } else if(!isLimitata && èCompresoAritmeticaModulare(sinDestraR, sinSinitraR, senoAttuale)){
                        // limitatore velocità arrivati in prossimità dell'angolo
                        //setVelocita(0,0,velocitaCorrente.getAngular().getZ()/15); creerebbe problemi
                        if(velocitaCorrente.getAngular().getZ() >= 0){
                            setVelocitaCorrente(0, 0, Costanti.VELOCITA_ANGOLARE_RIDOTTA);
                        }else{
                            setVelocitaCorrente(0, 0, -Costanti.VELOCITA_ANGOLARE_RIDOTTA);
                        }
                        publisherVelocita.publish(velocitaCorrente);
                        isLimitata = true;
                        Log.e(TAG,"velocità angolare ridotta");
                        Applicazione.getInstance().toastSuActivityCorrente(l.getVelAngRidotta());
                    } else if(isLimitata && !èCompresoAritmeticaModulare(sinDestraR, sinSinitraR, senoAttuale)){
                        if(velocitaCorrente.getAngular().getZ() >= 0){
                            setVelocitaCorrente(0, 0, -Costanti.VELOCITA_ANGOLARE_RIDOTTA*2);
                        } else{
                            setVelocitaCorrente(0, 0, Costanti.VELOCITA_ANGOLARE_RIDOTTA*2);
                        }
                        Applicazione.getInstance().toastSuActivityCorrente(l.getObbiettivoSuperato());
                        isLimitata = false;
                        publisherVelocita.publish(velocitaCorrente);
                        tentativi++;
                    }
                }
                if(Comando.METRI == comando.getTipo()){
                    double pAy = messaggio.getPose().getPose().getPosition().getY();
                    double pAx = messaggio.getPose().getPose().getPosition().getX();
                    double distanzaAttuale = Math.sqrt(Math.pow((xFinale-pAx),2)+Math.pow((yFinale-pAy),2)); //distanza dall'obbiettivo
                    Log.e(TAG,"x atteso:"+xFinale+" y atteso:"+yFinale+" x:"+pAx+" y:"+pAy +" distanza:"+distanzaAttuale);
                    if(distanzaAttuale <= Costanti.PRECISIONE_LINEARE_M){
                        //Per il controllo nel case si è fermato fuori dallo spazio,
                        //potrei salvare la vel precedente, settare vel 0, inviare vel, e risettare la vel a vel precedente
                        //aspettare il prossimo messaggio e quindi non cambiare più nulla.
                        //se risulta di nuovo fermo nel raggio giusto(questo): chiudere tutto; altrimenti sarà tutto automatico come prima
                        //è un ricontrollo da fermo tramite un nuovo messaggio.
                        fermarsi(l.getObbiettivoRaggiuntoSi());
                    } else if(!isLimitata){
                        if(distanzaAttuale <= Costanti.PRECISIONE_LINEARE_RIDUZIONE_M){
                            if(velocitaCorrente.getLinear().getX() >= 0){
                                setVelocitaCorrente(Costanti.VELOCITA_LINEARE_RIDOTTA, 0, 0);
                            } else {
                                setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_RIDOTTA, 0, 0);
                            }
                            //avrei potuto usare setVelocita(velocitaCorrente.getLinear().getX()/10,0,0);bug amcl->vedi in costanti il problema.
                            publisherVelocita.publish(velocitaCorrente);
                            isLimitata = true;
                            Log.e(TAG,"velocità lineare ridotta");
                            Applicazione.getInstance().toastSuActivityCorrente(l.getVelLinRidotta());
                        } else {
                            //controllo che stia andando nella giusta direzione
                            Comando comandoCorrezione = correzioneTraiettoriaOriginale(messaggio.getPose().getPose().getOrientation().getZ(), distanzaAttuale, distanzaPrecedente, pAx, pAy, xFinale, yFinale);
                            if(comandoCorrezione != null){
                                velPX = velocitaCorrente.getLinear().getX();
                                setVelocitaCorrente(0,0,0);
                                publisherVelocita.publish(velocitaCorrente);
                                comando = comandoCorrezione;
                                staCorreggengo = true;
                                poseIniziale = null;
                                tentativiT++;
                                Log.e(TAG,"correzione traiettoria");
                            } else {
                                tentativiT = 0;
                            }
                        }
                    } else if(isLimitata && distanzaAttuale > Costanti.PRECISIONE_LINEARE_RIDUZIONE_M){
                        if(velocitaCorrente.getLinear().getX() >= 0){
                            setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_RIDOTTA*2, 0, 0);
                        } else{
                            setVelocitaCorrente(Costanti.VELOCITA_LINEARE_RIDOTTA*2, 0, 0);
                        }
                        velPX = velocitaCorrente.getLinear().getX();
                        isLimitata = false;
                        publisherVelocita.publish(velocitaCorrente);
                        tentativi++;
                        Applicazione.getInstance().toastSuActivityCorrente(l.getObbiettivoSuperato());
                    }
                    distanzaPrecedente = distanzaAttuale;
                }
                if(tentativi > Costanti.TENTATIVI_OBBIETTIVO || tentativiT > Costanti.TENTATIVI_OBBIETTIVO){
                    fermarsi(l.getObbiettivoRaggiuntoNo());
                }
                if(salvaPose){ //se durante il movimento è richiesto il salvataggio della posizione
                    Applicazione.getInstance().getModello().putBean(Costanti.POSE_SALVATO,messaggio.getPose().getPose());
                    salvaPose = false;
                    possoPrenderePoseSalvata = true;
                }
                Log.e(TAG, "--- --- --- --- --- --- ---");
            }
        });
        */
    /*
    subscriber_amcl_pose = connectedNode.newSubscriber(Costanti.NOME_TOPIC_AMCL_POSE, PoseWithCovarianceStamped._TYPE);
    subscriber_amcl_pose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>(){
        Pose poseIniziale = null;
        double velPX; //da eliminare in prova
        Comando comando = comandoIniziale;
        Double alfa;
        Double valore = comando.getValoreDefault(); //CAMBIATO PER PROVA IN DEFAULT-->

        double distanzaPrecedente = 10000000;  //da cambiare in null!
        double xIniziale;
        double yIniziale;
        double xFinale;
        double yFinale;

        Double sinDestra;       //variabile che assumerà il valore di x o il valore sin destro finali
        Double sinSinistra;     // ... valore di y o valore sin sinistro finali
        Double sinDestraR;      //sin destro riduzione
        Double sinSinitraR;     //sin sinistro riduzione

        boolean isLimitata = false;
        boolean staCorreggengo = false;
        int tentativi = 0;    //se i tentativi sono "relativamente" troppi si ferma
        int tentativiT = 0;
        @Override
        public void onNewMessage(PoseWithCovarianceStamped messaggio) {
            Log.e(TAG,"-------------------------------------------------");
            //set vista_informazioni_vecchio iniziali e finali
            if(poseIniziale == null){
                valore = comando.getValoreDefault(); //CAMBIATO PER PROVA IN DEFAULT-->
                tentativi = 0;
                isLimitata = false;
                siÈFermato = false; //in prova.
                poseIniziale = messaggio.getPose().getPose();
                //quaternione rappresentato in forma polare w + v e ricordo che può effettuare la rotazione solo sul asse z;
                //alfa= getRadiantiQuaternioneAMCL(.getW(), .getZ());
                alfa = getRadiantiQuaternioneAMCL(poseIniziale.getOrientation().getZ());
                xIniziale = poseIniziale.getPosition().getX();
                yIniziale = poseIniziale.getPosition().getY();
                Log.e(TAG,"POSA INIZIALE\nx:"+ xIniziale + " y:"+yIniziale + "\nalfa in gradi: "+alfa*180/Math.PI);
                //parte iniz. destra o sinistra
                if(Comando.GRADI_A_RADIANTI == comando.getTipo()){
                    //TOLTO/TOGLIERE PER PROVA--> INUTILE ATTUALMENTE
                    if(valore == null){
                        valore = Math.PI/2; //di default, se non specificato, è 90 gradi in radianti
                        if(comando.getDove() == Comando.ANGOLO_DA_RAGGIUNGERE){
                            valore = 0.0; //si raddrizzerà all'angolo 0;
                        }
                    }
                    //////////////////////////////////////
                    Log.e(TAG,"ANGOLO DA EFFETTUARE:\t-radiandi:"+valore + "\t-gradi:"+valore*180/Math.PI);
                    if(comando.getDove() == Comando.SINISTRA){
                        alfa = alfa + valore;
                        setVelocitaCorrente(0,0, Costanti.VELOCITA_ANGOLARE_MAX);
                    }else if(comando.getDove()==Comando.DESTRA){
                        alfa = alfa - valore;//destra
                        setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);
                    }else if(comando.getDove() == Comando.ANGOLO_DA_RAGGIUNGERE){ //per portarsi ad un angolo preciso ssj3 livello D: ?!?!
                        setVelocitaCorrente(0,0, Costanti.VELOCITA_ANGOLARE_MAX); //sinistra
                        if(0.0 <= valore && valore < Math.PI){
                            if(alfa > valore && alfa <= (valore + Math.PI)){
                                setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                            }
                        } else {
                            if(alfa < valore && alfa >= (valore-Math.PI)){//resta u monn cum è
                            } else {
                                setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                            }
                        }
                        alfa = valore;
                    }
                    alfa = alfa%(2*Math.PI);
                    Log.e(TAG,"OBBIETTIVO:\t\t\t\t-radiandi:"+alfa + "\t-gradi:"+alfa*180/Math.PI); //se alfa>90 && alfa<270-->deltaAgolare
                    sinDestra = conversioneAngoloPerAMCL(alfa-Costanti.PRECISIONE_ANGOLARE_RAD);
                    sinSinistra = conversioneAngoloPerAMCL(alfa+Costanti.PRECISIONE_ANGOLARE_RAD);
                    sinSinitraR = conversioneAngoloPerAMCL(alfa+Costanti.PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD);
                    sinDestraR = conversioneAngoloPerAMCL(alfa-Costanti.PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD);
                    ////// nel caso in cui ci siano ritardi di pacchetti
                    if(èCompresoAngoloAMCL(sinDestraR, sinSinitraR, poseIniziale.getOrientation().getZ())){
                        setVelocitaCorrente(0,0,velocitaCorrente.getAngular().getZ()/11);
                        Log.e(TAG,"velocità già ridotta");
                    }
                }
                // parte iniz. avanti o indietro
                if(Comando.METRI == comando.getTipo()) {
                    Log.e(TAG, "valore metri: " + valore);
                    if (comando.getDove() == Comando.AVANTI) {
                        xFinale = xIniziale + valore * Math.cos(alfa); // x finale
                        yFinale = yIniziale + valore * Math.sin(alfa); // y finale
                        setVelocitaCorrente(Costanti.VELOCITA_LINEARE_MAX, 0, 0);
                    } else {
                        xFinale = xIniziale - valore * Math.cos(alfa);
                        yFinale = yIniziale - valore * Math.sin(alfa);
                        setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_MAX, 0, 0);
                    }
                    aggiornaObbiettivoGriglia(new Punto2D((float)xFinale,(float)yFinale));
                    Log.e(TAG, "valore metri- x finale:" + xFinale + " y finale:"+yFinale);
                }
                //fine iniz. e publicazione velocità
                publisherVelocita.publish(velocitaCorrente);
            }
            if(Comando.GRADI_A_RADIANTI == comando.getTipo()){
                double senoAttuale=messaggio.getPose().getPose().getOrientation().getZ();
                Log.e(TAG,"A:"+ sinDestra +" B:"+ sinSinistra +" sinM:"+messaggio.getPose().getPose().getOrientation().getZ()+" //non necessario..cosM:"+messaggio.getPose().getPose().getOrientation().getW());
                if(èCompresoAngoloAMCL(sinDestra, sinSinistra, senoAttuale)){
                    if(staCorreggengo){
                        setVelocitaCorrente(velPX,0,0);
                        publisherVelocita.publish(velocitaCorrente);
                        comando = comandoIniziale;
                        //reinizializza queste variabili
                        distanzaPrecedente = 10000000;                                        //prova a convertire l'operazione usando un Double e null
                        staCorreggengo = false;
                        isLimitata = false;
                    } else{
                        fermarsi(l.getObbiettivoRaggiuntoSi());
                    }
                } else if(!isLimitata && èCompresoAngoloAMCL(sinDestraR, sinSinitraR, senoAttuale)){
                    // limitatore velocità arrivati in prossimità dell'angolo
                    //setVelocita(0,0,velocitaCorrente.getAngular().getZ()/15); creerebbe problemi
                    if(velocitaCorrente.getAngular().getZ() >= 0){
                        setVelocitaCorrente(0, 0, Costanti.VELOCITA_ANGOLARE_RIDOTTA);
                    }else{
                        setVelocitaCorrente(0, 0, -Costanti.VELOCITA_ANGOLARE_RIDOTTA);
                    }
                    publisherVelocita.publish(velocitaCorrente);
                    isLimitata = true;
                    Log.e(TAG,"velocità angolare ridotta");
                    Applicazione.getInstance().toastSuActivityCorrente(l.getVelAngRidotta());
                } else if(isLimitata && !èCompresoAngoloAMCL(sinDestraR, sinSinitraR, senoAttuale)){
                    if(velocitaCorrente.getAngular().getZ() >= 0){
                        setVelocitaCorrente(0, 0, -Costanti.VELOCITA_ANGOLARE_RIDOTTA*2);
                    } else{
                        setVelocitaCorrente(0, 0, Costanti.VELOCITA_ANGOLARE_RIDOTTA*2);
                    }
                    Applicazione.getInstance().toastSuActivityCorrente(l.getObbiettivoSuperato());
                    isLimitata = false;
                    publisherVelocita.publish(velocitaCorrente);
                    tentativi++;
                }
            }
            if(Comando.METRI == comando.getTipo()){
                double pAy = messaggio.getPose().getPose().getPosition().getY();
                double pAx = messaggio.getPose().getPose().getPosition().getX();
                double distanzaAttuale = Math.sqrt(Math.pow((xFinale-pAx),2)+Math.pow((yFinale-pAy),2)); //distanza dall'obbiettivo
                Log.e(TAG,"x atteso:"+xFinale+" y atteso:"+yFinale+" x:"+pAx+" y:"+pAy +" distanza:"+distanzaAttuale);
                if(distanzaAttuale <= Costanti.PRECISIONE_LINEARE_M){
                    //Per il controllo nel case si è fermato fuori dallo spazio,
                    //potrei salvare la vel precedente, settare vel 0, inviare vel, e risettare la vel a vel precedente
                    //aspettare il prossimo messaggio e quindi non cambiare più nulla.
                    //se risulta di nuovo fermo nel raggio giusto(questo): chiudere tutto; altrimenti sarà tutto automatico come prima
                    //è un ricontrollo da fermo tramite un nuovo messaggio.
                    fermarsi(l.getObbiettivoRaggiuntoSi());
                } else if(!isLimitata){
                    if(distanzaAttuale <= Costanti.PRECISIONE_LINEARE_RIDUZIONE_M){
                        if(velocitaCorrente.getLinear().getX() >= 0){
                            setVelocitaCorrente(Costanti.VELOCITA_LINEARE_RIDOTTA, 0, 0);
                        } else {
                            setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_RIDOTTA, 0, 0);
                        }
                        //avrei potuto usare setVelocita(velocitaCorrente.getLinear().getX()/10,0,0);bug amcl->vedi in costanti il problema.
                        publisherVelocita.publish(velocitaCorrente);
                        isLimitata = true;
                        Log.e(TAG,"velocità lineare ridotta");
                        Applicazione.getInstance().toastSuActivityCorrente(l.getVelLinRidotta());
                    } else {
                        //controllo che stia andando nella giusta direzione
                        Comando comandoCorrezione = correzioneTraiettoria(messaggio.getPose().getPose().getOrientation().getZ(), distanzaAttuale, distanzaPrecedente, pAx, pAy, xFinale, yFinale);
                        if(comandoCorrezione != null){
                            velPX = velocitaCorrente.getLinear().getX();
                            setVelocitaCorrente(0,0,0);
                            publisherVelocita.publish(velocitaCorrente);
                            comando = comandoCorrezione;
                            staCorreggengo = true;
                            poseIniziale = null;
                            tentativiT++;
                            Log.e(TAG,"correzione traiettoria");
                        } else {
                            tentativiT = 0;
                        }
                    }
                } else if(isLimitata && distanzaAttuale > Costanti.PRECISIONE_LINEARE_RIDUZIONE_M){
                    if(velocitaCorrente.getLinear().getX() >= 0){
                        setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_RIDOTTA*2, 0, 0);
                    } else{
                        setVelocitaCorrente(Costanti.VELOCITA_LINEARE_RIDOTTA*2, 0, 0);
                    }
                    velPX = velocitaCorrente.getLinear().getX();
                    isLimitata = false;
                    publisherVelocita.publish(velocitaCorrente);
                    tentativi++;
                    Applicazione.getInstance().toastSuActivityCorrente(l.getObbiettivoSuperato());
                }
                distanzaPrecedente = distanzaAttuale;
            }
            if(tentativi > Costanti.TENTATIVI_OBBIETTIVO || tentativiT > Costanti.TENTATIVI_OBBIETTIVO){
                fermarsi(l.getObbiettivoRaggiuntoNo());
            }
            if(salvaPose){ //se durante il movimento è richiesto il salvataggio della posizione
                Applicazione.getInstance().getModello().putBean(Costanti.POSE_SALVATO,messaggio.getPose().getPose());
                salvaPose = false;
                possoPrenderePoseSalvata = true;
            }
        }
    });
    */
    /*private Double getRadiantiQuaternioneAMCL(Double coseno, Double seno){
        if(seno<0){
            return 2*(Math.PI-Math.acos(coseno));
        }
        return 2*Math.acos(coseno);
    }*/
    /*
    private Comando correzioneTraiettoriaOriginale(double senoAttuale, double distanzaAttuale, double distanzaPrecedente, double posAttualeX, double posAttualeY, double obbiettivoX, double obbiettivoY){
        //calcolo l'angolo tangente rispetto la posizione attuale e la posizione dell'obbiettivo e del suo raggio RAGGIUNTO e non più RIDUZIONE.
        //se l'angolo tangente è inferiore a l'angolo di precisione_
        //    ->usare angolo di precisione
        //se controlloTraiettoria risulta 0
        //    va bene così, continuerà così.
        //altrimenti se ritorna -1
        //  l'angolo obbiettivo non è compreso nella sua traiettoria, quindi fermarsi, sì perché nel frattempo che effettui
        //  i calcoli e il tempo necessario affinché venga inviato la nuova velocità(lineare + angolare) avrei uno spostamento
        //  che anche se lo calcolassi, sarebbe solo una approssimazione utilizzando valori precedenti. Io non voglio questo.
        //  Quindi fermare il suo spostamento lineare e ruotare il robot verso l'angolo dell'obbiettivo.
        //  Nel caso non ci riesca per n volte interrompere tutto e fermare la sottoscrizione.

        //PROBLEMI DA RISOLVERE:
        //come gestire l'operazione?
        //-creazione di un nuovo comando(locale?) del tipo ANGOLO_ZERO dove gli passerò l'angolo dell'obbiettivo //operazione risolta da un metodo già creato.
        //-quindi eseguirà il comando angolare, quindi non più come comando lineare. //risolve la questione tentativi
        //-come ripristinare operazione iniziale?--> creare un nuovo comando, con distanza precedente e controllare se debba andare avanti o indietro.
        Log.e(TAG, "distanza attuale:"+distanzaAttuale +"  distanza precedente:"+ distanzaPrecedente);
        double angoloObbiettivo = angoloSegmento(posAttualeX,posAttualeY,obbiettivoX,obbiettivoY);
        if(velocitaCorrente.getLinear().getX()<0.0){     //serve in caso vada all'indietro
            angoloObbiettivo=angoloObbiettivo-Math.PI;   //sfrutto le proprietà geometriche del cerchio
        }
        double angoloTangente = Math.asin(Costanti.PRECISIONE_LINEARE_M/distanzaAttuale);
        if(angoloTangente < Costanti.PRECISIONE_ANGOLARE_CT_RAD){
            angoloTangente = Costanti.PRECISIONE_ANGOLARE_CT_RAD;
        }
        double sinA = conversioneAngoloPerAMCL(angoloObbiettivo-angoloTangente);
        double sinB = conversioneAngoloPerAMCL(angoloObbiettivo+angoloTangente);
        if(distanzaAttuale >= distanzaPrecedente || !èCompresoAritmeticaModulare(sinA,sinB,senoAttuale)){
            angoloObbiettivo = angoloObbiettivo*180/Math.PI;
            //Log.e(TAG,"angolo obbiettivo dopo: "+angoloObbiettivo+ " angolo tangente:"+angoloTangente*180/Math.PI);
            return new Comando(angoloObbiettivo, Comando.GRADI_A_RADIANTI, Comando.ANGOLO_DA_RAGGIUNGERE);
        }
        //Log.e(TAG,"angolo obbiettivo prima: "+angoloObbiettivo*180/Math.PI + " angolo tangente: "+ angoloTangente*180/Math.PI);
        return null;
    }
    */
    //// NODO COMANDO VOCALE
    /*
    private Publisher<GridCells> publisherGridCell;
    private Publisher<BoolParameter> publisherBoolParam = connectedNode.newPublisher("vuoto/2", BoolParameter._TYPE);
    private Publisher<IntParameter> publisherIntParam = connectedNode.newPublisher("vuoto/3", IntParameter._TYPE);
    private Publisher<StrParameter> publisherStrParam = connectedNode.newPublisher("vuoto/4", StrParameter._TYPE);
    */
    /*private void sottoscrittoreOdom(final int dove){
        subscriber_odom = connectedNode.newSubscriber(Costanti.NOME_TOPIC_ODOMETRIA, Odometry._TYPE);
        subscriber_odom.addMessageListener(new MessageListener<Odometry>(){
            List<Odometry> lista = new ArrayList<>();
            int velZeroCons = 0;
            int contEmergenza = 0;
            double velPrecedente = 0;

            @Override
            public void onNewMessage(Odometry messaggio) {
                //operazioni da eseguire in meno di 34 ms!
                èProntoSottoscrittoreOdom = true;
                double vel;
                if(dove == ComandoVel.DESTRA || dove == ComandoVel.SINISTRA){
                    vel = messaggio.getTwist().getTwist().getAngular().getZ();
                } else {
                    vel = messaggio.getTwist().getTwist().getLinear().getX();
                }
                Log.e(TAG,"------ nuovo odom  ------> "+vel);
                lista.add(messaggio);
                if(siÈFermato){
                    if(Math.abs(vel) <= TempiPerTimer.VALORE_DA_CONSIDERARE_ZERO){
                        velZeroCons++;
                        if(velZeroCons > TempiPerTimer.MAX_VEL_UGUALI_CONSECUTIVE){
                            new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    if(NodoComandoVocale.this.tempiPerTimer.creazioneESetMovimento(lista, dove)){
                                        Applicazione.getInstance().toastSuActivityCorrente(l.getTabellaCreata());
                                    }else{
                                        Applicazione.getInstance().toastSuActivityCorrente(l.getErroreCreazioneTabella());
                                    }
                                }
                            }).start();
                            èProntoSottoscrittoreOdom = false;
                            Log.e(TAG, "odom terminato");
                            subscriber_odom.shutdown();
                        }
                    }else if(Math.abs(vel-velPrecedente) <= TempiPerTimer.VALORE_DA_CONSIDERARE_ZERO){
                        contEmergenza++;
                        if(contEmergenza > 40){ //dovrebbe rallentare ma non lo fa.
                            èProntoSottoscrittoreOdom = false;
                            Applicazione.getInstance().toastSuActivityCorrente(l.getErroreCreazioneTabella());
                            Log.e(TAG, "odom terminato per falsa frenata");
                            subscriber_odom.shutdown();
                        }
                        velZeroCons=0;
                    } else {
                        contEmergenza=0;
                        velZeroCons=0;
                    }
                }
                velPrecedente = vel;
            }
        });
    }*/
    /*
    public boolean eseguiComandoN(ArrayList<String> frasiDaUsare){
        if(!prontoPerNuovoComando){
            Log.e(TAG, "non è pronto o non è connesso");
            return false;
        }
        Modello modello = Applicazione.getInstance().getModello();
        int comandoRisultato = comandiVocali.provaPerSwitch(frasiDaUsare);
        if(comandoRisultato != ComandiVocali.NESSUNO_RISULTATO && comandoRisultato != ComandiVocali.FRASE_SALVA_POSIZIONE){
            azzeraSottiscrittoreAmclPose();
            azzeraSottiscrittoreScanPose();
        }
        Log.e(TAG, "subscriber_amcl: "+ subscriber_amcl_pose);
        switch (comandoRisultato) {
            case ComandiVocali.NESSUNO_RISULTATO://non pubblicherà niente
                Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                Log.e(TAG, "non è stato possibile pubblicare niente #ritenta sarai più fortunato");
                break;
            case ComandiVocali.FRASE_POSIZIONE_RICONOSCIUTA: //pubblicherà le coordinate della posiziona dove dovrà andare
                Target target = (Target) modello.getBean(Costanti.TARGET_MOMENTANEO);
                setPosizioneObbiettivoPoseStamped(target.getX(), target.getY(), target.getAlfa());
                publisherPosizione.publish(posizioneObbiettivo);
                modello.putBean(Costanti.TARGET_MOMENTANEO,null);
                memorizzaPosizioneInviata(posizioneObbiettivo);
                logComandoPubblicato();
                Log.e(TAG, "posizione obbiettivo pubblicata correttamente");
                break;
            //publisherPosizione.publish(posizioneObbiettivo);
            //memorizzaPosizioneInviata(posizioneObbiettivo);
            //logComandoPubblicato();
            case ComandiVocali.COMANDO_VELOCITA_RICOSCIUTA: //pubblicherà la velocità corrente
                Comando comando = (Comando)modello.getBean(Costanti.COMANDO_VELOCITA_DA_INVIARE);
                if(comando.getDove() == Comando.FERMO){
                    publisherGoalID.publish(publisherGoalID.newMessage()); //per fermare un eventuale guida autonoma verso un obiettivo dato a move_base.
                    setVelocitaCorrente(0,0,0);
                    publisherVelocita.publish(velocitaCorrente);
                    Log.e(TAG, "comando fermo eseguito");
                    break;
                }
                NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
                if(voglioUsareAMCL && comando.getTipo() != Comando.SECONDI){
                    boolean esisteTopicAmclPose = controlloConnessione.esisteQuestoTopic("/"+Costanti.NOME_TOPIC_AMCL_POSE,nodeConfiguration.getMasterUri());
                    if(parametriAMCLCorretti && esisteTopicAmclPose){
                        dragonballZ(comando);
                        controllaPresentaOstacoli(comando);
                        break;
                    }
                }
                timerVelocitàPerGazebo(comando);
                controllaPresentaOstacoli(comando);
                break;
            case ComandiVocali.FRASE_SALVA_POSIZIONE: //salva la posizione
                String nomePosizione = (String)modello.getBean(Costanti.NOME_POSIZIONE_SALVATA);
                salvaPosizioneCorrente(nomePosizione);
                break;
            case ComandiVocali.FRASE_RIPOSIZIONA_RICONOSCIUTA: //inivierà un messaggio al topic initialPose
                nuovaPosaIniziale((Target)modello.getBean(Costanti.TARGET_MOMENTANEO));
                modello.putBean(Costanti.TARGET_MOMENTANEO,null);
                break;
        }
        Log.e(TAG, "è pronto per fare un'altra sciammerca");
        return prontoPerNuovoComando = true;
    }*/
    /*
    private volatile boolean possoGestireUnNuovoComando = false;
    private ArrayList<String> risultati;
    private Timer publisherTimer;
    private void avviaPublisherTimer(){
        publisherTimer = new Timer();
        publisherTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                NodoComandoVocale.this.provaNuovoAvviaPublisher();
            }
        }, 100, 500);
    }

    public void abilitaLaGestioneDiUnNuovoComando(ArrayList<String> frasiDaUsare){
        if(frasiDaUsare == null || frasiDaUsare.isEmpty()){
            return;
        }
        this.risultati = frasiDaUsare;//risultato del intent fatto su app vocale (google)
        this.possoGestireUnNuovoComando = true;
        Log.e(TAG, "posso gestire nuovo comando:"+true);
    }

    private void provaNuovoAvviaPublisher(){
        if(!possoGestireUnNuovoComando){
            return;
        }
        Modello modello = Applicazione.getInstance().getModello();
        int comandoRisultato = comandiVocali.provaPerSwitch(risultati, posizioneObbiettivo);
        if(comandoRisultato != ComandiVocali.NESSUNO_RISULTATO && comandoRisultato != ComandiVocali.FRASE_SALVA_POSIZIONE){
            azzeraSottiscrittoreAmclPose();
            azzeraSottiscrittoreScanPose();
        }
        Log.e(TAG, "subscriber_amcl: "+ subscriber_amcl_pose);
        switch (comandoRisultato) {
            case ComandiVocali.NESSUNO_RISULTATO://non pubblicherà niente
                Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                Log.e(TAG, "non è stato possibile pubblicare niente #ritenta sarai più fortunato");
                break;
            case ComandiVocali.FRASE_POSIZIONE_RICONOSCIUTA: //pubblicherà le coordinate della posiziona dove dovrà andare
                Target target = (Target)modello.getBean(Costanti.TARGET_MOMENTANEO);
                setPosizioneObbiettivoPoseStamped(target.getX(), target.getY(), target.getAlfa());
                publisherPosizione.publish(posizioneObbiettivo);
                modello.putBean(Costanti.TARGET_MOMENTANEO,null);
                memorizzaPosizioneInviata(posizioneObbiettivo);
                logComandoPubblicato();
                Log.e(TAG, "posizione obbiettivo pubblicata correttamente");
                break;
                //publisherPosizione.publish(posizioneObbiettivo);
                //memorizzaPosizioneInviata(posizioneObbiettivo);
                //logComandoPubblicato();
            case ComandiVocali.COMANDO_VELOCITA_RICOSCIUTA: //pubblicherà la velocità corrente
                Comando comando = (Comando)modello.getBean(Costanti.COMANDO_VELOCITA_DA_INVIARE);
                if(comando.getDove() == Comando.FERMO){
                    publisherGoalID.publish(publisherGoalID.newMessage()); //per fermare un eventuale guida autonoma verso un obiettivo dato a move_base.
                    setVelocitaCorrente(0,0,0);
                    publisherVelocita.publish(velocitaCorrente);
                    Log.e(TAG, "comando fermo eseguito");
                    break;
                }
                NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
                if(voglioUsareAMCL && comando.getTipo() != Comando.SECONDI){
                    boolean esisteTopicAmclPose = controlloConnessione.esisteQuestoTopic("/"+Costanti.NOME_TOPIC_AMCL_POSE,nodeConfiguration.getMasterUri());
                    if(parametriAMCLCorretti && esisteTopicAmclPose){
                        dragonballZ(comando);
                        controllaPresentaOstacoli(comando);
                        break;
                    }
                }
                timerVelocitàPerGazebo(comando);
                controllaPresentaOstacoli(comando);
                break;
            case ComandiVocali.FRASE_SALVA_POSIZIONE: //salva la posizione
                String nomePosizione = (String)modello.getBean(Costanti.NOME_POSIZIONE_SALVATA);
                salvaPosizioneCorrente(nomePosizione);
                break;
            case ComandiVocali.FRASE_RIPOSIZIONA_RICONOSCIUTA: //inivierà un messaggio al topic initialPose
                nuovaPosaIniziale((Target)modello.getBean(Costanti.TARGET_MOMENTANEO));
                modello.putBean(Costanti.TARGET_MOMENTANEO,null);
                break;
        }
        possoGestireUnNuovoComando = false;
        Log.e(TAG, "posso_pubblicare_messaggio:"+possoGestireUnNuovoComando);
    }*/

    /*
    private void salvaPosizioneCorrente(final String nomePosizione){
        //prende la posizione da amcl_pose o es. hector o altro in base allo stato del check amcl
        //non posso avere più sottoscrittori nello stesso nodo allo stesso topic.Perché?
        NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
        if(voglioUsareAMCL){
            if(subscriber_amcl_pose != null){
                //significa che è attualmente già in uso, per un comando?
                //devo sincronizzarmi lanciando un nuovo timer thread e ottenere la posizione dal sottoscrittore già in esecuzione.
                salvaPose = true;
                Log.e(TAG, "avvio timer salva posizione");
                final Timer timer = new Timer();
                timer.schedule(new TimerTask(){
                    int tentativi = 0;
                    @Override
                    public void run() {
                        Log.e(TAG, "timer salva posizione");
                        if(possoPrenderePoseSalvata){
                            Pose posaAttuale = (Pose)Applicazione.getInstance().getModello().getBean(Costanti.POSE_SALVATO);
                            Target target = nuovoTargetDaPoseAMCL(posaAttuale, nomePosizione);
                            //--POSIZIONI SALVATE IN MANIERA PERMANENTE
                            salvaTargetPermanente(target);
                            possoPrenderePoseSalvata = false;
                            Log.e(TAG,"salvataggio posizione:\n"+target.toString());
                            Applicazione.getInstance().toastSuActivityCorrente(l.getPosizioneSalvata()+": "+nomePosizione);
                            azzeraTimer(timer);
                        }
                        tentativi++;
                        if(tentativi >= 20){
                            Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                            azzeraTimer(timer);
                        }
                    }
                },10, 100);
            }else{
                boolean esisteAmclPose = controlloConnessione.esisteQuestoTopic("/"+Costanti.NOME_TOPIC_AMCL_POSE, nodeConfiguration.getMasterUri());
                if(esisteAmclPose){
                    subscriber_amcl_pose = connectedNode.newSubscriber(Costanti.NOME_TOPIC_AMCL_POSE, PoseWithCovarianceStamped._TYPE);
                    subscriber_amcl_pose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>(){
                        @Override
                        public void onNewMessage(PoseWithCovarianceStamped messaggio) {
                            Log.e(TAG,"------ NUOVO MESSAGGIO amcl_pose salva posizione ------ ");
                            Pose posaAttuale=messaggio.getPose().getPose();
                            Target target = nuovoTargetDaPoseAMCL(posaAttuale, nomePosizione);
                            //--POSIZIONI SALVATE IN MANIERA PERMANENTE
                            salvaTargetPermanente(target);
                            Log.e(TAG,"salvataggio posizione:\n"+target.toString());
                            subscriber_amcl_pose.shutdown();
                        }
                    });
                    Applicazione.getInstance().toastSuActivityCorrente(l.getPosizioneSalvata()+": "+nomePosizione);
                }else{
                    Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                }
            }
        } else {
            Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallitoAMCL());
        }
        // potrei effettuare qui il salvataggio della posizione anche attraverso un altro/i topic di posizione(es.odom)
    }
    */
    /*
        Da usare esclusivamente fermando la marcia del robot e spegnendo qualsiasi altro sotto scrittore ad amcl.
        Quindi:
        -fermerà sempre il robot.
        -controlla che connected node non sia nullo e se lo è non prosegue.-> toast! operazione non riuscita.
        -Se viene inviata una nuova posizione specificata fermerà sempre il robot, e utilizzerà i dati forniti per riposizionarsi.
        -altrimenti, dunque se null, si iscriverà ad amcl pose e prenderà la posizione attuale invierà quella per riposizionarsi .
        */
    /*
    private void nuovaPosaIniziale(final Target initialPose){

        fermarsi(null);
        if(initialPose == null){
            NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
            boolean esisteTopicAmclPose = controlloConnessione.esisteQuestoTopic("/"+Costanti.NOME_TOPIC_AMCL_POSE, nodeConfiguration.getMasterUri());
            if(esisteTopicAmclPose){
                subscriber_amcl_pose = connectedNode.newSubscriber(Costanti.NOME_TOPIC_AMCL_POSE, PoseWithCovarianceStamped._TYPE);
                subscriber_amcl_pose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>(){
                    @Override
                    public void onNewMessage(PoseWithCovarianceStamped messaggio) {
                        Log.e(TAG,"------ NUOVO MESSAGGIO amcl_pose  ------ ");
                        pubblicaPosizioneIniziale(nuovoTargetDaPoseAMCL(messaggio.getPose().getPose(), null));
                        subscriber_amcl_pose.shutdown();
                    }
                });
            }else{
                Log.e(TAG,"Non è stato possibile riposizionarsi. Non esiste il topic amcl_pose");
                Applicazione.getInstance().toastSuActivityCorrente(l.getErroreRiposizioneAMCL());
                return;
            }
        }else{
            pubblicaPosizioneIniziale(initialPose);
        }
    }
    */
    //////GESTIONE NUOVO COMANDO CON TIMER////////////////////
    /*
    public boolean eseguiComandoN(ArrayList<String> frasiDaUsare){
        if(!prontoPerNuovoComando){
            Log.e(TAG, "non è pronto o non è connesso");
            return false;
        }
        Modello modello = Applicazione.getInstance().getModello();
        int comandoRisultato = comandiVocali.provaPerSwitch(frasiDaUsare);
        if(comandoRisultato != ComandiVocali.NESSUNO_RISULTATO && comandoRisultato != ComandiVocali.FRASE_SALVA_POSIZIONE){
            azzeraSottiscrittoreAmclPose();
            azzeraSottiscrittoreScanPose();
        }
        Log.e(TAG, "subscriber_amcl: "+ subscriber_amcl_pose);
        switch (comandoRisultato) {
            case ComandiVocali.NESSUNO_RISULTATO://non pubblicherà niente
                Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                Log.e(TAG, "non è stato possibile pubblicare niente #ritenta sarai più fortunato");
                break;
            case ComandiVocali.FRASE_POSIZIONE_RICONOSCIUTA: //pubblicherà le coordinate della posiziona dove dovrà andare
                Target target = (Target) modello.getBean(Costanti.TARGET_MOMENTANEO);
                setPosizioneObbiettivoPoseStamped(target.getX(), target.getY(), target.getAlfa());
                publisherPosizione.publish(posizioneObbiettivo);
                modello.putBean(Costanti.TARGET_MOMENTANEO,null);
                memorizzaPosizioneInviata(posizioneObbiettivo);
                logComandoPubblicato();
                Log.e(TAG, "posizione obbiettivo pubblicata correttamente");
                break;
            //publisherPosizione.publish(posizioneObbiettivo);
            //memorizzaPosizioneInviata(posizioneObbiettivo);
            //logComandoPubblicato();
            case ComandiVocali.COMANDO_VELOCITA_RICOSCIUTA: //pubblicherà la velocità corrente
                Comando comando = (Comando)modello.getBean(Costanti.COMANDO_VELOCITA_DA_INVIARE);
                if(comando.getDove() == Comando.FERMO){
                    publisherGoalID.publish(publisherGoalID.newMessage()); //per fermare un eventuale guida autonoma verso un obiettivo dato a move_base.
                    setVelocitaCorrente(0,0,0);
                    publisherVelocita.publish(velocitaCorrente);
                    Log.e(TAG, "comando fermo eseguito");
                    break;
                }
                NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
                if(voglioUsareAMCL && comando.getTipo() != Comando.SECONDI){
                    boolean esisteTopicAmclPose = controlloConnessione.esisteQuestoTopic("/"+Costanti.NOME_TOPIC_AMCL_POSE,nodeConfiguration.getMasterUri());
                    if(parametriAMCLCorretti && esisteTopicAmclPose){
                        dragonballZ(comando);
                        controllaPresentaOstacoli(comando);
                        break;
                    }
                }
                timerVelocitàPerGazebo(comando);
                controllaPresentaOstacoli(comando);
                break;
            case ComandiVocali.FRASE_SALVA_POSIZIONE: //salva la posizione
                String nomePosizione = (String)modello.getBean(Costanti.NOME_POSIZIONE_SALVATA);
                salvaPosizioneCorrente(nomePosizione);
                break;
            case ComandiVocali.FRASE_RIPOSIZIONA_RICONOSCIUTA: //inivierà un messaggio al topic initialPose
                nuovaPosaIniziale((Target)modello.getBean(Costanti.TARGET_MOMENTANEO));
                modello.putBean(Costanti.TARGET_MOMENTANEO,null);
                break;
        }
        Log.e(TAG, "è pronto per fare un'altra sciammerca");
        return prontoPerNuovoComando = true;
    }*/

    ////// SOTTOSCRITTORE LASER VECCHIO ////////
    /*
        subscriber_scan.addMessageListener(new MessageListener<LaserScan>(){
            boolean inizializzato = false;
            int [] indiciIndietro = new int[3];
            int [] indiciAvanti = new int[3];

            @Override
            public void onNewMessage(LaserScan messaggio) {
                Log.e(TAG,"nuovo messaggio laser");
                float[] valoriLaser = messaggio.getRanges();
                double velLin = velocitaCorrente.getLinear().getX();
                boolean ostacoloRilevato;
                if(!inizializzato){
                    float valoreMin = messaggio.getAngleMin();
                    float valoreMax = messaggio.getAngleMax();
                    if((valoreMin > 0.0F || valoreMax < 6.28F) && (messaggio.getRangeMin() < Costanti.DISTANZA_OSTACOLO_RILEVATO)){
                        //Non è possibile utilizzare il sensore laser per rilevare ostacoli perché il sensore non è 360 gradi
                        //oppure il rangeMin è troppo grande
                        Log.e(TAG, "sensore laser non utilizzabile");
                        Applicazione.getInstance().toastSuActivityCorrente(l.getNoLaser());
                        azzeraSottiscrittoreScanPose();
                        return;
                    }
                    float indiceAppPerGrado = valoriLaser.length/(valoreMax-valoreMin);//es. 720/180=4 //es -30 a +50 ==> 500/80=
                    indiciAvanti[0] = (int)((0-(valoreMin))*indiceAppPerGrado);
                    indiciAvanti[1] = (int)((5*Costanti.DA_GRADI_A_RADIANTI-(valoreMin))*indiceAppPerGrado);
                    indiciAvanti[2] = (int)((355*Costanti.DA_GRADI_A_RADIANTI-(valoreMin))*indiceAppPerGrado);

                    indiciIndietro[0] = (int)((Math.PI-(valoreMin))*indiceAppPerGrado);
                    indiciIndietro[1] = (int)((185*Costanti.DA_GRADI_A_RADIANTI-(valoreMin))*indiceAppPerGrado);
                    indiciIndietro[2] = (int)((175*Costanti.DA_GRADI_A_RADIANTI-(valoreMin))*indiceAppPerGrado);
                    inizializzato = true;
                    //Log.e(TAG,"-- inizializzato -- rilevatori ostacoli");
                    //Log.e(TAG,"-- indici avanti  :"+indiciAvanti[0]+ " "+indiciAvanti[1]+ " "+indiciAvanti[2]);
                    //Log.e(TAG,"-- indici indietro:"+indiciIndietro[0]+ " "+indiciIndietro[1]+ " "+indiciIndietro[2]);
                    ///
                }
                boolean ostacoloRilevato = presenzaDiOstacoloNuovo(indiciAvanti, indiciIndietro, valoriLaser);
                if(ostacoloRilevato || siÈFermato){
                    azzeraTimer(timerGazebo);
                    azzeraSottiscrittoreAmclPose();
                    setVelocitaCorrente(0, 0, 0);
                    publisherVelocita.publish(velocitaCorrente);
                    siÈFermato = true;
                    //azzeraSubscriberOdom();
                    if(ostacoloRilevato){
                        Log.e(TAG,"ostacolo rilevato");
                        Applicazione.getInstance().toastSuActivityCorrente(l.getOstacoloRilevato());
                    }
                    aggiornaObbiettivoGriglia(null);
                    azzeraSottiscrittoreScanPose();
                }
            }

            private boolean presenzaDiOstacoloNuovo(int[] indiciAvanti, int[] indiciIndietro, float[] valoriLaser){
                double velLin = velocitaCorrente.getLinear().getX();
                if(Math.abs(velLin) < 0.0001){   //costante della vel praticamente zero
                    return false;
                }
                int[] indiciDaUsare;
                if(velLin > 0.0){
                    indiciDaUsare = indiciAvanti;
                } else{
                    indiciDaUsare = indiciIndietro;
                }
                for(int i:indiciDaUsare){
                    //Log.e(TAG,"valore dell'indice: "+i);
                    if(valoriLaser[i] < Costanti.DISTANZA_OSTACOLO_RILEVATO){
                        return true;
                    }
                }
                return false;
            }
        }); */
    ////// PARAMETRI AMCL //////////////////////
    /*
    private Config getConfigDynamicMessage(){
        //Config messaggioConfigDynamicMessage = publisherDynamic.newMessage();
        //GroupState messaggioGroupParameter = publisherGroupParam.newMessage();
        //group
        messaggioGroupParameter.setId(0);
        messaggioGroupParameter.setParent(0);
        messaggioGroupParameter.setState(true);
        messaggioGroupParameter.setName(Costanti.NOME_GROUP_PARAMETER);
        List<GroupState> listaGroupStateParameter = messaggioConfigDynamicMessage.getGroups();
        listaGroupStateParameter.add(messaggioGroupParameter);
        //set
        messaggioConfigDynamicMessage.setGroups(listaGroupStateParameter);  //necessario??
        return messaggioConfigDynamicMessage;
    }*/
    /*
    private boolean init;
    private List<DoubleParameter> originali;
    private void controlloValore(List<Parametro<Double>> corretti, List<DoubleParameter> attuali){
        List<DoubleParameter> listaDaCambiare = new ArrayList<>();
        int dimC = corretti.size();
        for(DoubleParameter pA:attuali){
            for(Parametro pC:corretti){
                if(pA.getName().equalsIgnoreCase(pC.getNome())){
                    if(pC.getValore().equals(pA.getValue())){
                        if(!init){
                            originali.add(pA);
                        }
                        listaDaCambiare.add(pA)
                    }

                    dimC--;
                }
            }
            if(dimC == 0){
                //non è più necessario scansionare. Sono stati trovati tutti.
                break;
            }
        }
        if(dimC != 0){
            // parametro/i non esistono in quelli usati dal nodo e quindi
            // dovrei precludere la possibilità di utilizzo.
        }
        init = true;
    }*/
    /*
        private Comando correzioneTraiettoria(double angoloObbiettivo, boolean staAndandoAvanti, double angoloAttuale, double distanzaAttuale, double distanzaPrecedente){
            //Log.e(TAG,"angolo obbiettivo"+ angoloObbiettivo);
            if(!staAndandoAvanti){                              //serve in caso vada all'indietro
                angoloObbiettivo = angoloObbiettivo-Math.PI;    //sfrutto le proprietà geometriche del cerchio
            }
            //Log.e(TAG,"angolo obbiettivo"+ angoloObbiettivo);
            double angoloTangente = Math.asin(Costanti.PRECISIONE_LINEARE_M/distanzaAttuale);
            if(angoloTangente < Costanti.PRECISIONE_ANGOLARE_CT_RAD){ //nel caso sia troppo lontano dall'obiettivo
                angoloTangente = Costanti.PRECISIONE_ANGOLARE_CT_RAD;
            }
            double destra = angoloObbiettivo-angoloTangente;
            double sinistra = angoloObbiettivo+angoloTangente;
            if(destra < 0){
                destra = destra+2*Math.PI;
            }
            if(sinistra >= 2*Math.PI){
                sinistra = sinistra-2*Math.PI;
            }
            if(distanzaAttuale >= distanzaPrecedente ||!èCompresoAritmeticaModulare(destra, sinistra,angoloAttuale)){
                angoloObbiettivo = angoloObbiettivo*180/Math.PI;
                //Log.e(TAG,"angolo obbiettivo dopo: "+angoloObbiettivo+ " angolo tangente:"+angoloTangente*180/Math.PI);
                return new Comando(angoloObbiettivo, Comando.GRADI_A_RADIANTI, Comando.ANGOLO_DA_RAGGIUNGERE);
            }
            //Log.e(TAG,"angolo obbiettivo prima: "+angoloObbiettivo*180/Math.PI + " angolo tangente: "+ angoloTangente*180/Math.PI);
            return null;
        }
        */

    /*private Comando correzioneTraiettoriaVecchio(double senoAttuale, double distanzaAttuale, double distanzaPrecedente, double posAttualeX, double posAttualeY, double obbiettivoX, double obbiettivoY){
        calcolo l'angolo tangente rispetto la posizione attuale e la posizione dell'obbiettivo e del suo raggio RAGGIUNTO e non più RIDUZIONE.

        se l'angolo tangente è inferiore a l'angolo di precisione_
            ->usare angolo di precisione
        se controlloTraiettoria risulta 0
            va bene così, continuerà così.
        altrimenti se ritorna -1
            l'angolo obbiettivo non è compreso nella sua traiettoria, quindi fermarsi, sì perché nel frattempo che effettui
            i calcoli e il tempo necessario affinché venga inviato la nuova velocità(lineare + angolare) avrei uno spostamento
            che anche se lo calcolassi, sarebbe solo una approssimazione utilizzando valori precedenti. Io non voglio questo.
            Quindi fermare il suo spostamento lineare e ruotare il robot verso l'angolo dell'obbiettivo.
            Nel caso non ci riesca per n volte interrompere tutto e fermare la sottoscrizione.

        PROBLEMI DA RISOLVERE:
        come gestire l'operazione?
        -creazione di un nuovo comando(locale?) del tipo ANGOLO_ZERO dove gli passerò l'angolo dell'obbiettivo //operazione risolta da un metodo già creato.
        -quindi eseguirà il comando angolare, quindi non più come comando lineare. //risolve la questione tentativi
        -come ripristinare operazione iniziale?--> creare un nuovo comando, con distanza precedente e controllare se debba andare avanti o indietro.

        //una sorta di coda
        double angoloObbiettivo = angoloSegmento(posAttualeX,posAttualeY,obbiettivoX,obbiettivoY);
        if(velocitaCorrente.getLinear().getX()<0.0){     //serve in caso vada all'indietro
            angoloObbiettivo=angoloObbiettivo-Math.PI;   //sfrutto le proprietà geometriche del cerchio
        }
        double angoloTangente = Math.asin(Costanti.PRECISIONE_LINEARE_M/distanzaAttuale);
        if(angoloTangente < Costanti.PRECISIONE_ANGOLARE_CT_RAD){
            angoloTangente = Costanti.PRECISIONE_ANGOLARE_CT_RAD;
        }
        double sinA = conversioneAngoloPerAMCL(angoloObbiettivo-angoloTangente);
        double sinB = conversioneAngoloPerAMCL(angoloObbiettivo+angoloTangente);
        if(distanzaAttuale >= distanzaPrecedente || !èCompresoAngoloAMCL(sinA,sinB,senoAttuale)){
            angoloObbiettivo = angoloObbiettivo*180/Math.PI;
            //Log.e(TAG,"angolo obbiettivo dopo: "+angoloObbiettivo+ " angolo tangente:"+angoloTangente*180/Math.PI);
            return new Comando(angoloObbiettivo, Comando.GRADI_A_RADIANTI, Comando.ANGOLO_DA_RAGGIUNGERE);
        }
        //Log.e(TAG,"angolo obbiettivo prima: "+angoloObbiettivo*180/Math.PI + " angolo tangente: "+ angoloTangente*180/Math.PI);
        return null;
    }*/
    /*
    private void publicaConfigurazioneSuTopic(Double velocità, Double velocitàAngolare){
        Config dynamicMessage = publisherDynamic.newMessage();
        DoubleParameter doubleParameter1 = publisherDoubleParam.newMessage();
        DoubleParameter doubleParameter2 = publisherDoubleParam.newMessage();
        BoolParameter boolParameter  = publisherBoolParam.newMessage();
        StrParameter strParameter = publisherStrParam.newMessage();
        GroupState groupParameter = publisherGroupParam.newMessage();
        IntParameter intParameter = publisherIntParam.newMessage();
        Log.e(TAG, "#### DynamicMessage new message");
        //double
        doubleParameter1.setName(Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_VEL);
        doubleParameter1.setValue(velocità);
        doubleParameter2.setName(Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_ANG);
        doubleParameter2.setValue(velocitàAngolare);
        List<DoubleParameter> listaDoubleParameter = dynamicMessage.getDoubles();
        listaDoubleParameter.add(doubleParameter1);
        listaDoubleParameter.add(doubleParameter2);
        //int
        intParameter.setName("");
        intParameter.setName("");
        List<IntParameter> listaIntParameter = dynamicMessage.getInts();
        listaIntParameter.add(intParameter);
        //bool
        boolParameter.setName("");
        boolParameter.setValue(false);
        List<BoolParameter> listaBoolParameter = dynamicMessage.getBools();
        listaBoolParameter.add(boolParameter);
        //string
        strParameter.setName("");
        strParameter.setValue("");
        List<StrParameter> listaStrParameter = dynamicMessage.getStrs();
        listaStrParameter.add(strParameter);
        //group
        groupParameter.setId(0);
        groupParameter.setParent(0);
        groupParameter.setState(true);
        groupParameter.setName("default");
        List<GroupState> listaGroupStateParameter = dynamicMessage.getGroups();
        listaGroupStateParameter.add(groupParameter);
        //aggiungo i vari parametri
        dynamicMessage.setDoubles(listaDoubleParameter);
        publisherDynamic.publish(dynamicMessage);
    }*/
    /*
    private void invioParametriVelocitàServiceAMCL(final double velocità, final double velocitàAngolare){
        Log.e(TAG, "messaggio DynamicMessage");
        //non ho librerie che mi permettano di avere messaggi di tipo "dynamic_reconfigure/Config" e tutti i restanti messaggi collegati ad esso. Quindi sono
        //costretto, dato il funzionamento della classe che utilizza il classloader a creare una libreria per utilizzare questo tipo di
        //messaggi, oppure dovrei usare la riflessione per ottenere la mappa e aggiungere manualmente il il tipo e la la definizione
        //La libreria l'aggiungero
        //sono costretto(per via della libreria) e ros stesso a farli in questo modo osceno.
        Config dynamicMessage = publisherDynamic.newMessage();
        DoubleParameter doubleParameter1 = publisherDoubleParam.newMessage();
        DoubleParameter doubleParameter2 = publisherDoubleParam.newMessage();
        GroupState groupParameter = publisherGroupParam.newMessage();
        //double
        doubleParameter1.setName(Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_VEL);
        doubleParameter1.setValue(velocità);
        doubleParameter2.setName(Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_ANG);
        doubleParameter2.setValue(velocitàAngolare);
        List<DoubleParameter> listaDoubleParameter = dynamicMessage.getDoubles();
        listaDoubleParameter.add(doubleParameter1);
        listaDoubleParameter.add(doubleParameter2);
        //group
        groupParameter.setId(0);
        groupParameter.setParent(0);
        groupParameter.setState(true);
        groupParameter.setName("default");
        List<GroupState> listaGroupStateParameter = dynamicMessage.getGroups();
        listaGroupStateParameter.add(groupParameter);
        //aggiungo i vari parametri
        dynamicMessage.setDoubles(listaDoubleParameter);
        ReconfigureRequest request = serviceClientAMCL.newMessage();
        request.setConfig(dynamicMessage);
        //setta richiesta con dynamicMessage
        serviceClientAMCL.call(request, new ServiceResponseListener<ReconfigureResponse>() {
            @Override
            public void onSuccess(ReconfigureResponse config) {
                Log.e(TAG,"waglio la configurazione amcl inviata ha avuto successo ;)");
                Applicazione.getInstance().toastSuActivityCorrente(l.getConfigurazioneAMCL_OK()+"\n"+
                        Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_VEL+":"+velocità+"\n"+
                        Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_ANG+":"+velocitàAngolare);
            }
            @Override
            public void onFailure(RemoteException e) {
                Log.e(TAG,"ERRORE. la configurazione inviata non ha avuto successo");
                Applicazione.getInstance().toastSuActivityCorrente(l.getConfigurazioneAMCL_NO());
            }
        });
        Log.e(TAG, "dynamicMessage inviato al service\nvelocità lineare:"+ velocità+ "\tvelocità angolare:"+velocitàAngolare);
    }*/
    ////////timer gazebo vecchio /////////////
    /*
    private void timerVelocitàPerGazeboVecchio(Comando comando){
        //il timer è necessario perché altrimenti il software gazebo continuerebbe ad utilizzare
        //l'ultima velocità inviatagli(velocitàCorrente). Dato che non è possibile settare la distanza(quindi un timeout automatico) direttamente da gazebo,
        //creo un timer che prenderà in input la durata del comando di velocità in secondi, e se non detti, di default.

        Log.e(TAG, "esecuzione comando usando timer");
        int dove = comando.getDove();
        if(dove==Comando.ANGOLO_DA_RAGGIUNGERE){
            Applicazione.getInstance().toastSuActivityCorrente(l.getTimerComandoNonDisponibile());
            Log.e(TAG,"comando non disponibile per il timer");
            return;
        }
        if(azzeraTimer(timerGazebo)){
            Applicazione.getInstance().toastSuActivityCorrente(l.getTimerNuovo());
        }
        long millisecondi = 1000;
        if(dove==Comando.SINISTRA){
            setVelocitaCorrente(0,0,Costanti.VELOCITA_ANGOLARE_MAX);
        }else if(dove==Comando.DESTRA){
            setVelocitaCorrente(0,0,-Costanti.VELOCITA_ANGOLARE_MAX);
        } else if(dove==Comando.AVANTI){
            setVelocitaCorrente(Costanti.VELOCITA_LINEARE_MAX, 0, 0);
        } else if(dove==Comando.INDIETRO){
            setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_MAX, 0, 0);
        }
        //millisecondi =((Double)(millisecondi*comando.getValoreInSecondiDefault())).intValue();
        //Log.e(TAG, "millisecondi v: "+);
        millisecondi = ((Double)(millisecondi*tempiPerTimer.getTempoPerTimer(comando))).intValue();
        siÈFermato=false;
        sottoscrittoreOdom(dove);

        Log.e(TAG,"millisecondi: "+ millisecondi);

        timerGazebo = new Timer();
        publisherVelocita.publish(velocitaCorrente);
        timerGazebo.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "timer per gazebo");
                NodoComandoVocale.this.setVelocitaCorrente(0, 0, 0);
                NodoComandoVocale.this.publisherVelocita.publish(velocitaCorrente);
                siÈFermato=true;
                NodoComandoVocale.this.memorizzaVelocitàInviata(velocitaCorrente);
                NodoComandoVocale.this.logComandoPubblicato();
            }
        }, millisecondi);
        memorizzaVelocitàInviata(velocitaCorrente);
        logComandoPubblicato();
    }*/
    /*
    private void costruisciGrigliaCelle(ConnectedNode connectedNode, int larghezzaPixel, int altezzaPixel){
        Log.e(TAG, "costruisci griglia celle");
        Publisher<GridCells> publisherGridCell = connectedNode.newPublisher("g", GridCells._TYPE);
        Publisher<Point> publisherPoint = connectedNode.newPublisher("p", Point._TYPE);
        Publisher<Header> publisherHeader = connectedNode.newPublisher("h", Header._TYPE);


        Header header= publisherHeader.newMessage();
        GridCells messaggio = publisherGridCell.newMessage();
        Point point = publisherPoint.newMessage();

        point.setZ(0.0);
        point.setX(0.0);
        point.setY(0.0);
        List<Point> listaPoint= new ArrayList<>();
        listaPoint.add(point);

        header.setFrameId("odom");//Costanti.NOME_TOPIC_OCCUPACITY_GRID);
        header.setStamp(Time.fromMillis(new GregorianCalendar().getTime().getTime()));
        header.setSeq(0);
        messaggio.setCellWidth(5.0f);
        messaggio.setCellHeight(5.0f);
        messaggio.setHeader(header);
        messaggio.setCells(listaPoint);


        Double metriOrigineX = -12.8*0.05;
        Double metriOrigineY = -12.8*0.05;
        Double metriAltezzaMax = -altezzaPixel*0.05;
        Double metriLarghezzaMax = -larghezzaPixel*0.05;

        //parte a parte//
         cells.header.frame_id=" ";
        cells.cell_height=0.3;
        cells.cell_width=0.3;
        cells.cells.resize(3);
        cells.cells[0].x=1;
        cells.cells[0].y=1;
        cells.cells[0].z=0;
        // fine //
        //parte a parte//
        for(int i=0; i< 250; i++){
            for(int j=0; j< 250; j++){
                Log.e(TAG, "cella ++");
                Point point = publisherPoint.newMessage();
                point.setX(i);
                point.setY(j);
                point.setZ(0);
                listaPoint.add(point);
            }
        }
        // fine //
        Log.e(TAG, "dimensioni lista: "+listaPoint.size());
        messaggio.setCells(listaPoint);

        Log.e(TAG, "dimensioni lista: "+listaPoint.size());
        publisherGridCell.publish(messaggio);
        //potrebbe essere una soluzione per evitare di creare un topic solo per poter creare messaggio!
        //ParameterTree params = connectedNode.getParameterTree();
        //params.
    }*/
    /*
    private TimerTask getNuovoTimerTaskAZero(){ //dato che non è possibile riutilizzarli
        TimerTask timerTaskAZero = new TimerTask() {
            @Override
            public void run() {
                NodoComandoVocale.this.setVelocitaCorrente(0, 0, 0);
                NodoComandoVocale.this.publisherVelocita.publish(velocitaCorrente);
                siÈFermato = true;
                Log.e(TAG, "timer per gazebo");
                NodoComandoVocale.this.memorizzaVelocitàInviata(velocitaCorrente);
                NodoComandoVocale.this.logComandoPubblicato();
            }
        };
        return timerTaskAZero;
    }*/
    /*
    private void timerVelocitàPerGazebo(ComandoVel comando){
        Log.e(TAG, "esecuzione comando usando timer");
        int dove = comando.getDove();
        if(dove == ComandoVel.ANGOLO_DA_RAGGIUNGERE || dove ==-1){
            Applicazione.getInstance().toastSuActivityCorrente(l.getTimerComandoNonDisponibile());
            Log.e(TAG,"comando non disponibile per il timer");
            return;
        }
        if(azzeraTimer(timerGazebo)){
            azzeraSubscriberOdom();
            Applicazione.getInstance().toastSuActivityCorrente(l.getTimerNuovo());
        }
        if(dove == ComandoVel.SINISTRA){
            setVelocitaCorrente(0, 0, Costanti.VELOCITA_ANGOLARE_MAX);
        }else if(dove == ComandoVel.DESTRA){
            setVelocitaCorrente(0, 0, -Costanti.VELOCITA_ANGOLARE_MAX);
        } else if(dove == ComandoVel.AVANTI){
            setVelocitaCorrente(Costanti.VELOCITA_LINEARE_MAX, 0, 0);
        } else if(dove == ComandoVel.INDIETRO){
            setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_MAX, 0, 0);
        }
        final TimerTask timerTaskAZero = getNuovoTimerTaskAZero();  //nuovo perché non riutilizzabile
        timerGazebo = new Timer();
        siÈFermato = false;
        //dovrei calcolare il ritardo tra l'invio e l'avvio anche se, comunque sia, dovrebbe essere colmato da uno stesso simile ritardo per la fermata.
        if(!voglioUsareOdom){
            long millisecondiApprossimati = convertiSecondiInMillisecondiApprossimati(comando.getValoreInSecondiDefault());
            publisherVelocita.publish(velocitaCorrente);
            timerGazebo.schedule(timerTaskAZero, millisecondiApprossimati);
        }else if(tempiPerTimer.getMovimento(dove) != null){
            Double secondi = tempiPerTimer.getTempoPerTimer(comando);
            if(secondi == null){
                secondi = comando.getValoreInSecondiDefault();
            }
            long millisecondiApprossimati = convertiSecondiInMillisecondiApprossimati(secondi);
            publisherVelocita.publish(velocitaCorrente);
            timerGazebo.schedule(timerTaskAZero, millisecondiApprossimati);
        } else {
            Applicazione.getInstance().toastSuActivityCorrente(l.getNoTabella()+l.getTentRecuperoDaTopic()+Costanti.NOME_TOPIC_ODOMETRIA);
            final long millisecondiApprossimati = convertiSecondiInMillisecondiApprossimati(comando.getValoreInSecondiDefault());
            sottoscrittoreOdom(dove);
            final Timer provvisorio = new Timer();
            provvisorio.schedule(new TimerTask(){
                int tentativi = 1;
                @Override
                public void run() {
                    Log.e(TAG, "avvio timer provvisorio");
                    if(NodoComandoVocale.this.èProntoSottoscrittoreOdom || tentativi > TENTATIVI_ODOM){
                        if(tentativi > TENTATIVI_ODOM){
                            Log.e(TAG, "odom terminato per tentativi");
                            subscriber_odom.shutdown();
                            èProntoSottoscrittoreOdom = false;
                            Applicazione.getInstance().toastSuActivityCorrente(l.getErrRecuperoDaOdometria());
                        }
                        publisherVelocita.publish(velocitaCorrente); // consapevole del ritardo di invio
                        timerGazebo.schedule(timerTaskAZero, millisecondiApprossimati);
                        NodoComandoVocale.this.azzeraTimer(provvisorio);
                    }
                    tentativi++;
                }
            },10, 20);
        }
    }*/
    //////// LINGUE //////////////////////////
    /* interfaccia vecchia Lingua
    public interface ILinguaVecchio {
    Locale getLingua();

    String getMeno();

    String[] getChiaviAvanti();

    String[] getChiaviIndietro();

    String[] getChiaviDestra();

    String[] getChiaviSinistra();

    String[] getChiaviAngolo();

    String[] getChiaviFermo();

    String[] getCoordinataX();

    String[] getCoordinataY();

    String[] getCoordinataW();

    String[] getChiaviVai();    // ricavabili, da pattern

    String[] getChiaviSalva();

    String[] getChiaviRiposizione();

    Pattern getPatternDoubleCompleto(); // pattern

    Pattern getPatternMetri();

    Pattern getPatternSecondi();

    Pattern getPatternGradi();

    Pattern getPatternVaiSalvata();

    Pattern getPatternX();

    Pattern getPatternY();

    Pattern getPatternW();

    Pattern getPatternPosizioneDaSalvare();

    Pattern getPatternPosizioneDaSalvareIncompleto();
}
    */
    /* costruttore lingua comandoEN
     public LinguaVecchioEN(){
        meno = "minus";
        coordinataX = new String[]{"x", "ex"};
        coordinataY = new String[]{"y", "wye"};
        coordinataW = new String[]{"w", "orientation", "omega"};
        chiaviAvanti= new String[]{"forward", "ahead", "on", "forth", "cristmovt"};
        chiaviIndietro = new String[]{"back"};
        chiaviDestra = new String[]{"right"};
        chiaviSinistra = new String[]{"left"};
        chiaviAngolo = new String[]{"angle", "straighten", "straightened"};
        chiaviFermo = new String[]{"stop", "still", "firm"};
        chiaviVai = new String[]{"go"};
        chiaviSalva = new String[]{"save", "store", "stores"};
        chiaviRiposiziona = new String[]{"reposition", "repositioned", "repositioning", "relocate"};
        String regexMenoDinamico = "(("+meno+"|-)( )?)?";
        String regexDoubleCompletoDinamico = regexMenoDinamico + ComandiVocali.REGEX_DOUBLE_SOLO_POSITIVI;
        String globaliComuniSMG = " ((for|of) )?" + regexDoubleCompletoDinamico;
        String regexMetri = globaliComuniSMG + "( meter[s] |( )?m )"; //globaliComuniSMG + "( " + l.getMetri2() + " |( )?m )";
        String regexSeco = globaliComuniSMG + "( second[s] |( )?s )";
        String regexGrad = globaliComuniSMG + "( degree[s] |( )?° )";
        String regexVaiPosizioneSalvataNuovo = "(" + getStringOrForRegex(chiaviVai) + ") (to|in|to the) (position )?" + ComandiVocali.REGEX_FORMATO_NOME_SALVABILE;
        String regexDoubleXNuovo = " (" + getStringOrForRegex(coordinataX) + ")( )?" + regexDoubleCompletoDinamico;
        String regexDoubleYNuovo = " (" + getStringOrForRegex(coordinataY) + ")( )?" + regexDoubleCompletoDinamico;
        String regexDoubleWNuovo = " (" + getStringOrForRegex(coordinataW) + ")( )?" + regexDoubleCompletoDinamico;
        String regexSalvaPosizionePP = "(" + getStringOrForRegex(chiaviSalva) + ") (the )?(location|position) (as|with (the )?name)";
        String regexSalvaPosizione = regexSalvaPosizionePP + " " + ComandiVocali.REGEX_FORMATO_NOME_SALVABILE;
        patternDoubleCompleto = Pattern.compile(regexDoubleCompletoDinamico);
        patternMetri = Pattern.compile(regexMetri);
        patternSecondi = Pattern.compile(regexSeco);
        patternGradi = Pattern.compile(regexGrad);
        patternX = Pattern.compile(regexDoubleXNuovo);
        patternY = Pattern.compile(regexDoubleYNuovo);
        patternW = Pattern.compile(regexDoubleWNuovo);
        patternVaiSalvata = Pattern.compile(regexVaiPosizioneSalvataNuovo);
        patternPosizioneDaSalvareIncompleto = Pattern.compile(regexSalvaPosizionePP);
        patternPosizioneDaSalvare = Pattern.compile(regexSalvaPosizione);
    }
    */
    /* costruttore lingua comando It
    public LinguaVecchioIT(){
        meno = "meno";
        coordinataX = new String[]{"x", "ics"};
        coordinataY = new String[]{"y", "ipsilon","ypsilon"};
        coordinataW = new String[]{"w", "orientamento", "omega"};
        chiaviAvanti= new String[]{"avanti", "cristmovt"};
        chiaviIndietro = new String[]{"indietro"};
        chiaviDestra = new String[]{"destra"};
        chiaviSinistra = new String[]{"sinistra"};
        chiaviAngolo = new String[]{"raddrizzati","angolo"};
        chiaviFermo = new String[]{"fermo", "fermati", "stop"};
        chiaviVai = new String[]{"vai", "andare", "recati"};
        chiaviSalva = new String[]{"salva", "salvare", "memorizza", "memorizzare"};
        chiaviRiposiziona = new String[]{"riposiziona", "riposizionati", "riposizionamento", "ricollocare", "ricollocati"};
        String regexMenoDinamico = "(("+meno+"|-)( )?)?";
        String regexDoubleCompletoDinamico = regexMenoDinamico + ComandiVocali.REGEX_DOUBLE_SOLO_POSITIVI;

        ///  angolo di 90 gradi // gira all'angolo 90° // gira all'angolo zaino 90°
        // (( (per|di) )| )  //" ((per|di) )?"
        //( per | di | )
        String globaliComuniSMG = " " + regexDoubleCompletoDinamico;
        String regexMetri = globaliComuniSMG + "( metr(i|o) |( )?m )";
        String regexSeco = globaliComuniSMG + "( second(i|o) |( )?s )";
        String regexGrad = globaliComuniSMG + "( grad(i|o) |( )?° )";
        String regexVaiPosizioneSalvataNuovo = "(" + getStringOrForRegex(chiaviVai) + ") (in|da|all|allo|alla|nella) (posizione )?" + ComandiVocali.REGEX_FORMATO_NOME_SALVABILE;
        String regexDoubleXNuovo = " (" + getStringOrForRegex(coordinataX) + ")( )?" + regexDoubleCompletoDinamico;
        String regexDoubleYNuovo = " (" + getStringOrForRegex(coordinataY) + ")( )?" + regexDoubleCompletoDinamico;
        String regexDoubleWNuovo = " (" + getStringOrForRegex(coordinataW) + ")( )?" + regexDoubleCompletoDinamico;
        String regexSalvaPosizionePP = "(" + getStringOrForRegex(chiaviSalva) + ") (la )?(posizione) (come|con il nome)";
        String regexSalvaPosizione = regexSalvaPosizionePP + " " + ComandiVocali.REGEX_FORMATO_NOME_SALVABILE;
        patternDoubleCompleto = Pattern.compile(regexDoubleCompletoDinamico);
        patternMetri = Pattern.compile(regexMetri);
        patternSecondi = Pattern.compile(regexSeco);
        patternGradi = Pattern.compile(regexGrad);
        patternX = Pattern.compile(regexDoubleXNuovo);
        patternY = Pattern.compile(regexDoubleYNuovo);
        patternW = Pattern.compile(regexDoubleWNuovo);
        patternVaiSalvata = Pattern.compile(regexVaiPosizioneSalvataNuovo);
        patternPosizioneDaSalvareIncompleto = Pattern.compile(regexSalvaPosizionePP);
        patternPosizioneDaSalvare = Pattern.compile(regexSalvaPosizione);
    }
    */

    //////// TempiPerTimer VECCHIE VERSIONI ///
    /*
    // parte vecchia//
    public Movimento inizializzaMovimento(List<Odometry> listaValoriOdom, final boolean isVelLineare){
        int indiceInizioAccelerazione;
        int indiceFineAccelerazione;
        int indiceInizioFrenata;
        ElementoMovimento[] tab1;
        ElementoMovimento[] tab2;
        double velRegime;

        indiceInizioAccelerazione = indicePrimaVariazioneVelocita(listaValoriOdom, isVelLineare, 0, 0);
        tab1 = creaTabAccelerazioneOFrenata(listaValoriOdom, true, isVelLineare, indiceInizioAccelerazione, 0);
        if(tab1 == null){
            Log.e(TAG,"--->    Tab1 nulla");
            System.out.println("--->    Tab1 nulla");
            return null;
        }
        int dimTab1 = tab1.length-1;
        indiceFineAccelerazione = indiceInizioAccelerazione+dimTab1;
        velRegime = listaValoriOdom.get(indiceFineAccelerazione).getTwist().getTwist().getLinear().getX();
        if(!isVelLineare){
            velRegime = listaValoriOdom.get(indiceFineAccelerazione).getTwist().getTwist().getAngular().getZ();
        }
        indiceFineAccelerazione = indiceFineAccelerazione + MAX_VEL_UGUALI_CONSECUTIVE; //perché sono già stati scansionati
        indiceInizioFrenata = indicePrimaVariazioneVelocita(listaValoriOdom, isVelLineare, indiceFineAccelerazione, velRegime);
        /////////////// SOSTITUZIONE ultimo valore tab1 con velocità media di regime //////////////////////////////////
        try{
            velRegime = Math.abs(mediaVelTra(listaValoriOdom, isVelLineare, indiceFineAccelerazione-MAX_VEL_UGUALI_CONSECUTIVE, indiceInizioFrenata));
        } catch(IndexOutOfBoundsException e) {
            Log.e(TAG, "non è stata fatta la media");
        }
        ElementoMovimento daSostituire = tab1[dimTab1];
        //double metriRicalcolati = Segmento.metriPercosi(tab1[dimTab1-1].getTempo(), tab1[dimTab1-1].getVelocità(), daSostituire.getTempo(), velRegime);
        tab1[dimTab1] = new ElementoMovimento(daSostituire.getTempo(), velRegime);//, metriRicalcolati);//
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        tab2 = creaTabAccelerazioneOFrenata(listaValoriOdom, false, isVelLineare, indiceInizioFrenata, tab1[dimTab1].getVelocità());
        if(tab2 == null){
            Log.e(TAG,"--->    Tab2 nulla");
            System.out.println("--->    Tab2 nulla");
            return null;
        }
        return new Movimento(tab1, tab2);
    }

    private double mediaVelTra(List<Odometry> listaValoriOdom, final boolean isVelLineare, int iInf, int iSup){
        if(iInf >= iSup || iInf < 0 || iInf >= listaValoriOdom.size()){
            Log.e(TAG,"iInf:"+iInf+" iSup:"+iSup+" dimLista:"+listaValoriOdom.size());
            throw new IndexOutOfBoundsException();
        }
        double somma = 0;
        for (int i = iInf; i < iSup; i++) {
            if (isVelLineare) {
                somma = somma+listaValoriOdom.get(i).getTwist().getTwist().getLinear().getX();
            } else {
                somma = somma+listaValoriOdom.get(i).getTwist().getTwist().getAngular().getZ();
            }
        }
        return somma/(iSup-iInf);
    }

    private int indicePrimaVariazioneVelocita(List<Odometry> listaValoriOdom, boolean isVelLineare,int indicePartenza, double velocitaRiferimento){
        for(int i = indicePartenza; i < listaValoriOdom.size(); i++){
            double velocitaAttuale;
            if(isVelLineare){
                velocitaAttuale = listaValoriOdom.get(i).getTwist().getTwist().getLinear().getX();
            } else {
                velocitaAttuale = listaValoriOdom.get(i).getTwist().getTwist().getAngular().getZ();
            }
            if(Math.abs(velocitaRiferimento-velocitaAttuale) > DELTA_VELOCITA){
                return i-1;
            }
        }
        return -1;
    }

    private ElementoMovimento[] creaTabAccelerazioneOFrenata(List<Odometry> listaValoriOdom, boolean isAccelerazione, boolean isVelLineare, int indiceInizio, double velPrimoElemento){
        if(indiceInizio == -1){
            Log.e(TAG, "indice inizio: -1");
            return null;
        }
        Log.e(TAG, "tabella");
        System.out.println("tabella");
        int contatoreVelR = 0;
        int contatoreVelInferiori = 0;
        int iL = 0;
        List<ElementoMovimento> lista = new ArrayList<>();
        Odometry primoElemento = listaValoriOdom.get(indiceInizio);
        int idSeqPrec = primoElemento.getHeader().getSeq();
        lista.add(new ElementoMovimento(primoElemento.getHeader().getStamp().totalNsecs(), velPrimoElemento));//, 0.0));
        for(int i = indiceInizio+1; i<listaValoriOdom.size(); i++){
            // precedente, viene preso dalla lista per assicurare le condizioni iniziali, soprattutto i metri.
            double vP = lista.get(iL).getVelocità();
            double tP = lista.get(iL).getTempo();
            Log.e(TAG,"vel tab :"+vP);
            System.out.println("vel tab :"+vP);
            Odometry successivo = listaValoriOdom.get(i);
            int idSeqSuc = successivo.getHeader().getSeq();
            if(idSeqSuc-idSeqPrec != 1){
                //potrei anche usarli, ma dovrei avere la sicurezza che gli estremi dell'insieme che andrò a creare siano sicuri
                //Cioè siano rispettate queste due condizioni:
                //-la prima variazione preceduta da un valore i cui ID siano consegutivi
                //-l'ultimo valore sia accompagnato da un i cui ID siano conseguito
                Log.e(TAG, idSeqPrec+"errore sequenza dati Odom!"+idSeqSuc);
                return null;
            }
            idSeqPrec = idSeqSuc;
            double vS;
            if(isVelLineare){
                vS = Math.abs(successivo.getTwist().getTwist().getLinear().getX());
            } else {
                vS = Math.abs(successivo.getTwist().getTwist().getAngular().getZ());
            }
            //modulo per gestire anche il caso in cui vada indietro
            long tS = successivo.getHeader().getStamp().totalNsecs(); //ATTENZIONE
            //double metriDaPrecedente = Segmento.metriPercosi(tP, vP, tS*1.0E-9D, vS);
            //if(metriDaPrecedente < 0){
                //Log.e(TAG,"i tempi dei valori sono scorretti!");
                //return null;
            //}
            lista.add(new ElementoMovimento(tS, vS));//, metriDaPrecedente));
            if(!isAccelerazione){
                double triangolazione = vP;
                vP = vS;
                vS = triangolazione;
            }
            if((vS-vP) > DELTA_VELOCITA){ //la velocità è concorde con la fase di accelerazione/decelerazione prevista
                contatoreVelInferiori = 0;
                contatoreVelR = 0;
            }else if((vP-vS) > DELTA_VELOCITA){ //la velocità non è concorde con la fase di accelerazione/decelerazione prevista
                contatoreVelInferiori++;
                if(contatoreVelInferiori > MAX_VEL_NEGATIVE_CONSECUTIVE){
                    Log.e(TAG, "la velocità non si comporta come dovrebbe");
                    System.out.println("la velocità non si comporta come dovrebbe");
                    return null; //troppi. non dovrebbe essere normale
                }
            }else if(Math.abs(vS-vP) <= DELTA_VELOCITA){  //la fase prevista forse sta terminando
                contatoreVelR++;
                if(contatoreVelR == MAX_VEL_UGUALI_CONSECUTIVE){ //la fase è terminata
                    int j = lista.size() - MAX_VEL_UGUALI_CONSECUTIVE;
                    lista = lista.subList(0, j);
                    if(!isAccelerazione){
                        // ATTENZIONE ALLA TRIANGOLAZIONE vP <--> vS
                        if(vP > VALORE_DA_CONSIDERARE_ZERO){ //se la fase è di decelarazione dovrebbe essere 0
                            System.out.println("valore dovrebbe essere 0");
                            return null;
                        }
                        ElementoMovimento ultimo = lista.get(j-1);
                        //ElementoMovimento penultimo = lista.get(j-2);
                        //metriDaPrecedente = Segmento.metriPercosi(penultimo.getTempo(), penultimo.getVelocità(), ultimo.getTempo(), 0);
                        ultimo = new ElementoMovimento(ultimo.getTempo(), 0);//, metriDaPrecedente);
                        lista.remove(j-1);
                        lista.add(ultimo);
                        Log.e(TAG, "elemento sostituito");
                    }
                    Log.e(TAG, "raggiunta velocità di regime!");
                    return lista.toArray(new ElementoMovimento[0]);
                }
            }
            iL++;
        }
        Log.e(TAG, "è frnut lu gtton e nun't è bastat");
        System.out.println("è finito il gettone");
        return null;
    }
    */
    /*
    private Movimento inizializzaMovimentoN(List<Odometry> listaValoriOdom, final boolean isVelLineare){
        Felix[] felixes = creaListaSpecificaDaOdometria(listaValoriOdom, isVelLineare);
        //aggiungere controllo che il primo e ultimo elemento delle due tabelle sia effettivamente prossimo a zero.
        int iInizioAccelerazione = indicePrimaVariazioneVelocitaN(felixes, 0, 0);
        int iFineAccelerazione = primoIndiceFaseRegime(felixes, iInizioAccelerazione);
        int iFineFrenata = -1;
        if(iFineAccelerazione != -1){ //esiste l'inizio di una fase a velocità costante di almeno 6 elementi.
            double velRegimeR = felixes[iFineAccelerazione].velocità;
            int iInizioFrenata = indicePrimaVariazioneVelocitaN(felixes, iFineAccelerazione, velRegimeR);
            if(iInizioFrenata != -1){ // esiste la fine della fase a velocità costante.
                for(int i = iFineAccelerazione+1; i <= iInizioFrenata; i++){
                    velRegimeR = velRegimeR + felixes[i].velocità;
                }
                velRegimeR = velRegimeR/(iInizioFrenata-iFineAccelerazione);
                for(int i = iInizioFrenata; i < felixes.length; i++){
                    if(Math.abs(felixes[i].velocità) < VALORE_DA_CONSIDERARE_ZERO){
                        iFineFrenata = i;
                        break;
                    }
                }
                // correzione
                felixes[]

                ///
                ElementoMovimento[] tabA = creaTabellaPerMovimento(felixes, iInizioAccelerazione, iFineAccelerazione);
                ElementoMovimento[] tabF = creaTabellaPerMovimento(felixes, iInizioFrenata, iFineFrenata);
                if(tabA != null && tabF != null){ //correzione e controllo limiti delle tabelle
                    tabA[0] = new ElementoMovimento(tabA[0].getTempo(), 0);
                    tabA[tabA.length-1] = new ElementoMovimento(tabA[tabA.length-1].getTempo(), velRegime);
                    tabF[0] = new ElementoMovimento(tabF[0].getTempo(), velRegime);
                    tabF[tabA.length-1] = new ElementoMovimento(tabF[tabF.length-1].getTempo(), 0);
                }
                return new Movimento(tabA, tabF); //qui verranno fatti i controlli ora. Dove è giusto che siano.

            }
        }
        return null;
    }

    private int primoIndiceFaseRegime(Felix[] felixes, int indicePartenza){
        if(indicePartenza > 0){
            int cont = 0;
            double velRiferimento = felixes[indicePartenza].velocità;
            for(int i = indicePartenza+1; i < felixes.length; i++){
                if(Math.abs(velRiferimento - felixes[i].velocità) <= DELTA_VELOCITA){
                    cont++;
                    if(cont == MAX_VEL_UGUALI_CONSECUTIVE){ //la considero come fase a regime
                        return i - MAX_VEL_UGUALI_CONSECUTIVE;
                    }
                } else {
                    cont = 0;
                }
            }
        }
        return -1;
    }

    private ElementoMovimento[] creaTabellaPerMovimento(Felix[] felixes, int indiceInizio, int indiceFine){
        int dim = indiceFine - indiceInizio;
        if(dim < 2 || indiceInizio < 0 || indiceFine > felixes.length-1){
            return null;
        }
        ElementoMovimento[] tabella = new ElementoMovimento[dim];
        int j = 0;
        for(int i = indiceInizio; i < indiceFine; i++){
            if(felixes[i+1].indiceSequenza - felixes[i].indiceSequenza != 1){
                Log.e(TAG, felixes[i].indiceSequenza+"errore sequenza dati Odom!"+felixes[i+1].indiceSequenza);
                return null;
            }
            tabella[j] = new ElementoMovimento(felixes[i].tempoNs, felixes[i].velocità);
            j++;
        }
        tabella[j] = new ElementoMovimento(felixes[indiceFine].tempoNs, felixes[indiceFine].velocità);
        return tabella;
    }

    private int indicePrimaVariazioneVelocitaN(Felix[] felixes, int indicePartenza, double velocitaRiferimento){
        if(indicePartenza > -1 && indicePartenza < felixes.length){
            for(int i = indicePartenza; i < felixes.length; i++){
                if(Math.abs(velocitaRiferimento-felixes[i].velocità) > DELTA_VELOCITA){
                    return i-1;
                }
            }
        }
        return -1;
    }

    private Felix[] creaListaSpecificaDaOdometria(List<Odometry> listaValoriOdom, final boolean èVelLineare){
        Felix[] array = new Felix[listaValoriOdom.size()];
        int i = 0;
        for(Odometry messaggio: listaValoriOdom){
            int indiceSequenza = messaggio.getHeader().getSeq();
            long tempo = messaggio.getHeader().getStamp().totalNsecs();
            double velocita;
            if(èVelLineare){
                velocita = messaggio.getTwist().getTwist().getLinear().getX();
            } else {
                velocita = messaggio.getTwist().getTwist().getAngular().getZ();
            }
            array[i] = new Felix(indiceSequenza, tempo, velocita);
            i++;
        }
        return array;
    }

    private class Felix{
        private int indiceSequenza;
        private long tempoNs;
        private double velocità;

        public Felix(int indiceSequenza, long tempoNs, double velocità) {
            this.indiceSequenza = indiceSequenza;
            this.tempoNs = tempoNs;
            this.velocità = velocità;
        }
    }
    */
    /*
    private final int NUMERO_MAX_ITERAZIONI = 100;
    public static final double VALORE_DA_CONSIDERARE_ZERO = 0.00000015;
    public static final double DELTA_VELOCITA = 0.0001;

    private double ricercaTempoTimerRimanenteVecchio(double velInf, double velSup, double metriRimanenti, double tA1, double tA2, CoefficientiRetta a, double tF1, double tF2, CoefficientiRetta f){
        //ricerca binaria iterativa!
        Log.e(TAG, //System.out.println(
                "-------- VALORI USATI RICERCA BINARIA--------"+
                "\nvInf: "+ velInf +
                "\nvSup: "+velSup+
                "\nvmetri rimanenti: "+metriRimanenti+
                "\ntA1: "+tA1+
                "\ntA2: "+tA2+
                "\nmA: "+a.getM()+ " qA: "+a.getQ()+
                "\ntF1: "+tF1+
                "\ntF2: "+tF2+
                "\nmF: "+f.getM()+ " qF: "+f.getQ()
        );
        if(metriRimanenti < 0 || Math.abs(metriRimanenti) <= VALORE_DA_CONSIDERARE_ZERO){
            return tA1;
        }
        double mA = a.getM();
        double qA = a.getQ();
        double mF = f.getM();
        double qF = f.getQ();
        double velConfronto = (velSup+velInf)/2;
        double limInf = velInf;
        double limSup = velSup;
        int i = 0;
        while(i < NUMERO_MAX_ITERAZIONI){
            i++;
            tA2 = segmento.xDaEquazioneRetta(velConfronto, mA, qA);
            double t2 = segmento.xDaEquazioneRetta(velConfronto, mF, qF);
            double metriPA = segmento.metriPercosi(tA1, velInf, tA2, velConfronto);
            double metriPF = segmento.metriPercosi(t2, velConfronto, tF2, velInf);
            double somma = metriPA+metriPF;
            if(Math.abs(somma-metriRimanenti) <= VALORE_DA_CONSIDERARE_ZERO){
                break;
            }else if(somma > metriRimanenti){
                limSup = velConfronto;
            } else{
                limInf = velConfronto;
            }
            velConfronto = (limInf+limSup)/2;
        }
        return tA2;
    }

    public Double getTempoPerTimer(ComandoVel comando){
        if(comando.getTipo() == ComandoVel.SECONDI){
            return comando.getValoreInSecondiDefault();
        }
        int dove = comando.getDove();
        Double risultato = null;
        this.lock = true;
        if(dove == ComandoVel.AVANTI){
            risultato = risultatoDaTabella(comando, avanti);
        } else if(dove == ComandoVel.INDIETRO){
            risultato = risultatoDaTabella(comando, indietro);
        } else if(dove == ComandoVel.DESTRA){
            risultato = risultatoDaTabella(comando, destra);
        } else if(dove == ComandoVel.SINISTRA){
            risultato = risultatoDaTabella(comando, sinistra);
        }
        this.lock = false;
        return risultato;
    }
    public Double risultatoDaTabella(ComandoVel comando, Movimento movimento){
        //System.out.println("VECCHIA VERSIONE");
        if(movimento != null ){// &&  movimento.correttamenteInizializzato){
            Log.e(TAG, "tabelle presenti");
            Double tempoNecessario = movimento.getValoreCalcolato(comando.getValoreDefault());
            if(tempoNecessario != null){
                Log.e(TAG, "valore era già stato calcolato");
                return tempoNecessario;
            }
            double mTAF = movimento.getPercorsiTotaliAccelerazione()+movimento.getPercorsiTotaliFrenata();
            Log.e(TAG, "mTAF: "+mTAF );
            if(comando.getValoreDefault() < mTAF){
                tempoNecessario = tempoPerTimerGenerale(comando.getValoreDefault(), movimento);
            } else {
                double valoreRichiesto = comando.getValoreDefault();
                valoreRichiesto = valoreRichiesto - mTAF;
                ElementoMovimento[] tabA = movimento.getTabellaAccelerazione();
                double tempoAccelerazione = tabA[tabA.length-1].getTempo() - tabA[0].getTempo();
                double tempo= (valoreRichiesto/movimento.getVelRegime());
                Log.e(TAG, "tempo accelerazione: "+ tempoAccelerazione +"\ttempo di regime: "+tempo+ "\tvelocità regime:"+movimento.getVelRegime());
                tempoNecessario = tempoAccelerazione + tempo;
            }
            movimento.salvaRisultato(comando.getValoreDefault(), tempoNecessario);
            return tempoNecessario;
        }
        return null;
    }
    public double tempoPerTimerGeneraleV(double metriRichiesti, Movimento movimento){
        metriRichiesti = Math.abs(metriRichiesti);
        if(metriRichiesti <= VALORE_DA_CONSIDERARE_ZERO){
            return 0;
        }
        //una sorta di interpolazione lineare su due funzioni. Avrei dovuto usare la spline?
        //Log.e(TAG, "ricerca funzioni");
        //////////////////inizializzazione///////////////////////////
        ElementoMovimento[] tabA = movimento.getTabellaAccelerazione();
        ElementoMovimento[] tabF = movimento.getTabellaFrenata();
        int iTabA = tabA.length-2;
        int iTabF = 1;
        double velSuperiore = tabA[iTabA+1].getVelocità();
        double tA1 = tabA[iTabA].getTempo();
        double tA2 = tabA[iTabA+1].getTempo();
        double tF1 = tabF[0].getTempo(); //>tf2
        double tF2 = tabF[iTabF].getTempo();
        double mA = movimento.getPercorsiTotaliAccelerazione()-tabA[iTabA+1].getPercorsiDalPrecedente();
        double mF = movimento.getPercorsiTotaliFrenata()-tabF[1].getPercorsiDalPrecedente(); //attenzione ad assegnare bene questo questo valore! //prima =0 //
        CoefficientiRetta coeA = segmento.coefficientiRetta(tA1, tabA[iTabA].getVelocità(), tA2, velSuperiore);
        CoefficientiRetta coeF = segmento.coefficientiRetta(tF1, velSuperiore, tF2, tabF[iTabF].getVelocità());
        /////////////////fine inizializzazione/////////////////////
        for(int i = tabA.length+tabF.length-2; i>0; i--){
            ElementoMovimento elemRifA = tabA[iTabA];
            ElementoMovimento elemRifF = tabF[iTabF];
            ///*
            System.out.println("-------------------------------------");
            System.out.println(
                            "\nvelocità superiore: "+velSuperiore+
                            "\ntA1: "+tA1+
                            "\ntA2: "+tA2+
                            "\ncoefficienti accele m: "+coeA.getM()+ " q: "+coeA.getQ()+
                            "\ntF1: "+tF1+
                            "\ntF2: "+tF2+
                            "\ncoefficienti frenat m: "+coeF.getM()+ " q: "+coeF.getQ()
            );
            System.out.println("eleRifeA:"+elemRifA.getVelocità()+"\teleRifeF: "+elemRifF.getVelocità()+"\n\n");
            //
            if(elemRifA.getVelocità() == elemRifF.getVelocità()){
                //System.out.println("PRIMO: (velRiferimentoA == velRifermentoF)");
                double velInferiore = elemRifA.getVelocità();
                //System.out.println("velocita inferiore: "+velInferiore);
                double metriAttuali = mA + mF;
                //System.out.println("metri attuali= "+mA+"+"+mF +" = "+metriAttuali);
                if(metriAttuali <= metriRichiesti){
                    double metriRimanenti = metriRichiesti - metriAttuali;
                    double risultatoRicerca = ricercaTempoTimerRimanente(velInferiore, velSuperiore, metriRimanenti, elemRifA.getTempo(), tA2, coeA, tF1, elemRifF.getTempo(), coeF);
                    return risultatoRicerca-tabA[0].getTempo();
                }
                velSuperiore = velInferiore;
                tA2 = elemRifA.getTempo();
                tF1 = elemRifF.getTempo();
                mA = mA-tabA[iTabA].getPercorsiDalPrecedente();
                iTabF++;
                iTabA--;
                mF = mF-tabF[iTabF].getPercorsiDalPrecedente();
                tA1 = tabA[iTabA].getTempo();
                tF2 = tabF[iTabF].getTempo();
                coeA = segmento.coefficientiRetta(tA1, tabA[iTabA].getVelocità(), tA2, velSuperiore);
                coeF = segmento.coefficientiRetta(tF1, velSuperiore, tF2, tabF[iTabF].getVelocità());
            } else if(elemRifA.getVelocità() > elemRifF.getVelocità()){
                //System.out.println("SECONDO:  (velRiferimentoA > velRifermentoF)");
                double velInferiore = elemRifA.getVelocità();

                double v = elemRifF.getVelocità();
                double t = elemRifF.getTempo();
                tF2 = segmento.xDaEquazioneRetta(velInferiore, coeF);
                double metriPercorsi = segmento.metriPercosi(tF2, velInferiore, t, v);
                double metriAttuali = mA+mF+metriPercorsi;
                //System.out.println("velocita inferiore: "+velInferiore);
                //System.out.println("metri attuali= "+mA+"+"+mF+"+"+metriPercorsi +" = "+metriAttuali);
                if(metriAttuali <= metriRichiesti){
                    double metriRimanenti = metriRichiesti-metriAttuali;
                    double risultatoRicerca = ricercaTempoTimerRimanente(velInferiore, velSuperiore, metriRimanenti, tA1, tA2, coeA, tF1, tF2, coeF);
                    return risultatoRicerca-tabA[0].getTempo();
                }
                velSuperiore = velInferiore;
                tA2 = tA1;
                tF1 = tF2;
                tF2=t;  //
                mA = mA-tabA[iTabA].getPercorsiDalPrecedente();
                iTabA--;
                tA1 = tabA[iTabA].getTempo(); //!! possibile eccezione se non si esce nel if precedente
                //coeA = retta.coefficientiRetta(tA1, tabA[iTabA].getVelocità(), tA2, velSuperiore); //va benissimo
                coeA = segmento.coefficientiRetta(tA1, tabA[iTabA].getVelocità(), tabA[iTabA+1].getTempo(), tabA[iTabA+1].getVelocità());
            } else {
                //System.out.println("TERZO: (velRiferimentoA < velRifermentoF)");
                double velInferiore = elemRifF.getVelocità();
                double v = elemRifA.getVelocità();
                double t = elemRifA.getTempo();
                tA1 = segmento.xDaEquazioneRetta(velInferiore, coeA);
                double metriPercorsi = segmento.metriPercosi(t, v, tA1, velInferiore);
                double metriAttuali = mA+mF+metriPercorsi;
                //System.out.println("velocita inferiore: "+velInferiore);
                //System.out.println("metri attuali= "+mA+"+"+mF+"+"+metriPercorsi +" = "+metriAttuali);
                if(metriAttuali <= metriRichiesti){
                    double metriRimanenti = metriRichiesti-metriAttuali;
                    double risultatoRicerca = ricercaTempoTimerRimanente(velInferiore, velSuperiore, metriRimanenti, tA1, tA2, coeA, tF1, tF2, coeF);
                    return risultatoRicerca-tabA[0].getTempo();
                }
                velSuperiore = velInferiore;
                tF1 = tF2;
                tA2 = tA1;
                tA1 = t;
                iTabF++;
                mF = mF-tabF[iTabF].getPercorsiDalPrecedente();
                tF2 = tabF[iTabF].getTempo();
                //coeF = retta.coefficientiRetta(tF1, velSuperiore, tF2, tabF[iTabF].getVelocità());// va benissimo
                coeF = segmento.coefficientiRetta(tabF[iTabF-1].getTempo(), tabF[iTabF-1].getVelocità(), tF2, tabF[iTabF].getVelocità());
            }
        }
        //System.out.println("da controllare cause");
        return 0;
    }
    private double ricercaTempoTimerRimanente(double velInf, double velSup, double metriRimanenti, double tA1, double tA2, CoefficientiRetta a, double tF1, double tF2, CoefficientiRetta f){
        //quella veloce, con meno operazioni effettutate nel ciclo
        if(metriRimanenti <= VALORE_DA_CONSIDERARE_ZERO){
            return tA1;
        }
        double mA = a.getM();
        double qA = a.getQ();
        double mF = f.getM();
        double qF = f.getQ();
        if(mA > 0){
            double velConfronto = (velSup+velInf)*0.5;
            double velInfQuadro = velInf*velInf;
            double invAccelerazioneMezzi = (1/mA)*0.5;
            double invFrenataMezzi = (1/mF)*0.5;
            for(int i = 0; i < NUMERO_MAX_ITERAZIONI; i++){
                // Tecnica che dovrebbe fare meno operazioni nel ciclo
                double velConfrontoQuadro = velConfronto*velConfronto;
                double percorsiAccelerazione = (velConfrontoQuadro - velInfQuadro)*invAccelerazioneMezzi;
                double percorsiFrenata = (velInfQuadro - velConfrontoQuadro)*invFrenataMezzi;
                double somma = percorsiAccelerazione + percorsiFrenata;
                if(Math.abs(somma-metriRimanenti) <= VALORE_DA_CONSIDERARE_ZERO){
                    return segmento.xDaEquazioneRetta(velConfronto, mA, qA);
                }else if(somma > metriRimanenti){
                    velSup = velConfronto;
                } else {
                    velInf = velConfronto;
                }
                velConfronto = (velInf+velSup)*0.5;
            }
            tA2 = segmento.xDaEquazioneRetta(velConfronto, mA, qA);
        } else if(mA == 0){ //l'accelerazione è pari a 0 e dunque il tratto ha velocità costante
            return metriRimanenti/velSup + tA1;
        }
        return tA2;
    }

    public double ricercaSpazioFrenataN(Movimento movimento, double velocita){
        velocita = Math.abs(velocita);
        if(movimento == null || velocita <= VALORE_DA_CONSIDERARE_ZERO){
            return 0;
        }
        ElementoMovimento[] tabF = movimento.getTabellaFrenata();
        double velMaxTabella = movimento.getVelRegime();
        if((velocita) > velMaxTabella-0.002){  //fare in modo che rientrino pìù casi
            return movimento.getPercorsiTotaliFrenata();
        }
        int iMin = 0;
        int iMax = tabF.length-1;
        while(iMin <= iMax){
            int iMed = (iMax+iMin)/2;
            //System.out.println("iMed:"+iMed);
            if(tabF[iMed].getVelocità() < velocita){
                iMax = iMed;
            } else if (tabF[iMed+1].getVelocità() > velocita){
                iMin = iMed;
            } else {
                int iMedPiù1 = iMed+1;
                double v2 = tabF[iMedPiù1].getVelocità();
                double t2 = tabF[iMedPiù1].getTempo();
                Coefficienti coeff = segmento.coefficientiRetta(tabF[iMed].getTempo() ,tabF[iMed].getVelocità(), t2, v2);
                double t1 = segmento.xDaEquazioneRetta(velocita, coeff); //la x corrisponte al tempo
                return movimento.getSommatoriaFrenata()[iMedPiù1] + segmento.metriPercosi(t1, velocita, t2, v2);
            }
        }
        return 0;
    }
    */
    /*public double getSpazioFrenataLineare(double velocita){
        Log.e(TAG, "velocità attuale"+ velocita);
        if(velocita > 0){
            return ricercaSpazioFrenata(avanti, velocita);
        } else {
            return ricercaSpazioFrenata(indietro, velocita);
        }
    }
    private double ricercaSpazioFrenata(Movimento movimento, double velocita){
        if(movimento != null && velocita > VALORE_DA_CONSIDERARE_ZERO){
            if(velocita < (movimento.getVelRegime() - 0.002)){
                return ricercaSpazioFrenataSenzaControlli(movimento, velocita);
            }
            return movimento.getPercorsiTotaliFrenata();
        }
        return 0;
    }
    private double ricercaSpazioFrenataSenzaControlli(Movimento movimento, double velocita){
        ElementoMovimento[] tabF = movimento.getTabellaFrenata();
        int iMin = 0;
        int iMax = tabF.length-1;
        while(iMin <= iMax){
            int iMed = (iMax+iMin)/2;
            if(tabF[iMed].getVelocità() < velocita){
                iMax = iMed;
            } else if (tabF[iMed+1].getVelocità() > velocita){
                iMin = iMed;
            } else {
                //dalla formula V^2 = Vo^2 + 2*acc*spazio , dove acc = (v2-v1)/(t2-t2)
                // ==> spazio = (V^2 - Vo^2)/2*acc , ora per evitare un ulteriore divisione
                // spazio = (V^2 - Vo^2)*0.5*acc^-1
                int iMedPiù1 = iMed+1;
                double v2 = tabF[iMedPiù1].getVelocità();
                double t2 = tabF[iMedPiù1].getTempo();
                double unoSuAcc = (t2 - tabF[iMed].getTempo()) / (v2 - tabF[iMed].getVelocità());
                double spazioParziale = (v2*v2 - velocita*velocita)*0.5*unoSuAcc;
                return movimento.getSommatoriaPercorsiFrenata(iMedPiù1)+spazioParziale;
            }
        }
        return 0;
    }
     */
    /*public boolean creazioneESetMovimento(List<Odometry> listaValoriOdom, int dove){
        //if(lock){ //aggiungere i valori di odom ad una possibile lista di attesa e proseguire l'operazioni in seguito
        //    return false;
        //}
        if(dove == ComandoVel.AVANTI){
            daCancellare(listaValoriOdom);
            avanti = inizializzaMovimento(listaValoriOdom, true);
            return movimentoDiversoDaNull(avanti);
        }
        if(dove == ComandoVel.INDIETRO){
            daCancellare(listaValoriOdom);
            indietro = inizializzaMovimento(listaValoriOdom, true);
            return movimentoDiversoDaNull(indietro);
        }
        if(dove == ComandoVel.DESTRA){
            daCancellare2(listaValoriOdom);
            destra = inizializzaMovimento(listaValoriOdom, false);
            return movimentoDiversoDaNull(destra);
        }
        if(dove == ComandoVel.SINISTRA ){
            daCancellare2(listaValoriOdom);
            sinistra = inizializzaMovimento(listaValoriOdom, false);
            return movimentoDiversoDaNull(sinistra);
        }
        return false;
    }

    private boolean movimentoDiversoDaNull(Movimento movimento){
        return movimento != null;
    }
    //da sistemare ---> in accordo con NodoComandoVocale
    public Double getTempoPerTimer(ComandoVel comando){
         //* IL VECCHIO AVEVA:
         //* - il lock = true prima dell'operazione
         //* - il valore presente in comando.getValoreDefault() era in valore assoluto.
         //* - il lock = false dopo l'operazione
         //*
         //* Dunque controllare che non vi siano problemi

        if(comando.getTipo() == ComandoVel.SECONDI){
            return comando.getValoreInSecondiDefault();
        }
        Movimento movimento = getMovimento(comando.getDove());
        if(movimento != null){
            return movimento.tempoPerTimerGenerale(comando.getValoreDefault());
        }
        return null;
    }

    public Movimento getMovimento(int dove){
        if(dove == ComandoVel.AVANTI) return avanti;
        if(dove == ComandoVel.INDIETRO) return indietro;
        if(dove == ComandoVel.DESTRA) return destra;
        if(dove == ComandoVel.SINISTRA) return sinistra;
        return null;
    }

    public boolean eliminaMovimento(int dove){
        //if(lock || dove == null){
        //   return false;
        //}
        if(dove == ComandoVel.AVANTI){
            this.avanti = null;
            return true;
        }
        if(dove == ComandoVel.INDIETRO){
            this.indietro = null;
            return true;
        }
        if(dove == ComandoVel.DESTRA){
            this.destra = null;
            return true;
        }
        if(dove == ComandoVel.SINISTRA){
            this.sinistra = null;
            return true;
        }
        return false;
    }*/
    ////////////////////////
}
