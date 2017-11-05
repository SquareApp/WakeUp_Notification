package com.squareapp.notion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Valentin Purrucker on 02.11.2017.
 */

public class NLService extends NotificationListenerService
{

    private Context context;


    private SharedPreferences mySharedPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    public void onCreate()
    {
        super.onCreate();

        this.context = getApplicationContext();

        this.mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mEditor = mySharedPreferences.edit();


    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn)
    {
        super.onNotificationPosted(sbn);

        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, mySharedPreferences.getInt("TimeOutStartTime", 22));
        start.set(Calendar.MINUTE, 0);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, mySharedPreferences.getInt("TimeOutEndTime", 8));
        end.set(Calendar.MINUTE, 0);

        Log.i("TimeOutInfo", "Start:" + String.valueOf(start.get(Calendar.HOUR_OF_DAY)));
        Log.i("TimeOutInfo", "End:" + String.valueOf(end.get(Calendar.HOUR_OF_DAY)));
        /*
        SensorManager sensorManager;
        Sensor sensor;

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if(event.values[0] == 0)
                {

                }
                else
                {


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        }, sensor, 2 * 1000 * 1000);
        */

        if(mySharedPreferences.getBoolean("TimeOutEnabled", false) == true)
        {

            if(start.get(Calendar.HOUR_OF_DAY) < end.get(Calendar.HOUR_OF_DAY))
            {
                Log.i("LG_G5", "Start < End");

                if(now.get(Calendar.HOUR_OF_DAY) >= start.get(Calendar.HOUR_OF_DAY) && now.get(Calendar.HOUR_OF_DAY) < end.get(Calendar.HOUR_OF_DAY))
                {
                    Log.i("LG_G5", "TimeOut");
                }
                else
                {
                    //e.g.: 7 to 8 => between 7 and 8 do not turn on the screen
                    Log.i("LG_G5", "No TimeOut");
                    if(sbn.getNotification().priority > 0)
                    {
                        turnScreenOn();
                    }
                }
            }
            else
            {

                Log.i("LG_G5", "Start > end ");

                if(now.get(Calendar.HOUR_OF_DAY) >= start.get(Calendar.HOUR_OF_DAY) && now.get(Calendar.HOUR_OF_DAY) < end.get(Calendar.HOUR_OF_DAY))
                {

                }
                else
                {
                    if(sbn.getNotification().priority > 0)
                    {
                        turnScreenOn();
                    }
                }
            }

        }
        else
        {
            if(sbn.getNotification().priority > 0)
            {
                turnScreenOn();
            }
        }





    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }



    private void turnScreenOn()
    {
        PowerManager.WakeLock wakeLock = ((PowerManager)context.getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire();
        wakeLock.release();
    }


}
