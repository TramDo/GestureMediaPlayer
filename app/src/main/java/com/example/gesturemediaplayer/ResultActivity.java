package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
    }

    public void clickExitApp(View view)
    {
        Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        Log.i("MYDEBUG", String.valueOf(true));
        startActivity(intent);
    }
    public void clickSetup(View view)
    {
        // start experiment activity
        Intent i = new Intent(getApplicationContext(), SetupActivity.class);
        startActivity(i);
        // comment out (return to setup after clicking BACK in main activity
        //finish();
    }


}
