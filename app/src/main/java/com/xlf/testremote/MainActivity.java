package com.xlf.testremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xlf.remotelib.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Util().TestLog();
    }
}
