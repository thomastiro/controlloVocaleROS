package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Quaternion;

public class QuaternionT implements Quaternion {
    private double x;
    private double y;
    private double z;
    private double w;

    public QuaternionT(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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
    public double getW() {
        return w;
    }

    @Override
    public void setW(double w) {
        this.w = w;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
