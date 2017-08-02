package com.alphadriver;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BookingAlert extends AppCompatActivity
{

    SharedPreferences sharedpreferences;
    private static final String PREFER_NAME = "MyPref";

    ProgressDialog progressDialog;
    ConnectionDetector cd;
    Check_net_Connection check_net;
    String url_bookingAlert;

    private Toolbar toolbar_inner;
    private TextView tv_tool_header;
    private ImageView toolImg;
    private ProgressBar progressBar;

    MyCountDownTimer countDownTimer;
    long totalTimeCountInMilliseconds;  // total count down time in milliseconds
    private TextView tv_alert_timeM;
    private Button btn_alert_AcceptBooking,btn_alert_RejectBooking;

    String v_id;
    String response_bookDetails,response_bookCheck,response_Accept,response_Reject;

    private TextView tv_alert_pickTime,tv_alert_bookID,tv_alert_pickLOC,tv_alert_pickDist;

    public static boolean intent_status = false;

    String bookingId,refNo_Bookcheck,pick_address,cust_mobile,pick_up_distance;
    String cust_latitude,cust_longitude,time;
    int driver_status;

    private CoordinatorLayout coordinator_layout;

    //Timer timer;
   // MyLocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_alert);

        check_net = new Check_net_Connection();
        //timer = new Timer();
        //locationListener = new MyLocationListener(getApplicationContext());

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        toolImg = (ImageView) findViewById(R.id.toolImg);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("Booking Alert");
            toolImg.setVisibility(View.VISIBLE);
            toolImg.setImageResource(R.drawable.back_arrow);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookingAlert.this,DashBord.class);
                startActivity(intent);
                finish();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_alert_timeM = (TextView) findViewById(R.id.tv_alert_timeM);

        tv_alert_pickTime = (TextView) findViewById(R.id.tv_alert_pickTime);
        tv_alert_bookID = (TextView) findViewById(R.id.tv_alert_bookID);
        tv_alert_pickLOC = (TextView) findViewById(R.id.tv_alert_pickLOC);
        tv_alert_pickDist = (TextView) findViewById(R.id.tv_alert_pickDist);

        btn_alert_AcceptBooking = (Button) findViewById(R.id.btn_alert_AcceptBooking);
        btn_alert_RejectBooking = (Button) findViewById(R.id.btn_alert_RejectBooking);

        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        sharedpreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, getApplicationContext().MODE_PRIVATE);

        v_id = sharedpreferences.getString("vehicleId", "");
        Log.i("vehicleId", "vehicleId=" + v_id);

        cd = new ConnectionDetector(getApplicationContext());
        url_bookingAlert = cd.geturl();

        if (check_net.hasConnection(BookingAlert.this))
        {
            bookingCheck();
            //getBookingDetails();
        }
        else {
            check_net.showNetDisabledAlertToUser(BookingAlert.this);
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

        //totalTimeCountInMilliseconds = 45 * 1000;      // time count for 30 seconds
        totalTimeCountInMilliseconds = 120000;          //for two minutes
        countDownTimer = new MyCountDownTimer(totalTimeCountInMilliseconds, 1000);
        countDownTimer.start();

      /*  Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        String s = sdf.format(date);
        Log.i("date","date=="+s);
        tv_alert_pickTime.setText(s);*/


        btn_alert_AcceptBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (check_net.hasConnection(BookingAlert.this))
                {
                    acceptBooking();
                }
                else {
                    check_net.showNetDisabledAlertToUser(BookingAlert.this);
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

        btn_alert_RejectBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_net.hasConnection(BookingAlert.this))
                {
                    rejectBooking();
                }
                else {
                    check_net.showNetDisabledAlertToUser(BookingAlert.this);
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

    //for reject booking...
    public void rejectBooking()
    {
        class RejectBooking extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(BookingAlert.this,"Please wait","Accepting Booking...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                try
                {
  //http://alfaptop.safegird.com/owner/driverapi/driverRejectedReassign?
                    // vehicleId=1&bookingId=1&CustomerLat=100&CustomerLong=100

                    String url = url_bookingAlert+"/driverRejectedReassign?";
                    String query = String.format("vehicleId=%s&bookingId=%s&CustomerLat=%s&CustomerLong=%s",
                            URLEncoder.encode(v_id,"UTF-8"),
                            URLEncoder.encode(bookingId,"UTF-8"),
                            URLEncoder.encode(cust_latitude,"UTF-8"),
                            URLEncoder.encode(cust_longitude,"UTF-8"));

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
                        response_Reject="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_Reject += line;
                        }
                    }
                    else {
                        response_Reject = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
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

                Log.i("response","response_Reject=="+response_Reject);

                return response_Reject;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.i("response","response_Reject=s="+s);
                if (s == null || s.equals("[]"))
                {
                    Toast.makeText(BookingAlert.this,"No Response From Server",Toast.LENGTH_LONG).show();
                }
                else
                {

 //[{"responseCode":1,"responseMessage":["Booking Rejected"],"responseData":
                    // {"bookingId":79,"latitude":"19.906578555685","longitude":
                    // "75.346986053442","distance":"4.39","minute":"11",
                    // "refNo":"PP3WY791"}}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responseCode = jsonObject.getString("responseCode");
                        if (responseCode.equals("1"))
                        {
                            countDownTimer.cancel();
                            AlertDialog.Builder alert = new AlertDialog.Builder(BookingAlert.this);
                            alert.setMessage(responseMessage);
                            alert.setCancelable(false);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(BookingAlert.this,DashBord.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        RejectBooking rejectBooking = new RejectBooking();
        rejectBooking.execute();
    }

    //for accept booking...
    public void acceptBooking()
    {
        class AcceptBooking extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(BookingAlert.this,"Please wait","Accepting Booking...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {

                try
                {
                    String url = url_bookingAlert+"/acceptbooking?";
                    String query = String.format("vehicleId=%s&minute=%s",
                            URLEncoder.encode(v_id,"UTF-8"),
                            URLEncoder.encode(time,"UTF-8"));

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
                        response_Accept="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_Accept += line;
                        }
                    }
                    else {
                        response_Accept = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
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

                Log.i("response","bookingAccept=="+response_Accept);

                return response_Accept;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.i("response","bookAccept=s="+s);

                if (s == null || s.equals("[]"))
                {
                    Toast.makeText(BookingAlert.this,"Bad Internet Connection",Toast.LENGTH_LONG).show();
                }
                else {

  //[{"responseMessage":["Booking Accepted Successfully"],"responseCode":1}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responseCode = jsonObject.getString("responseCode");

                        if (responseCode.equals("1"))
                        {
                            countDownTimer.cancel();
                            intent_status = true;
                            Intent intent = new Intent(BookingAlert.this,MapActivity.class);
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        AcceptBooking acceptBooking = new AcceptBooking();
        acceptBooking.execute();
    }

    //for check booking and getting driver status...
    public void bookingCheck()
    {
        class BookingCheck extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(BookingAlert.this,"Please wait","Getting Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {

                try
                {
                    String url = url_bookingAlert+"/bookingCheck?";
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
                    Toast.makeText(BookingAlert.this,"Bad Internet Connection",Toast.LENGTH_LONG).show();
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
                                getBookingDetails();
                            }
                            else if (driver_status == 1)
                            {
                                pick_up_distance = object.getString("distance");
                                cust_latitude = object.getString("latitude");
                                cust_longitude = object.getString("longitude");
                                time = object.getString("minute");

                                countDownTimer.cancel();
                                intent_status = true;
                                Intent intent = new Intent(BookingAlert.this,MapActivity.class);
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
                                countDownTimer.cancel();
                                //intent_status = true;
                                Intent intent = new Intent(BookingAlert.this,MapActivity.class);
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

    //for get booking details...
    public void getBookingDetails()
    {
        class BookingDetails extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(BookingAlert.this,"Please Wait","Getting Booking Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {
                try
                {
                    String url = url_bookingAlert+"/notificationdata?";
                    String query = String.format("vehicleId=%s", URLEncoder.encode(v_id,"UTF-8"));

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
                        response_bookDetails="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response_bookDetails += line;
                        }
                    }
                    else {
                        response_bookDetails = "";
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
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

                Log.i("response","booking=="+response_bookDetails);

                return response_bookDetails;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }

                Log.i("response","booking=s="+s);

                if (s == null || s.equals("[]") )
                {
                    Toast.makeText(BookingAlert.this,"Bad Internet Connection",Toast.LENGTH_LONG).show();
                }
                else {

//[{"responseMessage":["Success"],"responseCode":1,"responseData":
// {"bookingId":24,"pickupLocation":"Unnamed Road, MIDC Industrial Area,
// Chilkalthana, Aurangabad, Maharashtra 431006, India","dropLocation":"42, J
// algaon Road, N 7, MIDC, Aurangabad, Maharashtra 431003","pickUpDistance":1.4,
// "pickupTime":"17:43:43","refNo":"PP2WY245"}}]
                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String responseMessage = jsonObject.getString("responseMessage");
                        Log.i("bookingAlert","responseMessage="+responseMessage);
                        String responseCode = jsonObject.getString("responseCode");
                        Log.i("bookingAlert","responseCode="+responseCode);

                        if (responseCode.equals("1"))
                        {
                            JSONObject object = jsonObject.getJSONObject("responseData");
                           // String bookingId = object.getString("bookingId");
                            //Log.i("bookingAlert","bookingId="+bookingId);
                            String pickupLocation = object.getString("pickupLocation");
                            Log.i("bookingAlert","pickupLocation="+pickupLocation);
                            String pickUpDistance = object.getString("pickUpDistance");
                            Log.i("bookingAlert","pickUpDistance="+pickUpDistance);
                            String pickupTime = object.getString("pickupTime");
                            String refNo = object.getString("refNo");

                            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
                            Date date = sdf1.parse(pickupTime);
                            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm");
                            String d = sdf2.format(date);
                            Log.i("date","date=="+d);
                            tv_alert_pickTime.setText(d);

                            tv_alert_bookID.setText(refNo);
                            tv_alert_pickLOC.setText(pickupLocation);
                            tv_alert_pickDist.setText(pickUpDistance);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.execute();
    }

    //setting countdown timer for 2 mitues.
    public class MyCountDownTimer extends CountDownTimer
    {

        public MyCountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            int progress = (int) (millisUntilFinished/1000);
            Log.i("progress","=="+progress+" millisUntilFinished="+millisUntilFinished);

            int s = progressBar.getMax() - progress;
            Log.i("progressbar","value="+s);
            progressBar.setProgress(s);
            tv_alert_timeM.setText(String.format("%02d", progress / 60) + ":" + String.format("%02d", progress % 60));

           /* if (progress<=80)
            {
                //progressBar.setBackgroundColor(Color.parseColor("#FF0000"));

               // progressBar.setIndeterminateDrawable(R.drawable.circel_shapered);
                //progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            }*/

        }

        @Override
        public void onFinish()
        {
           rejectBooking();
        }
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(BookingAlert.this,DashBord.class);
        startActivity(intent);
        finish();

    }
}
