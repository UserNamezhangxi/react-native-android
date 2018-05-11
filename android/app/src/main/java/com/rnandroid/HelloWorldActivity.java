package com.rnandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.react.ReactActivity;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2018/5/5.
 */

public class HelloWorldActivity extends ReactActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        btn = (Button) findViewById(R.id.button);
        TextView textView = (TextView) findViewById(R.id.params);

        String data = getIntent().getStringExtra("params");
        textView.setText(data);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回React native
                finish();
            }
        });


    }



}
