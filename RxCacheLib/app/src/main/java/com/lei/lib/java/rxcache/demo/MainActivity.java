package com.lei.lib.java.rxcache.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button
            button1, button2, button3, button4, button5, button6, button7, button8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                to(BasicActivity.class);
                break;
            case R.id.button2:
                to(ArrayActivity.class);
                break;
            case R.id.button3:
                to(AsListActivity.class);
                break;
            case R.id.button4:
                to(BeanActivity.class);
                break;
            case R.id.button5:
                to(BeanListActivity.class);
                break;
            case R.id.button6:
                to(ExpireActivity.class);
                break;
            case R.id.button7:
                to(ModeActivity.class);
                break;
            case R.id.button8:
                to(ConverterActivity.class);
                break;
        }
    }

    private void to(Class<?> clazz) {
        Intent intent = new Intent(MainActivity.this, clazz);
        startActivity(intent);
    }
}
