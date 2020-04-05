package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class SensorActivity extends Activity implements SensorEventListener {

    final static String MYDEBUG = "MYDEBUG";

    final static int changedDegreeValue = 10;


    private Handler myHandler = new Handler();

    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private  Sensor rotateSensor;
    private AudioManager audioManager;
    private int maxvolume;
    private int currentvolume;
    private float currentRollValue;
    private float currentPitchValue;
    private float pitch;
    private float roll;
    private boolean enterActivity;
    private boolean changed = false;

    private double startTime = 0;
    private double finalTime = 0;

    //UI component
    private SeekBar volumeSeekbar, progressSeekbar;
    private ImageButton pasuePlaybutton;
    private TextView durationtxt,  progresstxt, remainTimetxt, titletxt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        volumeSeekbar = findViewById(R.id.volumeControl);
        pasuePlaybutton = findViewById(R.id.btnPause);
        progressSeekbar = findViewById(R.id.seekBar);
        durationtxt = (TextView)findViewById(R.id.durationtxt);
        progresstxt = (TextView)findViewById(R.id.progresstxt);
        remainTimetxt = (TextView)findViewById(R.id.remainTimetxt);
        titletxt = (TextView)findViewById(R.id.titletxt);
        titletxt.setText("Song.mp3");

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        maxvolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentvolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekbar.setMax(maxvolume);
        volumeSeekbar.setProgress(currentvolume);
        volumeSeekbar.setEnabled(false);
        float volumePercent = currentvolume / (float)maxvolume;
        progresstxt.setText(String.valueOf(volumePercent * 100) + "%");
        //Log.i(MYDEBUG, "start voulme" + String.valueOf(volumeSeekbar.getProgress() / maxvolume));
        progressSeekbar.setEnabled(false);
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
       // mediaPlayer.seekTo(0);
        progressSeekbar.setMax((int) finalTime);

        remainTimetxt.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
        );
    }

    @Override
    protected void onResume() {
       // mediaPlayer.pause();
        super.onResume();
        enterActivity = false;
        myHandler.postDelayed(UpdateSongTime, 100);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        remainTimetxt.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
        );
    }

    @Override
    protected void onPause() {
        mediaPlayer.pause();
        pasuePlaybutton.setImageResource(R.drawable.play);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mediaPlayer.pause();
        pasuePlaybutton.setImageResource(R.drawable.play);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.pause();
        mediaPlayer.stop();
        super.onDestroy();
    }

    // Called when the "Exit" button is pressed.
    public void clickExit(View view) {
        this.finish(); // terminate
    }


    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {

            startTime = mediaPlayer.getCurrentPosition();
            durationtxt.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            progressSeekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            //   Log.i(MYDEBUG, "X" + String.valueOf(event.values[0]));
//            Log.i(MYDEBUG, "Y" + String.valueOf(event.values[1]));
          //  Log.i(MYDEBUG, "Z" +String.valueOf(event.values[2]));
        }
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            int worldAxisX = SensorManager.AXIS_X;
            int worldAxisZ = SensorManager.AXIS_Z;
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);
            pitch = orientation[1] * -57;
            roll = orientation[2] * -57;
             Log.i(MYDEBUG, "X" + String.valueOf(pitch));
//            Log.i(MYDEBUG, "Y" + String.valueOf(orientations[1]));
            Log.i(MYDEBUG, "Z" +String.valueOf(roll));
            if (!enterActivity){
                enterActivity = !enterActivity;
                currentPitchValue = pitch;
                currentRollValue = roll;
            }
            if (roll >= -30 && roll <= 30){
                changed = false;
            }
            if (pitch >= 45.0f && Math.abs(pitch - currentPitchValue) >= changedDegreeValue){
                // mediaPlayer.start();
              //  Log.i(MYDEBUG, "X" + String.valueOf(pitch));
                mediaPlayer.pause();
                pasuePlaybutton.setImageResource(R.drawable.play);
            }
            else if (pitch <= -45.0f && Math.abs(pitch - currentPitchValue) >= changedDegreeValue){
                // mediaPlayer.pause();
                //Log.i(MYDEBUG, "X" + String.valueOf(pitch));
                mediaPlayer.start();
                pasuePlaybutton.setImageResource(R.drawable.pause);
            }
            else if (roll <= -30.0f && roll - currentRollValue <= -changedDegreeValue && Math.abs(pitch) < 30 && !changed){
                 // audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
               // mediaPlayer.start();
              //  Log.i(MYDEBUG, "Z" +String.valueOf(roll));
                currentvolume =  currentvolume < maxvolume? currentvolume + 1 : currentvolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentvolume, AudioManager.FLAG_SHOW_UI);
                volumeSeekbar.setProgress(currentvolume);
                float volumePercent = currentvolume / (float)maxvolume;
                progresstxt.setText(String.valueOf(  volumePercent * 100) + "%");
                changed = !changed;

            }
            else if(roll >= 30.0f  && roll - currentRollValue >= changedDegreeValue &&  Math.abs(pitch) < 30 && !changed){
                //  audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
               // mediaPlayer.pause();
              //  Log.i(MYDEBUG, "Z" +String.valueOf(roll));
                currentvolume =  currentvolume > 0? currentvolume - 1 : currentvolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentvolume, AudioManager.FLAG_SHOW_UI);
                volumeSeekbar.setProgress(currentvolume);
                float volumePercent = currentvolume / (float)maxvolume;
                progresstxt.setText(String.valueOf(  volumePercent * 100) + "%");
                changed = !changed;
            }
        }
        currentRollValue = roll;
        currentPitchValue = pitch;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
