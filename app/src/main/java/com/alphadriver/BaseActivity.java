package com.alphadriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseActivity extends AppCompatActivity
{
    private static final String TAG_RESPONSE_F_NAME = "name";
    private static final String TAG_RESPONSE_L_NAME = "lastName";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    private static final String PREFER_NAME = "MyPref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    CheckInternetConnection check_net;
    UserSessionManager session;
    ConnectionDetector conn_detect;
    NetworkChange receiver;
    private Toolbar toolbar_inner;
   // private ListView drawer_list;
    ImageView Headerimage;
    private List<String> listDataHeader;
    private ArrayList<NavDrawerItem> navDrawerItems;
    NavDrawerListAdapter adapter;

    String driver_full_name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        receiver = new NetworkChange();

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        setSupportActionBar(toolbar_inner);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        String fName = pref.getString(TAG_RESPONSE_F_NAME,"");
        Log.i("fName","=="+fName);
        //String lName = pref.getString(TAG_RESPONSE_L_NAME,"");
       // Log.i("lName","=="+lName);
        driver_full_name = fName;
        Log.i("driver_name","=="+driver_full_name);

        session = new UserSessionManager(BaseActivity.this);

    }

    public void set(String[] navMenuTitles, TypedArray navMenuIcons)
    {
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList   = (ListView) findViewById(R.id.left_drawer);

        //layout for navigation drawer header...
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.side_menu_header, null, false);

        TextView user_name=(TextView)listHeaderView.findViewById(R.id.user_name);
        user_name.setText(driver_full_name);

        //ImageView heder_driver_pic=(ImageView)listHeaderView.findViewById(R.id.heder_driver_pic);

       /* if(!driver_image.equals("")) {
            Picasso.with(getApplicationContext()).load(driver_image).into(heder_driver_pic);

        }*/
        mDrawerList.addHeaderView(listHeaderView);
        //end adding navigation drawer header...



        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items
        if (navMenuIcons == null)
        {
            for (int i = 0; i < navMenuTitles.length; i++)
            {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i]));
            }
        }
        else
        {
            for (int i = 0; i < navMenuTitles.length; i++)
            {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
            }
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar_inner, R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerClosed(View view)
            {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                getSupportActionBar().setTitle(mDrawerTitle);

                supportInvalidateOptionsMenu();
            }
        };

       // mDrawerToggle.setHomeAsUpIndicator(R.drawable.menu_icon);
        //mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.menu_icon,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            displayView(position);
        }
    }

    private void displayView(int position)
    {
        switch (position)
        {
           /* case 0:
                Intent intent = new Intent(this,BaseActivity.class);
                startActivity(intent);
                finish();
                break;*/

            case 1:
                //dashboard
                Intent intent1 = new Intent(this, DashBord.class);
                //Dashboard.timer_start = true;
                startActivity(intent1);
                finish();
                break;

            case 2:
                //my rides
                Intent intent2 = new Intent(this, MyRides.class);
                startActivity(intent2);
                finish();
                break;

            case 3:
                //my earnings
                Intent intent4 = new Intent(this, MyEarnings.class);
                //Intent intent4 = new Intent(this, BookingAlert.class);
                startActivity(intent4);
                finish();
                break;

            case 4:
                //language
                Intent intent8 = new Intent(this, Testing.class);
                startActivity(intent8);
                finish();
                break;

            case 5:
                //help
                Intent intent9 = new Intent(this,MapActivity.class);
                startActivity(intent9);
                finish();
                break;

            case 6:
                //log out
                AlertDialog.Builder alert = new AlertDialog.Builder(BaseActivity.this);
                alert.setTitle("Log Out");
                alert.setMessage("Are you sure to logout?");
                alert.setCancelable(false);
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        session.logoutUser();
                        editor.clear();
                        editor.commit();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                break;

            default:
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (mDrawerLayout.isDrawerOpen(mDrawerList))
            {
                mDrawerLayout.closeDrawer(mDrawerList);
            }
            else
            {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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
