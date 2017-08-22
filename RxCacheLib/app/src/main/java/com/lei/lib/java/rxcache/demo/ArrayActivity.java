package com.lei.lib.java.rxcache.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.util.RxUtil;

import io.reactivex.functions.Consumer;

public class ArrayActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textView, textView1;
    Button button1, button2, button3;
    int[] data = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

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
        textView.setText("待处理的数据为：" + intArray2String(data));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                RxCache.getInstance()
                        .put("testArray", data, 10 * 1000)
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)
                                    textView1.setText("数据保存成功啦！" + intArray2String(data));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView1.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.button2:
                RxCache.getInstance()
                        .get("testArray", false, int[].class)
                        .compose(RxUtil.<CacheResponse<int[]>>io_main())
                        .subscribe(new Consumer<CacheResponse<int[]>>() {
                            @Override
                            public void accept(CacheResponse<int[]> cacheResponse) throws Exception {
                                int[] cacheData = cacheResponse.getData();
                                String cacheStr = "";
                                if (cacheData == null) {
                                    cacheStr = "empty";
                                } else {
                                    cacheStr = intArray2String(cacheData);
                                }
                                textView1.setText("数据获取成功啦！" + cacheStr);
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
                        .remove("testArray")
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

    private String intArray2String(int[] a){
        String str = "[";
        for (int i:a) {
            str+=i+",";
        }
        str=str.substring(0,str.length()-1);
        str+="]";
        return str;
    }
}
