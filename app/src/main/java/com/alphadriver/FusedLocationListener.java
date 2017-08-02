package com.alphadriver;

import android.*;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.SyncStateContract;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by infogird on 20/07/2017.
 */

public class FusedLocationListener extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    SharedPreferences pref;
    private static final String PREFER_NAME = "MyPref";
    public static final String Tag_uid = "uId";
    String json_response="";

    private static final long INTERVAL = 1000 * 30;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    ConnectionDetector cd;

    private GoogleApiClient mGoogleApiClient;
    private PowerManager.WakeLock mWakeLock;
    private LocationRequest mLocationRequest;

    double Latitude, Longitude;
    String CompleteAddress;
    String Url, IPAddress, APIKEY,uId;
    Context context;
    Activity activity;
    Timer timer;
    public static boolean timer_start1 = false;

    IBinder mBinder = new LocalBinder();

    private boolean mInProgress;

    private Boolean servicesAvailable = false;

    public class LocalBinder extends Binder {
        public FusedLocationListener getServerInstance() {
            return FusedLocationListener.this;
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mInProgress = false;
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the update interval to 5 seconds
        //mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setInterval(INTERVAL);
        // Set the fastest update interval to 1 second
        //mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        servicesAvailable = servicesConnected();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        setUpLocationClientIfNeeded();
    }

    private boolean servicesConnected()
    {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode)
        {

            return true;
        }
        else {

            return false;
        }
    }

    private void setUpLocationClientIfNeeded()
    {
        if(mGoogleApiClient == null)
            buildGoogleApiClient();
    }
    protected synchronized void buildGoogleApiClient()
    {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);


        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);

        /*
        WakeLock is reference counted so we don't want to create multiple WakeLocks. So do a check before initializing and acquiring.
        This will fix the "java.lang.Exception: WakeLock finalized while still held: MyWakeLock" error that you may find.
        */
        if (this.mWakeLock == null) { //**Added this
            this.mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        }

        if (!this.mWakeLock.isHeld()) { //**Added this
            this.mWakeLock.acquire();
        }

        if(!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;

        setUpLocationClientIfNeeded();
        if(!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress)
        {
            //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Started", Constants.LOG_FILE);
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    /*
    public FusedLocationListener(Context context)
    {
        this.context  = context;
        timer = new Timer();
        Log.i("onStartCommand","onStartCommand");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        cd = new ConnectionDetector(context);
        Url = cd.geturl();
        //Log.i("Url", "" + Url);
        APIKEY = cd.getAPIKEY();
        //Log.i("APIKEY", "" + APIKEY);
        IPAddress = cd.getLocalIpAddress();
        //Log.i("IPAddress", "" + IPAddress);
        pref = context.getSharedPreferences(PREFER_NAME, context.MODE_PRIVATE);
        uId = pref.getString("uId","");
        Log.i("uid","locationListener="+uId);


        if (!timer_start1) {
            Log.i("timer_start", "timer_start");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timer_start1 = true;
                    Log.i("c", "DashBoard");
                    //locationListener.SendLatLong(v_id);
                    SendLatLong("1");
                }
            }, 1000, 30000);
        }

    }

*/

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i("fused","onConnected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.i("fused","requestPermissions");
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1 );
        }
        else {
            Log.i("fused","Permission Granted");

            //LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this);
            Intent intent = new Intent(this, LocationReceiver.class);
            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(this, 54321, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient,
                    mLocationRequest, pendingIntent);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.i("fused","onLocationChanged");
        String msg = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

        Log.i("fused","locationListener=="+msg);

        Log.i("fused","Latitude=="+Latitude);
        Log.i("fused","Longitude=="+Longitude);
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();

        //SendLatLong("1");

    }

    //sending lat long contineousely....
    public void SendLatLong(String Vehicle_id)
    {
        Log.i("Vehicle_id",Vehicle_id);
        if (Latitude>0.0 && Longitude>0.0)
        {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String result = null;

            try
            {
                List<Address> addressList = geocoder.getFromLocation(Latitude, Longitude, 1);
                String address = addressList.get(0).getAddressLine(0);
                Log.i("address", address);
                String area = addressList.get(0).getAddressLine(1);
                // Log.i("area", area);
                String city = addressList.get(0).getAddressLine(2);
                Log.i("city", city);

                CompleteAddress = address + ", " + area + ", " + city;
                Log.i("CompleteAddress", CompleteAddress);
                //SendLatLong();

            } catch (IOException e) {
                //Log.e(TAG, "Unable connect to Geocoder", e);
            }


          /*  class LatLong extends AsyncTask<String,Void,String>
            {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(String... strings)
                {*/

            //http://alfaptop.safegird.com/owner/driverapi/addgpsaddress?lat=varad&long=mayee&userId=1&address=cidco

            String url=""+Url+"/addgpsaddress?";
            Log.i("url","locationlistener1111="+url);

            try
            {
                String query = String.format("lat=%s&long=%s&userId=%s&address=%s",
                        URLEncoder.encode(String.valueOf(Latitude),"UTF-8"),
                        URLEncoder.encode(String.valueOf(Longitude),"UTF-8"),
                        URLEncoder.encode(uId,"UTF-8"),
                        URLEncoder.encode(CompleteAddress,"UTF-8"));


                Log.i("query","query=="+query);
                URL url1 = new URL(url + query);
                Log.i("url","locationlistener222="+url1);

                HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoInput(true);
                int responseCode = connection.getResponseCode();
                Log.i("responseCode","responseCode");

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    json_response = "";
                    String line;
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while ((line = reader.readLine())!= null)
                    {
                        json_response += line;
                    }
                }else {
                    json_response = "";
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            Log.i("json_response","locationlistener="+json_response);
                  /*  Log.i("json_response","locationlistener="+json_response);
                    return json_response;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);


                }
            }
            LatLong latLong = new LatLong();
            latLong.execute();*/

        }else {
            Log.i("nolocation","locationlistener");
            //Toast.makeText(_context,"Make sure GPS is on",Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.mInProgress = false;

        if (this.servicesAvailable && this.mGoogleApiClient != null) {
            this.mGoogleApiClient.unregisterConnectionCallbacks(this);
            this.mGoogleApiClient.unregisterConnectionFailedListener(this);
            this.mGoogleApiClient.disconnect();
            // Destroy the current location client
            this.mGoogleApiClient = null;
        }
        // Display the connection status
        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ":
        // Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();

        if (this.mWakeLock != null) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }

        //super.onDestroy();
    }

    public class LocationReceiver extends BroadcastReceiver
    {
        //private String TAG = this.getClass().getSimpleName();

        private LocationResult mLocationResult;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Need to check and grab the Intent's extras like so
            if(LocationResult.hasResult(intent))
            {
                Log.i("fused","LocationReceiver=onReceive=");
                this.mLocationResult = LocationResult.extractResult(intent);
                Log.i("fused", "Location Received: " + this.mLocationResult.toString());
            }

        }
    }
}
