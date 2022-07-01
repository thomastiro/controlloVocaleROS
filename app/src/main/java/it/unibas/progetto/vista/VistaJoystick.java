package it.unibas.progetto.vista;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.ros.android.view.VirtualJoystickView;
import org.ros.android.view.visualization.XYOrthographicCamera;
import org.ros.node.ConnectedNode;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.R;
import it.unibas.progetto.controllo.ControlloConnessione;

public class VistaJoystick extends Fragment {
    private final String TAG = VistaJoystick.class.getName();
    private VirtualJoystickViewN virtualJoystickView;
    private Timer timer;

    public VistaJoystick() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vistaJoystick = inflater.inflate(R.layout.vista_joystick, container, false);
        virtualJoystickView = vistaJoystick.findViewById(R.id.virtual_joystick);
        return vistaJoystick;
    }

    @Override
    public void onResume() {
        ControlloConnessione controlloConnessione = Applicazione.getInstance().getControlloConnessione();
        if(controlloConnessione.connettiInitGenericoNodoROS(virtualJoystickView, Costanti.NOME_NODO_JOYSTICK)){
            controlloInitTimer2();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if(timer != null){
            Log.e(TAG,"stranamente è ancora attivo il timer");
            timer.cancel();
            timer.purge();
        }
        Applicazione.getInstance().getControlloConnessione().spegniNodo(virtualJoystickView, Costanti.NOME_NODO_JOYSTICK);
        super.onDestroy();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    purtroppo la classe della libreria alfa ros-android non effettua controllo su ritardi di inizializzazione
    , quindi anche apparentemente connesso, in caso di connessione lenta o altro tipo di rallentamento, potrebbe essere lanciata l'eccezione di nullPointerExeption
    sull'oggetto publisher twist privato interno. Quindi le opzioni disponibili sono:
    1)estendo la classe e sovrascrivo alcuni metodi e utilizzo i metodi di View
    2)uso la riflessione.
    3)uso blocco try catch costante su onTouchEvent
    */
    //non è bastato usarare la riflessione(per come la conosco)per impedire ai metodi interni la generazione dell'eccezione
    /*private void controlloInitTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG,"timer controllo è ancora in esecuzione");
                try {
                    Field field = virtualJoystickView.getClass().getDeclaredField("currentVelocityCommand");
                    //field.setAccessible(true);
                    if(field!=null){
                        //field.setAccessible(false);
                        if(virtualJoystickView.getVisibility()!=View.VISIBLE){
                            Applicazione.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    virtualJoystickView.setVisibility(View.VISIBLE);
                                    Log.e(TAG,"virtualjoystick è visibile");
                                }
                            });
                        }
                        VistaJoystick.this.timer.cancel();
                        VistaJoystick.this.timer.purge();;
                        Log.e(TAG,"valore diverso da null !!!");
                    }
                } catch (NoSuchFieldException e) {
                    Log.e(TAG,"FALLITO recupero valore currentVelocityCommand."+e.getMessage());
                }

            }
        }, 100, 20);
    }*/
    //ho dovuto sovrascrivere alcuni metodi della classe e usare i metodi della vista per impedire il lancio di eccezioni.
    //con questo metodo ho evitato di fare un blocco costante try catch sul metodo onClick
    private void controlloInitTimer2(){
        timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                Log.e(TAG, "avvio timer");
                if(VistaJoystick.this.virtualJoystickView.isInizializzato()){
                    Applicazione.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VistaJoystick.this.virtualJoystickView.setVisibility(View.VISIBLE);
                            Log.e(TAG,"virtualjoystick è visibile");
                            VistaJoystick.this.timer.cancel();
                            VistaJoystick.this.timer.purge();
                            Log.e(TAG, "fine timer");
                        }
                    });
                }

            }
        },100, 20);
    }
}
