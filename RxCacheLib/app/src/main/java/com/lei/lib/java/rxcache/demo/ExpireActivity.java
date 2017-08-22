package com.lei.lib.java.rxcache.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.util.RxUtil;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.functions.Consumer;

public class ExpireActivity extends AppCompatActivity {
    TextView textView;
    Timer timer;
    int p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expire);

        textView = findViewById(R.id.textview);

        RxCache.getInstance().put("testData", "Hello, I'll lost in 10 secs.", 10 * 1000)
                .compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) textView.setText("数据缓存成功，缓存时间是10s");
                    }
                });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                p++;
                RxCache.getInstance()
                        .get("testData", false, String.class)
                        .compose(RxUtil.<CacheResponse<String>>io_main())
                        .subscribe(new Consumer<CacheResponse<String>>() {
                            @Override
                            public void accept(CacheResponse<String> stringCacheResponse) throws Exception {
                                textView.setText("第" + p + "次获取的数据：" + (stringCacheResponse.getData() == null ? "empty" : stringCacheResponse.getData()));
                            }
                        });
            }
        }, 1000, 1000);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
