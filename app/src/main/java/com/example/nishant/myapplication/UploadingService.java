package com.example.nishant.myapplication;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



/**
 * To create the service for the api response of alcohol and driver drowsiness detection.
 */
public class UploadingService extends Service {
    Context mcontext;
    MediaPlayer mp;



    public UploadingService(Context applicationContext) {
        super();
        mcontext = applicationContext;
    }

    public UploadingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        mp = MediaPlayer.create(this, R.raw.alarm);
        mp.setLooping(false);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, BroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        Log.d("timer123", "timer");
        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d("RESPONSE", "DATA223");

                if (!isForeground("com.example.nishant.myapplication"))
                {
                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, "https://sihh.herokuapp.com/data/4/", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                                Log.d("RESPONSE", "DATA223");
                                if (response.getString("response").equals("True")) {

                                    Intent dialogIntent = new Intent(UploadingService.this, MapsActivity.class);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);


                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                                    int notificationId = 1;
                                    String channelId = "channel-01";
                                    String channelName = "Channel Name";
                                    int importance = NotificationManager.IMPORTANCE_HIGH;

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        NotificationChannel mChannel = new NotificationChannel(
                                                channelId, channelName, importance);
                                        notificationManager.createNotificationChannel(mChannel);
                                    }

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setContentTitle("Excedding Permissible limit")
                                            .setContentText("Strongly adviced to stop driving, SMS sent to emergency contact")
                                    ;

                                    Notification notification = mBuilder.setWhen(0)
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Strongly adviced to stop driving, SMS sent to emergency contact"))
                                           .build();

                                    mp.start();



                                    notificationManager.notify(notificationId, notification);



                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("volleyerror", error.toString());
                        }
                    }));

                } else {
                    Log.d("network-condition", "offlie");
                }

            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

}

