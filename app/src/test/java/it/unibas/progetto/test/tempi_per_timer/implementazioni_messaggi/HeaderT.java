package it.unibas.progetto.test.tempi_per_timer.implementazioni_messaggi;

import org.ros.internal.message.RawMessage;
import org.ros.message.Time;

import std_msgs.Header;

public class HeaderT implements Header {
    private Time time;
    private int seq;

    public HeaderT(Time time, int seq) {
        this.time = time;
        this.seq = seq;
    }

    @Override
    public int getSeq() {
        return seq;
    }

    @Override
    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public Time getStamp() {
        return time;
    }

    @Override
    public void setStamp(Time time) {
        this.time = time;
    }

    @Override
    public String getFrameId() {
        return null;
    }

    @Override
    public void setFrameId(String s) {

    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
