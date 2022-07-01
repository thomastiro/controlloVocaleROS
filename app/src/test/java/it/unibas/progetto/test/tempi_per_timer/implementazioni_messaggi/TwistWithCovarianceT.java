package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Twist;
import geometry_msgs.TwistWithCovariance;

public class TwistWithCovarianceT implements TwistWithCovariance {
    private Twist twist;

    public TwistWithCovarianceT(Twist twist) {
        this.twist = twist;
    }

    @Override
    public Twist getTwist() {
        return twist;
    }

    @Override
    public void setTwist(Twist twist) {
        this.twist = twist;
    }

    @Override
    public double[] getCovariance() {
        return new double[36];
    }

    @Override
    public void setCovariance(double[] doubles) {

    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
