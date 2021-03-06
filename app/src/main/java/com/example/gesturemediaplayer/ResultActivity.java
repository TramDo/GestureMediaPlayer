package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends Activity {

    private float durationTimeArray[];
    private int errorTimeArray[];
    private int errorNumber;
    private UserData userData;
    private String durationForUI = "";
    private String errorNumberForUI = "";
    private float averageTrialTime;
    private  float errorRate;
    private  String method;
    private List<Float> timePerTrial = new ArrayList<Float>();
    private List<Integer>errorPerTrial = new ArrayList<Integer>();


    //UI
    private TextView initialUI, genderUI, TrialUI, inputUI, durationtimeUI,averagetimeUI, errorNumberUI, errorRateUI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        initialUI = findViewById(R.id.InitialInputText);
        genderUI = findViewById(R.id.GenderText);
        TrialUI = findViewById(R.id.trialLabelInput);
        inputUI = findViewById(R.id.Method);
        durationtimeUI = findViewById(R.id.TimePerTrial);
        averagetimeUI = findViewById(R.id.averagetrailtime);
        errorNumberUI = findViewById(R.id.errornumber);
        errorRateUI = findViewById(R.id.errorrate);

        Bundle extras = getIntent().getExtras();
        method = extras.getString("method");

        if (extras != null) {

            if (method.equals("Sensor")) {

                durationTimeArray = extras.getFloatArray("trailTimeArray");
                errorTimeArray = extras.getIntArray("errornumberArray");
                errorNumber = extras.getInt("errornumber");
                Log.i("MYDEBUG", "errorNumber" + String.valueOf(errorNumber));
                userData = (UserData) getIntent().getSerializableExtra("userdata");

                for (float i : durationTimeArray) {
                    durationForUI += String.format("%.02f", i) + ", ";
                    averageTrialTime += i;
                    Log.i("MYDEBUG", "saved time" + String.valueOf(i));
                }

                for (int i : errorTimeArray) {
                    errorNumberForUI += i + ", ";
                    errorRate += i;
                    //averageTrialTime += i;
                    // Log.i("MYDEBUG", "saved time" + String.valueOf(i));
                }

            }

            else if (method.equals("Button")) {

                timePerTrial = (List<Float>) extras.getSerializable("timePerTrial");
                errorPerTrial = (List<Integer>) extras.getSerializable("errorPerTrial");
                userData = (UserData) getIntent().getSerializableExtra("userdata");

                for (float i : timePerTrial) {
                    durationForUI += String.format("%.02f", i) + ", ";
                    averageTrialTime += i;
                    Log.i("MYDEBUG", "saved time" + String.valueOf(i));
                }

                for (int i : errorPerTrial) {
                    errorNumberForUI += i + ", ";
                    errorRate += i;
                }

            }


            averageTrialTime /= userData.getTrialnumber() + 1;
            errorRate = (errorRate / ((userData.getTrialnumber() + 1) * 8)) * 100;
            initialUI.setText("Initial = " + userData.getInitial());
            genderUI.setText("Gender = " + userData.getGender());
            TrialUI.setText("Number of Trials = " + userData.getTrial());
            inputUI.setText("Input Method = " + userData.getMethod());
            durationtimeUI.setText("Time Per Trial = " + durationForUI + " (s)");
            averagetimeUI.setText("Average Per Trial = " + String.format("%.02f", averageTrialTime) + " (s)");
            errorNumberUI.setText("Error Number = " + errorNumberForUI);
            errorRateUI.setText("Error Rate = " + String.format("%.02f", errorRate) + "%");
        }

    }

    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

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
        finish();
    }


}
