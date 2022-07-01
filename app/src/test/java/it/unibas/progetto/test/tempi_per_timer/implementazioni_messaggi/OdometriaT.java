package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.PoseWithCovariance;
import geometry_msgs.TwistWithCovariance;
import nav_msgs.Odometry;
import std_msgs.Header;

public class OdometriaT implements Odometry {
    private Header header;
    private TwistWithCovariance twist;
    private PoseWithCovariance poseWithCovariance;

    public OdometriaT(Header header, TwistWithCovariance twist, PoseWithCovariance poseWithCovariance) {
        this.header = header;
        this.twist = twist;
        this.poseWithCovariance = poseWithCovariance;
    }

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public String getChildFrameId() {
        return null;
    }

    @Override
    public void setChildFrameId(String s) {
    }

    @Override
    public PoseWithCovariance getPose() {
        return poseWithCovariance;
    }

    @Override
    public void setPose(PoseWithCovariance poseWithCovariance) {
        this.poseWithCovariance = poseWithCovariance;
    }

    @Override
    public TwistWithCovariance getTwist() {
        return twist;
    }

    @Override
    public void setTwist(TwistWithCovariance twistWithCovariance) {
        this.twist = twistWithCovariance;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
