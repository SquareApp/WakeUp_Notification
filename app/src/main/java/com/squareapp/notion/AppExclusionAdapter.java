package com.squareapp.notion;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Valentin Purrucker on 09.11.2017.
 */

public class AppExclusionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private boolean allChecked;

    private ArrayList<AppItem> mData;

    private Context context;

    private LayoutInflater inflater;

    private PackageManager packageManager;

    private DatabaseHelperClass myDb;






    public AppExclusionAdapter(Context context, ArrayList<AppItem> mData, LayoutInflater inflater, PackageManager packageManager)
    {
        this.context = context;

        this.inflater = inflater;

        this.mData = mData;
        Log.d("AppExclusionAdapter", "Size : " + String.valueOf(mData.size()));


        this.packageManager = packageManager;

        //this.myDb = new DatabaseHelperClass(context);



    }






    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.appexclusion_list_row_layout, parent, false);

        AppViewHolder appViewHolder = new AppViewHolder(view);
        return appViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final AppViewHolder appViewHolder = (AppViewHolder)holder;

        final AppItem appItem = mData.get(position);


        appViewHolder.nameText.setText(appItem.getAppName());


        appViewHolder.appIcon.setImageDrawable(appItem.getAppIcon());

        if(appItem.isChecked() == 0)
        {
            appViewHolder.excludeCheckBox.setChecked(false);
        }
        else
        {
            if(appItem.isChecked() == 1)
            {
                appViewHolder.excludeCheckBox.setChecked(true);
            }

        }


        appViewHolder.mainContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(appItem.isChecked() == 1)
                {
                    myDb.updateAppItem(appItem.getAppID(), 0);
                    appViewHolder.excludeCheckBox.setChecked(false);
                    mData.get(position).setChecked(0);
                    notifyDataSetChanged();
                }
                else
                {
                    myDb.updateAppItem(appItem.getAppID(), 1);
                    appViewHolder.excludeCheckBox.setChecked(true);
                    mData.get(position).setChecked(1);
                    notifyDataSetChanged();
                }



            }
        });
    }



    @Override
    public int getItemCount()
    {
        return mData.size();
    }






    class AppViewHolder extends RecyclerView.ViewHolder
    {


        TextView nameText;

        ImageView appIcon;

        CheckBox excludeCheckBox;

        LinearLayout mainContent;

        public AppViewHolder(View itemView)
        {
            super(itemView);

            nameText = (TextView)itemView.findViewById(R.id.appName);

            appIcon = (ImageView)itemView.findViewById(R.id.appIcon);

            mainContent = (LinearLayout)itemView.findViewById(R.id.mainContent);

            excludeCheckBox = (CheckBox)itemView.findViewById(R.id.excludeCheckBox);


        }
    }




}
