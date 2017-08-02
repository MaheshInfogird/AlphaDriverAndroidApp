package com.alphadriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.net.URL;
import java.net.URLEncoder;

public class Testing extends AppCompatActivity {

    ConnectionDetector cd;
    String Url, IPAddress, APIKEY;
    private static final String PREFER_NAME = "MyPref";
    SharedPreferences sharedpreferences;
    String v_id,uId;
    ProgressDialog progressDialog;
    private Button btn_acceptbooking,btn_clientlocate,btn_completebooking;
    String sub_url="",response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);


        cd = new ConnectionDetector(getApplicationContext());
        Url = cd.geturl();
        APIKEY = cd.getAPIKEY();
        IPAddress = cd.getLocalIpAddress();

        sharedpreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, getApplicationContext().MODE_PRIVATE);

        v_id = sharedpreferences.getString("vehicleId", "");
        Log.i("vehicleId", "vehicleId=" + v_id);

        uId = sharedpreferences.getString("uId", "");
        Log.i("uId", "" + uId);

        btn_acceptbooking = (Button) findViewById(R.id.btn_acceptbooking);
        btn_clientlocate = (Button) findViewById(R.id.btn_clientlocate);
        btn_completebooking = (Button) findViewById(R.id.btn_completebooking);

        btn_acceptbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sub_url = "acceptbooking?";
                performAction();
                btn_acceptbooking.setEnabled(false);
            }
        });

        btn_clientlocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sub_url = "clientlocate?";
                performAction();
            }
        });

        btn_completebooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sub_url = "completebooking?";
                performAction();
                btn_acceptbooking.setEnabled(true);
            }
        });

    }

    public void performAction()
    {
        class Action extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(Testing.this,"Plaese wait","Getting Data...",true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {


                try
                {

                    //http://alfaptop.safegird.com/owner/driverapi/signindriver?
                    // email=pavan@infogird.com&password=Shriram12345

                    String url = Url+"/"+sub_url;
                    Log.i("url","domain=="+url);

                    String query = String.format("vehicleId=%s",
                            URLEncoder.encode(v_id,"UTF-8"));

                    //String final_url = url + query;
                    //final_url = final_url.replace("%40","@");

                    URL url1 = new URL(url+query);
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
                } catch (IOException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(LogInActivity.this,"Connection TimeOut",Toast.LENGTH_LONG).show();
                }
                Log.i("response","response=="+response);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                if (s.equals("[]") || s.equals(""))
                {
                    Toast.makeText(Testing.this,"Bad Internet Connection",Toast.LENGTH_LONG).show();
                }
                else {
                    Log.i("response","s=="+s);

                    //[{"responseMessage":["Booking Completed Successfully"],"responseCode":1}]
                    try
                    {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject object = jsonArray.getJSONObject(0);
                        String responseMessage = object.getString("responseMessage");
                        String responseCode = object.getString("responseCode");

                        if (responseCode.equals("1"))
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Testing.this);
                            alert.setMessage(responseMessage);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                        else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Testing.this);
                            alert.setMessage(responseMessage);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Action action = new Action();
        action.execute();
    }
}
