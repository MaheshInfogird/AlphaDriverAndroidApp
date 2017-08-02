package com.alphadriver;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

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

public class LogInActivity extends AppCompatActivity
{

    private static final String TAG_RESPONSE_MSG = "responseMessage";
    private static final String TAG_RESPONSE_CODE = "responseCode";
    private static final String TAG_RESPONSE_DATE = "responseData";
    private static final String TAG_RESPONSE_UID = "uId";
    private static final String TAG_RESPONSE_F_NAME = "firstName";
    private static final String TAG_RESPONSE_L_NAME = "lastName";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    //private static final String PREFER_NAME = "MyPref";
    public static final String Name = "name";
    public static final String LName = "lastName";
    public static final String Email = "emailKey";
    public static final String Tag_Sign = "responseCode";
    public static final String Tag_SubuserId = "subuserid";
    public static final String Tag_login_Stats = "responseCode";
    public static final String Tag_uid = "uId";
    public static final String Tag_VehicleId = "vehicleId";
    public static final String Tag_driver_image="driverImage";
    public static final String TAG_Vehicle_Name="";
    public static final String TAG_Vehicle_no="";
    public static final String Tag_Balance = "balance";
    public static final String Tag_Email = "email";
    public static final String Tag_Mobile = "mobile";
    public static final String Tag_FirstName = "firstName";
    public static final String Tag_LastName = "lastName";
    public static final String TAG_vahicle_name="vehicleName";
    public static final String TAG_vehicle_no="vehicleNo";


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String PREFER_NAME = "MyPref";

    CheckInternetConnection check_net;
    ConnectionDetector conn_detect;
    UserSessionManager session;
    NetworkChange receiver;

    String url_domain,IPAddress,API_KEY;
    String mobileID,androidID,Androidversion;
    int version_code;

    TextView tv_username_input,tv_password_input;
    Button signin_btn;

    String userName,userPassword;
    ProgressDialog progressDialog;

    private String response = "";
    String fcm_token = "";

    private CoordinatorLayout coordinator_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        check_net = new CheckInternetConnection(LogInActivity.this);
        receiver = new NetworkChange();

        //getting URL IPADDRESS and API key...
        conn_detect = new ConnectionDetector(LogInActivity.this);
        url_domain = conn_detect.geturl();
        IPAddress = conn_detect.getLocalIpAddress();
        API_KEY = conn_detect.getAPIKEY();
        Log.i("conn_detect","url="+url_domain+" IPAddress="+IPAddress+" API_KEY="+API_KEY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
               // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                //ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                //ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LogInActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            //Manifest.permission.ACCESS_COARSE_LOCATION,
                           // Manifest.permission.READ_EXTERNAL_STORAGE,
                           // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_SETTINGS,}, 1);
        }
        else
        {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            mobileID = telephonyManager.getDeviceId();
            Log.i("mobileID","mobileID="+mobileID);
            androidID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.i("androidID","androidID="+androidID);

            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            int version = Build.VERSION.SDK_INT;
            String versionRelease = Build.VERSION.RELEASE;
            Androidversion = manufacturer + model + version+ versionRelease;
            Log.e("MyActivity", "manufacturer " + manufacturer
                    + " \n model " + model
                    + " \n version " + version
                    + " \n versionRelease " + versionRelease
            );
        }

        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            //string version_code = info.versionName;
            version_code = info.versionCode;
            Log.i("version_code", "" + version_code);
        }
        catch (PackageManager.NameNotFoundException e)
        {

        }

        //getting FCM token....
        fcm_token = MyFirebaseInstanceIDService.getTokenFrom();
        Log.i("fcm_token","fcm_token=="+fcm_token);

        //creating user session...
        session = new UserSessionManager(LogInActivity.this);

        pref = getApplicationContext().getSharedPreferences(PREFER_NAME, this.MODE_PRIVATE);
        editor = pref.edit();

        if (session.isUserLoggedIn())
        {
            Intent intent1 = new Intent(LogInActivity.this, DashBord.class);
            startActivity(intent1);
            finish();
        }

        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        tv_username_input = (TextView)findViewById(R.id.username_input);
        tv_password_input = (TextView)findViewById(R.id.password_input);
        signin_btn = (Button)findViewById(R.id.signin_btn);

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode)
        {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                // Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            }
            else
            {
                // Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        }
        else
        {
            //Starting intent to register device
            //Intent itent = new Intent(this, GCMRegistrationIntentService.class);
           // startService(itent);
        }

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (check_net.hasConnection(LogInActivity.this))
                {
                    userName = tv_username_input.getText().toString();
                    Log.i("username","=="+userName);
                    userPassword = tv_password_input.getText().toString();
                    Log.i("password","=="+userPassword);

                    if (userName.equals("") && userPassword.equals(""))
                    {
                        tv_username_input.setError("Please Enter Name");
                        tv_password_input.setError("Please Enter Password");
                        textChange();
                    }
                    else if (userName.equals(""))
                    {
                        tv_username_input.setError("Please Enter Name");
                        textChange();
                    }
                    else if (userPassword.equals(""))
                    {
                        tv_password_input.setError("Please Enter Password");
                        textChange();
                    }
                    else{

                        if (fcm_token == null)
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(LogInActivity.this);
                            alert.setMessage("Please Try Again");
                            alert.setCancelable(false);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   // dialogInterface.dismiss();
                                    Intent intent = new Intent(LogInActivity.this,LogInActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }).show();
                        }
                        else
                        {
                            signIn_User();
                        }
                    }

                }
                else
                {
                    check_net.showDisableAlert(LogInActivity.this);
                }

            }
        });

    }

    public void textChange()
    {
        tv_username_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_username_input.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tv_password_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_password_input.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void signIn_User()
    {
        class SignIn extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(LogInActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("Signing In...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings)
            {
                try
                {

    //http://alfaptop.safegird.com/owner/api/signindriver?email=varad@infogird.com&
                    // password=varad&token=1234&imeiNumber=123&androidVersion=1&
                    // androidId=1&version=1

                    String url = url_domain+"/signindriver?";
                    Log.i("url","domain=="+url);

                    String query = String.format("email=%s&password=%s&token=%s&imeiNumber=%s&androidVersion=%s&androidId=%s&version=%s",
                            URLEncoder.encode(userName,"UTF-8"),
                            URLEncoder.encode(userPassword,"UTF-8"),
                            URLEncoder.encode(fcm_token,"UTF-8"),
                            URLEncoder.encode(mobileID,"UTF-8"),
                            URLEncoder.encode(Androidversion,"UTF-8"),
                            URLEncoder.encode(androidID,"UTF-8"),
                            URLEncoder.encode("1","UTF-8"));

                    //************ version put as static REMEMBER *********//

                    String final_url = url + query;
                    final_url = final_url.replace("%40","@");

                    URL url1 = new URL(final_url );
                    Log.i("url","final="+url1);

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
                        response="";
                        String line="";
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        while ((line=reader.readLine())!=null)
                        {
                            response += line;
                        }
                    }else {
                        response = "";
                    }

                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                } catch (ProtocolException e)
                {
                    e.printStackTrace();
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }catch (SocketTimeoutException e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar.make(coordinator_layout,"Server Connection Timeout Try Again",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(LogInActivity.this,"Connection TimeOut",Toast.LENGTH_LONG).show();
                }
               /* catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
                catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                }*/

                Log.i("response","login="+response);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                if (s.equals("[]") || s == null)
                {
                    Toast.makeText(LogInActivity.this, "Sorry... Bad internet connection", Toast.LENGTH_LONG).show();
                }
                else {

                    Log.i("response","s=="+s);
                    //[{"responseMessage":["Success"],"responseCode":1,
                    // "responseData":[{"uId":3,"firstName":"Pavan","lastName":"Patil"}]}]

                    try
                    {
   //[{"responseMessage":["Success"],"responseCode":1,"responseData":
                        // [{"uId":5,"name":"Rishi Jadhav","vehicleId":2}]}]

                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String responseMessage = jsonObject.getString(TAG_RESPONSE_MSG);
                        Log.i("response","responseMessage="+responseMessage);
                        String responseCode = jsonObject.getString(TAG_RESPONSE_CODE);
                        Log.i("response","responseCode="+responseCode);

                        if (responseCode.equals("1"))
                        {
                            session.createUserLoginSession(userName,userPassword);
                            JSONArray array = jsonObject.getJSONArray(TAG_RESPONSE_DATE);
                            Log.i("response","array="+array);
                            JSONObject object = array.getJSONObject(0);
                            String uId = object.getString(Tag_uid);
                            Log.i("response","uId="+uId);
                            String firstName = object.getString(Name);
                            Log.i("response","firstName="+firstName);
                            //String lastName = object.getString(LName);
                           // Log.i("response","lastName="+lastName);
                            String vehicleId =  object.getString(Tag_VehicleId);
                            Log.i("response","vehicleId="+vehicleId);

                            editor.putString(Name,firstName);
                            //editor.putString(LName,lastName);
                            editor.putString("uId",uId);
                            editor.putString("vehicleId",vehicleId);
                            editor.commit();

                            Intent intent = new Intent(LogInActivity.this,DashBord.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            String msg = responseMessage.substring(2,responseMessage.length()-2);
                            Log.i("msg","msg="+msg);
                            Log.i("LoginStatus_else", responseCode);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LogInActivity.this);
                            alertDialog.setTitle("");
                            alertDialog.setMessage(msg);
                            alertDialog.setCancelable(false);
                            //   alertDialog.setIcon(R.drawable.fail);
                            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            });
                            alertDialog.show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        SignIn signIn = new SignIn();
        signIn.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("requestCode","requestCode=="+requestCode+" grantResults=="+grantResults);
        if (requestCode == 1)
        {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("requestCode","IN-PERMISSION=requestCode=="+requestCode+" grantResults=="+grantResults);
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                mobileID = telephonyManager.getDeviceId();
                Log.i("mobileID","mobileID="+mobileID);
                androidID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.i("androidID","androidID="+androidID);

                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                int version = Build.VERSION.SDK_INT;
                String versionRelease = Build.VERSION.RELEASE;
                Androidversion = manufacturer + model + version+ versionRelease;
                Log.e("MyActivity", "manufacturer " + manufacturer
                        + " \n model " + model
                        + " \n version " + version
                        + " \n versionRelease " + versionRelease
                );
            }
        }
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
}
