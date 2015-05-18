package org.hobbyista.fittimer;

import android.os.CountDownTimer;

/**
 * Created by denvik on 4/22/15.
 */
public class TimeTrackerJob {

    private final TimeTrackerConf conf;
    private final CountDownTimer lapTimer;
//    private CountDownTimer delayTimer;
    private final TimeTrackerListener listener;

    public TimeTrackerJob(TimeTrackerConf config, TimeTrackerListener eventListener) {
        this.conf = config;
        this.listener = eventListener;

        final int tick = 1000;
        final Tracker tracker = new Tracker(conf.getLapCount(),
                conf.getLapTime() * tick,
                conf.getDelay() * tick,
                conf.getPause() * tick);
        this.lapTimer = createLapTimer(tracker, tick);

    }

    private CountDownTimer createLapTimer(final Tracker tracker, final long tick) {
        //The onTick() event skips the last tick, so increase total by 1 tick
        final long countdownTotal = tracker.lapTotal + tick;
        return new CountDownTimer(countdownTotal, tick) {
            @Override
            public void onTick(long millisUntilFinished) {
                //usually millisUntilFinished are deviated from tick (modulo tick > 0)
                //the whole solution below expects this "imperfection"
                //but sometimes millis come in perfect without deviation (modulo tick == 0)
                //For this reason we want to manually deviate millis to always guarantee imperfect ticks
                millisUntilFinished -= 1;
                long elapsedTime = countdownTotal - millisUntilFinished;

                if (elapsedTime < tracker.delay) {
                    listener.onDelay((int) ((tracker.delay - elapsedTime + tick) / tick));
                    return;
                }

                long lap_end = tracker.timeSlots[tracker.lapCurrent - 1];
                long pause_end = tracker.pauseSlots[tracker.lapCurrent - 1];

                if (elapsedTime > lap_end && elapsedTime > pause_end) {
                    tracker.lapCurrent += 1;
                    lap_end = tracker.timeSlots[tracker.lapCurrent - 1];
                    pause_end = tracker.pauseSlots[tracker.lapCurrent - 1];
                }

                if (elapsedTime <= lap_end) {
                    listener.onLap((int)((lap_end - elapsedTime + tick) / tick), tracker.lapCurrent);
                }
                else if (elapsedTime > lap_end &&
                        elapsedTime <= pause_end &&
                        tracker.lapCurrent < tracker.lapCount) {
                    listener.onPause((int)((pause_end - elapsedTime + tick) / tick));
                }
            }

            @Override
            public void onFinish() {

                listener.onFinish();

            }
        };
    }

    public void start() {
        this.lapTimer.start();
    }

    public void stop() {
        this.lapTimer.cancel();
        this.listener.onCancel();
    }

    public interface TimeTrackerListener {
        void onDelay(int secUntilFinished);
        void onLap(int secUntilFinished, int currentLap);
        void onPause(int secUntilFinished);
        void onFinish();
        void onCancel();
    }

    private class Tracker {
        int lapCurrent;
        int lapCount;
        long lapTime;
        long delay;
        long pause;
        long lapTotal;
        long[] timeSlots;
        long[] pauseSlots;

        Tracker(int lapCount, long lapTime, long delay, long pause) {
            this.lapCount = lapCount;
            this.lapTime = lapTime;
            this.delay = delay;
            this.pause = pause;
            this.lapCurrent = 1;
            this.lapTotal = delay + lapCount * lapTime + (lapCount - 1) * pause;

            //Create time slots
            timeSlots = new long[lapCount];
            pauseSlots = new long[lapCount];
            timeSlots[0] = delay + lapTime;
            pauseSlots[0] = timeSlots[0] + pause;
            for (int i = 1; i < lapCount; i++) {
                timeSlots[i] = timeSlots[i - 1] + pause + lapTime;
                pauseSlots[i] = timeSlots[i] + pause;
            }

        }

    }

}
