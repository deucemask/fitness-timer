package org.hobbyista.fittimer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.String.format;


public class HomeActivity extends Activity {
    private MediaPlayer beepSound;
    private MediaPlayer startSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //TODO: use prepareAsync to unblock UI during mediaplayer setup
        beepSound = MediaPlayer.create(this, R.raw.beep_fit);
        startSound = MediaPlayer.create(this, R.raw.beep_start);
    }


    @Override
    public void onStop() {
        beepSound.release();
        beepSound = null;
        startSound.release();
        startSound = null;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onStop();
    }

    private TimeTrackerJob job;
    public void startTracker(View view) {
        Log.d(this.getLocalClassName(), "startTracker");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TimeTrackerConf conf = new TimeTrackerConf();
        conf.setDelay(getEditTextValueInt(R.id.tracker_delay));
        conf.setLapCount(getEditTextValueInt(R.id.tracker_lap_count));
        conf.setLapTime(getEditTextValueInt(R.id.tracker_lap_time));
        conf.setPause(getEditTextValueInt(R.id.tracker_pause));

        startSound.start();

        this.job = new TimeTrackerJob(conf, createTrackerListener());
        this.job.start();
    }

    public void stopTracker(View view) {
        if(this.job != null) {
            this.job.stop();
        }
    }


    private TimeTrackerJob.TimeTrackerListener createTrackerListener() {
        return new TimeTrackerJob.TimeTrackerListener() {
            @Override
            public void onDelay(int secUntilFinished) {
                beep(secUntilFinished);
                setTextViewValue(R.id.tracker_progress, format("Warm up: %d sec", secUntilFinished));
                Log.d("tracker", "onDelay: " + secUntilFinished);
            }

            @Override
            public void onLap(int secUntilFinished, int currentLap) {
                beep(secUntilFinished);
                setTextViewValue(R.id.tracker_progress, format("Lap %d: %d sec", currentLap, secUntilFinished));
                Log.d("tracker", format("onLap: %d / %d", currentLap, secUntilFinished));
            }

            @Override
            public void onPause(int secUntilFinished) {
                beep(secUntilFinished);
                setTextViewValue(R.id.tracker_progress, format("Break: %d sec", secUntilFinished));
                Log.d("tracker", "onPause: " + secUntilFinished);
            }

            @Override
            public void onFinish() {
                setTextViewValue(R.id.tracker_progress, "All Done!");
                Log.d("tracker", "DONE");
            }

            @Override
            public void onCancel() {
                setTextViewValue(R.id.tracker_progress, "Canceled!");
                Log.d("tracker", "CANCELED");
            }

        };
    }

    private void beep(int sec) {
        if (sec < 4 && !beepSound.isPlaying()) {
            beepSound.start();
        }
    }

    private Integer getEditTextValueInt(int fieldId) {
        return getEditTextValueInt(fieldId, 0);
    }

    private Integer getEditTextValueInt(int fieldId, Integer defaultValue) {
        Integer retVal = defaultValue;
        try {
            retVal = Integer.valueOf(((EditText) findViewById(fieldId)).getText().toString());
        } catch(Exception e) {
            Log.e(this.getLocalClassName(), "failed to get field value for fieldId " + fieldId, e);
        }
        return retVal;
    }

    private void setTextViewValue(int fieldId, String value) {
        try {
            ((TextView) findViewById(fieldId)).setText(value);
        } catch(Exception e) {
            Log.e(this.getLocalClassName(), "failed to set field value for fieldId " + fieldId, e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
