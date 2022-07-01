package it.unibas.progetto.vista;

import android.content.Context;
import android.util.AttributeSet;

import org.ros.android.view.VirtualJoystickView;
import org.ros.node.ConnectedNode;

public class VirtualJoystickViewN extends VirtualJoystickView {
    //private final String TAG = VirtualJoystickViewN.class.getName();
    private boolean init = false;
    public VirtualJoystickViewN(Context context) {
        super(context);
    }

    public VirtualJoystickViewN(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VirtualJoystickViewN(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //funziona ma forse troppo costoso? non viene ottimizzato da JVM il codice all'interno del blocco try catch
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try{
            return super.onTouchEvent(event);
        } catch (NullPointerException e){
            Log.e(TAG,e.getMessage());
            return false;
        }
    }*/

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        this.init=true;
    }

    public boolean isInizializzato() {
        return init;
    }
}
