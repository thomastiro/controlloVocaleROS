package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;

public class PoseT implements Pose {
    private Point point;
    private Quaternion quaternion;

    public PoseT(Point point, Quaternion quaternion) {
        this.point = point;
        this.quaternion = quaternion;
    }

    @Override
    public Point getPosition() {
        return point;
    }

    @Override
    public void setPosition(Point point) {
        this.point = point;
    }

    @Override
    public Quaternion getOrientation() {
        return quaternion;
    }

    @Override
    public void setOrientation(Quaternion quaternion) {
        this.quaternion = quaternion;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
