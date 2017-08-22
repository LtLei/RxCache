package com.lei.lib.java.rxcache.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.converter.GsonConverter;
import com.lei.lib.java.rxcache.converter.SerializableConverter;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.util.RxUtil;

import io.reactivex.functions.Consumer;

public class ConverterActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textView , textView1;
    Button button1,button2,button11,button12,button13;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button11 = findViewById(R.id.button11);
        button12 = findViewById(R.id.button12);
        button13 = findViewById(R.id.button13);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);
        textView.setText("当前转换模式为：Gson转换");

        data = "这是测试转换方式的数据，使用Gson时必须传type或class";
        textView1.setText("待处理的数据："+data);
    }
boolean isGson = true;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                isGson = true;
                RxCache.getInstance().setConverter(new GsonConverter());textView.setText("当前转换模式为：Gson转换");
                break;
            case R.id.button2:
                isGson = false;
                RxCache.getInstance().setConverter(new SerializableConverter());textView.setText("当前转换模式为：序列化转换");
                break;
            case R.id.button11:
                RxCache.getInstance()
                        .put("test",data,60 *1000)
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)textView1.setText("存储成功啦！"+data);
                            }
                        });
                break;
            case R.id.button12:
                if (isGson){
                    RxCache.getInstance()
                            .get("test",false,String.class)
                            .compose(RxUtil.<CacheResponse<String>>io_main())
                            .subscribe(new Consumer<CacheResponse<String>>() {
                                @Override
                                public void accept(CacheResponse<String> stringCacheResponse) throws Exception {
                                    textView1.setText("获取到的数据："+(stringCacheResponse.getData()==null?"empty":stringCacheResponse.getData()));
                                }
                            });
                }else {
                    RxCache.getInstance()
                            .<String>get("test",false)
                            .compose(RxUtil.<CacheResponse<String>>io_main())
                            .subscribe(new Consumer<CacheResponse<String>>() {
                                @Override
                                public void accept(CacheResponse<String> stringCacheResponse) throws Exception {
                                    textView1.setText("获取到的数据："+(stringCacheResponse.getData()==null?"empty":stringCacheResponse.getData()));
                                }
                            });
                }
                break;
            case R.id.button13:
                RxCache.getInstance()
                        .remove("test")
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)textView1.setText("清理成功啦！");
                            }
                        });
                break;
        }
    }
}
