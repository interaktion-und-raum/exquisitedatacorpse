package de.dennisppaul.ciid2015.exquisitdatacorpse.additional.examples;

import de.dennisppaul.ciid2015.exquisitdatacorpse.NetworkClient;
import processing.core.PApplet;

public class SketchDataStreamAtomicWorldClock extends PApplet {

    private NetworkClient mClient;

    public void setup() {
        size(15, 15);
        frameRate(1);

        mClient = new NetworkClient(this, "127.0.0.1", "time");
    }

    public void draw() {
        mClient.send("local", hour(), minute(), second());
    }

    public static void main(String[] args) {
        PApplet.main(SketchDataStreamAtomicWorldClock.class.getName());
    }
}