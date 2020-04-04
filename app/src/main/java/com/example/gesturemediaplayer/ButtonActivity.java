package com.example.gesturemediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class ButtonActivity extends Activity {

    SeekBar volumeControl; //slider

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_layout);

        volumeControl = (SeekBar) findViewById(R.id.volumeControl);
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
        });
    }

    //play music
    public void clickPlay(View v) {
        Log.i("Button", "Play");
    }

    //pause music
    public void clickPause(View v) {
        Log.i("Button", "Pause");
    }



    // Called when the "Exit" button is pressed.
    public void clickExit(View view) {
        this.finish(); // terminate
    }
}
