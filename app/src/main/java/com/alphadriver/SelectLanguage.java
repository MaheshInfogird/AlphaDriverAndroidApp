package com.alphadriver;

import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SelectLanguage extends BaseActivity
{
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    String[] navMenuTitles;
    TypedArray navMenuIcons;

    NetworkChange receiver;
    CheckInternetConnection check_net;
    UserSessionManager session;
    ConnectionDetector conn_detect;

    private Toolbar toolbar_inner;
    private TextView tv_tool_header;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_language);

        receiver = new NetworkChange();

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("SELECT LANGUAGE");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles,navMenuIcons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(SelectLanguage.this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
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
