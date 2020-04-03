package com.example.gesturemediaplayer;

import android.app.Activity;
import android.os.Bundle;

public class ButtonActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_layout);
    }
}
