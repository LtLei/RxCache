package com.lei.lib.java.rxcache.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.util.RxUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class AsListActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textView, textView1;
    Button button1, button2, button3;
    List<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_array);
        textView = findViewById(R.id.textview);
        textView1 = findViewById(R.id.textview1);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        for (int i = 0; i < 20; i++) {
            dataList.add("Hello List" + i + "!");
        }

        textView.setText("待处理的数据：" + dataList.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                RxCache.getInstance()
                        .put("testList", dataList, 10 * 1000)
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)
                                    textView1.setText("数据保存成功啦！" + dataList.toString());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView1.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.button2:
                Type type = new TypeToken<List<String>>() {
                }.getType();
                RxCache.getInstance()
                        .<List<String>>get("testList", false, type)
                        .compose(RxUtil.<CacheResponse<List<String>>>io_main())
                        .subscribe(new Consumer<CacheResponse<List<String>>>() {
                            @Override
                            public void accept(CacheResponse<List<String>> listCacheResponse) throws Exception {
                                textView1.setText("数据获取成功啦！" + (listCacheResponse.getData() == null ? "empty" : listCacheResponse.getData().toString()));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView1.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.button3:
                RxCache.getInstance()
                        .remove("testList")
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)
                                    textView1.setText("数据删除成功啦！");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView1.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
        }
    }
}
