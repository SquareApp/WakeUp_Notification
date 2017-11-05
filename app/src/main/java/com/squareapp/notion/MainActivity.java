package com.squareapp.notion;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Transition;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private boolean isChecked = false;

    private Button continueButton;

    private TextView necessarytext;

    private Switch permissionSwitch;

    private Animation shakeAnim;
    private Animation gotoLeftAnim;

    private FrameLayout mainLayout;

    private CoordinatorLayout coordinationLayoutMain;

    private SharedPreferences mySharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTransitionAnimation();



        init();

    }


    private void init()
    {

        this.mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mEditor = mySharedPreferences.edit();

        this.continueButton = (Button)findViewById(R.id.continueButton);
        this.continueButton.setOnClickListener(this);

        this.mainLayout = (FrameLayout)findViewById(R.id.mainLayout);

        this.permissionSwitch = (Switch)findViewById(R.id.permissionSwitch);
        this.permissionSwitch.setOnClickListener(this);

        this.necessarytext = (TextView)findViewById(R.id.necessarytext);
        this.necessarytext.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);



        this.coordinationLayoutMain = (CoordinatorLayout)findViewById(R.id.coordinationLayoutMain);

        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        gotoLeftAnim = AnimationUtils.loadAnimation(this, R.anim.goto_left);
    }

    private void setupTransitionAnimation()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            Transition explode = new Explode();

            getWindow().setExitTransition(explode);
        }

    }

    private void setupSwitchButton()
    {

        ComponentName cn = new ComponentName(this, NLService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        final boolean enabled = flat != null && flat.contains(cn.flattenToString());

        if(enabled)
        {
            this.permissionSwitch.setChecked(true);
        }
        else
        {
            this.permissionSwitch.setChecked(false);
        }
    }



    @Override
    protected void onResume()
    {
        setupSwitchButton();

        super.onPostResume();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.continueButton:
                if(!permissionSwitch.isChecked())
                {
                    continueButton.startAnimation(shakeAnim);
                }
                else
                {
                    mEditor.putBoolean("PERMISSIONS_GIVEN", true);
                    mEditor.commit();
                    Intent generalSettingsIntent = new Intent(this, GeneralSettingsActivity.class);
                    startActivity(generalSettingsIntent);
                }

                break;

            case R.id.permissionSwitch:
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                break;
        }
    }


}
