package com.alphadriver;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Picasso;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;*/
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class DashBord extends BaseActivity implements LocationListener
       // GoogleApiClient.ConnectionCallbacks,
        //GoogleApiClient.OnConnectionFailedListener

{

    String yogesh_abchiu88888;


    SharedPreferences sharedpreferences;
    private static final String PREFER_NAME = "MyPref";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final String Name = "name";
    public static final String LName = "lastName";

    private static final String TAG_d_response_massage = "responseMessage";
    private static final String TAG_d_change_status = "responseCode";
    //private static  final String TAG_d_change_status="responseCode";

    private static final String TAG_totalbusiness = "totalBusiness";
    private static final String TAG_totalbusiness_basic = "totalBusinessBasic";
    private static final String TAG_service_tax = "totalServiceTax";
    private static final String TAG_oro_commition = "totalOroCommAmt";
    private static final String TAG_tds_amt = "totalTds";
    private static final String TAG_paid_to_oro = "totalOroPaid";
    private static final String TAG_my_earning = "totalMyEarnings";
    private static final String TAG_d_r_status = "status";
    private static final String TAG_todays_booking = "totalBookings";

    ///
    private static final String TAG_booking_id = "bookingId";
    private static final String TAG_crn_no = "refNo";
    private static final String TAG_pick_location = "address";
    private static final String TAG_cust_mob_no = "mobile";
    private static final String TAG_pick_distance = "distance";
    private static final String TAG_customer_lat = "latitude";
    private static final String TAG_customer_long = "longitude";
    private static final String TAG_reject_time = "minute";


    ///
    String yogi12345;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    ProgressDialog progressDialog_earning;
    Check_net_Connection checkNet;

    private static final long INTERVAL = 1000 * 30;
    private static final long FASTEST_INTERVAL = 1000 * 5;
   // protected static final int REQUEST_CHECK_SETTINGS = 0x1;
   LocationRequest locationRequest1;


    ProgressDialog progressDialog_bo;
    MyLocationListener locationListener;
    ConnectionDetector cd;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog progressDialog_dash;

    NetworkChange networkChange;
    ProgressDialog dialog_d_status;
    String Url, IPAddress, APIKEY;
    String myJSON;
    String todays_booking, todays_earning_on_booking;
    String VehicleId;
    Double Latitude_1;
    Double Longitude_1;
    String v_id, Vehicle_Name, Vehicle_No;
    String driver_image, d_name, d_last_name, d_full_name, vehicle_name, vehicle_no, uId;


    int dri_current_status;
    TextView tv_total_my_earning, tv_my_total_business, uname, tv_vehicle_name, tv_vehicle_no, mt_earning_header, tv_todays_no_bookind, tv_todays_earning;
    TextView tv_tds, tv_penalty_deduct, tv_other, tv_total_earning, tv_service_tax, tv_you_earned;
    TextView btn_OnDuty, btn_offDuty, tv_total_business, tv_oro_commition, tv_my_earning;
    Switch swich_driver_status;
    ImageView tv_driver_pic;
    Button ON, OFF;
    EditText select_date;
    int d_Status;
    String res_status_booking, Booking_id, CRN_NO, pick_up_loc, cust_mob_no;
    //String pick_up_distance, customer_lat, customer_long, time;
    int Dri_status;

    LocationListener locationListener1;

    double Latitude, Longitude;

    public int mYear, mMonth, mDay, mHour, mMinute;
    String CurrentDate;
    String UserstartDate;
    //GPSTracker gpsTracker;
    ToggleButton toggleButton;
    TextView text;
    //Switch status_switch;

    private NetworkChange receiver;
    int version_code;
    String myJson_version, Packagename;

    String bestProvider;
    LocationManager locManager;
    public static boolean cancelled = false;

     Timer timer;
    String total_business, total_business_basic, service_tax, oro_commition;
    String tds_amt, paid_to_oro, my_earning, todays_bookings, Status_res_massge, Status_res_massge1;

    public static boolean timer_start = false;
    private RatingBar driver_rating;

    ProgressDialog progressDialog;

    String response_bookCheck;
    String bookingId,refNo_Bookcheck,pick_address,cust_mobile,pick_up_distance;
    String cust_latitude,cust_longitude,time;
    int driver_status;

    private CoordinatorLayout coordinator_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

       /* createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();*/

        Toolbar toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        TextView tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);

        setSupportActionBar(toolbar_inner);

        if (toolbar_inner != null)
        {
            tv_tool_header.setText("Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setIcon(R.drawable.menu_icon);
        }

        //Log.i("onCreate", "onCreate");

        //select_date=(EditText) findViewById(R.id.ed_select_date);

        uname = (TextView) findViewById(R.id.dash_uname);
        tv_vehicle_name = (TextView) findViewById(R.id.tv_vehicle_name);
        //tv_vehicle_no = (TextView) findViewById(R.id.tv_vehicle_no);
        tv_todays_no_bookind = (TextView) findViewById(R.id.tv_todays_no_bookind);
        mt_earning_header = (TextView) findViewById(R.id.mt_earning_header);

        driver_rating = (RatingBar) findViewById(R.id.driver_rating);

        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);


        driver_rating.setRating(1);
        driver_rating.setClickable(false);
        // driver_rating.setActivated(false);
        driver_rating.setEnabled(false);


        //tv_penalty_deduct=(TextView)findViewById(R.id.tv_penalty_deduct);
        //tv_other=(TextView)findViewById(R.id.tv_other);
        //tv_total_earning=(TextView)findViewById(R.id.tv_total_earning);

        //tv_you_earned=(TextView)findViewById(R.id.tv_you_earned);
//        ON = (Button)findViewById(R.id.ON);
//        OFF = (Button)findViewById(R.id.OFF);

        btn_OnDuty = (TextView) findViewById(R.id.btn_onDuty);
        btn_offDuty = (TextView) findViewById(R.id.btn_offDuty);

        //tv_total_business=(TextView)findViewById(R.id.tv_total_business);
        //tv_service_tax = (TextView) findViewById(R.id.tv_service_tax);
        //tv_tds = (TextView) findViewById(R.id.tv_tds);
        //tv_oro_commition = (TextView) findViewById(R.id.tv_oro_commition);
        //tv_my_total_business = (TextView) findViewById(R.id.tv_my_total_business);
        tv_driver_pic = (ImageView) findViewById(R.id.driver_pic);
        //tv_total_my_earning = (TextView) findViewById(R.id.tv_total_my_earning);
        //obj for net connection
        checkNet = new Check_net_Connection();
        //net coonect check broad cast
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChange();
        registerReceiver(receiver, filter);
        // brad cast


        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        APIKEY = cd.getAPIKEY();
        IPAddress = cd.getLocalIpAddress();

        if (cancelled)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBord.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialog.setTitle("");
            alertDialog.setMessage("Booking cancelled by customer");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }


        sharedpreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, getApplicationContext().MODE_PRIVATE);

        v_id = sharedpreferences.getString("vehicleId", "");
        Log.i("vehicleId", "vehicleId=" + v_id);

        /*Vehicle_Name=sharedpreferences.getString("Vehicle_Name","");
        Log.i("Vehicle_Name",""+Vehicle_Name);

        Vehicle_No=sharedpreferences.getString("Vehicle_No","");
        Log.i("Vehicle_No",""+Vehicle_No);*/

        d_name = sharedpreferences.getString(Name, "");
        Log.i("d_name", "" + d_name);

       // d_last_name = sharedpreferences.getString(LName, "");
       // Log.i("d_last_name", "" + d_last_name);

        d_full_name = d_name;
        Log.i("d_full_name", "" + d_full_name);

        //vehicle_name = sharedpreferences.getString("vehicle_name", "");
        //Log.i("vehicle_name", "" + vehicle_name);

        //vehicle_no = sharedpreferences.getString("vehicle_no", "");
        //Log.i("vehicle_no", "" + vehicle_no);
        uId = sharedpreferences.getString("uId", "");
        Log.i("uId", "" + uId);

        //driver_image = sharedpreferences.getString("driver_image", "");
        //Log.i("driver_image",""+driver_image);

      /*  if (!driver_image.equals("")) {

            Picasso.with(getApplicationContext()).load(driver_image).into(tv_driver_pic);
        }*/



        //code for check version


        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            //string version_code = info.versionName;
            version_code = info.versionCode;
            Packagename = info.packageName;
            //Log.i("version_code", "" + version_code);
        } catch (PackageManager.NameNotFoundException e) {

        }

        turnGPSOn();
       /* if (checkNet.hasConnection(DashBord.this))
        {
            checkversion();
        }
        else
        {
            checkNet.showNetDisabledAlertToUser(DashBord.this);
        }*/

        //gpsTracker = new GPSTracker(this);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        bestProvider = locManager.getBestProvider(criteria, true);
        //Log.i("bestProvider", bestProvider);

        //get location update from MyLocationListener so pass locationListener in para else pass this
        //
        locationListener = new MyLocationListener(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DashBord.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
        }
        else
        {
            locManager.requestLocationUpdates(bestProvider, 1000, 0, locationListener);
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest1, this);

        }

        //startService(new Intent(this,FusedLocationListener.class));

        timer = new Timer();

        if (!timer_start) {
            Log.i("timer_start", "timer_start-DashBoard");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timer_start = true;
                    Log.i("c", "DashBoard");
                    locationListener.SendLatLong();
                }
            }, 1000, 15000);
        }

        //mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).build();
        //mGoogleApiClient.connect();

        //check for booking available or not...
        if (checkNet.hasConnection(DashBord.this))
        {
            bookingCheck();
        }
        else
        {
            checkNet.showNetDisabledAlertToUser(DashBord.this);
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


        btn_OnDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet.hasConnection(DashBord.this)) {
                    //dialog_d_status = ProgressDialog.show(DashBord.this, "", "Online... Please Wait", true);
                   /* new Thread(new Runnable() {
                        public void run() {
                            d_Status = 2;
                            // status_change();

                        }
                    }).start();*/
                } else {
                    checkNet.showNetDisabledAlertToUser(DashBord.this);
                }

            }
        });

        btn_offDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet.hasConnection(DashBord.this)) {

                    //dialog_d_status = ProgressDialog.show(DashBord.this, "", "Offline... Please Wait", true);
                   /* new Thread(new Runnable() {
                        public void run() {
                            d_Status = 3;
                            //status_change();

                        }
                    }).start();*/
                } else {
                    checkNet.showNetDisabledAlertToUser(DashBord.this);
                }
            }
        });

        //code for calendar listnar

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        Log.i("mMonth", "" + mMonth);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        if (mMonth + 1 < 10 && mDay < 10) {
            CurrentDate = mYear + "-" + "0" + (mMonth + 1) + "-" + "0" + mDay;
            //Log.i("CurrentDate_MntDt", "" + CurrentDate);
        } else if (mMonth + 1 == 10 && mDay < 10) {
            CurrentDate = mYear + "-" + "" + (mMonth + 1) + "-" + "0" + mDay;
            //Log.i("CurrentDate_MntDt", ""+CurrentDate);
        } else if (mMonth + 1 < 10) {
            CurrentDate = mYear + "-" + "0" + (mMonth + 1) + "-" + mDay;
            //Log.i("CurrentDate_Mnth", "" + CurrentDate)
        } else if (mDay < 10) {
            CurrentDate = mYear + "-" + (mMonth + 1) + "-" + "0" + mDay;
            //Log.i("CurrentDate_Dt", "" + CurrentDate);
        } else {
            CurrentDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
            //Log.i("CurrentDate", "" + CurrentDate);
        }
        ///code for booking check

        //get_bokking();


        uname.setText(d_full_name);
        // tv_vehicle_name.setText(vehicle_name);
        //tv_vehicle_no.setText(vehicle_no);


        // getSupportActionBar().setDisplayShowTitleEnabled(false);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void createLocationRequest() {
        locationRequest1 = new LocationRequest();
        locationRequest1.setInterval(INTERVAL);
        locationRequest1.setFastestInterval(FASTEST_INTERVAL);
        locationRequest1.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //for driver rating....
    public void getDriverRatings() {
        class DriverRating extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
        DriverRating driverRating = new DriverRating();
        driverRating.execute();
    }

    //code for net  temp code
    @Override
    protected void onDestroy() {
        Log.v("", "onDestory");
        super.onDestroy();

        unregisterReceiver(receiver);

    }


    //for check booking and getting driver status...
    public void bookingCheck()
    {
        class BookingCheck extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(DashBord.this,"Please wait","Getting Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {

                try
                {
                    String url = Url+"/bookingCheck?";
                    String query = String.format("vehicleId=%s", URLEncoder.encode(v_id,"UTF-8"));
                    //String query = String.format("vehicleId=%s", URLEncoder.encode("3","UTF-8"));

                    URL url1 = new URL(url + query);
                    Log.i("url","bookingAlert="+url1);

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
                        response_bookCheck="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_bookCheck += line;
                        }
                    }
                    else {
                        response_bookCheck = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }catch (SocketTimeoutException e)
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

                Log.i("response","bookCheck=="+response_bookCheck);

                return response_bookCheck;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.i("response","bookCheck=s="+s);

                if (s == null || s.equals("[]"))
                {
                    Toast.makeText(DashBord.this,"Bad Internet Connection",Toast.LENGTH_LONG).show();
                }
                else
                {

                    //if booking available || status = 1
                    //[{"responseMessage":["Success"],"responseCode":1,"responseData":
                    // {"bookingId":24,"refNo":"PP2WY245","latitude":19.8917675,
                    // "longitude":75.3698766,"address":"Unnamed Road, MIDC Industrial
                    // Area, Chilkalthana, Aurangabad, Maharashtra 431006, India",
                    // "mobile":"9890025507","distance":"0.75","minute":"2","status":1}}]

                    //if status = 4
                    //[{"responseMessage":["Success"],"responseCode":1,"responseData":
                    // {"bookingId":44,"refNo":"PP2WY448","address":null,
                    // "mobile":"9890025507","status":4}}]

                    //if no booking
                    //[{"responseMessage":["Do not have any current booking"],"responseCode":0}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responseCode = jsonObject.getString("responseCode");

                        if (responseCode.equals("1"))
                        {
                            JSONObject object = jsonObject.getJSONObject("responseData");

                            bookingId = object.getString("bookingId");
                            refNo_Bookcheck = object.getString("refNo");
                            //cust_latitude = object.getString("latitude");
                            //cust_longitude = object.getString("longitude");
                            pick_address = object.getString("address");
                            cust_mobile = object.getString("mobile");
                            // pick_up_distance = object.getString("distance");
                            //time = object.getString("minute");
                            driver_status = object.getInt("status");

                            if (driver_status == 0)
                            {
                                pick_up_distance = object.getString("distance");
                                cust_latitude = object.getString("latitude");
                                cust_longitude = object.getString("longitude");
                                time = object.getString("minute");
                                //getBookingDetails();
                            }
                            else if (driver_status == 1)
                            {
                                pick_up_distance = object.getString("distance");
                                cust_latitude = object.getString("latitude");
                                cust_longitude = object.getString("longitude");
                                time = object.getString("minute");

                                //countDownTimer.cancel();
                                BookingAlert.intent_status = true;
                                Intent intent = new Intent(DashBord.this,MapActivity.class);
                                intent.putExtra("Dri_status",driver_status);
                                intent.putExtra("latitude_cust", cust_latitude);
                                intent.putExtra("customer_mob_no",cust_mobile);
                                intent.putExtra("longitude_cust", cust_longitude);
                                intent.putExtra("Booking_id", bookingId);
                                intent.putExtra("CRN_NO",refNo_Bookcheck);
                                intent.putExtra("pick_up_location",pick_address);
                                intent.putExtra("time", time);
                                startActivity(intent);
                                finish();
                            }
                            else if (driver_status == 4 || driver_status == 5)
                            {
                                //countDownTimer.cancel();
                                BookingAlert.intent_status = false;
                                Intent intent = new Intent(DashBord.this,MapActivity.class);
                                intent.putExtra("Dri_status",driver_status);
                                // intent.putExtra("latitude_cust", cust_latitude);
                                intent.putExtra("customer_mob_no",cust_mobile);
                                //intent.putExtra("longitude_cust", cust_longitude);
                                intent.putExtra("Booking_id", bookingId);
                                intent.putExtra("CRN_NO",refNo_Bookcheck);
                                intent.putExtra("pick_up_location",pick_address);
                                //intent.putExtra("time", time);
                                startActivity(intent);
                                finish();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        BookingCheck bookingCheck = new BookingCheck();
        bookingCheck.execute();
    }


    public void get_my_earning() {
        /*//final String stat_date=my_earning_calender_start.getText().toString();
        Log.i("stat_date",""+stat_date);

        final  String end_date=my_earning_calender_enddate.getText().toString();
        Log.i("end_date",""+end_date);
*/
        class GetData extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                // progressDialog_earning = ProgressDialog.show(DashBord.this, "", "Loading Data... Please Wait", true);
                //progressDialog_earning.show();
            }

            @Override
            protected String doInBackground(String... params) {
                //http://jaidevcoolcab.cabsaas.in/sandbox/ptop/myearnings/?uId=911&startDate=2016-08-24&endDate=2016-08-27&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserID=1212&UserIPAddress=203.48.165.229&UserAgent=androidApp&responsetype=2
                String responsetype = "2";
                //String KEY ="S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
                String url = "" + Url + "/myearnings/";

                String uri = Uri.parse(url)
                        .buildUpon()
                        .appendQueryParameter("vehicleId", v_id)
                        .appendQueryParameter("uId", uId)
                        .appendQueryParameter("startDate", CurrentDate)
                        .appendQueryParameter("endDate", CurrentDate)
                        .appendQueryParameter("ApiKey", APIKEY)
                        .appendQueryParameter("UserIPAddress", IPAddress)
                        .appendQueryParameter("UserID", "1212")
                        .appendQueryParameter("UserAgent", "androidApp")
                        .appendQueryParameter("responsetype", responsetype)
                        .build().toString();

                Log.i("uri_get_my_earning", uri);

                //HttpClient httpclient = new DefaultHttpClient();
                // HttpPost httppost = new HttpPost(uri);

                InputStream inputStream = null;
                String result = null;
                try {
                    // HttpResponse response = httpclient.execute(httppost);
                    // HttpEntity entity = response.getEntity();
                    // inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                return result;

            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                //show_my_earning();
                //progressDialog_earning.dismiss();
            }
        }
        GetData getData = new GetData();
        getData.execute();
    }



/*
    public void show_my_earning()
    {
        try
        {
            Log.i("json_dash", "json_dash");
            Log.i("json_earning", myJSON);

            JSONArray json = new JSONArray(myJSON);
            Log.i("json_earning", "" + json);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);

                dri_current_status = jsonObject.getInt("status");
                Log.i("dri_current_status","" +dri_current_status);

                Status_res_massge = jsonObject.getString("responseMessage");
                Log.i("d_change_Status", "" + Status_res_massge);

                Status_res_massge1 = Status_res_massge.substring(2, Status_res_massge.length() - 2);


                Log.i("d_Status_oncreate",""+dri_current_status);
                if (dri_current_status!=9)
                {
                    total_business = jsonObject.getString(TAG_totalbusiness);
                    Log.i("total_business", total_business);

                   total_business_basic = jsonObject.getString(TAG_totalbusiness_basic);
                    Log.i("total_business_basic", total_business_basic);

                    service_tax = jsonObject.getString(TAG_service_tax);
                    Log.i("service_tax", service_tax);

                     oro_commition = jsonObject.getString(TAG_oro_commition);
                    Log.i("oro_commition", oro_commition);

                     tds_amt = jsonObject.getString(TAG_tds_amt);
                    Log.i("tds_amt", tds_amt);

                     paid_to_oro = jsonObject.getString(TAG_paid_to_oro);
                    Log.i("paid_to_oro", paid_to_oro);

                     my_earning = jsonObject.getString(TAG_my_earning);
                    Log.i("my_earning", my_earning);

                     todays_bookings = jsonObject.getString(TAG_todays_booking);
                    Log.i("todays_bookings123", todays_bookings);


                }
                if (dri_current_status == 2)
                {
                    btn_OnDuty.setBackground(getResources().getDrawable(R.drawable.on_selector));
                    btn_offDuty.setBackgroundColor(Color.TRANSPARENT);
                    btn_offDuty.setTextColor(getResources().getColor(R.color.GreyTextColor));
                }
                else if (dri_current_status == 3)
                {
                    btn_offDuty.setBackground(getResources().getDrawable(R.drawable.off_selector));
                    btn_offDuty.setTextColor(Color.WHITE);
                    btn_OnDuty.setBackgroundColor(Color.TRANSPARENT);
                }
                if(dri_current_status==9)
                {
                    Log.i("Status0", "Status0");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBord.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    alertDialog.setTitle("DashBord");
                    alertDialog.setMessage(Status_res_massge1);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent =new Intent(DashBord.this,Logout.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                //tv_total_business.setText(total_business_basic);


                    tv_service_tax.setText(service_tax);
                    tv_oro_commition.setText(oro_commition);
                    tv_tds.setText(tds_amt);
                    //tv_paid_to_oro.setText(paid_to_oro);
                    tv_my_total_business.setText(total_business);//my earnibg==my total business
                    mt_earning_header.setText(my_earning);
                    tv_total_my_earning.setText(my_earning);
                    tv_todays_no_bookind.setText(todays_bookings);

            }
            Log.e("pass 1", "connection success ");
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
        }
    }

    */


    /*

    public void status_change()
    {
        //http://jaidevcoolcab.cabsaas.in/sandbox/ptop/driverStatus/?vehicleId=115&status=3&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserID=1212&UserIPAddress=203.48.165.229&UserAgent=androidApp&responsetype=2
        String responsetype = "2";

        String url = "" + Url + "/driverStatus/?";
        //String KEY ="S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        try
        {
            String uri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("vehicleId", v_id)
                    .appendQueryParameter("status", String.valueOf(d_Status))
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", responsetype)
                    .build().toString();


            Log.i("uri_driver_status", uri);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            //  httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);
            Log.i("response_reject_all", "" + response);

            ////


                ///



            JSONArray json = new JSONArray(response);
            //Log.i("json", "" + json);

            JSONObject jsonObject = json.getJSONObject(0);

            final int d_change_Status = jsonObject.getInt(TAG_d_change_status);
            Log.i("d_change_Status", "" + d_change_Status);

            String Status_res_massge = jsonObject.getString(TAG_d_response_massage);
            Log.i("d_change_Status", "" + d_change_Status);

          *//*  final String r_status = jsonObject.getString(TAG_d_r_status);
            Log.i("r_status", "" + r_status);*//*

            final String Status_res_massge1 = Status_res_massge.substring(2, Status_res_massge.length() - 2);

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    dialog_d_status.dismiss();

                    if (d_change_Status == 9)
                    {
                        Log.i("Status0", "Status0");
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBord.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        alertDialog.setTitle("DashBoard");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                Intent intent =new Intent(DashBord.this,Logout.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                        alertDialog.show();
                    }
                    if (d_change_Status == 1)
                    {
                        Log.i("Status1", "Status1");
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBord.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        alertDialog.setTitle("DashBoard");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (d_Status == 2)
                                {
                                    btn_OnDuty.setBackground(getResources().getDrawable(R.drawable.on_selector));
                                    btn_offDuty.setBackgroundColor(Color.TRANSPARENT);
                                    btn_offDuty.setTextColor(getResources().getColor(R.color.GreyTextColor));
                                }
                                else if (d_Status == 3)
                                {
                                    btn_offDuty.setBackground(getResources().getDrawable(R.drawable.off_selector));
                                    btn_offDuty.setTextColor(Color.WHITE);
                                    btn_OnDuty.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }
                        });
                        alertDialog.show();
                    }
                    if (d_change_Status == 0) {
                        Log.i("Status0", "Status0");
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBord.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        alertDialog.setTitle("DashBoard");
                        alertDialog.setMessage(Status_res_massge1);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
        }
    }


    */
/*

    ///version code method
    public void checkversion() {
        try {
            String urlnew = "" + Url + "/versionCheck/?";
            //  String APIKEY ="PVU1ZE-ZE4TPC-5IXWAJ-P2E6ZE-QONPEC-4IUGWD";
            String uri = Uri.parse(urlnew)
                    .buildUpon()
                    .appendQueryParameter("uId", uId)
                    .appendQueryParameter("ptopVersion", String.valueOf(version_code))
                    .appendQueryParameter("type", "1")
                    .appendQueryParameter("ApiKey", APIKEY)
                    .appendQueryParameter("UserID", "1212")
                    .appendQueryParameter("UserIPAddress", IPAddress)
                    .appendQueryParameter("UserAgent", "androidApp")
                    .appendQueryParameter("responsetype", "2")
                    .build().toString();

            Log.i("versionuri", uri);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            myJson_version = response;

            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONArray json = new JSONArray(myJson_version);
                        Log.i("json", "" + json);
                        JSONObject jsonObject = json.getJSONObject(0);
                        String resp = jsonObject.getString("responseCode");
                        String respmsg = jsonObject.getString("responseMessage");
                        final String link_update_res = jsonObject.getString("updateLink");

                        final String respmsg1 = respmsg.substring(2, respmsg.length() - 2);

                        if (resp.equals("0"))
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashBord.this);
                            alertDialogBuilder.setMessage(respmsg1)
                                    .setTitle("Update App")
                                    .setCancelable(false)
                                    .setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(link_update_res));
                                            startActivity(intent);

                                        }
                                    });

                            alertDialogBuilder.setNegativeButton(" No ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(startMain);
                                    finish();


                                }
                            });

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }

                        if (resp.equals("1"))
                        {

                        }

                    }
                    catch (JSONException j) {
                        j.printStackTrace();
                    }
                }
            });
        }
        catch (IOException e) {
            Log.i("Error", "" + e.toString());
        }
    }
*/

    private void turnGPSOn()
    {
        mGoogleApiClient = null;
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    //.addConnectionCallbacks(this)
                    //.addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>()
            {
                @Override
                public void onResult(LocationSettingsResult result)
                {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (status.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.SUCCESS:

                             locationListener = new MyLocationListener(getApplicationContext());
                            if (ActivityCompat.checkSelfPermission(DashBord.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(DashBord.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(DashBord.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION},1 );
                            }
                            else
                            {
                                locManager.requestLocationUpdates(bestProvider, 2000, 0, locationListener);
                            }
                            //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                           /* if (!timer_start)
                            {
                                Log.i("timer_start", "timer_start-GPS-ALEARDY-ON");
                                timer.scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        timer_start = true;
                                        Log.i("c", "DashBoard");
                                        locationListener.SendLatLong(v_id);
                                    }
                                }, 1000, 30000);
                            }*/

                            break;

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try
                            {
//                                status.startResolutionForResult(MapActivity.this, 1000);
                                status.startResolutionForResult(DashBord.this, REQUEST_CHECK_SETTINGS);
                            }
                            catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
        else {
            Log.i("mGoogleApiClient", "not-null");
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Latitude = location.getLatitude();
        Log.e("Latitude11", "" + Latitude);
        Longitude = location.getLongitude();
        Log.e("Longitude11", "" + Longitude);
    }

/*

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DashBord.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
        }
        else
        {
            locManager.requestLocationUpdates(bestProvider, 1000, 0, locationListener);
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest1, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i("onConnectionSuspended","onConnectionSuspended=="+i);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i("onConnectionFailed","onConnectionFailed=="+connectionResult);
    }

*/


    @Override
    protected void onResume()
    {
        Log.i("OnResume", "OnResume");
        super.onResume();
        locationListener = new MyLocationListener(getApplicationContext());

        turnGPSOn();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DashBord.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1 );
        }
        else
        {
            locManager.requestLocationUpdates(bestProvider, 2000, 0, locationListener);
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest1,this);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("requestCode","requestCode=="+requestCode+" grantResults=="+grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("requestCode","locManager=requestCode=="+requestCode+" grantResults=="+grantResults);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locManager.requestLocationUpdates(bestProvider, 2000, 0, locationListener);
                    //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest1,this);

                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS :

                Log.i("Result", Integer.toString(REQUEST_CHECK_SETTINGS));
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        
                       /* if (!timer_start)
                        {
                            Log.i("timer_start", "timer_start-gps-on-by-user");
                            timer.scheduleAtFixedRate(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    timer_start = true;
                                    Log.i("c", "DashBoard");
                                    locationListener.SendLatLong(v_id);
                                }
                            }, 1000, 30000);
                        }*/

                        break;

                    case Activity.RESULT_CANCELED:
                        //Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        finish();
                        break;
                }
                break;
        }
    }



    @Override
    protected void onPause()
    {
        super.onPause();
        cancelled = false;
        /*ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelled = false;
    }

    @Override
    public void onBackPressed() {
        return;
    }


}
