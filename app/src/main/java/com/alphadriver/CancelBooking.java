package com.alphadriver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CancelBooking extends AppCompatActivity {

    private Toolbar toolbar_inner;
    private TextView tv_tool_header;
    private ImageView toolImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_booking);

        toolbar_inner = (Toolbar) findViewById(R.id.toolbar_inner);
        tv_tool_header = (TextView) findViewById(R.id.tv_tool_header);
        toolImg = (ImageView) findViewById(R.id.toolImg);
        setSupportActionBar(toolbar_inner);
        if (toolbar_inner != null)
        {
            tv_tool_header.setText("Cancel Booking");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
