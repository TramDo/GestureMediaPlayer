package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.example.gesturemediaplayer.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ButtonActivity extends Activity implements OnClickListener {

    private ImageButton btnPlay, btnPause, btnForward,  btnBackward;
    private ImageView iv;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    private double startTime = 0;
    private double finalTime = 0;
    private int maxvolume;
    private int currentvolume;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private TextView durationtxt, remainTimetxt, titletxt, progresstxt, trialtxt, erortxt;
    private SeekBar volumeControl, seekbar; //slider

    private UserData userData;

    public static int oneTimeOnly = 0;
    //int maxVolume = 100;
    long startTimer, endTimer; //System.nanoTime();
    float timer; //=(endTimer - startTimer)/ 1000000000f;
    final String PLAY = "play", PAUSE = "pause", VOL_UP="vol_up", VOL_DOWN="vol_down";
    List<String> defaultTasks = new ArrayList<String>();

    List<String> userTasks = new ArrayList<String>();
    int trials=0;
    int timesOfPlay=0, timeOfPause =0, timeOfVolUp=0, timeOfVolDown=0, numberOfError=0, numberOfScreenTouch=0;
    List<Float>timePerTrial = new ArrayList<Float>();
    List<Integer>errorPerTrial = new ArrayList<Integer>();
    int totalTrailTime;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_layout);

        Intent intent = getIntent();
        if (intent != null){
            userData = (UserData) intent.getSerializableExtra("userdata");

        }
        totalTrailTime = userData.getTrialnumber() + 1;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        defaultTasks.add(PLAY);
        defaultTasks.add(VOL_UP);
        defaultTasks.add(VOL_UP);
        defaultTasks.add(VOL_DOWN);
        defaultTasks.add(VOL_DOWN);
        defaultTasks.add(PAUSE);
        defaultTasks.add(PLAY);
        defaultTasks.add(PAUSE);

        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnPause = (ImageButton)findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);
        btnBackward = (ImageButton)findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(this);
        btnForward = (ImageButton)findViewById(R.id.btnForward);
        btnForward.setOnClickListener(this);
        iv = (ImageView)findViewById(R.id.imageView);

        durationtxt = (TextView)findViewById(R.id.durationtxt);
        titletxt = (TextView)findViewById(R.id.titletxt);
        remainTimetxt = (TextView)findViewById(R.id.remainTimetxt);
        progresstxt = (TextView)findViewById(R.id.progresstxt);
        titletxt.setText("Song.mp3");
        trialtxt = (TextView)findViewById(R.id.trialtxt);
        erortxt = (TextView)findViewById(R.id.erortxt);


        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        btnPause.setEnabled(false);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        maxvolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentvolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl = (SeekBar) findViewById(R.id.volumeControl);

        progresstxt.setText(String.valueOf(Math.round(((currentvolume / (float)maxvolume) * 100)) + "%"));

        volumeControl.setMax(maxvolume);
        volumeControl.setProgress(currentvolume);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;

                float volumePercent = progress / (float)maxvolume;
                progresstxt.setText(String.valueOf(Math.round(volumePercent * 100)) + "%");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add whatever, might not be needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.i("Slider", "Progress: "+ progressChangedValue);

                if (progressChangedValue > currentvolume ){
                    userTasks.add(VOL_UP);
                    Log.i("user task ",userTasks.toString());
                    recordError();
                    endTask();
                    endTrial();
                }
                else if (progressChangedValue < currentvolume){
                    userTasks.add(VOL_DOWN);
                    Log.i("user task ",userTasks.toString());
                    recordError();
                    endTask();
                    endTrial();
                }

                currentvolume = progressChangedValue;

            }
        });



    }

    @Override
    public void onClick(View view) {
        if (view == btnPlay){

            mediaPlayer.start();
            timesOfPlay++;
            if(timesOfPlay == 1){
                startTimer = System.nanoTime();
            }
            userTasks.add(PLAY);
            Log.i("user task ",userTasks.toString());
            recordError();
            endTask();
            endTrial();

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            seekbar.setMax((int) finalTime);

            durationtxt.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            remainTimetxt.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
            );

            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            btnPause.setEnabled(true);
            btnPlay.setEnabled(false);

        } else if (view == btnPause){
            //Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
            timeOfPause++;
            userTasks.add(PAUSE);
            Log.i("user task ",userTasks.toString());
            recordError();
            endTask();
            endTrial();
            btnPause.setEnabled(false);
            btnPlay.setEnabled(true);


        }
        else if (view == btnBackward){
            int temp = (int)startTime;

            if((temp-backwardTime)>0){
                startTime = startTime - backwardTime;
                mediaPlayer.seekTo((int) startTime);
                //Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
            }



        } else if (view == btnForward){
            int temp = (int)startTime;

            if((temp+forwardTime)<=finalTime){
                startTime = startTime + forwardTime;
                mediaPlayer.seekTo((int) startTime);
                //Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
            }
        }


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
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public  void onUserInteraction(){
        numberOfScreenTouch++;

    }

    public void endTask(){
        if(userTasks.size()==8) {
            endTimer = System.nanoTime();
            timer = (endTimer - startTimer)/ 1000000000f;
            timePerTrial.add(timer);
            errorPerTrial.add(numberOfError);
            timeOfPause=0;
            timesOfPlay=0;
            numberOfError=0;
            erortxt.setText("Number of errors: " + numberOfError);
            userTasks.clear();
            trials++;
            trialtxt.setText("Number of trials: " + trials);

        }

    }

    public void endTrial(){
        if (trials==totalTrailTime){
            Intent i = new Intent(getApplicationContext(), ResultActivity.class);
            Bundle b = new Bundle();
            b.putString("method", "Button");
            i.putExtra("timePerTrial", (Serializable) timePerTrial);
            i.putExtra("errorPerTrial", (Serializable)errorPerTrial);
            i.putExtra("userdata", userData);
            i.putExtras(b);
            startActivity(i);
        }
    }

    public void recordError() {
        if (userTasks.size() != 0) {
            int lastItem = userTasks.size() - 1;
            if (!userTasks.get(lastItem).equals(defaultTasks.get(lastItem))) {
                numberOfError++;
                erortxt.setText("Number of errors: " + numberOfError);
                Log.i("last user task",userTasks.get(lastItem) );
                Log.i("last default tag",defaultTasks.get(lastItem) );
            }
        }
    }


    @Override
    public void onPause() {
        mediaPlayer.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mediaPlayer.pause();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.pause();
        super.onDestroy();
    }


}
