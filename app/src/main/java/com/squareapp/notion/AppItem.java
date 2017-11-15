package com.squareapp.notion;

import android.graphics.drawable.Drawable;

/**
 * Created by Valentin Purrucker on 11.11.2017.
 */

public class AppItem
{

    private int appID;
    private int checked;

    private String appName;
    private String appPackageName;



    private Drawable appIcon;


    public void createAppItem(int appID, String appPackageName, String appName, int checked, Drawable appIcon)
    {
        this.setAppID(appID);
        this.setChecked(checked);
        this.setAppName(appName);
        this.setAppIcon(appIcon);
        this.setAppPackageName(appPackageName);
    }




    public void setAppName(String appName)
    {
        this.appName = appName;
    }



    public String getAppName()
    {
        return appName;
    }

    public int isChecked()
    {
        return checked;
    }

    public void setChecked(int checked)
    {
        this.checked = checked;
    }

    public Drawable getAppIcon()
    {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon)
    {
        this.appIcon = appIcon;
    }

    public int getAppID()
    {
        return appID;
    }

    public void setAppID(int appID)
    {
        this.appID = appID;
    }

    public String getAppPackageName()
    {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName)
    {
        this.appPackageName = appPackageName;
    }
}
