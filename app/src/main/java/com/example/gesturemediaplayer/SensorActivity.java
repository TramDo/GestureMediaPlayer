package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SensorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);
    }

    // Called when the "Exit" button is pressed.
    public void clickExit(View view) {
        this.finish(); // terminate
    }
}
