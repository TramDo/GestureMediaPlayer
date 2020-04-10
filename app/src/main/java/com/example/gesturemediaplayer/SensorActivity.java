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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SensorActivity extends Activity implements SensorEventListener {

    final static String MYDEBUG = "MYDEBUG";

    final static int changedDegreeValue = 20;
    final static int changedDegreeValuePitch = 30;
    final static int timeForCount = 1000000;


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
    private boolean pchanged = false;
    private CountDownTimer countUptimer;
    private UserData userData;
    private int totalTrailTime = 5;

    private double startTime = 0;
    private double finalTime = 0;

    //variable for experiment use
    private int trailTime = 0;
    private int currentGestureIndex = 0;
    private float currenttime = 0.0f;
    private int errorNumber = 0;
    private String[] GestureArray = {"Play", "Up", "Up", "Down", "Down", "Pause", "Play", "Pause"};
    private float durationTimeArray[];
    private int errorTimeArray[];

    //UI component
    private SeekBar volumeSeekbar, progressSeekbar;
    private ImageButton pausePlaybutton;
    private TextView durationtxt,  progresstxt, remainTimetxt, titletxt, gestureText;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);

        Intent intent = getIntent();
        if (intent != null){
            userData = (UserData) intent.getSerializableExtra("userdata");
            Log.i(MYDEBUG, "gender" + userData.getInitial());
        }
        totalTrailTime = userData.getTrialnumber() + 1;
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        volumeSeekbar = findViewById(R.id.volumeControl);
        pausePlaybutton = findViewById(R.id.btnPause);
        progressSeekbar = findViewById(R.id.seekBar);
        durationtxt = (TextView)findViewById(R.id.durationtxt);
        progresstxt = (TextView)findViewById(R.id.progresstxt);
        remainTimetxt = (TextView)findViewById(R.id.remainTimetxt);
        gestureText = findViewById(R.id.gesturetext);
        titletxt = (TextView)findViewById(R.id.titletxt);
        titletxt.setText("Song.mp3");
        gestureText.setText("Play");

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
        progresstxt.setText(String.valueOf(Math.round((volumePercent * 100)) + "%"));
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

        countUptimer = new CountDownTimer(timeForCount, 100) {

            public void onTick(long millisUntilFinished) {

                Log.i(MYDEBUG, "seconds remaining: " +( timeForCount - millisUntilFinished) / (float)1000);
                currenttime = ( timeForCount - millisUntilFinished) / (float)1000;
            }

            public void onFinish() {
                Log.i(MYDEBUG, "done");
            }
        };


    }

    @Override
    protected void onResume() {
       // mediaPlayer.pause();
        super.onResume();
        enterActivity = false;
        trailTime = 0;
        currentGestureIndex = 0;
        errorNumber = 0;
        durationTimeArray = new float[totalTrailTime];
        errorTimeArray = new int[totalTrailTime];
        gestureText.setText("Play");
       // Log.i(MYDEBUG, "trail time" + String.valueOf(durationTimeArray[trailTime]));
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
        pausePlaybutton.setImageResource(R.drawable.play);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mediaPlayer.pause();
        pausePlaybutton.setImageResource(R.drawable.play);
        mediaPlayer.stop();
        sensorManager.unregisterListener(this, gyroSensor);
        sensorManager.unregisterListener(this, rotateSensor);
        if (countUptimer != null)
            countUptimer.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.pause();
        mediaPlayer.stop();
        sensorManager.unregisterListener(this, gyroSensor);
        sensorManager.unregisterListener(this, rotateSensor);

        if (countUptimer != null)
            countUptimer.cancel();
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
           //  Log.i(MYDEBUG, "X" + String.valueOf(pitch));
//            Log.i(MYDEBUG, "Y" + String.valueOf(orientations[1]));
          //  Log.i(MYDEBUG, "Z" +String.valueOf(roll));
            if (!enterActivity){
                enterActivity = !enterActivity;
                currentPitchValue = pitch;
                currentRollValue = roll;
            }
            if (roll >= -30 && roll <= 30){
                changed = false;
            }
            if (pitch >= -30 && pitch <= 30){
                pchanged = false;
            }
            if (pitch >= 20.0f && pitch - currentPitchValue >= changedDegreeValuePitch && !pchanged){
                // mediaPlayer.start();
              //  Log.i(MYDEBUG, "X" + String.valueOf(pitch));
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                pausePlaybutton.setImageResource(R.drawable.play);
                pchanged = !pchanged;
                if ( currentGestureIndex < GestureArray.length && GestureArray[currentGestureIndex] == "Pause"){
                    currentGestureIndex++;
                    if (currentGestureIndex == GestureArray.length){
                        if (countUptimer != null)
                            countUptimer.cancel();
                        durationTimeArray[trailTime] = currenttime;
                        errorTimeArray[trailTime] = errorNumber;
                        Log.i(MYDEBUG, "trail time" + String.valueOf( errorNumber));
                        trailTime++;
                        currentGestureIndex = 0;
                        errorNumber = 0;
                        if (trailTime == totalTrailTime){
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(this, ResultActivity.class);
                            intent.putExtra("trailTimeArray", durationTimeArray);
                            intent.putExtra("errornumberArray", errorTimeArray);
                            intent.putExtra("errornumber", errorNumber);
                            intent.putExtra("userdata", userData);
                            trailTime = 0;
                            durationTimeArray = new float[totalTrailTime];
                            errorTimeArray = new int[totalTrailTime];
                            errorNumber = 0;
                            startActivity(intent);
                        }
                    }
                    else{
                        gestureText.setText(GestureArray[currentGestureIndex]);
                    }
                }
                else{
                    Log.i("MYDEBUGG", "error");
                    errorNumber++;
                }

            }
            else if (pitch <= -20.0f && pitch - currentPitchValue <= -changedDegreeValuePitch && !pchanged){
                // mediaPlayer.pause();
                //Log.i(MYDEBUG, "X" + String.valueOf(pitch));
                mediaPlayer.start();
                pausePlaybutton.setImageResource(R.drawable.pause);
                pchanged = !pchanged;
                if (currentGestureIndex == 0){
                    countUptimer.cancel();
                    countUptimer.start();
                }
                else{
                    //countUptimer.start();
                }
                if ( currentGestureIndex < GestureArray.length && GestureArray[currentGestureIndex] == "Play"){
                    currentGestureIndex++;
                    gestureText.setText(GestureArray[currentGestureIndex]);
                }
                else{
                    Log.i("MYDEBUGG", String.valueOf(pchanged));
                    errorNumber++;
                }
            }
            else if (roll <= -30.0f && roll - currentRollValue <= -changedDegreeValue && Math.abs(pitch) < 30 && !changed){
                 // audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
               // mediaPlayer.start();
              //  Log.i(MYDEBUG, "Z" +String.valueOf(roll));
                currentvolume =  currentvolume < maxvolume? currentvolume + 1 : currentvolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentvolume, AudioManager.FLAG_SHOW_UI);
                volumeSeekbar.setProgress(currentvolume);
                float volumePercent = currentvolume / (float)maxvolume;
                progresstxt.setText(String.valueOf(Math.round((  volumePercent * 100)) + "%"));
                changed = !changed;
                if ( currentGestureIndex < GestureArray.length && GestureArray[currentGestureIndex] == "Up"){
                    currentGestureIndex++;
                    gestureText.setText(GestureArray[currentGestureIndex]);
                }
                else{
                    Log.i("MYDEBUGG", "error3");
                    errorNumber++;
                }
            }
            else if(roll >= 30.0f  && roll - currentRollValue >= changedDegreeValue &&  Math.abs(pitch) < 30 && !changed){
                //  audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
               // mediaPlayer.pause();
              //  Log.i(MYDEBUG, "Z" +String.valueOf(roll));
                currentvolume =  currentvolume > 0? currentvolume - 1 : currentvolume;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentvolume, AudioManager.FLAG_SHOW_UI);
                volumeSeekbar.setProgress(currentvolume);
                float volumePercent = currentvolume / (float)maxvolume;
                progresstxt.setText(String.valueOf(Math.round((  volumePercent * 100)) + "%"));
                changed = !changed;
                if ( currentGestureIndex < GestureArray.length && GestureArray[currentGestureIndex] == "Down"){
                    currentGestureIndex++;
                    gestureText.setText(GestureArray[currentGestureIndex]);
                }
                else{
                    Log.i("MYDEBUGG", "error4");
                    errorNumber++;
                }
            }
            currentRollValue = roll;
            currentPitchValue = pitch;
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
