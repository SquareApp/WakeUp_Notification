package com.squareapp.notion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity
{


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = sharedPreferences.edit();

        /*
        mEditor.remove("PERMISSIONS_GIVEN");
        mEditor.commit();
        */

        if(sharedPreferences.getBoolean("PERMISSIONS_GIVEN", false) == true)
        {
            Intent intent = new Intent(this, GeneralSettingsActivity.class);
            startActivity(intent);
        }
        else
        {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
