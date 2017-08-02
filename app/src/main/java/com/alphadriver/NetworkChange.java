package com.alphadriver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.INotificationSideChannel;

/**
 * Created by infogird on 12/06/2017.
 */

public class NetworkChange extends BroadcastReceiver
{

    private boolean isConnected = false;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        isNetworkAvailable(context);
    }
    private boolean isNetworkAvailable(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo[] allNetWorkinfo = cm.getAllNetworkInfo();
            if (allNetWorkinfo != null)
            {
                for (int i = 0;i<allNetWorkinfo.length;i++)
                {
                    if (allNetWorkinfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        if (!isConnected)
                        {
                            isConnected = true;

                        }

                        return true;
                    }
                }
            }
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alert.setTitle("");
        alert.setMessage("No internet connection on your device..");
        alert.setCancelable(false);
        alert.setPositiveButton(" Enable Internet ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        alert.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert1 = alert.create();
        alert1.show();

        isConnected = false;
        return false;
    }
}
