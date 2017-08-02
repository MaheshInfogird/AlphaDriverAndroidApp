package com.alphadriver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by infogird on 03/07/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        try
        {
            JSONObject json = new JSONObject(String.valueOf(remoteMessage.getData()));
            Log.i("normal_noti ", "notification==" + json);

            String title = json.getString("title");
            Log.i("json_title: ", "" + title);
            String body = json.getString("message");
            Log.i("json_body: ", "" + body);

            String title1 = title.replaceAll("&", " ");
            Log.i("title1: ", "" + title1);
            String body1 = body.replaceAll("&", " ");
            Log.i("body: ", "" + body1);

            sendNotification(title1,body1);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        //String title = remoteMessage.getNotification().getTitle().toString();
       /// String  body = remoteMessage.getNotification().getBody().toString();
       // Log.i("notification","title="+title+" boby="+body);

      /*  if (title.equals("vijay"))
        {
            *//*Intent intent = new Intent(this,BookingAlert.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*//*
            sendNotification(title);
        }*/
    }

    private void sendNotification(String title,String message)
    {
        Intent intent = new Intent(this, BookingAlert.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //temp code
        //Settings.System.putInt(getContentResolver(),android.provider.Settings.System.NOTIFICATION_SOUND, 1);

        //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound = Uri.parse(("android.resource://" + getPackageName() + "/" + R.raw.app_audio));

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setVibrate(new long[] {1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, noBuilder.build()); //0 = ID of notification
    }
}
