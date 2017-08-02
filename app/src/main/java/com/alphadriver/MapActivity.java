package com.alphadriver;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>, GoogleMap.OnMyLocationChangeListener

{
    SharedPreferences sharedpreferences;
    private static final String PREFER_NAME = "MyPref";

    ConnectionDetector cd;
    CheckInternetConnection check_net;
    NetworkChange receiver;
    ProgressDialog progressDialog;

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location mLastLocation;
    private GoogleMap mGoogleMap;
    Marker mCurrLocationMarker;
    LatLngBounds.Builder builder;
    LatLngBounds bounds;


    // Toolbar parameters.
    Toolbar toolbar_inner;
    TextView tv_tool_header;
    ImageView toolImg;

    boolean is_map_ready = false;
    double Latitude, Longitude;
    protected String mLastUpdateTime;
    double Latitude1_dd, Logtute_dd;
    LatLng latLng_locChange;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    Location location = null;
    LocationManager locationManager;
    ArrayList<LatLng> markerPoints;
    LatLng latLng_customer;
    double Latitude_customer = 0.0, Longitude_customer = 0.0;
    Polyline polyline;
    String  Booking_id, CRN_NO, time, pick_up_loc, cust_mob_no, cust_latitude = "",cust_longitude = "";
    int Dri_status;

    private TextView tv_map_bookID,tv_map_pickLOC,tv_map_speed,tv_map_speed2;
    private Button btn_client_locate,btn_start_trip,btn_stop_trip;

    String v_id,domain_url;
    String response_clientLocate,response_startTrip,response_StopTrip;

    LinearLayout layout_clientLocate,layout_startCancel,layout_StopJourney;

    MyLocationListener locationListener;
    private CoordinatorLayout coordinator_layout;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        toolImg = (ImageView) findViewById(R.id.toolImg);

        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("MAP");
            toolImg.setVisibility(View.VISIBLE);
            toolImg.setImageResource(R.drawable.back_arrow);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, DashBord.class);
                startActivity(intent);
                finish();
            }
        });

        locationListener = new MyLocationListener(getApplicationContext());
        markerPoints = new ArrayList<LatLng>();

        check_net = new CheckInternetConnection(MapActivity.this);
        receiver = new NetworkChange();
        cd = new ConnectionDetector(MapActivity.this);
        domain_url = cd.geturl();
        Log.i("domain_url","domain_url="+domain_url);

        sharedpreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, getApplicationContext().MODE_PRIVATE);

        v_id = sharedpreferences.getString("vehicleId", "");
        Log.i("vehicleId", "vehicleId=" + v_id);

        Intent mIntent = getIntent();
        cust_latitude = mIntent.getStringExtra("latitude_cust");
        Log.i("dataFromAlert", "cust_latitude" + cust_latitude);
        cust_longitude = mIntent.getStringExtra("longitude_cust");
        Log.i("dataFromAlert", "cust_latitude" + cust_longitude);
        Booking_id = mIntent.getStringExtra("Booking_id");
        Log.i("dataFromAlert", "Booking_id" + Booking_id);
        CRN_NO = mIntent.getStringExtra("CRN_NO");
        Log.i("dataFromAlert", "CRN_NO" + CRN_NO);
        pick_up_loc = mIntent.getStringExtra("pick_up_location");
        Log.i("dataFromAlert", "pick_up_loc" + pick_up_loc);
        cust_mob_no = mIntent.getStringExtra("customer_mob_no");
        Log.i("dataFromAlert", "cust_mob_no" + cust_mob_no);
        time = mIntent.getStringExtra("time");
        Log.i("dataFromAlert", "time" + time);
        Dri_status = mIntent.getIntExtra("Dri_status",0);
        Log.i("dataFromAlert", "Dri_status" + Dri_status);

        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        tv_map_bookID = (TextView) findViewById(R.id.tv_map_bookID);
        tv_map_pickLOC = (TextView) findViewById(R.id.tv_map_pickLOC);
        tv_map_speed = (TextView) findViewById(R.id.tv_map_speed);
        tv_map_speed2 = (TextView) findViewById(R.id.tv_map_speed2);

        tv_map_bookID.setText(CRN_NO);
        tv_map_pickLOC.setText(pick_up_loc);

        btn_client_locate = (Button) findViewById(R.id.btn_client_locate);
        layout_clientLocate = (LinearLayout) findViewById(R.id.layout_clientLocate);

        btn_start_trip = (Button) findViewById(R.id.btn_start_trip);
        layout_startCancel = (LinearLayout) findViewById(R.id.layout_startCancel);

        btn_stop_trip = (Button) findViewById(R.id.btn_stop_trip);
        layout_StopJourney = (LinearLayout) findViewById(R.id.layout_StopJourney);


        if (BookingAlert.intent_status)
        {
            Log.i("map","BookingAlert.intent_status=if="+BookingAlert.intent_status);
            //get from intent from alert
            Latitude_customer = Double.parseDouble(cust_latitude);
            Longitude_customer = Double.parseDouble(cust_longitude);

            //Latitude_customer = Double.parseDouble("19.907170");
            //Longitude_customer = Double.parseDouble("75.347092");

            latLng_customer = new LatLng(Latitude_customer, Longitude_customer);

        }
        else
        {
            Log.i("map","BookingAlert.intent_status=else="+BookingAlert.intent_status);

        }

        setUpMapIfNeeded();
        //buildGoogleApiClient();

        if (Dri_status == 1 || Dri_status == 0)
        {
            layout_startCancel.setVisibility(View.GONE);
            layout_StopJourney.setVisibility(View.GONE);
        }
        else if (Dri_status == 4)
        {
            layout_clientLocate.setVisibility(View.GONE);
            layout_StopJourney.setVisibility(View.GONE);
            layout_startCancel.setVisibility(View.VISIBLE);
        }
        else if (Dri_status == 5)
        {
            layout_clientLocate.setVisibility(View.GONE);
            layout_startCancel.setVisibility(View.GONE);
            layout_StopJourney.setVisibility(View.VISIBLE);
        }

        btn_client_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_net.hasConnection(MapActivity.this)) {
                    clientLocate();
                }
                else {
                    check_net.showDisableAlert(MapActivity.this);
                   /* Snackbar snackbar = Snackbar.make(coordinator_layout,"No Internet",Snackbar.LENGTH_INDEFINITE)
                            .setAction("TURNON", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);
                                }
                            });
                    snackbar.setActionTextColor(Color.BLUE);
                    snackbar.show();*/
                }
            }
        });

        btn_start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_net.hasConnection(MapActivity.this)) {
                    startJourney();
                }else {
                    check_net.showDisableAlert(MapActivity.this);
                   /* Snackbar snackbar = Snackbar.make(coordinator_layout,"No Internet",Snackbar.LENGTH_INDEFINITE)
                            .setAction("TURNON", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);
                                }
                            });
                    snackbar.setActionTextColor(Color.BLUE);
                    snackbar.show();*/
                }
            }
        });

        btn_stop_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_net.hasConnection(MapActivity.this)) {
                    stopJourney();
                }else {
                    check_net.showDisableAlert(MapActivity.this);
                    /*Snackbar snackbar = Snackbar.make(coordinator_layout,"No Internet",Snackbar.LENGTH_INDEFINITE)
                            .setAction("TURNON", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);
                                }
                            });
                    snackbar.setActionTextColor(Color.BLUE);
                    snackbar.show();*/
                }
            }
        });

    }

    private void setUpMapIfNeeded()
    {
        if (mGoogleMap == null)
        {
            SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment1.getMapAsync(this);
        }
    }

    public void stopJourney()
    {
        class StopTrip extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MapActivity.this,"Please Wait","Gettting Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {
    //http://alfaptop.safegird.com/owner/driverapi/stopjourney?vehicleId=1&bookingId=1
                try
                {
                    String url = domain_url+"/stopjourney?";
                    String query = String.format("vehicleId=%s&bookingId=%s",
                            URLEncoder.encode(v_id,"UTF-8"),
                            URLEncoder.encode(Booking_id,"UTF-8"));


                    URL url1 = new URL(url + query);
                    Log.i("url","stopjourney="+url1);

                    HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setAllowUserInteraction(false);
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {
                        response_StopTrip="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_StopTrip += line;
                        }
                    }
                    else {
                        response_StopTrip = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (SocketTimeoutException e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar.make(coordinator_layout,"Server Connection Timeout",Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                        }
                    });
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("response_StopTrip","=="+response_StopTrip);
                return response_StopTrip;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.i("response_StopTrip","s=="+s);
                if (s == null || s.equals("[]"))
                {
                    Toast.makeText(MapActivity.this,"No Response From Server",Toast.LENGTH_LONG).show();
                }
                else
                {
 //[{"responseMessage":["Already Trip has completed"],"responseCode":1,"responseData":
                    // {"distance":"213","tripTime":182,"basicFare":"2243.00",
                    // "totalBasicFare":2378,"serviceTax":134.58,"dropLocation":null,
                    // "pickUpLocation":"Aurangabad - Phulambri - Sillod Rd,
                    // Rauza Baug, N 2, Cidco, Aurangabad, Maharashtra 431003,
                    // India","dryRunAmount":0,"paidByWallet":1086,"collectCash":0,
                    // "nightCharges":0}}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responseCode = jsonObject.getString("responseCode");

                        if (responseCode.equals("1"))
                        {
                            //JSONObject object = jsonObject.getJSONObject("responseData");

                            Intent intent = new Intent(MapActivity.this,JourneyComplete.class);
                            intent.putExtra("Booking_id",Booking_id);
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        StopTrip stopTrip = new StopTrip();
        stopTrip.execute();
    }

    public void startJourney()
    {
        class StartTrip extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MapActivity.this,"Please Wait","Gettting Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {
    //http://alfaptop.safegird.com/owner/driverapi/startjourney?vehicleId=1&bookingId=1

                try
                {
                    String url = domain_url+"/startjourney?";
                    String query = String.format("vehicleId=%s&bookingId=%s",
                            URLEncoder.encode(v_id,"UTF-8"),
                            URLEncoder.encode(Booking_id,"UTF-8"));


                    URL url1 = new URL(url + query);
                    Log.i("url","clientlocate="+url1);

                    HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setAllowUserInteraction(false);
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {
                        response_startTrip="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_startTrip += line;
                        }
                    }
                    else {
                        response_startTrip = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (SocketTimeoutException e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar.make(coordinator_layout,"Server Connection Timeout",Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                        }
                    });
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("response_startTrip","=="+response_startTrip);
                return response_startTrip;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.i("response_startTrip","s=="+response_startTrip);
                if (s == null  || s.equals("[]"))
                {
                    Toast.makeText(MapActivity.this,"No Response From Server",Toast.LENGTH_LONG).show();
                }
                else
                {
   //[{"responseMessage":["Journey has been started "],"responseCode":1}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responseCode = jsonObject.getString("responseCode");
                        if (responseCode.equals("1"))
                        {
                            layout_startCancel.setVisibility(View.GONE);
                            layout_StopJourney.setVisibility(View.VISIBLE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        StartTrip startTrip = new StartTrip();
        startTrip.execute();
    }

    public void clientLocate()
    {
        class ClientLocate extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MapActivity.this,"Please Wait","Gettting Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {
    //http://alfaptop.safegird.com/owner/driverapi/clientlocate?vehicleId=1&bookingId=1&latitude=100&longitude=100&address=cidco

                try
                {
                    String url = domain_url+"/clientlocate?";
                    String query = String.format("vehicleId=%s&bookingId=%s&latitude=%s&longitude=%s&address=%s",
                            URLEncoder.encode(v_id,"UTF-8"),
                            URLEncoder.encode(Booking_id,"UTF-8"),
                            URLEncoder.encode(cust_latitude,"UTF-8"),
                            URLEncoder.encode(cust_longitude,"UTF-8"),
                            URLEncoder.encode(pick_up_loc,"UTF-8"));

                    URL url1 = new URL(url + query);
                    Log.i("url","clientlocate="+url1);

                    HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setAllowUserInteraction(false);
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {
                        response_clientLocate="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_clientLocate += line;
                        }
                    }
                    else {
                        response_clientLocate = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (SocketTimeoutException e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar.make(coordinator_layout,"Server Connection Timeout",Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                        }
                    });
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("response_clientLocate","=="+response_clientLocate);
                return response_clientLocate;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }

                Log.i("response_clientLocate","s=="+response_clientLocate);

                if (s == null  || s.equals("[]"))
                {
                    Toast.makeText(MapActivity.this,"No Response From Server",Toast.LENGTH_LONG).show();
                }
                else
                {
  //[{"responseMessage":["Client has been located"],"responceCode":1}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responceCode = jsonObject.getString("responceCode");
                        if (responceCode.equals("1"))
                        {
                            layout_clientLocate.setVisibility(View.GONE);
                            layout_startCancel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
     //[{"responseMessage":["Booking cancelled by customer"],"responceCode":0}]
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ClientLocate clientLocate = new ClientLocate();
        clientLocate.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        Log.i("MapActivity", "onMapReady");
        is_map_ready = true;
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMyLocationChangeListener(this);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.i("PERMISSION_GRANTED", ">=Marsh");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            }
            else
                {
                Log.i("PERMISSION_GRANTED", "PERMISSION_GRANTED");
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else
            {
            Log.i("PERMISSION_GRANTED", "PERMISSION_GRANTED=else");
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

           /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("PERMISSION_GRANTED", "PERMISSION_GRANTED");
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);

            }
        }
        else {
            Log.i("PERMISSION_GRANTED", "PERMISSION_GRANTED=else");
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }*/

        if (BookingAlert.intent_status)
        {
            Log.i("latLng_customer_marker", "" + latLng_customer);
            MarkerOptions marker = new MarkerOptions()
                    .position(latLng_customer)
                    .title("customer_location_yogi");
            mGoogleMap.addMarker(marker);

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //code for get current loc of diver and add polyline
    private void updateLocationUI()
    {

        SupportMapFragment mapFragment12 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment12.getMapAsync(this);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},2 );
        }else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location != null)
        {
            Log.i("updateLocationUI","startLocationUpdates");
            Latitude = location.getLatitude();
            Log.i("Latitude",""+location.getLatitude());
            Longitude = location.getLongitude();
            Log.i("Longitude",""+location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng",""+latLng);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position_Driver_yogi");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

            Log.i("latLng_d", "mapready page" );
            Log.i("latLng_d", "" + Latitude);
            Log.i("latLng_d", "" + Longitude);

            //geting driver lat long
            LatLng latLng_driver = new LatLng(Latitude,Longitude);
            Log.i("latLng_driver", "" + latLng_driver);//latLng

            if (BookingAlert.intent_status)
            {
                // draw polyline
                Log.i("l_customer_marker_ui", "" + latLng_customer);
                Log.i("l_driver_marker", "" + latLng_driver);

                markerPoints.add(latLng_customer);
                markerPoints.add(latLng_driver);
                Log.i("markerPoints", "" + markerPoints);

                if (markerPoints.size() >= 2)
                {
                    LatLng origin = markerPoints.get(0);

                    LatLng dest = markerPoints.get(1);

                    String url = getDirectionsUrl(origin, dest);
                    Log.i("url", "" + url);

                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest)
    {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>>
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }
            // Drawing polyline in the Google Map for the i-th route
            polyline = mGoogleMap.addPolyline(lineOptions);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i("MapActivity", "Connected to GoogleApiClient");

        if (mLastLocation == null)
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();

            }

        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.i("MapActivity","onLocationChanged");
        mLastLocation = location;
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }
        //  temp code location not null for marker
        if (location!=null)
        {
            Latitude = location.getLatitude();
            Log.i("Latitude11", "" + location.getLatitude());
            Longitude = location.getLongitude();
            Log.i("Longitude11", "" + location.getLongitude());

            //Toast.makeText(MapActivity.this,"onLocationChanged"+location.getSpeed(),Toast.LENGTH_LONG);
            ///Log.i("speed","onLocationChanged=="+location.getSpeed());
            //tv_map_speed.setText("onLoc="+String.valueOf(location.getSpeed()));

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng", "" + latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        //stop location updates
        if (mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //OnMyLocationChangeListener interface method
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
    {

    }

    //OnMyLocationChangeListener interface method
    @Override
    public void onMyLocationChange(Location location)
    {

        //LatLng latLng_customer2 = new LatLng(Double.parseDouble("19.907170"),Double.parseDouble("75.347092"));
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }
        //for location not null for markere
        if(location!=null)
        {
            Latitude1_dd = location.getLatitude();
            Logtute_dd = location.getLongitude();

           // Toast.makeText(MapActivity.this,"onMyLocationChange"+location.getSpeed(),Toast.LENGTH_LONG);
            Log.i("speed","onMyLocationChange=="+location.getSpeed());
            double speed = 3.6 * location.getSpeed();
            int speed2 = (int) speed;
            tv_map_speed.setText(String.valueOf(speed2));
            tv_map_speed2.setText(String.format("%.2f",speed));

            Location targetLocation = new Location("");//provider name is unnecessary
            targetLocation.setLatitude(location.getLatitude());//your coords of course
            targetLocation.setLongitude(location.getLongitude());

            Location targetLocation1 = new Location("");

           // latLng_customer2 = new LatLng(Double.parseDouble("19.907170"),Double.parseDouble("75.347092"));
           // targetLocation.setLongitude(Double.parseDouble("19.907170"));
           // targetLocation.setLongitude(Double.parseDouble("75.347092"));
            location.bearingTo(targetLocation);
            Log.i("degree","bearingTo=="+location.bearingTo(targetLocation));
            Log.i("degree","getBearing="+location.getBearing());


            latLng_locChange = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("latLng_locChange", "" + latLng_locChange);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng_locChange);
            markerOptions.title("Current Position_Driver");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_mark_icon));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        }
        //move map camera
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_locChange));
       // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

        builder = new LatLngBounds.Builder();
        builder.include(latLng_locChange);

        if (BookingAlert.intent_status)
        {
            builder.include(latLng_customer);
        }
       // builder.include(latLng_customer2);
        bounds = builder.build();
        int padding = 60;
        mGoogleMap.setPadding(0, 410, 0, 500);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.animateCamera(cu);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {

                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                }
                else {
                    //  Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                //return;
            break;

            case 1:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                }
                break;

            case 2:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        &&  grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                break;


        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(MapActivity.this,DashBord.class);
        startActivity(intent);
        finish();
    }

}
