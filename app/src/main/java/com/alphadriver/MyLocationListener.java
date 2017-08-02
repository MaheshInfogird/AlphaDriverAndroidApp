package com.alphadriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/*
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
*/

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 04-08-2016.
 */
class MyLocationListener implements LocationListener
{

    SharedPreferences pref;
    private static final String PREFER_NAME = "MyPref";
    public static final String Tag_uid = "uId";
    String json_response="";

    double Latitude, Longitude;
    private GoogleMap mGoogleMap;
    ConnectionDetector cd;
    String Url, IPAddress, APIKEY;

    String VehicleId,uId;
    String CompleteAddress;

    Marker mCurrLocationMarker;

    private Context _context;

    public MyLocationListener(Context context)
    {
        this._context = context;
        cd = new ConnectionDetector((context));
        Url = cd.geturl();
        //Log.i("Url", "" + Url);
        APIKEY = cd.getAPIKEY();
        //Log.i("APIKEY", "" + APIKEY);
        IPAddress = cd.getLocalIpAddress();
        //Log.i("IPAddress", "" + IPAddress);
        pref = context.getSharedPreferences(PREFER_NAME, context.MODE_PRIVATE);
        uId = pref.getString("uId","");
        Log.i("uid","locationListener="+uId);
    }


    @Override
    public void onLocationChanged(Location locFromGps)
    {
        // called when the listener is notified with a location update from the GPS
        // Log.i("LOCATIONUPDATE", "LOCATIONUPDATE");
        Latitude = locFromGps.getLatitude();
        //Log.i("Latitude_MyLOCation",""+Latitude);
        Log.i("Latitude123789", "" +Latitude);
        Longitude = locFromGps.getLongitude();
        Log.i("Longitude123789", "" +Longitude);

        DecimalFormat format = new DecimalFormat("#.######");

        LatLng latLng = new LatLng(locFromGps.getLatitude(), locFromGps.getLongitude());
        //Log.i("latLng",""+latLng);

        //SendLatLong();
       // Toast.makeText(_context,"Latitude="+Latitude+",Longitude="+Longitude,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }

    public void SendLatLong()
    {
        //Log.i("Vehicle_id",Vehicle_id);
        if (Latitude>0.0 && Longitude>0.0)
        {

            Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
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
                //Toast.makeText(_context,CompleteAddress,Toast.LENGTH_LONG).show();
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
                       /* String query = String.format("lat=%s&long=%s&userId=%s&address=%s",
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
                        }*/

                        String uri = Uri.parse(url)
                                .buildUpon()
                                //.appendQueryParameter("vehicleId", Vehicle_id)
                                .appendQueryParameter("lat", String.valueOf(Latitude))
                                .appendQueryParameter("long", String.valueOf(Longitude))
                                .appendQueryParameter("userId", uId)
                                .appendQueryParameter("address", CompleteAddress)
                               // .appendQueryParameter("status", "")
                                //.appendQueryParameter("ApiKey", APIKEY)
                               // .appendQueryParameter("UserIPAddress", IPAddress)
                                //.appendQueryParameter("UserID", "1212")
                                //.appendQueryParameter("UserAgent", "androidApp")
                                //.appendQueryParameter("responsetype", responsetype)
                                .build().toString();

                        Log.i("uri_main_listner", uri);
                        long startTime = System.currentTimeMillis();
                        Log.i("StartTime",""+startTime);

                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(uri);

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        final String response = httpclient.execute(httppost, responseHandler);
                        Log.i("response", "MyLocationListener==" + response);
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        System.out.println("Took : " + ((elapsedTime - startTime) / 1000));
                        Log.i("elapsedTime",""+elapsedTime);
                        Log.i("response", "" + response);

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
//
//  http://jaidevcoolcab.cabsaas.in/sandbox/ptop/driverVehicleLocation/?
// vehicleId=32&latitude=18.876165&longitude=76.343314&bookingId=&address=&status=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserIPAddress=203.48.165.229&UserID=1212&UserAgent=androidApp&responsetype=2

// null/driverVehicleLocation/?vehicleId=null&latitude=19.8886016&longitude=75.3710061&bookingId=&status=&ApiKey=null&UserIPAddress=null&UserID=1212&UserAgent=androidApp&responsetype=2

        ///adduseraddress?lat=varad&long=mayee&userId=1&address=cidco&addressType=other

        String responsetype = "2";

        //String url=""+Url+"/driverVehicleLocation/?";


       /*if (Latitude>0.0 && Longitude>0.0)
        {

            Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
            String result = null;
            try
            {
                List<Address> addressList = geocoder.getFromLocation(Latitude, Longitude, 1);
                String address = addressList.get(0).getAddressLine(0);
                //Log.i("address", address);
                String area = addressList.get(0).getAddressLine(1);
                //Log.i("area", area);
                String city = addressList.get(0).getAddressLine(2);
//            Log.i("city", city);

                CompleteAddress = address + ", " +area+ ", "+ city;
                //Log.i("CompleteAddress", CompleteAddress);
                //SendLatLong();

            }
            catch (IOException e) {
                //Log.e(TAG, "Unable connect to Geocoder", e);
            }

            try {
                String uri = Uri.parse(url)
                        .buildUpon()
                        .appendQueryParameter("vehicleId", Vehicle_id)
                        .appendQueryParameter("latitude", String.valueOf(Latitude))
                        .appendQueryParameter("longitude", String.valueOf(Longitude))
                        .appendQueryParameter("bookingId", "")
                        .appendQueryParameter("address", CompleteAddress)
                        .appendQueryParameter("status", "")
                        .appendQueryParameter("ApiKey", APIKEY)
                        .appendQueryParameter("UserIPAddress", IPAddress)
                        .appendQueryParameter("UserID", "1212")
                        .appendQueryParameter("UserAgent", "androidApp")
                        .appendQueryParameter("responsetype", responsetype)
                        .build().toString();

                Log.i("uri_main_listner", uri);
                long startTime = System.currentTimeMillis();
                Log.i("StartTime",""+startTime);


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(uri);

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                final String response = httpclient.execute(httppost, responseHandler);
                Log.i("response", "" + response);
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Took : " + ((elapsedTime - startTime) / 1000));
                Log.i("elapsedTime",""+elapsedTime);
                Log.i("response", "" + response);

            } catch (Exception e) {
                Log.e("Fail 1", e.toString());

            }
        }*/
    }
}
