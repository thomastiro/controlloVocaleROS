package it.unibas.progetto.controllo;

import android.util.Log;

import org.ros.android.view.visualization.VisualizationView;
import org.ros.internal.node.client.MasterClient;
import org.ros.internal.node.response.Response;
import org.ros.internal.node.xmlrpc.XmlRpcTimeoutException;
import org.ros.master.client.SystemState;
import org.ros.master.client.TopicSystemState;
import org.ros.namespace.GraphName;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.modello.ILinguaDinamica;
import it.unibas.progetto.modello.Modello;

public class ControlloConnessione {
    private final String TAG= ControlloConnessione.class.getName();

    public List<TopicSystemState> getListaTopicConnessiAlMaster(URI masterUri){
        // ;) controparte android/java del comando da shell linux--->'rostopic list' ;)
        ILinguaDinamica l = Applicazione.getInstance().getLinguaDinamica();
        List<TopicSystemState> listaTopic;
        try {
            MasterClient masterClient = new MasterClient(masterUri);
            Response<SystemState> systemState = masterClient.getSystemState(GraphName.of("WHATEVER"));
            Collection<TopicSystemState> topicSystemState = systemState.getResult().getTopics();
            listaTopic = new ArrayList<TopicSystemState>(topicSystemState);
        } catch (XmlRpcTimeoutException ex) {
            listaTopic = new ArrayList<>();
            Applicazione.getInstance().toastSuActivityCorrente(l.getProblemaTimeout());
        }
        return listaTopic;
    }

    public boolean esisteQuestoTopic(String nomeTopic, URI masterUri){
        List<TopicSystemState> lista= getListaTopicConnessiAlMaster(masterUri);
        for(TopicSystemState i:lista){
            if(i.getTopicName().equals(nomeTopic)){
                return true;
            }
        }
        return false;
    }

    public boolean verificaConnessioneAlNodoMaster(){
        Modello modello = Applicazione.getInstance().getModello();
        NodeConfiguration nodeC = (NodeConfiguration) modello.getBean(Costanti.NODE_CONFIGURATION);
        NodeMainExecutor nodeM = (NodeMainExecutor) modello.getBean(Costanti.NODE_MAIN_EXECUTOR);
        if(nodeC == null || nodeM == null){
            Log.e(TAG,"ERRORE -"+ "node configuarazion: "+ nodeC + "---node main executor:"+ nodeM );
            //Applicazione.getInstance().toastSuActivityCorrente("L'applicazione non è connessa ad un nodo master");
            return false;
        }
        return true;
    }

    public boolean connettiNodoAlMaster(NodeMain nodoDaConnettere, String nomeNodo){
        ILinguaDinamica l = Applicazione.getInstance().getLinguaDinamica();
        Log.e(TAG,"avvio metodo connettiNodoAlMaster");
        if(!verificaConnessioneAlNodoMaster()){
            Applicazione.getInstance().toastSuActivityCorrente(l.getNodoNonConnessoMaster());
            return false;
        } else {
            Modello modello = Applicazione.getInstance().getModello();
            NodeConfiguration nodeC = (NodeConfiguration) modello.getBean(Costanti.NODE_CONFIGURATION);
            NodeMainExecutor nodeM = (NodeMainExecutor) modello.getBean(Costanti.NODE_MAIN_EXECUTOR);
            try {
                nodeM.execute(nodoDaConnettere, nodeC.setNodeName(nomeNodo));
                Log.e(TAG,"connessione al master effettuata correttamente");
                Applicazione.getInstance().toastSuActivityCorrente(l.getNodoInizializzato());
            } catch (Exception e){
                Log.e(TAG,"connessione al master non effettuata" + e.getMessage());
                Applicazione.getInstance().toastSuActivityCorrente(l.getNodoNonInizializzato());
                return false;
            }
        }
        return true;
    }

    public void spegniNodo(NodeMain nodoDaSpegnere, String nomeNodo){
        ILinguaDinamica l = Applicazione.getInstance().getLinguaDinamica();
        Modello modello = Applicazione.getInstance().getModello();
        if(!verificaConnessioneAlNodoMaster() || nodoDaSpegnere == null){
            Applicazione.getInstance().toastSuActivityCorrente(l.getErroreMaster());
        } else {
            try{
                NodeMainExecutor nodeM = (NodeMainExecutor) modello.getBean(Costanti.NODE_MAIN_EXECUTOR);
                nodeM.shutdownNodeMain(nodoDaSpegnere);
                modello.putBean(nomeNodo, false);
                Log.e(TAG, "nodo "+ nomeNodo+ " spento");
                Applicazione.getInstance().toastSuActivityCorrente(l.getNodoSpento());
            }catch (Exception e){
                Log.e(TAG,"ERRORE - nodo "+ nomeNodo+ " non spento " + e.getMessage());
                Applicazione.getInstance().toastSuActivityCorrente(l.getNodoNonSpento());
            }
        }

    }
    ///// 28/02 return boolean
    public boolean connettiInitGenericoNodoROS(NodeMain nodoDaConnettere, String nomeNodo){
        Modello modello = Applicazione.getInstance().getModello();
        Boolean nodoCreato = (Boolean) modello.getBean(nomeNodo);
        if(nodoCreato == null || !nodoCreato){
            if(connettiNodoAlMaster(nodoDaConnettere, nomeNodo)){
                if(nodoDaConnettere instanceof VisualizationView){
                    Log.e(TAG,  nomeNodo +" eseguo metodo init()");
                    ((VisualizationView) nodoDaConnettere).init((NodeMainExecutor) modello.getBean(Costanti.NODE_MAIN_EXECUTOR));
                } //da perfezionare il controllo
                modello.putBean(nomeNodo, true);
                Log.e(TAG,  nomeNodo +" creato e connesso");
                return true;
            } else {
                modello.putBean(nomeNodo, false);
                Log.e(TAG, "ERRORE- "+ nomeNodo+ " non creato");
                return false;
            }
        } else {
            Log.e(TAG,nomeNodo+" già creato.");
            return true;
        }
    }
}
