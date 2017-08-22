package com.lei.lib.java.rxcache.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.entity.CacheResponse;
import com.lei.lib.java.rxcache.util.RxUtil;

import io.reactivex.functions.Consumer;

public class BasicActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editText;
    TextView textView;
    Button button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        editText = findViewById(R.id.edittext);
        textView = findViewById(R.id.textview);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                final String text = editText.getText().toString().trim();
                RxCache.getInstance()
                        .put("testString", text, 10 * 1000)
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)
                                    textView.setText("数据保存成功啦！" + text);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.button2:
                RxCache.getInstance()
                        .get("testString", false, String.class)
                        .compose(RxUtil.<CacheResponse<String>>io_main())
                        .subscribe(new Consumer<CacheResponse<String>>() {
                            @Override
                            public void accept(CacheResponse<String> stringCacheResponse) throws Exception {
                                textView.setText("数据获取成功啦！" + (stringCacheResponse.getData() == null ? "empty" : stringCacheResponse.getData()));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.button3:
                RxCache.getInstance()
                        .remove("testString")
                        .compose(RxUtil.<Boolean>io_main())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean)
                                    textView.setText("数据删除成功啦！");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                textView.setText(throwable.getLocalizedMessage());
                            }
                        });
                break;
        }
    }
}
