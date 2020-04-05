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
import android.util.Log;
import android.view.View;

public class SensorActivity extends Activity implements SensorEventListener {

    final static String MYDEBUG = "MYDEBUG";

    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private  Sensor rotateSensor;
    private AudioManager audioManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Called when the "Exit" button is pressed.
    public void clickExit(View view) {
        this.finish(); // terminate
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            //   Log.i(MYDEBUG, "X" + String.valueOf(event.values[0]));
//            Log.i(MYDEBUG, "Y" + String.valueOf(event.values[1]));
            Log.i(MYDEBUG, "Z" +String.valueOf(event.values[2]));
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
            float pitch = orientation[1] * -57;
            float roll = orientation[2] * -57;
            // Log.i(MYDEBUG, "X" + String.valueOf(pitch));
//            Log.i(MYDEBUG, "Y" + String.valueOf(orientations[1]));
            Log.i(MYDEBUG, "Z" +String.valueOf(roll));
            if (pitch >= 45.0f){
                // mediaPlayer.start();
                Log.i(MYDEBUG, "X" + String.valueOf(pitch));
                mediaPlayer.pause();
            }
            else if (pitch <= -45.0f){
                // mediaPlayer.pause();
                Log.i(MYDEBUG, "X" + String.valueOf(pitch));
                mediaPlayer.start();
            }
            if (roll <= -45.0f){
                  audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
               // mediaPlayer.start();
            }
            else if(roll >= 45.0f ){
                  audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
               // mediaPlayer.pause();
            }
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
