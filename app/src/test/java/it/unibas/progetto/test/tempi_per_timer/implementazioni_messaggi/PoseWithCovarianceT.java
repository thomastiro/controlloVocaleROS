package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Pose;
import geometry_msgs.PoseWithCovariance;

public class PoseWithCovarianceT implements PoseWithCovariance {
    private Pose pose;

    public PoseWithCovarianceT(Pose pose) {
        this.pose = pose;
    }

    @Override
    public Pose getPose() {
        return pose;
    }

    @Override
    public void setPose(Pose pose) {
        this.pose = pose;
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
