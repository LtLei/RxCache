package com.lei.lib.java.rxcache.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.mode.CacheMode;
import com.lei.lib.java.rxcache.util.RxUtil;

import io.reactivex.functions.Consumer;

public class ModeActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textView, textView1;
    Button button1, button2, button3, button4, button11, button12, button13, button14;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        button11 = findViewById(R.id.button11);
        button12 = findViewById(R.id.button12);
        button13 = findViewById(R.id.button13);
        button14 = findViewById(R.id.button14);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);
        button14.setOnClickListener(this);

        textView.setText("当前的缓存模式为：二级缓存");

        data = "这是用来测试缓存模式的，具体可以看打印日志";
        textView1.setText("待处理的数据：" + data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                RxCache.getInstance()
                        .setCacheMode(CacheMode.NONE);
                textView.setText("当前的缓存模式为：无缓存");
                break;
            case R.id.button2:
                RxCache.getInstance()
                        .setCacheMode(CacheMode.ONLY_MEMORY);
                textView.setText("当前的缓存模式为：仅内存缓存");
                break;
            case R.id.button3:
                RxCache.getInstance()
                        .setCacheMode(CacheMode.ONLY_DISK);
                textView.setText("当前的缓存模式为：仅磁盘缓存");
                break;
            case R.id.button4:
                RxCache.getInstance()
                        .setCacheMode(CacheMode.BOTH);
                textView.setText("当前的缓存模式为：二级缓存");
                break;
            case R.id.button11:
                RxCache.getInstance()
                        .put("test", data, 60 * 1000)
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) textView1.setText("缓存成功！" + data);
                            }
                        });
                break;
            case R.id.button12:
                RxCache.getInstance()
                        .get("test", false, String.class)
                        .compose(RxUtil.<CacheResponse<String>>io_main())
                        .subscribe(new Consumer<CacheResponse<String>>() {
                            @Override
                            public void accept(CacheResponse<String> stringCacheResponse) throws Exception {
                                textView1.setText("获取到的数据：" + (stringCacheResponse.getData() == null ? "empty" : stringCacheResponse.getData()));
                            }
                        });
                break;
            case R.id.button13:
                RxCache.getInstance()
                        .remove("test")
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) textView1.setText("删除成功！");
                            }
                        });
                break;
            case R.id.button14:
                RxCache.getInstance()
                        .clear()
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) textView1.setText("清理成功");
                            }
                        });
                break;
        }
    }
}
