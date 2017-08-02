package com.alphadriver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by infogird on 10/06/2017.
 */

public class ConnectionDetector
{
    private Context _context;


    public ConnectionDetector(Context context)
    {
        this._context = context;
    }

    public boolean isConnectingToInternet()
    {

        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public String geturl()
    {
        //String url ="http://orocab.cabsaas.com/api/ptop";//live
        //String url ="http://jaidevcoolcab.cabsaas.in/sandbox/ptop";//jaidev
        //String url ="http://ptop.cabsaas.com/api/ptop";//jaidev
        String url = "http://alfaptop.safegird.com/owner/driverapi";//alfa driver.


        return url;
    }
    //http://jaidevcoolcab.cabsaas.in/sandbox/ptop/driverAccept/?bookingId=120&minute=&ApiKey=S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47&UserID=1212&UserIPAddress=203.48.165.229&UserAgent=androidApp&responsetype=2

    //get api key
    public String getAPIKEY()
    {

        //String key = "";//live
        String key = "S2AORU-KOXBNK-161JB3-S5HFAV-CI5O47";//jaidv
        return key;
    }
    // not used
    public String GetIpaddress()
    {
        WifiManager wm = (WifiManager)_context. getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
    //get user id address
    public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        //return inetAddress.getHostAddress().toString();
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("IPAddress", "***** IP=" + ip);
                        return ip;
                        // return inetAddress.getAddress().toString();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            //  Log.e("IP Address", ex.toString());
        }
        return null;
    }
}
