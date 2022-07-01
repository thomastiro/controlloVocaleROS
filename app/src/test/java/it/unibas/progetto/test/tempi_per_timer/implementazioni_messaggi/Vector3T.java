package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Vector3;

public class Vector3T implements Vector3 {
    private double x;
    private double y;
    private double z;

    public Vector3T(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
