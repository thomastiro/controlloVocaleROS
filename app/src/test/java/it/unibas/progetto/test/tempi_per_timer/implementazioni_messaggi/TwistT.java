package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;

public class TwistT implements Twist {
    private Vector3 lineare;
    private Vector3 angolare;

    public TwistT(Vector3 lineare, Vector3 angolare) {
        this.lineare = lineare;
        this.angolare = angolare;
    }

    @Override
    public Vector3 getLinear() {
        return lineare;
    }

    @Override
    public void setLinear(Vector3 lineare) {
        this.lineare = lineare;
    }

    @Override
    public Vector3 getAngular() {
        return angolare;
    }

    @Override
    public void setAngular(Vector3 angolare) {
        this.angolare = angolare;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
