package com.alphadriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class JourneyComplete extends AppCompatActivity
{

    //[{"responseMessage":["Success"],"responseCode":1,"responseData":{"totalbill":58,
    // "totalkm":"3.2","ridetime":4,"pickuplocation":"164,
    // Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011,
    // India","droplocation":"MIDC Industrial Area, Chilkalthana,
    // Aurangabad, Maharashtra, India","basicfare":"49","othercharges":9,
    // "pickuplocationlat":19.8827633,"picklocationlong":75.3913966,"droplocationlat":19.8827633,"droplocationlong":75.3913966}}]

    public static final String Tag_Total_Bill = "totalbill";
    public static final String Tag_Total_km = "totalkm";
    public static final String Tag_Ride_Time = "ridetime";
    public static final String Tag_Pickuplocation = "pickuplocation";
    public static final String Tag_Droplocation = "droplocation";
    public static final String Tag_Total_Basicfare = "basicfare";
    public static final String Tag_Othercharges = "othercharges";

    SharedPreferences sharedpreferences;
    private static final String PREFER_NAME = "MyPref";

    ConnectionDetector cd;
    Check_net_Connection checkNet;
    private NetworkChange receiver;


    private Toolbar toolbar_inner;

    String Url;
    String Booking_id,v_id;
    String json_response,response_CashCollection;

    ProgressDialog progressDialog;
    private TextView tv_bill_amount;
    private TextView tv_ride_km;
    private TextView tv_ride_time;
    private TextView tv_pickup_loc;
    private TextView tv_drop_loc;
    private TextView tv_total_fare;
    private TextView tv_other_chrges;
    private TextView tv_total_bill;
    private Button btn_CompleteJ_Paid;

    private CoordinatorLayout coordinator_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_complete);

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        TextView tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        ImageView toolImg = (ImageView) findViewById(R.id.toolImg);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("Journey Complete");
            toolImg.setVisibility(View.VISIBLE);
            toolImg.setImageResource(R.drawable.back_arrow);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JourneyComplete.this,DashBord.class);
                startActivity(intent);
                finish();
            }
        });

        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        tv_bill_amount = (TextView) findViewById(R.id.tv_bill_amount);
        tv_ride_km = (TextView) findViewById(R.id.tv_ride_km);
        tv_ride_time = (TextView) findViewById(R.id.tv_ride_time);
        tv_pickup_loc = (TextView) findViewById(R.id.tv_pickup_loc);
        tv_drop_loc = (TextView) findViewById(R.id.tv_drop_loc);
        tv_total_fare = (TextView) findViewById(R.id.tv_total_fare);
        tv_other_chrges = (TextView) findViewById(R.id.tv_other_chrges);
        tv_total_bill = (TextView) findViewById(R.id.tv_total_bill);

        btn_CompleteJ_Paid = (Button) findViewById(R.id.btn_CompleteJ_Paid);

        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        Log.i("url","journey="+Url);

        sharedpreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, getApplicationContext().MODE_PRIVATE);

        v_id = sharedpreferences.getString("vehicleId", "");
        Log.i("vehicleId", "vehicleId=" + v_id);

        Intent intent = getIntent();
        Booking_id = intent.getStringExtra("Booking_id");
        Log.i("journeyComplete","Booking_id="+Booking_id);

        receiver = new NetworkChange();

        checkNet = new Check_net_Connection();

        if (checkNet.hasConnection(JourneyComplete.this))
        {
            getJourneyCompleteData();
        }else {
            checkNet.showNetDisabledAlertToUser(JourneyComplete.this);
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

        btn_CompleteJ_Paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNet.hasConnection(JourneyComplete.this))
                {
                    cashCollected();
                }else {
                    checkNet.showNetDisabledAlertToUser(JourneyComplete.this);
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

    //for cash collection....
    public void cashCollected()
    {
        class BillPaid extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(JourneyComplete.this, ProgressDialog.THEME_HOLO_LIGHT);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Please wait");
                //progressDialog.setMessage("Signing In...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {

   //http://alfaptop.safegird.com/owner/driverapi/cashCollected?vehicleId=1&bookingId=1

                try
                {
                    String url = Url +"/cashCollected?";
                    Log.i("url","journey="+url);
                    String query = String.format("vehicleId=%s&bookingId=%s",
                            URLEncoder.encode(v_id,"UTF-8"),
                            URLEncoder.encode(Booking_id,"UTF-8"));

                    URL url1 = new URL(url + query);
                    Log.i("url","journeyFinal="+url1);

                    HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    int responsecode = connection.getResponseCode();
                    if (responsecode == HttpURLConnection.HTTP_OK)
                    {
                        String line;
                        response_CashCollection = "";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line = reader.readLine()) != null)
                        {
                            response_CashCollection += line;
                        }
                    }
                    else {
                        response_CashCollection = "";
                    }

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
                catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("response","response_CashCollection="+response_CashCollection);

                return response_CashCollection;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.i("reponse","cashCollected=s"+s);

                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }

                if (s == null || s.equals("[]"))
                {
                    Toast.makeText(JourneyComplete.this,"No Response From Server",Toast.LENGTH_LONG).show();
                }
                else
                {
  //[{"responseMessage":["Customer cash has been collected."],"responseCode":1}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        String responseCode = jsonObject.getString("responseCode");
                        if (responseCode.equals("1"))
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(JourneyComplete.this);
                            alert.setMessage(responseMessage);
                            alert.setCancelable(false);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(JourneyComplete.this,DashBord.class);
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
        BillPaid billPaid = new BillPaid();
        billPaid.execute();
    }

    // for journey complete data and generate bill....
    public void getJourneyCompleteData()
    {

        class GetData extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                progressDialog = new ProgressDialog(JourneyComplete.this, ProgressDialog.THEME_HOLO_LIGHT);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Please wait");
                //progressDialog.setMessage("Signing In...");
                progressDialog.show();

            }

            @Override
            protected String doInBackground(String... strings)
            {

                try
                {
                    String url = Url +"/journeycompletedriver?";
                    Log.i("url","journey="+url);
                    String query = String.format("bookingId=%s",
                            URLEncoder.encode(Booking_id,"UTF-8"));

                    URL url1 = new URL(url + query);
                    Log.i("url","journeyFinal="+url1);

                    HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    int responsecode = connection.getResponseCode();
                    if (responsecode == HttpURLConnection.HTTP_OK)
                    {
                        String line;
                        json_response = "";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line = reader.readLine()) != null)
                        {
                            json_response += line;
                        }
                    }
                    else
                    {
                        json_response = "";
                    }

                } catch (SocketTimeoutException e)
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
                catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("response","jouenryComp="+json_response);
                return json_response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.i("reponse","JC=s"+s);

                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }

                if (s == null || s.equals("[]"))
                {
                    Toast.makeText(JourneyComplete.this,"No Response From Server",Toast.LENGTH_SHORT).show();
                }
                else
                {

 //[{"responseMessage":["Success"],"responseCode":1,"responseData":{"totalbill":58,
                    // "totalkm":"3.2","ridetime":4,"pickuplocation":"164,
                    // Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011,
                    // India","droplocation":"MIDC Industrial Area, Chilkalthana,
                    // Aurangabad, Maharashtra, India","basicfare":"49","othercharges":9,
                    // "pickuplocationlat":19.8827633,"picklocationlong":75.3913966,"droplocationlat":19.8827633,"droplocationlong":75.3913966}}]

                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString("responseMessage");
                        Log.i("JC","responseMessage="+responseMessage);
                        String responseCode = jsonObject.getString("responseCode");
                        Log.i("JC","responseCode="+responseCode);

                        if (responseCode.equals("1"))
                        {
                            JSONObject object = jsonObject.getJSONObject("responseData");
                            String totalbill = object.getString(Tag_Total_Bill);
                            Log.i("JC","totalbill="+totalbill);
                            String totalkm = object.getString(Tag_Total_km);
                            Log.i("JC","totalkm="+totalkm);
                            String ridetime = object.getString(Tag_Ride_Time);
                            Log.i("JC","ridetime="+ridetime);
                            String pickuplocation = object.getString(Tag_Pickuplocation);
                            Log.i("JC","pickuplocation="+pickuplocation);
                            String droplocation = object.getString(Tag_Droplocation);
                            Log.i("JC","droplocation="+droplocation);
                            String basicfare = object.getString(Tag_Total_Basicfare);
                            Log.i("JC","basicfare="+basicfare);
                            String othercharges = object.getString(Tag_Othercharges);
                            Log.i("JC","othercharges="+othercharges);

                            tv_bill_amount.setText(totalbill);
                            tv_ride_km.setText(totalkm);
                            tv_ride_time.setText(ridetime);
                            tv_pickup_loc.setText(pickuplocation);
                            tv_drop_loc.setText(droplocation);
                            tv_total_fare.setText(basicfare);
                            tv_other_chrges.setText(othercharges);
                            tv_total_bill.setText(totalbill);


                        }
                        else
                        {
                            Log.i("JC","responseCode=0000");

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        GetData data = new GetData();
        data.execute();
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

        Intent intent = new Intent(JourneyComplete.this,DashBord.class);
        startActivity(intent);
        finish();
    }
}
