package com.squareapp.notion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Valentin Purrucker on 02.11.2017.
 */

public class NLService extends NotificationListenerService
{


    private Context context;

    private SharedPreferences mySharedPreferences;
    private SharedPreferences.Editor mEditor;

    private DatabaseHelperClass myDb;

    private SensorManager sensorManager;

    private Sensor sensor;


    @Override
    public void onCreate()
    {
        super.onCreate();

        this.context = getApplicationContext();

        this.mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mEditor = mySharedPreferences.edit();

        this.myDb = new DatabaseHelperClass(context);

        Log.i("NLService", "NLService created");


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




        if(mySharedPreferences.getBoolean("TimeOutEnabled", false) == true)
        {


            if(start.get(Calendar.HOUR_OF_DAY) < end.get(Calendar.HOUR_OF_DAY))
            {
                Log.i("NLService", "Start < End");

                if(now.after(start) && now.before(end))
                {
                    Log.i("NLService", "TimeOut");
                }
                else
                {
                    //e.g.: 7 to 8 => between 7 and 8 do not turn on the screen
                    Log.i("NLService", "No TimeOut");
                    if(sbn.getNotification().priority > 0)
                    {
                        turnScreenOn(sbn.getPackageName());
                    }
                }
            }
            else
            {

                Log.i("NLService", "Start > end ");

                end.add(Calendar.DAY_OF_MONTH, 1);

                if(now.after(start) && now.before(end))
                {
                    Log.i("NLService", "Timeout by Start > end");
                }
                else
                {

                        Log.i("NLService", "No Timeout by Start > end");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Log.d("NLService", "Date end: " + sdf.format(end.getTime()));
                        Log.d("NLService", "Date start: " + sdf.format(start.getTime()));
                        Log.d("NLService", "Date now: " + sdf.format(now.getTime()));

                        if(sbn.getNotification().priority > 0)
                        {
                            turnScreenOn(sbn.getPackageName());
                        }



                }
            }

        }
        else
        {
            if(sbn.getNotification().priority > 0)
            {
                turnScreenOn(sbn.getPackageName());
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



    private void turnScreenOn(String packageName)
    {
        final PowerManager.WakeLock wakeLock = ((PowerManager)context.getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

        final int duration = mySharedPreferences.getInt("ViewTimeDuration", 8);



                if(mySharedPreferences.getBoolean("SmartDeviceEnabled", false) == true)
                {
                    sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
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
                                wakeLock.acquire(duration * 1000);
                                sensorManager.unregisterListener(this);
                            }

                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy)
                        {

                        }
                    }, sensor, 2 * 1000 * 1000);

                }
                else
                {
                    wakeLock.acquire(duration * 1000);
                }












        


    }



}