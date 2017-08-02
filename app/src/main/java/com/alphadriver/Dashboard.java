package com.alphadriver;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

public class Dashboard extends BaseActivity implements LocationListener
{

    private static final String TAG_RESPONSE_F_NAME = "firstName";
    private static final String TAG_RESPONSE_L_NAME = "lastName";
    private static final String TAG_RESPONSE_UID = "uId";

    CheckInternetConnection check_net;
    UserSessionManager session;
    ConnectionDetector conn_detect;
    NetworkChange receiver;

    private static final String PREFER_NAME = "MyPref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location location;
    int PERMISSION_CODE = 1;


    String url,IPAddress,API_KEY;

    private TextView tv_vehicle_Name_No;
    private ImageView img_driver;
    private TextView tv_driver_name;
    private Button btn_onDuty;
    private Button btn_offDuty;
    private TextView tv_todays_booking;
    private TextView tv_Myearnings;

    private Toolbar toolbar_inner;
    private TextView tv_tool_header;

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    String driver_full_name,uId;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServiesAvailable()) {
            finish();
        }

       /* googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/

        setContentView(R.layout.dashboard);

        check_net = new CheckInternetConnection(Dashboard.this);
        receiver = new NetworkChange();

        //getting URL IPADDRESS and API key...
        conn_detect = new ConnectionDetector(Dashboard.this);
        url = conn_detect.geturl();
        IPAddress = conn_detect.getLocalIpAddress();
        API_KEY = conn_detect.getAPIKEY();
        Log.i("conn_detect","url="+conn_detect+" IPAddress="+IPAddress+" API_KEY="+API_KEY);

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setIcon(R.drawable.menu_icon);
        }

       /* tv_vehicle_Name_No = (TextView) findViewById(R.id.tv_vehicle_Name_No);
        img_driver = (ImageView) findViewById(R.id.img_driver);
        tv_driver_name = (TextView) findViewById(R.id.tv_driver_name);
        btn_onDuty = (Button) findViewById(R.id.btn_onDuty);
        btn_offDuty = (Button) findViewById(R.id.btn_offDuty);
        tv_todays_booking = (TextView) findViewById(R.id.tv_todays_booking);
        tv_Myearnings = (TextView) findViewById(R.id.tv_Myearnings);*/


       /* pref = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);

        String fName = pref.getString(TAG_RESPONSE_F_NAME,"");
        Log.i("fName","=="+fName);
        String lName = pref.getString(TAG_RESPONSE_L_NAME,"");
        Log.i("lName","=="+lName);
        driver_full_name = fName+ " " +lName;
        Log.i("driver_name","=="+driver_full_name);
        uId = pref.getString(TAG_RESPONSE_UID,"");
        Log.i("uId","=="+uId);*/

        //tv_driver_name.setText(driver_full_name);



       /* btn_onDuty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                btn_onDuty.setBackgroundResource(R.drawable.onduty_orange_btn);
                btn_onDuty.setTextColor(Color.WHITE);
                btn_offDuty.setBackgroundResource(R.drawable.offduty_grey_btn);
                btn_offDuty.setTextColor(Color.GRAY);

            }
        });

        btn_offDuty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                btn_onDuty.setBackgroundResource(R.drawable.onduty_grey_btn);
                btn_onDuty.setTextColor(Color.GRAY);
                btn_offDuty.setBackgroundResource(R.drawable.offduty_orange_btn);
                btn_offDuty.setTextColor(Color.WHITE);
            }
        });

*/
        // setUpDrawer();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }  // OnCreate() method end....

    //checking google play services is available or not...
    public boolean isGooglePlayServiesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
        {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
