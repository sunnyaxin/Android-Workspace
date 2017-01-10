package com.stydydemo.sunny.autoview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagerView pagerView = (PagerView)findViewById(R.id.pager);

        TextView textView1 = new TextView(this);
        textView1.setText("11111111111");
        textView1.setBackgroundColor(Color.BLUE);

        TextView textView2 = new TextView(this);
        textView2.setText("22222222222");
        textView2.setBackgroundColor(Color.RED);

        pagerView.addView(textView1);
        pagerView.addView(textView2);
    }
}
