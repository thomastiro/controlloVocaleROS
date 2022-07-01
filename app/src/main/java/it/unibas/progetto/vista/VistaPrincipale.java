package it.unibas.progetto.vista;

import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.ros.android.view.visualization.Color;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.layer.CameraControlLayer;
import org.ros.android.view.visualization.layer.LaserScanLayer;
import org.ros.android.view.visualization.layer.Layer;
import org.ros.android.view.visualization.layer.OccupancyGridLayer;
import org.ros.android.view.visualization.layer.PathLayer;
import org.ros.android.view.visualization.layer.PosePublisherLayer;
import org.ros.android.view.visualization.layer.PoseSubscriberLayer;
import org.ros.android.view.visualization.layer.RobotLayer;
import org.ros.namespace.GraphName;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.R;
import it.unibas.progetto.controllo.ControlloConnessione;
import it.unibas.progetto.modello.NodoComandoVocale;
import it.unibas.progetto.modello.layer_personali.GrigliaLayer;

public class VistaPrincipale extends Fragment {
    private final String TAG = VistaPrincipale.class.getName();
    private NodoComandoVocale nodoComandoVocale = new NodoComandoVocale(Applicazione.getInstance().getLinguaDinamica());
    private TextView viewStringaRisultato;
    private ImageView bottoneParla;
    private VisualizationView visualizationView;
    private GrigliaLayer grigliaLayer = null;

    private Color coloreGriglia = Color.fromHexAndAlpha("a0a0a4",0.6f); //grigio chiaro
    private Color coloreAssi = Color.fromHexAndAlpha("ff0000",0.6f); //rosso
    private Color coloreCerchioObbiettivo = Color.fromHexAndAlpha("4CAF50",0.4f);//verde chiaro
    private float spessoreLinee = 2.0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "vista principale");
        View vista = inflater.inflate(R.layout.vista_principale, container, false);
        visualizationView = vista.findViewById(R.id.visualizationView);
        visualizationView.onCreate(Lists.<Layer>newArrayList(
                new CameraControlLayer(),
                new OccupancyGridLayer(Costanti.NOME_TOPIC_OCCUPACITY_GRID),
                grigliaLayer = new GrigliaLayer(coloreGriglia, coloreAssi, coloreCerchioObbiettivo, spessoreLinee, Costanti.NOME_FILE_TTF_ASSETS),
                new LaserScanLayer(Costanti.NOME_TOPIC_LASER_SCAN),
                new RobotLayer(Costanti.ROBOT_FRAME),
                new PathLayer(Costanti.NOME_TOPIC_PERCORSO),
                new PosePublisherLayerN(Costanti.NOME_TOPIC_POSIZIONE), ///classe sovrascritta per controllare l'inizializzazione
                new PoseSubscriberLayer(Costanti.NOME_TOPIC_OBBIETTIVO_CORRENTE)
                ));
        //permette di muovere la mappa sotto il cursore che indica la posizione del robot
        visualizationView.getCamera().jumpToFrame(Costanti.ROBOT_FRAME);
        Applicazione.getInstance().getModello().putBean(Costanti.LAYER_GRIGLIA, grigliaLayer);
        //visualizationView.getCamera().jumpToFrame(Costanti.NOME_TOPIC_OCCUPACITY_GRID);
        return vista;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewStringaRisultato = view.findViewById(R.id.viewStringaRisultato);
        bottoneParla = view.findViewById(R.id.bottoneParla);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inizializzaAzioni();
    }

    private void mostraGriglia(Boolean value){
        if(grigliaLayer != null && value != null){
            grigliaLayer.setReady(value);
        }
    }

    public void aggiornaTestoTextView(String stringa){
        if(stringa == null){
            return;
        }
        viewStringaRisultato.setText(stringa); //nella label inserisco la prima frase della lista dei risultati
        daCancellare(stringa);
    }

    public NodoComandoVocale getNodoComandoVocale() {
        return nodoComandoVocale;
    }

    private void inizializzaAzioni(){
        bottoneParla.setOnClickListener(Applicazione.getInstance().getControlloPrincipale().getAzioneParla());
        viewStringaRisultato.setText(getString(R.string.testoBenvenuto));
    }

    @Override
    public void onResume(){
        //da sistemare in maniera più elegante
        ControlloConnessione controlloConnessione= Applicazione.getInstance().getControlloConnessione();
        for(int i=1; i < 11; i++){
            Log.e(TAG,"tentativi connessione: "+i);
            boolean cVocale = controlloConnessione.connettiInitGenericoNodoROS(nodoComandoVocale, Costanti.NOME_NODO_COMANDO_VOCALE);
            boolean cVista = controlloConnessione.connettiInitGenericoNodoROS(visualizationView, Costanti.NOME_NODO_VISTA_MAPPA);
            if(cVista && cVocale){
                break;
            }
        }
        if(controlloConnessione.verificaConnessioneAlNodoMaster()){
            visualizationView.setVisibility(View.VISIBLE);
            Boolean value=(Boolean)Applicazione.getInstance().getModello().getBean(Costanti.MOSTRA_GRIGLIA);
            mostraGriglia(value);
        }
        nodoComandoVocale.cambioLingua();
        super.onResume();
        Log.e(TAG, "VistaPrincipale resume");
    }

    @Override
    public void onDestroy() {
        Applicazione.getInstance().getControlloConnessione().spegniNodo(nodoComandoVocale, Costanti.NOME_NODO_COMANDO_VOCALE);
        Applicazione.getInstance().getControlloConnessione().spegniNodo(visualizationView, Costanti.NOME_NODO_VISTA_MAPPA);
        super.onDestroy();
        Log.e(TAG, "VistaPrincipale distrutta");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "VistaPrincipale pausa");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "VistaPrincipale stop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "VistaPrincipale start");
    }

    private void daCancellare(String stringa){
        //nun aggia ra cunt a nsciun.
        if(stringa.equalsIgnoreCase("chi è l'autore dell'applicazione")){
            viewStringaRisultato.setText("THOMAS ANTONIO TIRONE");
        }
    }

    private class PosePublisherLayerN extends PosePublisherLayer{
        /*
        purtroppo la classe nella libreria alfa ros-android non effettua controllo su ritardi di inizializzazione
        , quindi anche se apparentemente connesso il layer, in caso di eccessivo ritardo, potrebbe essere lanciata
        l'eccezione di nullPointerExeption dal metodo ---> onTouchEvent().
        causata da un inizializzazione del oggetto gestureDetector successiva al tocco sul display.
         Quindi o estendevo la classe e gestivo l'eccezione o usavo la riflessione.
        */
        public PosePublisherLayerN(String topic) {
            super(topic);
        }

        public PosePublisherLayerN(GraphName topic) {
            super(topic);
        }

        @Override
        public boolean onTouchEvent(VisualizationView view, MotionEvent event) {
            try{
                return super.onTouchEvent(view, event);
            } catch (NullPointerException e){
                Log.e(TAG,"posePublisherLayerN: "+e.getMessage());
                return false;
            }
        }
    }

    /*private void controlloInitTimer(){
        //
        purtroppo la classe della libreria alfa ros-android non effettua controllo su ritardi di inizializzazione
        , quindi anche apparentemente connesso, in caso di connessione lenta, potrebbe essere lanciata l'eccezione di nullPointerExeption
        causata da un inizializzazione del oggetto gestureDetector successiva al tocco sul display. Quindi o estendevo la classe e sovrascrivevo alcuni metodi o usavo la riflessione.
        //
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG,"timer controllo è ancora in esecuzione");
                try {
                    Field field = posePublisherLayer.getClass().getDeclaredField("gestureDetector");
                    if(field!=null){
                        if(visualizationView.getVisibility()!=View.VISIBLE){
                            Applicazione.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //visualizationView.setClickable(true);
                                    visualizationView.setVisibility(View.VISIBLE);
                                    Log.e(TAG,"!!!!!!   CLICCABILE    !!!!!!");
                                }
                            });

                        }
                        VistaPrincipale.this.timer.cancel();
                        VistaPrincipale.this.timer.purge();;
                        Log.e(TAG,"valore diverso da null !!!");
                    }
                } catch (NoSuchFieldException e) {
                    Log.e(TAG,"FALLITO recupero valore gestureDetector."+e.getMessage());
                }

            }
        }, 100, 20);
    }*/
}
