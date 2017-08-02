package com.alphadriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRides extends BaseActivity
{

    //[{"responseMessage":["Success"],"responseCode":1,"responseData":[{"refNo":"PP2MG12",
    // "bookingId":1,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune,
    // Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilka
    // lthana, Aurangabad, Maharashtra, India","status":3,
    // "tdate":"22-Oct-2016","bookedon":"01-Jun-2017",
    // "pickupTime":"12:32:53","totalAmt":58},
    // {"refNo":"PP2MG13","bookingId":2,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"03-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG14","bookingId":3,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"06-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG15","bookingId":4,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"10-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG16","bookingId":5,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"12-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG17","bookingId":6,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"19-Jun-2017","pickupTime":"12:32:53","totalAmt":58}]}]

    public static final String Tag_refNo = "refNo";
    public static final String Tag_bookingId = "bookingId";
    public static final String Tag_pickUpLo = "pickUpLo";
    public static final String Tag_dropLoc = "dropLoc";
    public static final String Tag_status = "status";
    public static final String Tag_tdate = "tdate";
    public static final String Tag_bookedon = "bookedon";
    public static final String Tag_pickupTime = "pickupTime";
    public static final String Tag_totalAmt = "totalAmt";


    SharedPreferences pref;
    private static final String PREFER_NAME = "MyPref";

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    Check_net_Connection check_net;
    UserSessionManager session;
    ConnectionDetector cd;
    NetworkChange receiver;

    ProgressDialog progressDialog;

    String domain_url,uID;
    String json_response;

    HashMap<String,String> map_myrides;
    ArrayList<HashMap<String,String>> arrayList_myrides;

    ListView listview_myRides;
    private Toolbar toolbar_inner;
    TextView tv_tool_header;
    private EditText ed_myrides_calendar;
    private TextView tv_noData;

    int count = 0;
    private CoordinatorLayout coordinator_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_rides);

        arrayList_myrides = new ArrayList<>();

        check_net = new Check_net_Connection();
        receiver = new NetworkChange();

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("My Rides");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listview_myRides = (ListView)findViewById(R.id.listview_myRides);
        ed_myrides_calendar = (EditText) findViewById(R.id.ed_myrides_calendar);
        tv_noData = (TextView) findViewById(R.id.tv_noData);
        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        pref = getApplicationContext().getSharedPreferences(PREFER_NAME, this.MODE_PRIVATE);
        uID = pref.getString("uId","");
        Log.i("uId","myrides=="+uID);

        cd = new ConnectionDetector(MyRides.this);
        domain_url = cd.geturl();
        Log.i("domain_url","myrieds=="+domain_url);

        ed_myrides_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (check_net.hasConnection(MyRides.this))
        {
            getMyRidesData();
        }
        else {
            check_net.showNetDisabledAlertToUser(MyRides.this);
        }

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(MyRides.this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void getMyRidesData()
    {

        if (count != 2)
        {
            class MyRidesData extends AsyncTask<String,Void,String>
            {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    progressDialog = new ProgressDialog(MyRides.this,ProgressDialog.THEME_HOLO_LIGHT);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("Please wait");
                    progressDialog.show();
                }

                @Override
                protected String doInBackground(String... strings)
                {

                    try
                    {
                        String url = domain_url + "/mybookingsdriver?";
                        Log.i("url","myrieds="+url);

                        String query = String.format("uId=%s", URLEncoder.encode(uID,"UTF-8"));

                        URL url1 = new URL(url + query);
                        Log.i("url","myrides="+url1);

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
                            String line="";
                            InputStream in = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            while ((line = reader.readLine())!=null)
                            {
                                json_response += line;
                            }
                        }else {
                            json_response = "";
                        }

                    }
                    catch (SocketTimeoutException e)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Snackbar snackbar = Snackbar.make(coordinator_layout,"Server Connection Timeout",Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                count++;
                                                getMyRidesData();
                                            }
                                        });
                                snackbar.setActionTextColor(Color.BLUE);
                                snackbar.show();
                            }
                        });
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("MyRides","json_response="+json_response);
                    return json_response;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    Log.i("MyRides","json_s="+s);
                    if (s == null || s.equals("[]"))
                    {
                        Toast.makeText(MyRides.this,"No Response From Server",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        //[{"responseMessage":["Success"],"responseCode":1,"responseData":[{"refNo":"PP2MG12",
                        // "bookingId":1,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune,
                        // Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilka
                        // lthana, Aurangabad, Maharashtra, India","status":3,
                        // "tdate":"22-Oct-2016","bookedon":"01-Jun-2017",
                        // "pickupTime":"12:32:53","totalAmt":58},
                        // {"refNo":"PP2MG13","bookingId":2,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"03-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG14","bookingId":3,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"06-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG15","bookingId":4,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"10-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG16","bookingId":5,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"12-Jun-2017","pickupTime":"12:32:53","totalAmt":58},{"refNo":"PP2MG17","bookingId":6,"pickUpLo":"164, Nageswar Rasta, Somwar Peth, Pune, Maharashtra 411011, India","dropLoc":"MIDC Industrial Area, Chilkalthana, Aurangabad, Maharashtra, India","status":3,"tdate":"22-Oct-2016","bookedon":"19-Jun-2017","pickupTime":"12:32:53","totalAmt":58}]}]

                        try
                        {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String responseMessage = jsonObject.getString("responseMessage");
                            Log.i("parseMyRides","responseMessage="+responseMessage);
                            String responseCode = jsonObject.getString("responseCode");
                            Log.i("parseMyRides","responseCode="+responseCode);

                            if (responseCode.equals("1"))
                            {
                                listview_myRides.setVisibility(View.VISIBLE);
                                tv_noData.setVisibility(View.GONE);

                                JSONArray array = jsonObject.getJSONArray("responseData");
                                Log.i("parseMyRides","array="+array+" arrayLength="+array.length());

                                for (int i =0 ; i< array.length() ; i++)
                                {
                                    JSONObject object = array.getJSONObject(i);
                                    String refNo = object.getString(Tag_refNo);
                                    Log.i("parseMyRides","refNo="+refNo);
                                    String bookingId = object.getString(Tag_bookingId);
                                    Log.i("parseMyRides","bookingId="+bookingId);
                                    String pickUpLo = object.getString(Tag_pickUpLo);
                                    Log.i("parseMyRides","pickUpLo="+pickUpLo);
                                    String dropLoc = object.getString(Tag_dropLoc);
                                    Log.i("parseMyRides","dropLoc="+dropLoc);
                                    String status = object.getString(Tag_status);
                                    Log.i("parseMyRides","status="+status);
                                    String tdate = object.getString(Tag_tdate);
                                    Log.i("parseMyRides","tdate="+tdate);
                                    String bookedon = object.getString(Tag_bookedon);
                                    Log.i("parseMyRides","bookedon="+bookedon);
                                    String pickupTime = object.getString(Tag_pickupTime);
                                    Log.i("parseMyRides","pickupTime="+pickupTime);
                                    String totalAmt = object.getString(Tag_totalAmt);
                                    Log.i("parseMyRides","totalAmt="+totalAmt);

                                    map_myrides = new HashMap<>();
                                    map_myrides.put(Tag_refNo,refNo);
                                    map_myrides.put(Tag_bookingId,bookingId);
                                    map_myrides.put(Tag_pickUpLo,pickUpLo);
                                    map_myrides.put(Tag_dropLoc,dropLoc);
                                    map_myrides.put(Tag_status,status);
                                    map_myrides.put(Tag_tdate,tdate);
                                    map_myrides.put(Tag_bookedon,bookedon);
                                    map_myrides.put(Tag_pickupTime,pickupTime);
                                    map_myrides.put(Tag_totalAmt,totalAmt);

                                    arrayList_myrides.add(map_myrides);
                                }

                                Log.i("parsemyrides","arrayList_myrides="+arrayList_myrides+" length="+arrayList_myrides.size());
                                MyRidesAdapter adapter = new MyRidesAdapter(MyRides.this,arrayList_myrides,R.layout.my_rides_custom, new String[]{}, new int[]{});
                                listview_myRides.setAdapter(adapter);
                                //setListViewHeightBasedOnChildren(listview_myRides);
                            }
                            else {
                                //[{"responseMessage":["No Bookings Found"],"responseCode":0}]
                                listview_myRides.setVisibility(View.GONE);
                                tv_noData.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            MyRidesData myRidesData = new MyRidesData();
            myRidesData.execute();
        }
        else
        {
            count = 0;
            Snackbar snackbar = Snackbar.make(coordinator_layout,"Please Try After Some Time",Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    public class MyRidesAdapter extends SimpleAdapter
    {

        private Context mContext;
        public LayoutInflater inflater = null;

        public MyRidesAdapter(Context context, List<? extends Map<String,?>> data,int resource, String[] from, int[] to)
        {
            super(context,data,resource,from,to);
            mContext = context;
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            View view = convertView;

            if (view == null)
            {
                view = inflater.inflate(R.layout.my_rides_custom,null);
            }

            HashMap< String, Object > data = (HashMap<String, Object>) getItem(position);
            String refNo = (String) data.get(Tag_refNo);
            Log.i("adapter","refNo="+refNo);
            final String bookingId = (String) data.get(Tag_bookingId);
            Log.i("adapter","bookingId="+bookingId);
            String pickUpLo = (String) data.get(Tag_pickUpLo);
            Log.i("adapter","pickUpLo="+pickUpLo);
            String dropLoc = (String) data.get(Tag_dropLoc);
            Log.i("adapter","dropLoc="+dropLoc);
            final String status = (String) data.get(Tag_status);
            Log.i("adapter","status="+status);
            String tdate = (String) data.get(Tag_tdate);
            Log.i("adapter","tdate="+tdate);
            String bookedon = (String) data.get(Tag_bookedon);
            Log.i("adapter","bookedon="+bookedon);
            String pickupTime = (String) data.get(Tag_pickupTime);
            Log.i("adapter","pickupTime="+pickupTime);
            String totalAmt = (String) data.get(Tag_totalAmt);
            Log.i("adapter","totalAmt="+totalAmt);

            TextView tv_myridesC_CRN = (TextView)view.findViewById(R.id.tv_myridesC_CRN);
            TextView tv_myridesC_totalAMt = (TextView)view.findViewById(R.id.tv_myridesC_totalAMt);
            TextView tv_myridesC_pickUpLoc = (TextView)view.findViewById(R.id.tv_myridesC_pickUpLoc);
            TextView tv_myridesC_dropLoc = (TextView)view.findViewById(R.id.tv_myridesC_dropLoc);
            TextView tv_myridesC_dateTime = (TextView)view.findViewById(R.id.tv_myridesC_dateTime);
            Button btn_myridesC_status = (Button)view.findViewById(R.id.btn_myridesC_status);
            LinearLayout layout_onClick = (LinearLayout)view.findViewById(R.id.layout_onClick);

            tv_myridesC_CRN.setText(refNo);
            tv_myridesC_totalAMt.setText(totalAmt);
            tv_myridesC_pickUpLoc.setText(pickUpLo);
            tv_myridesC_dropLoc.setText(dropLoc);
            tv_myridesC_dateTime.setText(bookedon+","+pickupTime);

            if (status.equals(1))
            {
                btn_myridesC_status.setText("Confirmed");
            }
            if (status.equals(2))
            {
                btn_myridesC_status.setText("Cancelled");
                btn_myridesC_status.setBackgroundResource(R.drawable.cancelled_btn);
            }
            if (status.equals(3) || status.equals(4))
            {
                btn_myridesC_status.setText("Completed");
                btn_myridesC_status.setBackgroundResource(R.drawable.completed_btn);

            }


            layout_onClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MyRides.this,SingelRideDetails.class);
                    intent.putExtra("bookingId",bookingId);
                    //intent.putExtra("status",status);
                    startActivity(intent);
                    finish();
                }
            });

            return view;

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LinearLayout.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

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
        Intent intent = new Intent(this,DashBord.class);
        startActivity(intent);
        finish();
    }
}
