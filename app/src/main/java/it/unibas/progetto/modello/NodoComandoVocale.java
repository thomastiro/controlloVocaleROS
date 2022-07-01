package it.unibas.progetto.modello;

import android.util.Log;

import org.apache.commons.math3.util.Precision;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import actionlib_msgs.GoalID;
import dynamic_reconfigure.Config;
import dynamic_reconfigure.DoubleParameter;
import dynamic_reconfigure.GroupState;
import dynamic_reconfigure.Reconfigure;
import dynamic_reconfigure.ReconfigureRequest;
import dynamic_reconfigure.ReconfigureResponse;
import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.PoseWithCovarianceStamped;
import geometry_msgs.Twist;
import it.unibas.progetto.modello.comando_vocale.ComandoVel;
import it.unibas.progetto.modello.comando_vocale.sinistra_destra.soggetto_verbo_oggetto.ComandiVocali;
import it.unibas.progetto.modello.comando_vocale.ComandoUniversale;
import it.unibas.progetto.persistenza.DAOException;
import nav_msgs.Odometry;
import sensor_msgs.LaserScan;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.controllo.ControlloConnessione;
import it.unibas.progetto.modello.layer_personali.GrigliaLayer;
import it.unibas.progetto.modello.utilita.Movimento;
import it.unibas.progetto.modello.utilita.Punto2D;
import it.unibas.progetto.modello.utilita.Segmento;
import it.unibas.progetto.modello.utilita.Target;
import it.unibas.progetto.modello.utilita.TempiPerTimer;
import it.unibas.progetto.modello.utilita.UtilitàSetMessaggiRos;

public class NodoComandoVocale extends AbstractNodeMain {
    private final String TAG = NodoComandoVocale.class.getName();
    private final int TENTATIVI_ODOM = 100;

    private volatile boolean possoPrenderePoseSalvata = false;
    private volatile boolean voglioRilevareGliOstacoli = true;
    private volatile boolean voglioUsareAMCL = true;
    private volatile boolean voglioUsareOdom = true;
    private boolean prontoPerNuovoComando = false;
    private boolean parametriAMCLCorretti = false;
    private boolean salvaPose = false;
    private boolean siÈFermato = false;
    private boolean èProntoSottoscrittoreOdom = false;

    private ILinguaDinamica l;
    private TempiPerTimer tempiPerTimer;
    private ControlloConnessione controlloConnessione;
    private Timer timerGazebo;
    private GraphName graphName = GraphName.of(Costanti.GRAPH_NAME);
    private ConnectedNode connectedNode;
    private Publisher<Twist> publisherVelocita;
    private Twist velocitaCorrente;
    private Publisher<PoseStamped> publisherPosizione;
    private PoseStamped posizioneObbiettivo;
    private Publisher<GoalID> publisherGoalID;
    private Subscriber<PoseWithCovarianceStamped> subscriber_amcl_pose;
    private Subscriber<LaserScan> subscriber_scan;
    private Subscriber<Config> subscriber_parameter_update;
    private Subscriber<Odometry> subscriber_odom;
    private DoubleParameter parametroPrecedenteLineare;
    private DoubleParameter parametroPrecedenteAngolare;
    private Publisher<PoseWithCovarianceStamped> publisherInitialPose;
    private Config messaggioConfigDynamicMessage;
    private GroupState messaggioGroupParameter;
    private ServiceClient<ReconfigureRequest, ReconfigureResponse> serviceClientAMCL;
    private ModelloPersistente modelloPersistente;
    private ComandiVocali comandiVocali;

    public NodoComandoVocale(ILinguaDinamica l) {
        this.l = l;
        this.modelloPersistente = Applicazione.getInstance().getModelloPersistente();
        this.controlloConnessione = Applicazione.getInstance().getControlloConnessione();
        this.comandiVocali = new ComandiVocali();
        inizializzaTempiPerTimer();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return graphName;
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        super.onError(node, throwable);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log.e(TAG, "onStart inizio");
        this.connectedNode = connectedNode;
        publisherVelocita = connectedNode.newPublisher(Costanti.NOME_TOPIC_VELOCITA,geometry_msgs.Twist._TYPE);
        velocitaCorrente = publisherVelocita.newMessage();
        publisherPosizione = connectedNode.newPublisher(Costanti.NOME_TOPIC_POSIZIONE, PoseStamped._TYPE);
        posizioneObbiettivo = publisherPosizione.newMessage();
        publisherGoalID = connectedNode.newPublisher(Costanti.NOME_TOPIC_CANCELLA_POSIZIONE, GoalID._TYPE);
        publisherInitialPose = connectedNode.newPublisher(Costanti.NOME_TOPIC_INITIAL_POSE, PoseWithCovarianceStamped._TYPE);
        // necessario per creare messaggi/oggetti, in quanto l'unico modo di creare questi oggetti. Costanti.NOME_TOPIC_PARAMETER_UPDATE
        Publisher<Config> publisherDynamic = connectedNode.newPublisher("inutile/0", Config._TYPE);
        Publisher<DoubleParameter> publisherDoubleParam = connectedNode.newPublisher("inutile/1", DoubleParameter._TYPE);
        Publisher<GroupState> publisherGroupParam = connectedNode.newPublisher("inutile/5", GroupState._TYPE);
        parametroPrecedenteLineare = publisherDoubleParam.newMessage();
        parametroPrecedenteAngolare = publisherDoubleParam.newMessage();
        messaggioConfigDynamicMessage = publisherDynamic.newMessage();
        messaggioGroupParameter = publisherGroupParam.newMessage();
        publisherDynamic.shutdown();
        publisherDoubleParam.shutdown();
        publisherGroupParam.shutdown();
        cambioAutomaticoParametriAMCL(0.0, 0.0);
        prontoPerNuovoComando = true;
        Log.e(TAG, "onStart fine");
    }

    public boolean eseguiComando(ArrayList<String> frasiDaUsare){
        if(!prontoPerNuovoComando){
            Log.e(TAG, "non è pronto o non è connesso");
            return false;
        }
        prontoPerNuovoComando = false;
        Log.e(TAG,"inizio estrazione da frase");
        comandiVocali.setDatiPermanenti((DatiPermanenti) modelloPersistente.getPersistentBean(Costanti.DATI_PERMANENTI, DatiPermanenti.class));
        ComandoUniversale comandoUni = comandiVocali.convertiInComandoUniversale(frasiDaUsare);
        Log.e(TAG,"fine estrazione da frase");
        switch (comandoUni.getGenereRisultato()){
            case ComandoUniversale.RISULTATO_NESSUNO:
                Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                Log.e(TAG, "NESSUN RISULTATO");
                break;
            case ComandoUniversale.RISULTATO_POSIZIONE:
                azzeraSottiscrittoreAmclPose();
                azzeraSottiscrittoreScanPose();
                eseguiComandoInvioPosizione((Target)comandoUni.getRisultato());
                break;
            case ComandoUniversale.RISULTATO_COMANDO_VELOCITA:
                azzeraSottiscrittoreAmclPose();
                azzeraSottiscrittoreScanPose();
                eseguiComandoVelocità((ComandoVel)comandoUni.getRisultato());
                break;
            case ComandoUniversale.RISULTATO_SALVA_POSIZIONE:
                eseguiComandoSalvaggioPosizioneDaAMCL((String)comandoUni.getRisultato());
                break;
            case ComandoUniversale.RISULTATO_RIPOSIZIONA:
                azzeraSottiscrittoreAmclPose();
                azzeraSottiscrittoreScanPose();
                eseguiComandoRiposizione((Target)comandoUni.getRisultato());//il risultato dato può essere null
                break;
        }
        Log.e(TAG, "è pronto per fare un'altra sciammerca");
        return prontoPerNuovoComando = true;
    }

    public void cambioLingua(){
        comandiVocali.cambiaLingua();
    }

    private void eseguiComandoInvioPosizione(Target target){
        setPosizioneObbiettivoPoseStamped(target.getX(), target.getY(), target.getAlfa());
        publisherPosizione.publish(posizioneObbiettivo);
        memorizzaPosizioneInviata(posizioneObbiettivo);
        logComandoPubblicato();
        Log.e(TAG, "FRASE_POSIZIONE_RICONOSCIUTA OK");
    }

    private void eseguiComandoVelocità(ComandoVel comando){
        if(comando.getDove() == ComandoVel.FERMO){
            publisherGoalID.publish(publisherGoalID.newMessage()); //per fermare un eventuale guida autonoma verso un obiettivo dato a move_base.
            setVelocitaCorrente(0,0,0);
            publisherVelocita.publish(velocitaCorrente);
            Log.e(TAG, "FERMO");
        } else {
            NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
            if(voglioUsareAMCL && comando.getTipo() != ComandoVel.SECONDI){
                boolean esisteTopicAmclPose = controlloConnessione.esisteQuestoTopic("/"+Costanti.NOME_TOPIC_AMCL_POSE,nodeConfiguration.getMasterUri());
                if(parametriAMCLCorretti && esisteTopicAmclPose){
                    dragonballZ(comando);
                    controllaPresentaOstacoli(comando);
                    return;
                }
            }
            timerVelocitàPerGazebo(comando);
            controllaPresentaOstacoli(comando);
        }
    }

    private void eseguiComandoSalvaggioPosizioneDaAMCL(final String nomePosizione){
        if(!voglioUsareAMCL){
            Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallitoAMCL());
            return;
        }
        //se attualmente già è attivo un sottoscrittore.
        if(subscriber_amcl_pose != null){
            //significa che è attualmente già in uso, per un comando?
            //devo sincronizzarmi lanciando un nuovo timer thread e ottenere la posizione dal sottoscrittore già in esecuzione.
            salvaPose = true;
            Log.e(TAG, "avvio timer salva posizione");
            final Timer timer = new Timer();
            timer.schedule(new TimerTask(){
                int tentativi = 20;
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
                        return;
                    } else if(tentativi < 2){
                        Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
                        azzeraTimer(timer);
                    }
                    tentativi--;
                }
            },10, 100);
            return;
        }
        //provo ad avviarne un altro
        NodeConfiguration nodeConfiguration = (NodeConfiguration)Applicazione.getInstance().getModello().getBean(Costanti.NODE_CONFIGURATION);
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
                    Applicazione.getInstance().toastSuActivityCorrente(l.getPosizioneSalvata()+": "+nomePosizione);
                    subscriber_amcl_pose.shutdown();
                }
            });
        } else {
            //sostituire testo con "il topic Costanti.NOME_TOPIC_AMCL_POSE non esiste."
            Applicazione.getInstance().toastSuActivityCorrente(l.getComandoFallito());
        }
    }

    private void eseguiComandoRiposizione(Target initialPose){
        fermarsi(null);
        if(initialPose != null){
            pubblicaPosizioneIniziale(initialPose);
            return;
        }
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
        } else {
            Log.e(TAG,"Non è stato possibile riposizionarsi. Non esiste il topic amcl_pose");
            Applicazione.getInstance().toastSuActivityCorrente(l.getErroreRiposizioneAMCL());
        }
    }

    private void cambioAutomaticoParametriAMCL(final double parametroVelocitaLineare, final double parametroVelocitaAngolare){
        subscriber_parameter_update = connectedNode.newSubscriber(Costanti.NOME_TOPIC_PARAMETER_UPDATE, Config._TYPE);
        subscriber_parameter_update.addMessageListener(new MessageListener<Config>(){
            @Override
            public void onNewMessage(Config messaggio) {
                int cont = 2;
                List<DoubleParameter> daCambiare = new ArrayList<>();
                for(DoubleParameter dP: messaggio.getDoubles()){
                    Log.e(TAG, "nome valore double: "+dP.getName());
                    if(dP.getName().equals(Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_ANG)){
                        if(dP.getValue() != parametroVelocitaAngolare){
                            parametroPrecedenteAngolare.setName(dP.getName());
                            parametroPrecedenteAngolare.setValue(dP.getValue());
                            dP.setValue(parametroVelocitaAngolare);
                            daCambiare.add(dP);
                        }
                        cont--;
                    } else if(dP.getName().equals(Costanti.NOME_PARAMETRO_AMCL_AG_POSE_MIN_VEL)){
                        if(dP.getValue() != parametroVelocitaLineare){
                            parametroPrecedenteLineare.setName(dP.getName());
                            parametroPrecedenteLineare.setValue(dP.getValue());
                            dP.setValue(parametroVelocitaLineare);
                            daCambiare.add(dP);
                        }
                        cont--;
                    }
                    if(cont == 0){
                        break;
                    }
                }
                invioListaParametriDoubleServiceAMCL(daCambiare);
            }
        });
    }

    private ServiceClient<ReconfigureRequest, ReconfigureResponse> inizializzaServiceAMCL(){
        ///Non posso usare sempre lo stesso, perché nel caso si spegnesse il nodo e si riaccendesse, non potrei più usare lo stesso service.
        ServiceClient<ReconfigureRequest, ReconfigureResponse> serviceClientAMCL;
        try{
            serviceClientAMCL = connectedNode.newServiceClient(Costanti.NOME_SERVICE_PARAMETERS_AMCL, Reconfigure._TYPE);
            Log.e(TAG,"waglio sei collegato al service amcl!");
        } catch (ServiceNotFoundException e) {
            Log.e(TAG,"servizio non disponibile");
            serviceClientAMCL = null;
        } catch (Exception e){
            Log.e(TAG, "ERRORE DI ALTRO TIPO"+e.getMessage());
            serviceClientAMCL = null;
        }
        if(serviceClientAMCL == null){
            Applicazione.getInstance().toastSuActivityCorrente(l.getErroreServiceAMCL());
        }
        return this.serviceClientAMCL = serviceClientAMCL;
    }

    private void invioListaParametriDoubleServiceAMCL(List<DoubleParameter> listaParametri){
        if(listaParametri != null && listaParametri.isEmpty()){
            Log.e(TAG,"non è stato necessario cambiare i parametri");
            parametriAMCLCorretti = true;
            return;
        }
        parametriAMCLCorretti = false;
        String s = "";
        for(DoubleParameter d:listaParametri){
            s = s + d.getName()+ ": " + d.getValue() + "\n";
        }
        //// NUOVA //////////
        messaggioGroupParameter.setId(0);
        messaggioGroupParameter.setParent(0);
        messaggioGroupParameter.setState(true);
        messaggioGroupParameter.setName(Costanti.NOME_GROUP_PARAMETER);
        List<GroupState> listaGroupStateParameter = messaggioConfigDynamicMessage.getGroups();
        listaGroupStateParameter.add(messaggioGroupParameter);
        messaggioConfigDynamicMessage.setGroups(listaGroupStateParameter);  //necessario??
        messaggioConfigDynamicMessage.setDoubles(listaParametri);//aggiungo i vari parametri
        cambioParametriServiceAMCL(messaggioConfigDynamicMessage, s);
        ///// VECCHIA ///////
        //Config dynamicMessage = getConfigDynamicMessage();
        //dynamicMessage.setDoubles(listaParametri);//aggiungo i vari parametri
        //chiamataCambioParametriServiceAMCL(dynamicMessage, s);
    }

    private void cambioParametriServiceAMCL(Config dynamicMessage, final String messDaPubblicareSuToast){
        inizializzaServiceAMCL();
        if(serviceClientAMCL == null){
            parametriAMCLCorretti = false;
            Applicazione.getInstance().toastSuActivityCorrente(l.getErroreServiceAMCL());
            return;
        }
        ReconfigureRequest request = serviceClientAMCL.newMessage();
        request.setConfig(dynamicMessage);//setta richiesta con dynamicMessage
        serviceClientAMCL.call(request, new ServiceResponseListener<ReconfigureResponse>() {
            @Override
            public void onSuccess(ReconfigureResponse config) {
                parametriAMCLCorretti = true;
                Log.e(TAG,"waglio' la configurazione amcl inviata ha avuto successo ;)");
                Applicazione.getInstance().toastSuActivityCorrente(l.getConfigurazioneAMCL_OK()+"\n"+ messDaPubblicareSuToast);
            }
            @Override
            public void onFailure(RemoteException e) {
                parametriAMCLCorretti = false;
                Log.e(TAG,"ERRORE. la configurazione inviata non ha avuto successo");
                Applicazione.getInstance().toastSuActivityCorrente(l.getConfigurazioneAMCL_NO());
            }
        });
        Log.e(TAG, "dynamicMessage inviato al service\n"+messDaPubblicareSuToast);
    }
    ////////////////////// PARTE CALCOLI ANGOLI ///////////////////
    private boolean èCompresoAritmeticaModulare(double destro, double sinistro, double valore){
        /*
        la funzione è discontinua, e serve per quello.
        es. valori della funzione 0.0,0.1,..., 0.9, 1.0, -1.0, -0.9,..., -0.1, 0.0
        caso di es. 0.9 e -0.9 il seno non potrebbe essere compreso tra i due valori per diverse ragioni, anche se invertito
        */
        if(destro >= sinistro){
            return (valore >= destro || valore <= sinistro);
        }
        return (valore >= destro && valore <= sinistro);
    }

    private double conversioneAngoloPerAMCL(double alfa){
        if(alfa > Math.PI){
            alfa = Math.sin((alfa/2)-Math.PI);
        } else {
            if(alfa <= -Math.PI){
                alfa = alfa+2*Math.PI;
            }
            alfa = Math.sin(alfa/2);
        }
        return alfa;
    }

    public double getRadiantiQuaternioneAMCL(double seno){
        //sì, basta solo il seno per ricavare l'angolo in radianti dal quaternione di AMCL.
        if(seno < 0){
            return 2*(Math.asin(seno)+Math.PI);
        }
        return 2*Math.asin(seno);
    }

    private boolean èCompresoAngoloRad(double angoloObbiettivo, double angoloAttuale, double deltaObbiettivo){
        // ogni angolo alfa 0<=alfa<2PI
        double destra = angoloObbiettivo - deltaObbiettivo;
        double sinistra = angoloObbiettivo + deltaObbiettivo;
        if(destra < 0){
            destra = destra + 2*Math.PI;
        }
        if(sinistra >= 2*Math.PI){
            sinistra = sinistra - 2*Math.PI;
        }
        return èCompresoAritmeticaModulare(destra, sinistra, angoloAttuale);
    }
    /////////////////////////////////////////////////////////////////////////////////////
    private void timerVelocitàPerGazebo(final ComandoVel comando){
        /*
        il timer è necessario perché altrimenti il software gazebo continuerebbe ad utilizzare
        l'ultima velocità inviatagli (velocitàCorrente). Dato che non è possibile settare la
        distanza e quindi un timeout automatico direttamente da gazebo, viene creato un timer
        che prenderà in input la durata del comando di velocità in secondi.
        */
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
        Movimento movimento = null;
        if(dove == ComandoVel.SINISTRA){
            movimento = tempiPerTimer.getSinistra();
            setVelocitaCorrente(0, 0, Costanti.VELOCITA_ANGOLARE_MAX);
        }else if(dove == ComandoVel.DESTRA){
            movimento = tempiPerTimer.getDestra();
            setVelocitaCorrente(0, 0, -Costanti.VELOCITA_ANGOLARE_MAX);
        } else if(dove == ComandoVel.AVANTI){
            movimento = tempiPerTimer.getAvanti();
            setVelocitaCorrente(Costanti.VELOCITA_LINEARE_MAX, 0, 0);
        } else if(dove == ComandoVel.INDIETRO){
            movimento = tempiPerTimer.getIndietro();
            setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_MAX, 0, 0);
        }
        //dovrei calcolare il ritardo tra l'invio e l'avvio anche se, comunque sia, dovrebbe essere colmato da uno stesso simile ritardo per la fermata.
        if(!voglioUsareOdom || comando.getTipo() == ComandoVel.SECONDI){
            Log.e(TAG, "timer gazebo - secondi default");
            timerVelocitàPerGazeboParteComune(comando.getValoreInSecondiDefault());
        }else if(movimento != null){
            Log.e(TAG, "timer gazebo - movimento - valore default");
            timerVelocitàPerGazeboParteComune(movimento.tempoPerTimerGenerale(comando.getValoreDefault()));
        } else {
            /*
            - avvio il sottoscrittore (con booleano in attesa)
              - nel momento in cui ottengo il primo risultato, cambio lo stato del booleano.
                Altrimenti se è passato troppo tempo lo cambia lo stesso(Come farlo?).
              - chiudo il sottoscrittore nel momento in cui la velocità contenuta negli ultimi 6 messeggi è 0.
            - avvio un nuovo thread che avvii il timer nel momento in cui un booleano cambi stato
            */
            Log.e(TAG, "timer gazebo - tentativo creazione movimento - secondi default ");
            String toast = l.getNoTabella() + l.getTentRecuperoDaTopic() + Costanti.NOME_TOPIC_ODOMETRIA;
            Applicazione.getInstance().toastSuActivityCorrente(toast);
            subscriber_odom = connectedNode.newSubscriber(Costanti.NOME_TOPIC_ODOMETRIA, Odometry._TYPE);
            subscriber_odom.addMessageListener(new AcquisizioneDatiVelocitàOdometria(comando));
            final Timer provvisorio = new Timer();
            provvisorio.schedule(new TimerTask(){
                int tentativi = 1;
                @Override
                public void run() {
                    Log.e(TAG, "avvio timer provvisorio");
                    if(NodoComandoVocale.this.èProntoSottoscrittoreOdom || tentativi > TENTATIVI_ODOM){
                        if(tentativi > TENTATIVI_ODOM){
                            Log.e(TAG, "odom terminato per tentativi");
                            NodoComandoVocale.this.subscriber_odom.shutdown();
                            NodoComandoVocale.this.èProntoSottoscrittoreOdom = false;
                            Applicazione.getInstance().toastSuActivityCorrente(l.getErrRecuperoDaOdometria());
                        }
                        timerVelocitàPerGazeboParteComune(comando.getValoreInSecondiDefault());
                        NodoComandoVocale.this.azzeraTimer(provvisorio);
                    }
                    tentativi++;
                }
            },15, 20);
        }
    }

    private void timerVelocitàPerGazeboParteComune(Double secondi){
        TimerTask timerTaskAZero = new TimerTask() { //non è possibile riutilizzarli
            @Override
            public void run() {
                NodoComandoVocale.this.setVelocitaCorrente(0, 0, 0);
                NodoComandoVocale.this.publisherVelocita.publish(velocitaCorrente);
                NodoComandoVocale.this.siÈFermato = true;
                Log.e(TAG, "timerTaskZero");
                NodoComandoVocale.this.memorizzaVelocitàInviata(velocitaCorrente);
                NodoComandoVocale.this.logComandoPubblicato();
            }
        };
        this.timerGazebo = new Timer();
        long millisecondiApprossimati = convertiSecondiInMillisecondiApprossimati(secondi);
        siÈFermato = false;
        publisherVelocita.publish(velocitaCorrente); //eseguirli il più vicino possibile
        timerGazebo.schedule(timerTaskAZero, millisecondiApprossimati);
    }

    private void dragonballZ(final ComandoVel comando){
        /*
        avrei potuto direttamente calcolare lo spostamento e invare le coordinate della posizione al nodo move_base, ma quello
        che farà non è esattamente quello che voglio.
        Innanzitutto è impreciso(non di poco) sia nel spostamento angolare che in quello lineare. Poi, nel caso inviassi una posizione,
        move_base creerebbe un percorso anche se non fosse necessario, es. vai avanti 10 metri (senza che vi siano ostacoli).
        Per ultima cosa, nel caso vi siano ostacoli non deve aggirarli, si deve fermare prima(se attivata la rilevazione) o
        sbatterci contro.
        Semplicemente si deve limitare a fare quello che è stato chiesto e nient'altro.
        */
        Log.e(TAG, "esecuzione comando usando amcl_pose");
        subscriber_amcl_pose = connectedNode.newSubscriber(Costanti.NOME_TOPIC_AMCL_POSE, PoseWithCovarianceStamped._TYPE);
        subscriber_amcl_pose.addMessageListener(new MessageListenerSottoscrittoreAMCL(comando));
    }

    private long convertiSecondiInMillisecondiApprossimati(double secondi){
        double millisecondiOriginali = 1000*secondi;
        Double millisecondiApprossimati = Precision.round(millisecondiOriginali, 0);
        Log.e(TAG,"s originali: "+secondi + "\tmsec a:"+ millisecondiApprossimati);
        return millisecondiApprossimati.intValue();
    }

    private void memorizzaPosizioneInviata(PoseStamped pS){
        double x = pS.getPose().getPosition().getX();
        double y = pS.getPose().getPosition().getY();
        double w = pS.getPose().getOrientation().getW();
        double z = pS.getPose().getOrientation().getZ();
        String stringa= l.getPosizione()+" X:"+x+ "\tY:" + y + "\tquater. sin:"+z+"\tquater. cos"+ w; //int
        Applicazione.getInstance().getModello().putBean(Costanti.COMANDO_INVIATO, stringa);
    }

    private void memorizzaVelocitàInviata(Twist velocita){
        double x = velocita.getLinear().getX();
        double y = velocita.getLinear().getY();
        double z = velocita.getAngular().getZ();
        String stringa = l.getVelocità()+":"+ " X:"+x+ "\tY:" + y + "\tz"+ z;
        Applicazione.getInstance().getModello().putBean(Costanti.COMANDO_INVIATO, stringa);
    }

    private Target nuovoTargetDaPoseAMCL(Pose posaAttuale, String nomePosizione){
        double alfa = Math.toDegrees(getRadiantiQuaternioneAMCL(posaAttuale.getOrientation().getZ()));
        double x = posaAttuale.getPosition().getX();
        double y = posaAttuale.getPosition().getY();
        return new Target(alfa, x, y, nomePosizione);
    }

    //--POSIZIONI SALVATE IN MANIERA NON PERMANENTE
    /*
    private void salvaTargetNonPermanente(Target target){
        --POSIZIONI SALVATE IN MANIERA NON PERMANENTE
        Map<String,Target> targets = (Map<String,Target>)Applicazione.getInstance().getModello().getBean(Costanti.TARGET_SALVATI);
        if(targets==null){
            targets=new HashMap<>();
        }
        targets.put(target.getNome(),target);
        Applicazione.getInstance().getModello().putBean(Costanti.TARGET_SALVATI,targets); //farlo persistente?
        --POSIZIONI SALVATE IN MANIERA NON PERMANENTE
    }*/

    private void salvaTargetPermanente(Target target){
        //--POSIZIONI SALVATE IN MANIERA PERMANENTE
        DatiPermanenti datiPermanenti = (DatiPermanenti)Applicazione.getInstance().getModelloPersistente().getPersistentBean(Costanti.DATI_PERMANENTI, DatiPermanenti.class);
        Map<String, Target> targets = datiPermanenti.getTargets();
        if(targets == null){
            datiPermanenti.setTargets(new HashMap<String, Target>());
            targets = datiPermanenti.getTargets();
        }
        targets.put(target.getNome(), target);
        Applicazione.getInstance().getModelloPersistente().saveBean(Costanti.DATI_PERMANENTI, datiPermanenti);
    }

    private void logComandoPubblicato(){
        String comandoInviato = (String)Applicazione.getInstance().getModello().getBean(Costanti.COMANDO_INVIATO);
        Log.e(TAG, comandoInviato);
        Applicazione.getInstance().toastSuActivityCorrente(comandoInviato);
    }

    private void fermarsi(String s){
        setVelocitaCorrente(0, 0, 0);
        publisherVelocita.publish(velocitaCorrente);
        siÈFermato = true;
        aggiornaObbiettivoGriglia(null);
        Log.e(TAG,""+s);
        Applicazione.getInstance().toastSuActivityCorrente(s);
        azzeraSottiscrittoreAmclPose();
    }

    private void controllaPresentaOstacoli(final ComandoVel comando){
        if(!voglioRilevareGliOstacoli || (comando.getDove() != ComandoVel.INDIETRO && comando.getDove()!= ComandoVel.AVANTI)){
            Log.e(TAG, "controllo ostacoli laser non attivo");
            return;
        }
        Log.e(TAG,"controllo ostacoli laser attivo");
        subscriber_scan = connectedNode.newSubscriber(Costanti.NOME_TOPIC_LASER_SCAN, LaserScan._TYPE);
        subscriber_scan.addMessageListener(new MessageListenerOstacoliLaser());

    }

    private void setVelocitaCorrente(double velocitaLineareX, double velocitaLineareY, double velocitaAngolareZ) {
        UtilitàSetMessaggiRos.setVelocita(velocitaCorrente, velocitaLineareX, velocitaLineareY, velocitaAngolareZ);
    }

    private void setPosizioneObbiettivoPoseStamped(double X, double Y, double orientamento){
        UtilitàSetMessaggiRos.setPosizioneObbiettivoPoseStamped(posizioneObbiettivo, Costanti.NOME_TOPIC_OCCUPACITY_GRID, X, Y, orientamento);
    }

    public void setVoglioUsareOdom(boolean valore){
        this.voglioUsareOdom = valore;
        Log.e(TAG, "voglio usare odom:"+valore);
    }

    public void setVoglioRilevareGliOstacoli(boolean valore){
        this.voglioRilevareGliOstacoli = valore;
        Log.e(TAG, "voglio rilevare gli ostacoli:"+valore);
    }

    public void setVoglioUsareAMCL(boolean valore){
        this.voglioUsareAMCL = valore;
        Log.e(TAG, "voglio usare amcl:"+valore);
    }

    private void aggiornaObbiettivoGriglia(Punto2D obbiettivo){
        GrigliaLayer grigliaLayer = (GrigliaLayer)Applicazione.getInstance().getModello().getBean(Costanti.LAYER_GRIGLIA);
        if(grigliaLayer != null){
            grigliaLayer.aggiornaObbiettivo(obbiettivo);
        }
    }

    private void pubblicaPosizioneIniziale(Target target){
        PoseWithCovarianceStamped posaIniziale = publisherInitialPose.newMessage();
        UtilitàSetMessaggiRos.setHeader(posaIniziale.getHeader(), Costanti.NOME_TOPIC_OCCUPACITY_GRID);
        UtilitàSetMessaggiRos.setPosizionePose(posaIniziale.getPose().getPose(), target.getX(), target.getY(), target.getAlfa());
        //la matrice di covarianza è fissa. Esattamente come avviene con rviz quando viene eseguita la riposizione.
        posaIniziale.getPose().setCovariance(new double[]
                       {0.25, 0.0, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.25, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.06853891945200942});
        Log.e(TAG, "numero sottoscrittori:"+ publisherInitialPose.getNumberOfSubscribers());
        publisherInitialPose.publish(posaIniziale);
        Applicazione.getInstance().toastSuActivityCorrente(l.getOkRiposizione()+target.toString());
    }

    private boolean azzeraTimer(Timer timer){
        if(timer != null){
            timer.cancel();
            timer.purge();
            Log.e(TAG,"timer azzerato");
            return true;
        }
        return false;
    }

    private void azzeraSottiscrittoreAmclPose(){
        if(subscriber_amcl_pose != null){
            subscriber_amcl_pose.shutdown();
            subscriber_amcl_pose = null;
        }
    }

    private void azzeraSottiscrittoreScanPose(){
        if(subscriber_scan != null){
            subscriber_scan.shutdown();
            subscriber_scan = null;
        }
    }

    private void azzeraSubscriberOdom(){
        if(subscriber_odom != null){
            subscriber_odom.shutdown();
            subscriber_odom = null;
        }
    }

    private void inizializzaTempiPerTimer(){
        TempiPerTimer permanente = (TempiPerTimer)Applicazione.getInstance().getModelloPersistente().getPersistentBean(Costanti.TEMPI_PER_TIMER, TempiPerTimer.class);
        if(permanente != null){
            this.tempiPerTimer = permanente;
            Log.e(TAG,"recuperato 'tempiPerTimer' precedentemente salvato");
        } else {
            this.tempiPerTimer = new TempiPerTimer();
        }
        Applicazione.getInstance().getModello().putBean(Costanti.TEMPI_PER_TIMER, tempiPerTimer);
    }

    public void azzeraTempiPerTimer(){
        this.tempiPerTimer = new TempiPerTimer();
        Applicazione.getInstance().getModello().putBean(Costanti.TEMPI_PER_TIMER, this.tempiPerTimer);
        Applicazione.getInstance().getModelloPersistente().saveBean(Costanti.TEMPI_PER_TIMER, this.tempiPerTimer);
    }

    private void chiudiRipristinaParametriAMCL(){
        if(serviceClientAMCL != null){
            List<DoubleParameter> listaDoubleParameter = new ArrayList<>();
            if(parametroPrecedenteAngolare!= null && parametroPrecedenteAngolare.getName() != null){
                listaDoubleParameter.add(parametroPrecedenteAngolare);
            }
            if(parametroPrecedenteLineare != null && parametroPrecedenteLineare.getName() != null){
                listaDoubleParameter.add(parametroPrecedenteLineare);
            }
            invioListaParametriDoubleServiceAMCL(listaDoubleParameter);
            this.serviceClientAMCL.shutdown();
        }
    }

    private void salvaTempiTimerPermantente(){
        Applicazione.getInstance().getModelloPersistente().saveBean(Costanti.TEMPI_PER_TIMER, this.tempiPerTimer);
    }

    @Override
    public void onShutdown(Node node) {
        Log.e(TAG,"onShutDownComplete");
        salvaTempiTimerPermantente();
        chiudiRipristinaParametriAMCL();
        azzeraSottiscrittoreScanPose();
        azzeraSottiscrittoreAmclPose();
        if(subscriber_parameter_update != null){
            subscriber_parameter_update.shutdown();
            subscriber_parameter_update = null;
        }
        azzeraTimer(timerGazebo);
        //azzeraTimer(publisherTimer);
        Applicazione.getInstance().getModello().putBean(Costanti.TEMPI_PER_TIMER, null); //tecnica provvisoria
        super.onShutdown(node);
    }

    @Override
    public void onShutdownComplete(Node node) {
        super.onShutdownComplete(node); //da controllare se faccia qualcos'altro
    }

    //PER SOTTOSCRITTORE AMCL
    private class MessageListenerSottoscrittoreAMCL implements MessageListener<PoseWithCovarianceStamped> {
        private ComandoVel comandoOriginale;
        private ComandoVel comando;
        //bool
        private boolean inizializzato = false;
        private boolean attendereCheSiaFermo = false;
        private boolean isLimitata = false;
        private boolean soloCorrezione = false;
        private boolean obbiettivoSuperato = false;
        //int
        private int tentativiObbiettivoSuperato = 0;
        private int tentativiTraiettoria = 0;
        private int contAggDisP = 0;
        //double
        private double velocitaXVariazione;
        private double distanzaPrecedente;
        private double angoloRetta_AF_O_precedente;
        private double xFinale;
        private double yFinale;
        private double sinDestra;
        private double sinSinistra;
        private double sinDestraR;
        private double sinSinitraR;
        private double xP;
        private double yP;
        private long tP;

        public MessageListenerSottoscrittoreAMCL(final ComandoVel comandoOriginale){
            this.comandoOriginale = comandoOriginale;
            this.comando = comandoOriginale;
        }

        @Override
        public void onNewMessage(PoseWithCovarianceStamped messaggio) {
            Log.e(TAG,"-------------------------------------------------");
            Pose pose = messaggio.getPose().getPose();
            double yA = pose.getPosition().getY();
            double xA = pose.getPosition().getX();
            long tA = messaggio.getHeader().getStamp().totalNsecs();
            // ATTESA CHE SIA FERMO //
            if(attendereCheSiaFermo){
                double velocitàLineareAttuale = Segmento.velocitaMediaNsec(xP, yP, tP, xA, yA, tA);
                // fare anche controllo velocità angolare:
                // poiché reputo impossibile che riesca a compiere una rotazione completa e PERFETTA tra un messaggio e il suo precedente:
                // potrei effettuare il controllo sul sin e quindi l'angolo, verificando che corrispondano approssimativamente (es di 1°)
                if(Math.abs(velocitàLineareAttuale) >= 0.005){
                    xP = xA;
                    yP = yA;
                    tP = tA;
                    controlloSeRichiestoSalvataggioPosa(pose);
                    Log.e(TAG,"è in attesa. velAttuale:"+velocitàLineareAttuale);
                    return;
                }
                if(obbiettivoSuperato){
                    setVelocitaCorrente(velocitaXVariazione, 0, 0);
                    publisherVelocita.publish(velocitaCorrente);
                    obbiettivoSuperato = false;
                }
                attendereCheSiaFermo = false;
            }
            // INIZIALIZZAZIONE //
            double angoloAttuale = getRadiantiQuaternioneAMCL(pose.getOrientation().getZ());
            if(!inizializzato){
                double valore = comando.getValoreDefault();
                Log.e(TAG,"xIniziale:"+ xA + " yIniziale:"+yA + " orientamento:"+angoloAttuale*180/Math.PI);
                //parte iniz. destra o sinistra
                if(ComandoVel.GRADI_A_RADIANTI == comando.getTipo()){
                    Log.e(TAG,"ANGOLO DA EFFETTUARE:\t-radiandi:"+valore + "\t-gradi:"+valore*180/Math.PI);
                    if(comando.getDove() == ComandoVel.SINISTRA){
                        angoloAttuale = angoloAttuale + valore;
                        setVelocitaCorrente(0,0, Costanti.VELOCITA_ANGOLARE_MAX);
                    }else if(comando.getDove()== ComandoVel.DESTRA){
                        angoloAttuale = angoloAttuale - valore;
                        setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);
                    }else if(comando.getDove() == ComandoVel.ANGOLO_DA_RAGGIUNGERE){ //per portarsi ad un angolo preciso ssj3 livello D: ?!?!
                        setVelocitaCorrente(0,0, Costanti.VELOCITA_ANGOLARE_MAX); //sinistra
                        if(0.0 <= valore && valore < Math.PI){
                            if(angoloAttuale > valore && angoloAttuale <= (valore+Math.PI)){
                                setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                            }
                        } else if(!(angoloAttuale < valore && angoloAttuale >= (valore-Math.PI))){
                            setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                        }
                        /*} else {
                            if(angoloAttuale < valore && angoloAttuale >= (valore-Math.PI)){//resta u monn cum è

                            } else {
                                setVelocitaCorrente(0,0, -Costanti.VELOCITA_ANGOLARE_MAX);//verso destra
                        }*/
                        angoloAttuale = valore;
                    }
                    angoloAttuale = angoloAttuale%(2*Math.PI);
                    Log.e(TAG,"OBBIETTIVO:\t\t\t\t-radiandi:"+angoloAttuale + "\t-gradi:"+angoloAttuale*180/Math.PI);
                    sinDestra = conversioneAngoloPerAMCL(angoloAttuale-Costanti.PRECISIONE_ANGOLARE_RAD);
                    sinSinistra = conversioneAngoloPerAMCL(angoloAttuale+Costanti.PRECISIONE_ANGOLARE_RAD);
                    sinSinitraR = conversioneAngoloPerAMCL(angoloAttuale+Costanti.PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD);
                    sinDestraR = conversioneAngoloPerAMCL(angoloAttuale-Costanti.PRECISIONE_ANGOLARE_PER_RIDUZIONE_RAD);
                    // nel caso in cui ci siano ritardi di pacchetti
                    if(èCompresoAritmeticaModulare(sinDestraR, sinSinitraR, pose.getOrientation().getZ())){//poseIniziale.getOrientation().getZ())){
                        setVelocitaCorrente(0, 0, velocitaCorrente.getAngular().getZ()/11);
                        Log.e(TAG,"velocità già ridotta");
                    }
                }
                // parte iniz. avanti o indietro
                if(ComandoVel.METRI == comando.getTipo()) {
                    distanzaPrecedente = valore+1;
                    Log.e(TAG, "valore metri: " + valore);
                    if (comando.getDove() == ComandoVel.AVANTI) {
                        angoloRetta_AF_O_precedente = angoloAttuale; //sotto controllo
                        xFinale = xA+valore*Math.cos(angoloAttuale);
                        yFinale = yA+valore*Math.sin(angoloAttuale);
                        setVelocitaCorrente(Costanti.VELOCITA_LINEARE_MAX, 0, 0);
                    } else {
                        angoloRetta_AF_O_precedente = angoloAttuale-Math.PI;
                        xFinale = xA-valore*Math.cos(angoloAttuale);
                        yFinale = yA-valore*Math.sin(angoloAttuale);
                        setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_MAX, 0, 0);
                    }
                    aggiornaObbiettivoGriglia(new Punto2D((float)xFinale,(float)yFinale));
                    Log.e(TAG, "valore metri- x finale:" + xFinale + " y finale:"+yFinale);
                }
                tentativiObbiettivoSuperato = 0;
                isLimitata = false;
                siÈFermato = false;
                inizializzato = true;
                tP = tA;
                publisherVelocita.publish(velocitaCorrente);
            }
            // COMANDO ANGOLARE//
            if(ComandoVel.GRADI_A_RADIANTI == comando.getTipo()){
                double senoAttuale = pose.getOrientation().getZ();
                Log.e(TAG,"A:"+ sinDestra +" B:"+ sinSinistra +" sinAttuale:"+senoAttuale);
                if(èCompresoAritmeticaModulare(sinDestra, sinSinistra, senoAttuale)){
                    if(soloCorrezione){
                        setVelocitaCorrente(velocitaXVariazione,0,0);
                        publisherVelocita.publish(velocitaCorrente);
                        comando = comandoOriginale;
                        //reinizializza queste variabili
                        soloCorrezione = false;
                        isLimitata = false;
                    } else {
                        fermarsi(l.getObbiettivoRaggiuntoSi());
                    }
                } else if(!isLimitata && èCompresoAritmeticaModulare(sinDestraR, sinSinitraR, senoAttuale)){
                    // limitatore velocità arrivati in prossimità dell'angolo
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
                    tentativiObbiettivoSuperato++;
                }
            }
            // COMANDO LINEARE//
            if(ComandoVel.METRI == comando.getTipo()) {
                double velocitàAttualeMedia = Segmento.velocitaMediaNsec(xP, yP, tP, xA, yA, tA);
                boolean velocitàInviataPositiva = true;
                if(velocitaCorrente.getLinear().getX() < 0){ //quella inviata!
                    velocitàAttualeMedia = -velocitàAttualeMedia;
                    velocitàInviataPositiva = false;
                }
                double spazioFrenataAZero = tempiPerTimer.getSpazioFrenataLineare(velocitàAttualeMedia); //risultato sempre >= 0
                double xAF = spazioFrenataAZero*Math.cos(angoloAttuale);
                double yAF = spazioFrenataAZero*Math.sin(angoloAttuale);
                if(!velocitàInviataPositiva){
                    xAF = -xAF;
                    yAF = -yAF;
                }
                xAF = xA+xAF;
                yAF = yA+yAF;
                double distanzaAttualeDinamica = Segmento.distanzaTraDuePunti2D(xAF, yAF, xFinale, yFinale);
                Log.e(TAG, "distanzaAttualeDinamica:"+distanzaAttualeDinamica);
                // PARTE OBIETTIVO RAGGIUNTO //
                if(distanzaAttualeDinamica <= Costanti.PRECISIONE_LINEARE_M){
                    // fermarsi e ricontrollo (quindi attesa che sia effettivamente fermo)
                    fermarsi(l.getObbiettivoRaggiuntoSi());
                    return;
                }
                //Log.e(TAG,"spazioFrenata:"+spazioFrenataAZero+" xA:"+xA+" yA:"+yA+" xAF:"+xAF+" yAF:"+yAF);
                // PARTE OBIETTIVO SUPERATO //
                double angoloRetta_AF_O = Segmento.angoloSegmento(xAF, yAF, xFinale, yFinale);
                obbiettivoSuperato = obbiettivoSuperato(angoloRetta_AF_O_precedente, angoloRetta_AF_O);
                if(obbiettivoSuperato){
                    // se fatta bene dovrebbe poi oscillare tra un superato ed un altro, il tutto separato dal RAGGIO_OBBIETTIVO
                    setVelocitaCorrente(0, 0, 0);
                    publisherVelocita.publish(velocitaCorrente);
                    if(velocitàInviataPositiva){
                        velocitaXVariazione = -Costanti.VELOCITA_LINEARE_RIDOTTA;
                    } else {
                        velocitaXVariazione = Costanti.VELOCITA_LINEARE_RIDOTTA;
                    }
                    attendereCheSiaFermo = true;
                    isLimitata = true;
                    tentativiObbiettivoSuperato++;
                    Log.e(TAG,"obbiettivo superato");
                    Applicazione.getInstance().toastSuActivityCorrente(l.getObbiettivoSuperato());
                }
                // PARTE RIDUZIONE VELOCITÀ //
                else if(!isLimitata && distanzaAttualeDinamica <= Costanti.PRECISIONE_LINEARE_RIDUZIONE_M){
                    if(velocitàInviataPositiva){
                        setVelocitaCorrente(Costanti.VELOCITA_LINEARE_RIDOTTA, 0, 0);
                    } else {
                        setVelocitaCorrente(-Costanti.VELOCITA_LINEARE_RIDOTTA, 0, 0);
                    }
                    publisherVelocita.publish(velocitaCorrente);
                    isLimitata = true;
                    Log.e(TAG,"velocità lineare ridotta");
                    Applicazione.getInstance().toastSuActivityCorrente(l.getVelLinRidotta());
                }
                // PARTE CONTROLLO E CORREZIONE TRAIETTORIA //
                double angoloRetta_A_O = Segmento.angoloSegmento(xA, yA, xFinale, yFinale);
                double distanzaAttuale = Segmento.distanzaTraDuePunti2D(xA, yA, xFinale, yFinale);
                if(!obbiettivoSuperato){
                    ComandoVel comandoCorrezione = correzioneTraiettoria(angoloRetta_A_O, velocitàInviataPositiva, angoloAttuale, distanzaAttuale, distanzaPrecedente);
                    if(comandoCorrezione != null){
                        velocitaXVariazione = velocitaCorrente.getLinear().getX();
                        setVelocitaCorrente(0, 0, 0);
                        publisherVelocita.publish(velocitaCorrente);
                        comando = comandoCorrezione;
                        tentativiTraiettoria++;
                        soloCorrezione = true;
                        inizializzato = false;
                        attendereCheSiaFermo = true;
                        Log.e(TAG,"correzione traiettoria");
                    } else {
                        tentativiTraiettoria = 0;
                    }
                }
                angoloRetta_AF_O_precedente = angoloRetta_AF_O;
                xP = xA;
                yP = yA;
                tP = tA;
                if(contAggDisP == 3){//a causa dei problemi legati ad una possibile posizione erratta è necessario farlo carburare ;)
                    distanzaPrecedente = distanzaAttuale;
                    contAggDisP = 0;
                } else {
                    contAggDisP++;
                }
            }
            // SALVATAGGIO POSIZIONE ATTUALE RICHIESTO DURANTE MOVIMENTO//
            controlloSeRichiestoSalvataggioPosa(pose);
            // TROPPI TENTATIVI //
            if(tentativiObbiettivoSuperato > Costanti.TENTATIVI_OBBIETTIVO || tentativiTraiettoria > Costanti.TENTATIVI_OBBIETTIVO){
                fermarsi(l.getObbiettivoRaggiuntoNo());
            }
            Log.e(TAG,"--- --- --- --- --- --- --- --- ---");
        }

        private void controlloSeRichiestoSalvataggioPosa(Pose pose){
            if(salvaPose){
                Applicazione.getInstance().getModello().putBean(Costanti.POSE_SALVATO, pose);
                salvaPose = false;
                possoPrenderePoseSalvata = true;
            }
        }

        private boolean obbiettivoSuperato(double angoloPrecedente, double angoloAttuale){
            Log.e(TAG, "AngoloPrec:"+ angoloPrecedente + "AngoloAtt:"+ angoloAttuale);
            return !èCompresoAngoloRad(angoloPrecedente, angoloAttuale, Math.PI/2);
        }
        /// CORREGGERE INIZIALIZZAZIONE TABELLA FRENATA IN CASO DI VALORI TROPPO PICCOLI, E CHE QUINDI HANNO POCHI VALORI DI REGIME!!!!
        private ComandoVel correzioneTraiettoria(double angoloObbiettivo, boolean staAndandoAvanti, double angoloAttuale, double distanzaAttuale, double distanzaPrecedente){
            //Log.e(TAG,"angolo obbiettivo"+ angoloObbiettivo);
            //Log.e(TAG, "\ndistanzaAttuale:"+distanzaAttuale+"  distanzaPrecedente:"+distanzaPrecedente);
            if(!staAndandoAvanti){                              //serve in caso vada all'indietro
                angoloObbiettivo = angoloObbiettivo-Math.PI;    //sfrutto le proprietà geometriche del cerchio
            }
            //Log.e(TAG,"angolo obbiettivo"+ angoloObbiettivo);
            double angoloTangente = Math.asin(Costanti.PRECISIONE_LINEARE_M/distanzaAttuale);
            if(angoloTangente < Costanti.PRECISIONE_ANGOLARE_CT_RAD){ //nel caso sia troppo lontano dall'obiettivo
                angoloTangente = Costanti.PRECISIONE_ANGOLARE_CT_RAD;
            }
            if(distanzaAttuale >= distanzaPrecedente || !èCompresoAngoloRad(angoloObbiettivo, angoloAttuale, angoloTangente)){
                angoloObbiettivo = angoloObbiettivo*180/Math.PI;
                //Log.e(TAG,"angolo obbiettivo dopo: "+angoloObbiettivo+ " angolo tangente:"+angoloTangente*180/Math.PI);
                return new ComandoVel(angoloObbiettivo, ComandoVel.GRADI_A_RADIANTI, ComandoVel.ANGOLO_DA_RAGGIUNGERE);
            }
            return null;
        }
        /*
        private double velocitaMediaNsec1(double x1, double y1, long t1, double x2, double y2, long t2){
            if(t1 == t2){
                return 0;
            }
            double diffX = x2-x1;
            double diffY = y2-y1;
            return (Math.sqrt(diffX*diffX+diffY*diffY)/(t2-t1))*1.0E9D; //approssimarlo con Precision.round(millisecondiOriginali, 0);
        }*/
    }

    // PER SOTTOSCRITTORE LASER
    private class MessageListenerOstacoliLaser implements MessageListener<LaserScan> {
        private final double VELOCITÀ_DA_CONSIDERARE_ZERO = 0.0001;
        private boolean inizializzato = false;
        private int [] indiciIndietro = new int[3];
        private int [] indiciAvanti = new int[3];
        private float distanzaOstacoloA = Costanti.DISTANZA_MINIMA_OSTACOLO;
        private float distanzaOstacoloI = Costanti.DISTANZA_MINIMA_OSTACOLO;

        public MessageListenerOstacoliLaser() {
            inizializzazioneDistanzeOstacolo();
        }

        private void inizializzazioneDistanzeOstacolo(){
            Movimento avanti = tempiPerTimer.getAvanti();
            Movimento indietro = tempiPerTimer.getIndietro();
            if(avanti != null){
                distanzaOstacoloA = distanzaOstacoloA+(float) avanti.getPercorsiTotaliFrenata();
                Log.e(TAG, "distanza ostacolo avanti: "+ distanzaOstacoloA);
            }
            if(indietro != null){
                distanzaOstacoloI = distanzaOstacoloI+(float) indietro.getPercorsiTotaliFrenata();
                Log.e(TAG, "distanza ostacolo indietro: "+ distanzaOstacoloI);
            }
        }

        @Override
        public void onNewMessage(LaserScan messaggio) {
            Log.e(TAG,"nuovo messaggio laser");
            float[] valoriLaser = messaggio.getRanges();
            double velLin = velocitaCorrente.getLinear().getX();
            boolean ostacoloRilevato;
            if(!inizializzato){
                float valoreMin = messaggio.getAngleMin();
                float valoreMax = messaggio.getAngleMax();
                if((valoreMin > 0.0F || valoreMax < 6.28F) && (messaggio.getRangeMin() < Costanti.DISTANZA_MINIMA_OSTACOLO)){
                        /*Non è possibile utilizzare il sensore laser per rilevare ostacoli perché il sensore non è 360 gradi
                         oppure il rangeMin è troppo grande*/
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
            if(Math.abs(velLin) < VELOCITÀ_DA_CONSIDERARE_ZERO){
                ostacoloRilevato = false;
            } else if(velLin > 0.0){
                ostacoloRilevato = presenzaOstacolo(indiciAvanti, valoriLaser, distanzaOstacoloA);
            } else {
                ostacoloRilevato = presenzaOstacolo(indiciIndietro, valoriLaser, distanzaOstacoloI);
            }
            if(ostacoloRilevato) {
                azzeraTimer(timerGazebo);
                azzeraSottiscrittoreAmclPose();
                setVelocitaCorrente(0, 0, 0);
                publisherVelocita.publish(velocitaCorrente);
                siÈFermato = true;
                Applicazione.getInstance().toastSuActivityCorrente(l.getOstacoloRilevato());
                aggiornaObbiettivoGriglia(null);   //da togliere da qui dentro
                Log.e(TAG,"ostacolo rilevato");
                azzeraSottiscrittoreScanPose();
            } else if(siÈFermato){
                aggiornaObbiettivoGriglia(null);   //da togliere da qui dentro
                azzeraSottiscrittoreScanPose();
            }
        }

        private boolean presenzaOstacolo(int[] indiciConfronto, float[] valoriLaser, float distanzaLimiteOstacolo){
            for(int i:indiciConfronto){//Log.e(TAG,"valore dell'indice: "+i);
                if(valoriLaser[i] < distanzaLimiteOstacolo){
                    return true;
                }
            }
            return false;
        }
    }

    // SOTTOSCRITTORE ODOMETRIA. Memorizza dati odometria, e poi, in un thread a parte, gestisce la creazione del movimento desiderato.
    private class AcquisizioneDatiVelocitàOdometria implements MessageListener<Odometry>{
        private final boolean isLineare;
        private final int dove;
        private List<Odometry> lista = new ArrayList<>();
        private int velZeroCons = 0;
        private int contEmergenza = 0;
        private double velPrecedente = 0;

        public AcquisizioneDatiVelocitàOdometria(ComandoVel comandoVel){
            this.isLineare = isLineare(comandoVel.getDove());
            this.dove = comandoVel.getDove();
        }

        @Override
        public void onNewMessage(Odometry messaggio) {
            //operazioni da eseguire in meno di 34 ms!
            NodoComandoVocale.this.èProntoSottoscrittoreOdom = true;
            double vel;
            if(isLineare){
                vel = messaggio.getTwist().getTwist().getLinear().getX();
            } else {
                vel = messaggio.getTwist().getTwist().getAngular().getZ();
            }
            lista.add(messaggio);
            if(NodoComandoVocale.this.siÈFermato){
                if(Math.abs(vel) <= TempiPerTimer.VALORE_DA_CONSIDERARE_ZERO){
                    velZeroCons++;
                    if(velZeroCons > TempiPerTimer.MAX_VEL_UGUALI_CONSECUTIVE){
                        threadTentativoCreazioneMovimento().start();
                        NodoComandoVocale.this.èProntoSottoscrittoreOdom = false;
                        Log.e(TAG, "odom terminato");
                        NodoComandoVocale.this.subscriber_odom.shutdown();
                    }
                }else if(Math.abs(vel-velPrecedente) <= TempiPerTimer.VALORE_DA_CONSIDERARE_ZERO){
                    contEmergenza++;
                    if(contEmergenza > 40){ //dovrebbe rallentare ma non lo fa.
                        NodoComandoVocale.this.èProntoSottoscrittoreOdom = false;
                        Applicazione.getInstance().toastSuActivityCorrente(l.getErroreCreazioneTabella());
                        Log.e(TAG, "odom terminato per falsa frenata");
                        NodoComandoVocale.this.subscriber_odom.shutdown();
                    }
                    velZeroCons=0;
                } else {
                    contEmergenza=0;
                    velZeroCons=0;
                }
            }
        }

        private Thread threadTentativoCreazioneMovimento(){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean esitoOperazione = creazioneEdEsito(NodoComandoVocale.this.tempiPerTimer, dove, lista);
                    if(esitoOperazione){
                        Log.e(TAG,"nuovo thread. Creazione movimento riuscita");
                        Applicazione.getInstance().toastSuActivityCorrente(l.getTabellaCreata());
                    } else {
                        Log.e(TAG,"nuovo thread. Creazione movimento non riuscita");
                        Applicazione.getInstance().toastSuActivityCorrente(l.getErroreCreazioneTabella());
                    }
                }
            });
            return thread;
        }

        private boolean creazioneEdEsito(TempiPerTimer tempiPerTimer, int dove, List<Odometry> lista){
            if(dove == ComandoVel.AVANTI) return tempiPerTimer.creazioneMovimentoAvanti(lista);
            if(dove == ComandoVel.INDIETRO) return tempiPerTimer.creazioneMovimentoIndietro(lista);
            if(dove == ComandoVel.DESTRA) return tempiPerTimer.creazioneMovimentoDestra(lista);
            if(dove == ComandoVel.SINISTRA) return tempiPerTimer.creazioneMovimentoSinistra(lista);
            return false;
        }

        private boolean isLineare(int dove){
            if(dove == ComandoVel.AVANTI || dove == ComandoVel.INDIETRO) return true;
            if(dove == ComandoVel.DESTRA || dove == ComandoVel.SINISTRA) return false;
            throw new DAOException("Errore. Problema 'dove' costruttore AcquisizioneVelocitàOdometria");
        }
    }
    /*private void sottoscrittoreOdom(final int dove){
        subscriber_odom = connectedNode.newSubscriber(Costanti.NOME_TOPIC_ODOMETRIA, Odometry._TYPE);
        subscriber_odom.addMessageListener(new MessageListener<Odometry>(){

            private List<Odometry> lista = new ArrayList<>();
            private int velZeroCons = 0;
            private int contEmergenza = 0;
            private double velPrecedente = 0;

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
                lista.add(messaggio);
                if(siÈFermato){
                    if(Math.abs(vel) <= TempiPerTimer.VALORE_DA_CONSIDERARE_ZERO){
                        velZeroCons++;
                        if(velZeroCons > TempiPerTimer.MAX_VEL_UGUALI_CONSECUTIVE){
                            new Thread(new Runnable() {
                                @Override
                                public void run(){
                                    boolean esitoOperazione = creazioneEdEsito(NodoComandoVocale.this.tempiPerTimer, dove, lista);
                                    if(esitoOperazione){
                                        Log.e(TAG,"nuovo thread. Creazione movimento riuscita");
                                        Applicazione.getInstance().toastSuActivityCorrente(l.getTabellaCreata());
                                    } else {
                                        Log.e(TAG,"nuovo thread. Creazione movimento non riuscita");
                                        Applicazione.getInstance().toastSuActivityCorrente(l.getErroreCreazioneTabella());
                                    }
                                }

                                private boolean creazioneEdEsito(TempiPerTimer tempiPerTimer, int val, List<Odometry> lista){
                                    if(val == ComandoVel.AVANTI) return tempiPerTimer.creazioneMovimentoAvanti(lista);
                                    if(val == ComandoVel.INDIETRO) return tempiPerTimer.creazioneMovimentoIndietro(lista);
                                    if(val == ComandoVel.DESTRA) return tempiPerTimer.creazioneMovimentoDestra(lista);
                                    if(val == ComandoVel.SINISTRA) return tempiPerTimer.creazioneMovimentoSinistra(lista);
                                    return false;
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
}
