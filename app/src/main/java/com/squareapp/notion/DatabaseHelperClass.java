package com.squareapp.notion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Valentin Purrucker on 11.11.2017.
 */

public class DatabaseHelperClass extends SQLiteOpenHelper
{

    private Context context;

    private static final String DATABASE_NAME = "wakeup.db";
    //Table

    private static final String TABLE_APPLIST = "Applist";
    private static final String APPLIST_ID = "AppID";           //0
    private static final String APPLIST_PACKAGENAME ="AppPackagename"; // 1
    private static final String APPLIST_APPNAME = "Appname";    //2
    private static final String APPLIST_APPEXCLUDED = "Excluded";//3

    private static final int DATABASE_VERSION = 1;







    public DatabaseHelperClass(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        SQLiteDatabase db = getWritableDatabase();
    }




    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String createDatabase = "CREATE TABLE " + TABLE_APPLIST + "(" + APPLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + APPLIST_PACKAGENAME + " TEXT, "
                + APPLIST_APPNAME + " TEXT, "
                +APPLIST_APPEXCLUDED + " INTEGER "
                +")";

        db.execSQL(createDatabase);




    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }




    public boolean checkIfAppIsInList(String packageName)
    {
        boolean isInList;

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + APPLIST_ID + " FROM " + TABLE_APPLIST + " WHERE " + APPLIST_PACKAGENAME + " = " + "'" + packageName + "'";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }

    }



    public ArrayList<AppItem> getAllAppsFromExclusionList()
    {
        ArrayList<AppItem> appList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String allAppsQuery = "SELECT * FROM " + TABLE_APPLIST + " ORDER BY " + APPLIST_APPNAME + " ASC";

        Cursor allAppsCursor = db.rawQuery(allAppsQuery, null);

        if(allAppsCursor.moveToFirst())
        {
            do
            {
                AppItem appItem = new AppItem();
                appItem.setAppID(Integer.parseInt(allAppsCursor.getString(0)));
                appItem.setAppPackageName(allAppsCursor.getString(1));
                appItem.setAppName(allAppsCursor.getString(2));
                appItem.setChecked(Integer.parseInt((allAppsCursor.getString(3))));
                appList.add(appItem);
            }while (allAppsCursor.moveToNext());
        }

        return appList;
    }


    public AppItem getAppItemWithID(String appPackageName)
    {
        AppItem appItem = new AppItem();

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " +TABLE_APPLIST + " WHERE " + APPLIST_PACKAGENAME + "='" + appPackageName + "'";

        Cursor cursor = db.rawQuery(query, null);

        appItem.setAppID(Integer.parseInt(cursor.getString(0)));
        appItem.setAppName(cursor.getString(2));
        appItem.setChecked(Integer.parseInt(cursor.getString(3)));

        return appItem;
    }

    public void addAppToExclusionList(AppItem appItem)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(APPLIST_PACKAGENAME, appItem.getAppPackageName());
        contentValues.put(APPLIST_APPNAME, appItem.getAppName());
        contentValues.put(APPLIST_APPEXCLUDED, appItem.isChecked());

        db.insert(TABLE_APPLIST, null, contentValues);

        db.close();
    }


    public void updateAppItem(int appID, int isExcluded)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // 0 = false; 1 = true
        contentValues.put(APPLIST_APPEXCLUDED, isExcluded);

        db.update(TABLE_APPLIST, contentValues, APPLIST_ID + " = ?",
                new String[] {String.valueOf(appID)});
    }











    public void deleteDatabse()
    {
        SQLiteDatabase database = getWritableDatabase();
        context.deleteDatabase(DATABASE_NAME);
    }
}
