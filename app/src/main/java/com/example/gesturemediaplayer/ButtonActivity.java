package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Context;
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
    private TextView durationtxt, remainTimetxt, titletxt, progresstxt;
    private SeekBar volumeControl, seekbar; //slider
    private AudioManager audioManager;


    public static int oneTimeOnly = 0;
    //int maxVolume = 100;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_layout);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        btnPause.setEnabled(false);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        maxvolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentvolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl = (SeekBar) findViewById(R.id.volumeControl);

        progresstxt.setText(String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))+ "%");

        volumeControl.setMax(maxvolume);
        volumeControl.setProgress(currentvolume);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;

                float volumePercent = progress / (float)maxvolume;
                progresstxt.setText(String.valueOf(volumePercent * 100) + "%");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add whatever, might not be needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("Slider", "Progress: "+ progressChangedValue);


            }
        });



    }

    @Override
    public void onClick(View view) {
        if (view == btnPlay){

            //Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
           /* if (oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }*/
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
            btnPause.setEnabled(false);
            btnPlay.setEnabled(true);
        } else if (view == btnBackward){
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
