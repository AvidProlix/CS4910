package com.none.attack;

import android.app.job.JobParameters;
import android.app.job.JobService;

import android.util.Log;

public class ExampleJobService extends JobService {
    private static final String TAG = "EXAMPLE Job Service";
            private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }
    private void doBackgroundWork(final JobParameters params)
    {
        new Thread(new Runnable(){

            @Override
            public void run(){
                for(int i=0; i < 10; i++)
                {
                    if(jobCancelled){
                        return;
                    }

                    Log.d(TAG,"run: " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                Log.d(TAG,"Job Finished");
                jobFinished(params,false);
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"Job cancelled before completion");
        jobCancelled = true;
        return false;
    }
}
