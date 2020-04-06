package org.devopsfordefense.linerunner;

public class LineRunner {

    // current position
    private double t = 0.0;
    private double x = 0.0;
    private double v = 0.0;

    public LineRunner() {

    }

    public void step(double timeSec, double accel) {
        
        // high school physics
        double deltaT = timeSec - t;
        double xFinal = x + (v * deltaT) + (0.5 * accel * Math.pow(deltaT, 2));
        double vFinal = v + (accel * deltaT);

        t = timeSec;
        x = xFinal;
        v = vFinal;

    }

    public double getTimeSec() {
        return t;
    }

    public double getPosition() {
        return x;
    }

    public double getVelocity() {
        return v;
    }


}