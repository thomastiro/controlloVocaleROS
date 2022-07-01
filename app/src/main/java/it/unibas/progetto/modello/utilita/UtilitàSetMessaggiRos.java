package it.unibas.progetto.modello.utilita;

import org.ros.message.Time;

import java.util.GregorianCalendar;

import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Twist;
import it.unibas.progetto.Costanti;
import std_msgs.Header;

public class UtilitàSetMessaggiRos {
    public static void setVelocita(Twist velocitaCorrente, double velocitaLineareX, double velocitaLineareY, double velocitaAngolareZ) {
        velocitaCorrente.getLinear().setX(velocitaLineareX);
        velocitaCorrente.getLinear().setY(-velocitaLineareY);
        velocitaCorrente.getLinear().setZ(0);
        velocitaCorrente.getAngular().setX(0);
        velocitaCorrente.getAngular().setY(0);
        velocitaCorrente.getAngular().setZ(velocitaAngolareZ);
    }

    public static void setPosizionePose(Pose posizioneObbiettivoPose, double X, double Y, double gradiOrientamento){
        posizioneObbiettivoPose.getPosition().setX(X);
        posizioneObbiettivoPose.getPosition().setY(Y);
        posizioneObbiettivoPose.getPosition().setZ(0);

        Double radianti = gradiOrientamento*Costanti.DA_GRADI_A_RADIANTI;
        posizioneObbiettivoPose.getOrientation().setZ(Math.sin(radianti/2));   //componenti quaternione visto in forma polare
        posizioneObbiettivoPose.getOrientation().setW(Math.cos(radianti/2));
        /* purtroppo non corrispondono.
        Quaternion quaternione = Quaternion.fromAxisAngle(Vector3.zAxis(), gradiOrientamento);
        Log.e(TAG, "sin metodo classe quaternione:"+ quaternione.getZ());
        Log.e(TAG, "cos metodo classe quaternione:"+ quaternione.getW());
        posizioneObbiettivoPose.getOrientation().setZ(quaternione.getZ());   //componenti quaternione visto in forma polare
        posizioneObbiettivoPose.getOrientation().setW(quaternione.getW());
         */
    }

    public static void setPosizioneObbiettivoPoseStamped(PoseStamped posizioneObbiettivoPoseStamped, String frameId, double X, double Y, double gradiOrientamento){
        posizioneObbiettivoPoseStamped.getHeader().setStamp(Time.fromMillis(new GregorianCalendar().getTime().getTime()));
        posizioneObbiettivoPoseStamped.getHeader().setFrameId(frameId);
        setPosizionePose(posizioneObbiettivoPoseStamped.getPose(), X, Y, gradiOrientamento);
    }

    public static void setHeader(Header header, String frameId ){
        header.setFrameId(frameId);
        header.setStamp(Time.fromMillis(new GregorianCalendar().getTime().getTime()));
        header.setSeq(0);
    }
}
