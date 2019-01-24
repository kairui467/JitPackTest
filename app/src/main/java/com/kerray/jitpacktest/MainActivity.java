package com.kerray.jitpacktest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.kerray.library.ArcView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        String text = "测试测试测试测试测试测试测试测试测试测试测试你是的进口成本价实地考察假塞德";
        String text1 = "测试水电费水";

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.GRAY);

        ArcView up = new ArcView(this);
        up.setText(text);
        up.setBackgroundColor(0xFF666666);

        ArcView down = new ArcView(this);
        down.setText(text);
        down.setBackgroundColor(0xFF666666);
        down.setDirection(ArcView.DIRECTION_DOWN);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 100,0,0);

        ll.addView(up);
        ll.addView(down, lp);
        setContentView(ll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }
}
