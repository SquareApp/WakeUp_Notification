package com.squareapp.notion;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GeneralSettingsActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TimePickerDialog.OnTimeSetListener
{

    //timeout
    private static final String SHAREDPREFERENCES_TIMEOUT="TimeOutEnabled";
    private static final String SHAREDPREFERENCES_TIMEOUT_START="TimeOutStartTime";
    private static final String SHAREDPREFERENCES_TIMEOUT_END="TimeOutEndTime";
    //smartdevice
    private static final String SHAREDPREFERENCES_SMARTDEVICE="SmartDeviceEnabled";
    //quicksettings
    private static final String SHAREDPREFERENCES_QUICKSETTINGS="QuickSettingsEnabled";
    //appselection
    private static final String SHAREDPREFERENCES_APP_SELECTION="AppSelectionEnabled";

    //viewtime
    private static final String SHAREDPREFERENCES_VIEWTIME="ViewTimeDuration";


    private boolean enabled;
    private boolean oldEnabledValue;

    private Button activateButton;

    private ImageView aboutArrowImage;

    private TextView seekbarProgressText;
    private TextView timeOutStartText;
    private TextView timeOutEndText;

    private LinearLayout timeOut_TimeText;
    private LinearLayout smartDeviceWarningText;

    private SeekBar viewTimeSeekbar;

    private Switch timeOutSwitch;
    private Switch smartDeviceSwitch;


    private FrameLayout aboutLayout;

    private Animation activateBtnAnim;
    private Animation shakeAnim;
    private Animation textIncomingAnim;
    private Animation outComingAnim;

    private SharedPreferences mySharedPreferences;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setNavigationBarColor(Color.parseColor("#1ED760"));
        }

        init();
        initDefaults();
        initOnClickListener();
    }


    private void init()
    {


        this.mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.mEditor = this.mySharedPreferences.edit();

        this.activateButton = (Button)findViewById(R.id.activateButton);

        this.aboutLayout = (FrameLayout)findViewById(R.id.aboutLayout);


        //image
        this.aboutArrowImage = (ImageView)findViewById(R.id.aboutArrowImage);



        //textView
        this.seekbarProgressText = (TextView)findViewById(R.id.seekbarProgressText);
        this.timeOutStartText = (TextView)findViewById(R.id.timeOut_TimeText2);
        this.timeOutEndText = (TextView)findViewById(R.id.timeOut_TimeText4);


        //LinearLayout
        this.timeOut_TimeText = (LinearLayout) findViewById(R.id.timeOut_TimeText);
        this.smartDeviceWarningText = (LinearLayout) findViewById(R.id.smartDeviceWarningText);


        //seekbar
        this.viewTimeSeekbar = (SeekBar)findViewById(R.id.viewTimeSeekbar);
        this.viewTimeSeekbar.setOnSeekBarChangeListener(this);


        //switch
        this.timeOutSwitch = (Switch)findViewById(R.id.timeOutSwitch);
        this.smartDeviceSwitch = (Switch)findViewById(R.id.smartDeviceSwitch);



        //Animation
        activateBtnAnim = AnimationUtils.loadAnimation(this, R.anim.activate_button_anim);
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        textIncomingAnim = AnimationUtils.loadAnimation(this, R.anim.text_incoming_anim);
        outComingAnim = AnimationUtils.loadAnimation(this, R.anim.outcoming_anim);

        //onClickListener
        this.aboutArrowImage.setOnClickListener(this);
        this.timeOutSwitch.setOnClickListener(this);
        this.timeOutStartText.setOnClickListener(this);
        this.timeOutEndText.setOnClickListener(this);
        this.smartDeviceSwitch.setOnClickListener(this);



        //Flags
        this.timeOutStartText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.timeOutEndText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);


    }


    private void initDefaults()
    {
        SharedPreferences.Editor mEditor = mySharedPreferences.edit();


        //setup time-out
        boolean timeoutEnabled = mySharedPreferences.getBoolean(SHAREDPREFERENCES_TIMEOUT, false);
        if(timeoutEnabled)
        {
            this.timeOutSwitch.setChecked(true);
            this.timeOut_TimeText.setVisibility(View.VISIBLE);

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, mySharedPreferences.getInt(SHAREDPREFERENCES_TIMEOUT_START, 22));
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, mySharedPreferences.getInt(SHAREDPREFERENCES_TIMEOUT_END, 8));

            SimpleDateFormat sdf = new SimpleDateFormat("k a");
            String startTimeString = sdf.format(startTime.getTime());
            String endTimeString = sdf.format(endTime.getTime());
            this.timeOutStartText.setText(startTimeString);
            this.timeOutEndText.setText(endTimeString);
        }
        else
        {
            this.timeOutSwitch.setChecked(false);
            this.timeOut_TimeText.setVisibility(View.GONE);
        }
        //setup smartdevice
        boolean smartdeviceEnabled = mySharedPreferences.getBoolean(SHAREDPREFERENCES_SMARTDEVICE, false);
        if(smartdeviceEnabled)
        {
            this.smartDeviceSwitch.setChecked(true);
            this.smartDeviceWarningText.setVisibility(View.VISIBLE);
        }
        else
        {
            this.smartDeviceSwitch.setChecked(false);
            this.smartDeviceWarningText.setVisibility(View.GONE);
        }
        //setup quicksettings
        boolean quicksettingsEnabled = mySharedPreferences.getBoolean(SHAREDPREFERENCES_QUICKSETTINGS, false);
        if(quicksettingsEnabled)
        {

        }



        int viewTimeDuration = mySharedPreferences.getInt(SHAREDPREFERENCES_VIEWTIME, 0);
        this.viewTimeSeekbar.setProgress(viewTimeDuration);



    }

    private void initOnClickListener()
    {
        this.activateButton.setOnClickListener(this);
    }


    private void setupActivateButton()
    {



        if(enabled)
        {
            this.activateButton.setText("Disable");
        }
        else
        {
            this.activateButton.setText("Activate");
        }
    }

    private void updatePermissionState()
    {
        ComponentName cn = new ComponentName(this, NLService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        enabled = flat != null && flat.contains(cn.flattenToString());
    }


    private void setupTimeOut()
    {

        boolean isChecked = timeOutSwitch.isChecked();



        if(isChecked)
        {
            //enable timeout
            mEditor.putBoolean(SHAREDPREFERENCES_TIMEOUT, true);
            this.timeOut_TimeText.setVisibility(View.VISIBLE);
            this.timeOut_TimeText.startAnimation(textIncomingAnim);
        }
        else
        {

            //disable timeout
            mEditor.putBoolean(SHAREDPREFERENCES_TIMEOUT, false);
            this.timeOut_TimeText.startAnimation(outComingAnim);
            outComingAnim.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    timeOut_TimeText.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });
        }
        mEditor.commit();

    }

    private void setupSmartDevice()
    {

        boolean isChecked = smartDeviceSwitch.isChecked();

        if(isChecked)
        {
            mEditor.putBoolean(SHAREDPREFERENCES_SMARTDEVICE, true);
            smartDeviceWarningText.setVisibility(View.VISIBLE);
            smartDeviceWarningText.startAnimation(textIncomingAnim);
        }
        else
        {
            mEditor.putBoolean(SHAREDPREFERENCES_SMARTDEVICE, false);
            this.smartDeviceWarningText.startAnimation(outComingAnim);
            outComingAnim.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    smartDeviceWarningText.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });
        }

        mEditor.commit();

    }



    private void setupTimeOutTimePickerDialog()
    {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, mySharedPreferences.getInt(SHAREDPREFERENCES_TIMEOUT_START, 0));
        TimePickerDialog tpd = TimePickerDialog.newInstance(this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);

        tpd.setTitle("From");
        tpd.enableMinutes(false);
        tpd.setThemeDark(true);

        tpd.show(getFragmentManager(), "Timepickerdialog");

    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            case R.id.activateButton:
                oldEnabledValue = enabled;
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                break;


            case R.id.aboutArrowImage:
                aboutLayout.startAnimation(shakeAnim);
                break;



            case R.id.timeOutSwitch:
                setupTimeOut();
                break;

            case R.id.timeOut_TimeText2:
                setupTimeOutTimePickerDialog();
                break;

            case R.id.timeOut_TimeText4:
                setupTimeOutTimePickerDialog();
                break;


            case R.id.smartDeviceSwitch:
                setupSmartDevice();
                break;



        }
    }







    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        this.seekbarProgressText.setText(String.valueOf(progress +4) + "s");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        int duration = seekBar.getProgress();

        mEditor.putInt(SHAREDPREFERENCES_VIEWTIME, duration);
        mEditor.commit();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second)
    {
        if (view.getTitle().equals("From"))
        {

            Calendar startTime = Calendar.getInstance();

            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);

            mEditor.putInt(SHAREDPREFERENCES_TIMEOUT_START, hourOfDay);

            SimpleDateFormat sdf = new SimpleDateFormat("k a");
            String formattedTime = sdf.format(startTime.getTime());
            this.timeOutStartText.setText(formattedTime);


            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY, mySharedPreferences.getInt(SHAREDPREFERENCES_TIMEOUT_END, 0));
            TimePickerDialog tpd = TimePickerDialog.newInstance(this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true);

            tpd.setThemeDark(true);

            tpd.enableMinutes(false);

            tpd.setTitle("to");

            tpd.show(getFragmentManager(), "Timepickerdialog");
        }
        else
        {

            Calendar endTime = Calendar.getInstance();

            endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);

            mEditor.putInt(SHAREDPREFERENCES_TIMEOUT_END, hourOfDay);

            SimpleDateFormat sdf = new SimpleDateFormat("k a");
            String formattedTime = sdf.format(endTime.getTime());
            this.timeOutEndText.setText(formattedTime);


        }

    }



    @Override
    protected void onResume()
    {
        updatePermissionState();
        if(oldEnabledValue != enabled)
        {
            activateButton.startAnimation(activateBtnAnim);
            Handler handler = new Handler();
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    setupActivateButton();
                }
            };
            handler.postDelayed(runnable, 200);
        }

        super.onPostResume();
    }


    @Override
    protected void onPause()
    {
        mEditor.commit();
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
    }
}
