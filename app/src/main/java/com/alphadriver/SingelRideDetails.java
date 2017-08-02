package com.alphadriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SingelRideDetails extends AppCompatActivity implements OnMapReadyCallback
{

    //[{"responseMessage":["Success"],"responseCode":1,"responseData":
// {"driverName":"Pavan Patil","refNo":"PP2MG12","bookedon":"01-Jun-2017",
// "pickuplocationlat":19.8827633,"picklocationlong":75.3913966,
// "droplocationlat":19.8827633,"droplocationlong":75.3913966,
// "pickuplocation":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India",
// "droplocation":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India",
// "vehicleType":null,"vehicleName":"BMW 5 Series","vehicleNumber":"AN-01 CA 4356",
// "totalkm":"3.2","ridetime":4,"basicfare":"49","serviceTax":3.48,"totalbill":58,"status":3}}]

    public static final String Tag_driverName = "driverName";
    public static final String Tag_refNo = "refNo";
    public static final String Tag_bookedon = "bookedon";
    public static final String Tag_pickuplocationlat = "pickuplocationlat";
    public static final String Tag_picklocationlong = "picklocationlong";
    public static final String Tag_droplocationlat = "droplocationlat";
    public static final String Tag_droplocationlong = "droplocationlong";
    public static final String Tag_pickuplocation = "pickuplocation";
    public static final String Tag_droplocation = "droplocation";
    public static final String Tag_vehicleType= "vehicleType";
    public static final String Tag_vehicleName = "vehicleName";
    public static final String Tag_vehicleNumber = "vehicleNumber";
    public static final String Tag_totalkm = "totalkm";
    public static final String Tag_ridetime = "ridetime";
    public static final String Tag_basicfare = "basicfare";
    public static final String Tag_serviceTax= "serviceTax";
    public static final String Tag_totalbill = "totalbill";
    public static final String Tag_status = "status";




    Check_net_Connection check_net;
    UserSessionManager session;
    ConnectionDetector cd;
    NetworkChange receiver;

    Toolbar toolbar_inner;
    ProgressDialog progressDialog;

    GoogleMap googleMap;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> arraylist_latlngs = new ArrayList<>();
   // static final LatLng pickUp_latlong = new LatLng(19.891460,75.337002);
    //static final LatLng drop_latlong = new LatLng(19.891460,75.337002);


    String domain_url, bookingId,status;
    String json_response;
    private TextView tv_rideD_date,tv_rideD_CRN,tv_rideD_totalAMT,tv_rideD_pickLoc,tv_rideD_dropLoc;
    private TextView tv_rideD_rideKM,tv_rideD_rideTime,tv_rideD_basicFare,tv_rideD_serviceTax,tv_rideD_totalBill;

    String pickuplocationlat,picklocationlong,droplocationlat,droplocationlong;
    double pickup_lat,pickup_long,drop_lat,drop_long;
    private Button btn_rideD_status;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_details);

        receiver = new NetworkChange();

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        TextView tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        ImageView toolImg = (ImageView) findViewById(R.id.toolImg);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("My Rides");
            toolImg.setVisibility(View.VISIBLE);
            toolImg.setImageResource(R.drawable.back_arrow);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingelRideDetails.this,MyRides.class);
                startActivity(intent);
                finish();
            }
        });

        tv_rideD_date = (TextView) findViewById(R.id.tv_rideD_date);
        tv_rideD_CRN = (TextView)findViewById(R.id.tv_rideD_CRN);
        tv_rideD_totalAMT = (TextView) findViewById(R.id.tv_rideD_totalAMT);
        tv_rideD_pickLoc = (TextView) findViewById(R.id.tv_rideD_pickLoc);
        tv_rideD_dropLoc = (TextView) findViewById(R.id.tv_rideD_dropLoc);
        tv_rideD_rideKM = (TextView) findViewById(R.id.tv_rideD_rideKM);
        tv_rideD_rideTime = (TextView) findViewById(R.id.tv_rideD_rideTime);
        tv_rideD_basicFare = (TextView) findViewById(R.id.tv_rideD_basicFare);
        tv_rideD_serviceTax = (TextView) findViewById(R.id.tv_rideD_serviceTax);
        tv_rideD_totalBill = (TextView) findViewById(R.id.tv_rideD_totalBill);

        btn_rideD_status = (Button) findViewById(R.id.btn_rideD_status);

        setUpMapIfNeeded();

        cd = new ConnectionDetector(SingelRideDetails.this);
        domain_url = cd.geturl();
        Log.i("domain_url","ridedetails="+domain_url);

        bookingId = getIntent().getStringExtra("bookingId");
       // status = getIntent().getStringExtra("status");
        Log.i("bookingId","ridedetails="+bookingId);

        check_net = new Check_net_Connection();
        if (check_net.hasConnection(SingelRideDetails.this))
        {
            getSingelRideDetails();
        }
        else {
            check_net.showNetDisabledAlertToUser(SingelRideDetails.this);
        }

    }

    public void getSingelRideDetails()
    {
        class RideDetails extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(SingelRideDetails.this,ProgressDialog.THEME_HOLO_LIGHT);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Please wait");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {

                try
                {
                    String url = domain_url + "/bookingdetailsdriver?";
                    Log.i("url","rideD="+url);
                    String query = String.format("bookingId=%s", URLEncoder.encode(bookingId,"UTF-8"));
                    URL url1 = new URL(url + query);
                    Log.i("url","rideD="+url1);

                    HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    int responsecode = connection.getResponseCode();
                    if (responsecode == HttpURLConnection.HTTP_OK)
                    {
                        json_response = "";
                        String line = "";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line = reader.readLine()) != null)
                        {
                            json_response += line;
                        }
                    }else {
                        json_response = "";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("response","ridedetails="+json_response);
                return json_response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.i("response","rideD=S="+s);

                if (s.equals("") || s.equals("[]"))
                {
                    Toast.makeText(SingelRideDetails.this,"Bad Internet Connection",Toast.LENGTH_SHORT).show();
                }else{

//[{"responseMessage":["Success"],"responseCode":1,"responseData":
// {"driverName":"Pavan Patil","refNo":"PP2MG12","bookedon":"01-Jun-2017",
// "pickuplocationlat":19.8827633,"picklocationlong":75.3913966,
// "droplocationlat":19.8827633,"droplocationlong":75.3913966,
// "pickuplocation":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India",
// "droplocation":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India",
// "vehicleType":null,"vehicleName":"BMW 5 Series","vehicleNumber":"AN-01 CA 4356",
// "totalkm":"3.2","ridetime":4,"basicfare":"49","serviceTax":3.48,"totalbill":58,"status":3}}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        Log.i("rideD","responseMessage"+responseMessage);
                        String responseCode = jsonObject.getString("responseCode");
                        Log.i("rideD","responseCode"+responseCode);

                        if (responseCode.equals("1"))
                        {
                            JSONObject object = jsonObject.getJSONObject("responseData");
                            String driverName = object.getString(Tag_driverName);
                            Log.i("rideDetails","driverName="+driverName);
                            String refNo = object.getString(Tag_refNo);
                            Log.i("rideDetails","refNo="+refNo);
                            String bookedon = object.getString(Tag_bookedon);
                            Log.i("rideDetails","bookedon="+bookedon);
                            pickuplocationlat = object.getString(Tag_pickuplocationlat);
                            Log.i("rideDetails","pickuplocationlat="+pickuplocationlat);
                            picklocationlong = object.getString(Tag_picklocationlong);
                            Log.i("rideDetails","picklocationlong="+picklocationlong);
                            droplocationlat = object.getString(Tag_droplocationlat);
                            Log.i("rideDetails","droplocationlat="+droplocationlat);
                            droplocationlong = object.getString(Tag_droplocationlong);
                            Log.i("rideDetails","droplocationlong="+droplocationlong);
                            String pickuplocation = object.getString(Tag_pickuplocation);
                            String droplocation = object.getString(Tag_droplocation);
                            String vehicleType = object.getString(Tag_vehicleType);
                            String vehicleName = object.getString(Tag_vehicleName);
                            String vehicleNumber = object.getString(Tag_vehicleNumber);
                            String totalkm = object.getString(Tag_totalkm);
                            String ridetime = object.getString(Tag_ridetime);
                            String basicfare = object.getString(Tag_basicfare);
                            String serviceTax = object.getString(Tag_serviceTax);
                            String totalbill = object.getString(Tag_totalbill);
                            String status = object.getString(Tag_status);
                            Log.i("rideDetails","status="+status);

                            tv_rideD_date.setText(bookedon);
                            tv_rideD_CRN.setText(refNo);
                            tv_rideD_totalAMT.setText(totalbill);
                            tv_rideD_pickLoc.setText(pickuplocation);
                            tv_rideD_dropLoc.setText(droplocation);
                            tv_rideD_rideKM.setText(totalkm);
                            tv_rideD_rideTime.setText(ridetime);
                            tv_rideD_basicFare.setText(basicfare);
                            tv_rideD_serviceTax.setText(serviceTax);
                            tv_rideD_totalBill.setText(totalbill);

                            if (status.equals("1"))
                            {
                                btn_rideD_status.setText("Confirmed");
                            }
                            if (status.equals("2"))
                            {
                                btn_rideD_status.setText("Cancelled");
                            }
                            if (status.equals("3") || status.equals("4"))
                            {
                                btn_rideD_status.setText("Completed");
                            }

                            pickup_lat = Double.parseDouble(pickuplocationlat);
                            pickup_long = Double.parseDouble(picklocationlong);
                            drop_lat = Double.parseDouble(droplocationlat);
                            drop_long = Double.parseDouble(droplocationlong);

                            arraylist_latlngs.add(new LatLng(pickup_lat,pickup_long));
                            arraylist_latlngs.add(new LatLng(drop_lat,drop_long));

                            updateUI();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
        RideDetails rideDetails = new RideDetails();
        rideDetails.execute();
    }

    private void setUpMapIfNeeded()
    {
        if (googleMap == null)
        {
            SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment1.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap1)
    {
        googleMap = googleMap1;

    }
    public void updateUI()
    {
        Polyline polyline;
        
        //LatLng pickUp_latlong = new LatLng(pickup_lat,pickup_long);
        //LatLng drop_latlong = new LatLng(drop_lat,drop_long);

        LatLng pickUp_latlong = new LatLng(19.3504 ,75.2194 );
        LatLng drop_latlong = new LatLng(19.8762 ,75.3433 );

        MarkerOptions markerOptions1 = new MarkerOptions();
        MarkerOptions markerOptions2 = new MarkerOptions();

       /* for (int i=0;i<=arraylist_latlngs.size();i++)
        {
            LatLng pick = arraylist_latlngs.get(i);
            markerOptions.position(pick);
            googleMap.addMarker(markerOptions);

        }*/

        markerOptions1.position(pickUp_latlong);
        //markerOptions1.position(drop_latlong);
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        googleMap.addMarker(markerOptions1);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickUp_latlong));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(drop_latlong));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(7));

       // markerOptions2.position(pickUp_latlong);
        markerOptions2.position(drop_latlong);
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(markerOptions2);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickUp_latlong));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(drop_latlong));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(7));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(5);
        polylineOptions.color(Color.BLUE);
        polylineOptions.geodesic(true);
        polylineOptions.add(pickUp_latlong);
        polylineOptions.add(drop_latlong);
        polyline = googleMap.addPolyline(polylineOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SingelRideDetails.this,MyRides.class);
        startActivity(intent);
        finish();
    }

}
