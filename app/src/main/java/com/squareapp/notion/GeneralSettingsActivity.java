package com.squareapp.notion;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class GeneralSettingsActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DiscreteSeekBar.OnProgressChangeListener
{



    private static String appVersionString = "1.0";

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
    private boolean isAllSelected = false;

    private Button activateButton;

    private ImageView aboutArrowImage;
    private ImageView appIcon;

    private TextView viewTimeProgressText;
    private TextView timeOutStartText;
    private TextView timeOutEndText;

    private LinearLayout timeOut_TimeText;
    private LinearLayout smartDeviceWarningText;
    private LinearLayout editExclusionLayout;
    private LinearLayout editExclusionLayoutClickable;

    private DiscreteSeekBar viewTimeSeekbar;

    private Switch timeOutSwitch;
    private Switch smartDeviceSwitch;
    private Switch exclusionSwitch;

    ArrayList<AppItem> appItemsList = new ArrayList<>();

    private FrameLayout aboutLayout;

    private View appExclusionDialogView;

    private RecyclerView appExclusionRV;

    private Animation activateBtnAnim;
    private Animation shakeAnim;
    private Animation textIncomingAnim;
    private Animation outComingAnim;
    private Animation aboutDialogAnim;

    private SharedPreferences mySharedPreferences;

    private SharedPreferences.Editor mEditor;

    public AppExclusionAdapter mAdapter;

    private LinearLayoutManager lm;

    private Dialog appExclusionDialog;

    private DatabaseHelperClass myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setNavigationBarColor(Color.parseColor("#121314"));
        }

        init();
        initDefaults();
        initOnClickListener();




    }


    private void init()
    {


        this.myDb = new DatabaseHelperClass(this);
        //this.myDb.deleteDatabse();

        PackageInfo info = null;
        try
        {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //Adapter
        setupLists();
        createAppExclusionDialog();
        //adapter end


        appVersionString = info.versionName;

        this.mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.mEditor = this.mySharedPreferences.edit();

        this.activateButton = (Button)findViewById(R.id.activateButton);

        this.aboutLayout = (FrameLayout)findViewById(R.id.aboutLayout);


        //image
        this.aboutArrowImage = (ImageView)findViewById(R.id.aboutArrowImage);
        this.appIcon = (ImageView)findViewById(R.id.appIcon);



        //textView
        this.viewTimeProgressText = (TextView)findViewById(R.id.seekbarProgressText);
        this.timeOutStartText = (TextView)findViewById(R.id.timeOut_TimeText2);
        this.timeOutEndText = (TextView)findViewById(R.id.timeOut_TimeText4);


        //LinearLayout
        this.timeOut_TimeText = (LinearLayout) findViewById(R.id.timeOut_TimeText);
        this.smartDeviceWarningText = (LinearLayout) findViewById(R.id.smartDeviceWarningText);
        this.editExclusionLayout = (LinearLayout)findViewById(R.id.editExclusionLayout);
        this.editExclusionLayoutClickable =(LinearLayout)findViewById(R.id.editExclusionLayoutClickable);


        //seekbar
        this.viewTimeSeekbar = (DiscreteSeekBar) findViewById(R.id.viewTimeSeekbar);
        this.viewTimeSeekbar.setOnProgressChangeListener(this);


        //switch
        this.timeOutSwitch = (Switch)findViewById(R.id.timeOutSwitch);
        this.smartDeviceSwitch = (Switch)findViewById(R.id.smartDeviceSwitch);
        this.exclusionSwitch = (Switch)findViewById(R.id.exclusionSwitch);



        //Animation
        activateBtnAnim = AnimationUtils.loadAnimation(this, R.anim.activate_button_anim);
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        textIncomingAnim = AnimationUtils.loadAnimation(this, R.anim.text_incoming_anim);
        outComingAnim = AnimationUtils.loadAnimation(this, R.anim.outcoming_anim);
        aboutDialogAnim = AnimationUtils.loadAnimation(this, R.anim.about_dialog_start_anim);

        //onClickListener
        this.aboutArrowImage.setOnClickListener(this);
        this.appIcon.setOnClickListener(this);

        this.timeOutSwitch.setOnClickListener(this);
        this.timeOutStartText.setOnClickListener(this);
        this.timeOutEndText.setOnClickListener(this);
        this.smartDeviceSwitch.setOnClickListener(this);
        this.exclusionSwitch.setOnClickListener(this);

        this.aboutLayout.setOnClickListener(this);
        this.editExclusionLayoutClickable.setOnClickListener(this);




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
        boolean appExclusionEnabled = mySharedPreferences.getBoolean(SHAREDPREFERENCES_APP_SELECTION, false);
        if(appExclusionEnabled)
        {
            this.exclusionSwitch.setChecked(true);
            this.editExclusionLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            this.exclusionSwitch.setChecked(false);
            this.editExclusionLayout.setVisibility(View.GONE);
        }



        int viewTimeDuration = mySharedPreferences.getInt(SHAREDPREFERENCES_VIEWTIME, 8);
        this.viewTimeSeekbar.setProgress(viewTimeDuration);
        this.viewTimeProgressText.setText(String.valueOf(viewTimeDuration) + "s");



    }

    private void initOnClickListener()
    {
        this.activateButton.setOnClickListener(this);
    }


    private void setupActivateButton()
    {



        if(enabled)
        {
            this.activateButton.setText(getString(R.string.disable_text));
        }
        else
        {
            this.activateButton.setText(getString(R.string.activate_text));
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

    private void setupExclusion()
    {
        boolean isChecked = exclusionSwitch.isChecked();



        if(isChecked)
        {
            //enable timeout
            mEditor.putBoolean(SHAREDPREFERENCES_APP_SELECTION, true);
            this.editExclusionLayout.setVisibility(View.VISIBLE);
            this.editExclusionLayout.startAnimation(textIncomingAnim);
        }
        else
        {

            //disable timeout
            mEditor.putBoolean(SHAREDPREFERENCES_APP_SELECTION, false);
            this.editExclusionLayout.startAnimation(outComingAnim);
            outComingAnim.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    editExclusionLayout.setVisibility(View.GONE);
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


    private void showAboutDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);




        View view = getLayoutInflater().inflate(R.layout.about_dialog_layout, null);
        mBuilder.setView(view);

        FrameLayout dialogContent = (FrameLayout)view.findViewById(R.id.dialogContent);

        TextView appVersionText = (TextView)view.findViewById(R.id.appVersionText);
        appVersionText.setText(appVersionText.getText() + appVersionString);

        Button contactButton = (Button)view.findViewById(R.id.contactButton);
        Button shareButton = (Button)view.findViewById(R.id.shareButton);

        contactButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);



        Dialog dialog = mBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();



    }


    public void createAppExclusionDialog()
    {
        lm = new LinearLayoutManager(this);



        appExclusionDialogView = getLayoutInflater().inflate(R.layout.appexclusion_dialog, null);


        ArrayList<Drawable> appItemIcons = new ArrayList<>();

        appItemsList = myDb.getAllAppsFromExclusionList();
        for(int i = 0; i < appItemsList.size(); i++)
        {
            Drawable appIcon = null;
            try
            {
                appIcon = getPackageManager().getApplicationIcon(appItemsList.get(i).getAppPackageName());
            } catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }

            appItemsList.get(i).setAppIcon(appIcon);

        }


        mAdapter = new AppExclusionAdapter(this, appItemsList, getLayoutInflater(), getPackageManager());

        appExclusionRV = (RecyclerView)appExclusionDialogView.findViewById(R.id.appListRecyclerView);

        appExclusionRV.setAdapter(mAdapter);
        appExclusionRV.setLayoutManager(lm);

        Button selectAllButton = (Button)appExclusionDialogView.findViewById(R.id.selectAllButton);
        selectAllButton.setOnClickListener(this);


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);

        mBuilder.setView(appExclusionDialogView);

        appExclusionDialog = mBuilder.create();
        appExclusionDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

    }


    private void setupLists()
    {
        List<ApplicationInfo> packages;



        packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        Collections.sort(packages, new ApplicationInfo.DisplayNameComparator(getPackageManager()));

        for (int i = 0; i < packages.size(); i++)
        {

            if (getPackageManager().getLaunchIntentForPackage(packages.get(i).packageName) != null)
            {

                AppItem appItem = new AppItem();
                String appName = getPackageManager().getApplicationLabel(packages.get(i)).toString();


                appItem.createAppItem(0, packages.get(i).packageName, appName, 0, null);

                if (myDb.checkIfAppIsInList(packages.get(i).packageName) == false)
                {
                    this.myDb.addAppToExclusionList(appItem);
                    Log.d("Database", "App: " + appName + " has been added to list");
                }
                else
                {
                    Log.d("Database", "App: " + appName + " is already in the database");
                }
            }
        }


    }

    private void selectAll()
    {
        isAllSelected = !isAllSelected;
        for(int i = 0; i < appItemsList.size(); i++)
        {
            AppItem appItem = appItemsList.get(i);
            if(isAllSelected)
            {
                appItem.setChecked(1);
                myDb.updateAppItem(appItem.getAppID(), 1);
            }
            else
            {
                appItem.setChecked(0);
                myDb.updateAppItem(appItem.getAppID(), 0);
            }

            mAdapter.notifyDataSetChanged();
        }
    }



    private void showAppExclusionDialog()
    {
        appExclusionDialog.show();
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
                showAboutDialog();
                break;

            case R.id.aboutLayout:
                showAboutDialog();
                break;



            case R.id.appIcon:
                showAboutDialog();
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


            case R.id.exclusionSwitch:
                setupExclusion();
                break;

            case R.id.editExclusionLayoutClickable:
                showAppExclusionDialog();
                break;

            case R.id.selectAllButton:
                selectAll();
                break;


            case R.id.contactButton:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"squareapp.games@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WakeUp!");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, getString(R.string.share_via_email_text)));
                break;

            case R.id.shareButton:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Check out this App: WakeUp! \nLink");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_link_text)));
                break;



        }
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

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser)
    {

    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar)
    {
        int duration = seekBar.getProgress();

        viewTimeProgressText.setText(String.valueOf(duration) + "s");

        mEditor.putInt(SHAREDPREFERENCES_VIEWTIME, duration);
        mEditor.commit();
    }
}
