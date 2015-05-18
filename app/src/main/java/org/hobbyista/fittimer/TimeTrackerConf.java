package org.hobbyista.fittimer;

/**
 * Created by denvik on 4/22/15.
 */
public class TimeTrackerConf {
    private int delay;
    private int lapTime;
    private int lapCount;
    private int pause;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getLapTime() {
        return lapTime;
    }

    public void setLapTime(int lapTime) {
        this.lapTime = lapTime;
    }

    public int getLapCount() {
        return lapCount;
    }

    public void setLapCount(int lapCount) {
        this.lapCount = lapCount;
    }

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }
}
