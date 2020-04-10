package com.example.gesturemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Comparator;

public class SetupActivity extends Activity implements AdapterView.OnItemSelectedListener {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final static String[] INPUT_METHOD_STRING = {"Gesture", "Button"};
    final static String[] GENDER_STRING = {"Male", "Female", "Other"};
    final static String[] TRIAL_NUMBER_STRING = {"1", "2", "3", "4", "5"};
    private Spinner spinMethod, spinGender, spinTrial;
    private UserData userData;
    private EditText initialUI;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_layout);

        initialUI = (EditText) findViewById(R.id.InitialInput);
        spinMethod = (Spinner) findViewById(R.id.paramInput);
        spinGender = (Spinner) findViewById(R.id.GenderInput);
        spinTrial = (Spinner) findViewById(R.id.TrialInput);


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,R.layout.spinnerstyle,
                INPUT_METHOD_STRING);
        spinMethod.setAdapter(adapter);

        ArrayAdapter<CharSequence> adaptergender = new ArrayAdapter<CharSequence>(this,R.layout.spinnerstyle,
                GENDER_STRING);
        spinGender.setAdapter(adaptergender);

        ArrayAdapter<CharSequence> adapterTrial = new ArrayAdapter<CharSequence>(this,R.layout.spinnerstyle,
                TRIAL_NUMBER_STRING);
        spinTrial.setAdapter(adapterTrial);

        userData = new UserData();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // an OnItemSelectedListener method (must implement, but not needed)
    }

    /*
     * For the Sensor spinner, we need to check the user's selection because some sensors might appear that are not
     * supported by this demo (e.g., the "double twist" sensor). If the user selects one of these, use Toast to ask
     * the user to select again.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        //spinMethod.setSelection(pos);
    }


    // Called when the "OK" button is pressed.
    public void clickOK(View view) {
        // get user's choices --> the input method

        // locate the input method selected by the user
        String initial = initialUI.getText().toString();

        int methodIndex = spinMethod.getSelectedItemPosition();
        String inputMethod = INPUT_METHOD_STRING[methodIndex];

        int GenderIndex = spinGender.getSelectedItemPosition();
        String gendner = GENDER_STRING[GenderIndex];
        Log.i(MYDEBUG, gendner);

        int trialIndex = spinTrial.getSelectedItemPosition();
        String trial = TRIAL_NUMBER_STRING[trialIndex];


        userData.setInitial(initial);
        userData.setGender(gendner);
        userData.setMethod(inputMethod);
        userData.setTrial(trial);
        userData.setTrialnumber(trialIndex);
        Intent i;


        if (inputMethod == "Gesture") {
            i = new Intent(getApplicationContext(), SensorActivity.class);

        }
        else {
            i = new Intent(getApplicationContext(), ButtonActivity.class);

        }
        i.putExtra("userdata", userData);
        startActivity(i);
    }

    // Called when the "Exit" button is pressed.
    public void clickExit(View view) {
        this.finish(); // terminate
    }

    /*
     * The following lifecycle methods are included for an in-class demonstration of
     * activity-to-activity transitions. This demo app includes two activities, a setup activity and
     * a main activity. The app launches into the setup activity. The setup activity transitions to
     * the main activity and the main activity transitions back to the setup activity. The
     * setup-to-main transition occurs when the "OK" button is pressed in the setup activity. The
     * main-to-setup transition occurs when the "Back" button is pressed in the main activity.
     *
     * What lifecycle methods execute when the "OK" button is pressed (in the setup activity)? What
     * methods execute when the Back button is pressed (in the main activity)? To find out, run this
     * demo with the the LogCat window open (and with a device connected to the host in USB
     * debugging mode).
     */

    @Override
    public void onStart() {
        Log.i(MYDEBUG, "onStart! (setup)");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(MYDEBUG, "onResume! (setup)");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(MYDEBUG, "onPause! (setup)");
        super.onPause();
    }

    @Override
    public void onRestart() {
        Log.i(MYDEBUG, "onRestart! (setup)");
        super.onRestart();
    }

    @Override
    public void onStop() {
        Log.i(MYDEBUG, "onStop! (setup)");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(MYDEBUG, "onDestroy! (setup)");
        super.onDestroy();
    }


}


