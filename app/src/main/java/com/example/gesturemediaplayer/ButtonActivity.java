package com.example.gesturemediaplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gesturemediaplayer.*;

public class ButtonActivity extends Activity {

    private Button btnPlay, btnPause, btnForward,  btnBackward;
    private ImageView iv;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private TextView durationtxt, remainTimetxt, titletxt;
    private SeekBar volumeControl, seekbar; //slider

    public static int oneTimeOnly = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_layout);


       /* volumeControl = (SeekBar) findViewById(R.id.volumeControl);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add whatever, might not be needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("Slider", "Progress: "+ progressChangedValue);
            }
        });*/

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
    }

    //play music
    public void clickPlay(View v) {
        Log.i("Button", "Play");
    }

    //pause music
    public void clickPause(View v) {
        Log.i("Button", "Pause");
    }


}
