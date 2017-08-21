package com.lei.lib.java.rxcache.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.lei.lib.java.rxcache.RxCache;
import com.lei.lib.java.rxcache.util.LogUtil;
import com.lei.lib.java.rxcache.util.RxUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //毫无问题
        String testString = "Hello World!!!";
        RxCache.getInstance()
                .put("testString", testString, 10 * 1000)
                .compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtil.e("缓存String数据成功！");
                    }
                });

        RxCache.getInstance()
                .get("testString", false, String.class)
                .compose(RxUtil.<String>io_main())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {

                        LogUtil.e("取到的String缓存：" + string);
                    }
                });

        //由于Gson解析原因，会把数组解析成ArrayList。
        int[] testArray = new int[10];
        for (int i = 0; i < testArray.length; i++) {
            testArray[i] = i * i;
        }

        RxCache.getInstance()
                .put("testArray1", testArray, 10 * 1000)
                .compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtil.e("缓存Array数据成功！");
                    }
                });

        RxCache.getInstance()
                .get("testArray1", false, int[].class)
                .compose(RxUtil.<int[]>io_main())
                .subscribe(new Consumer<int[]>() {
                    @Override
                    public void accept(int[] ints) throws Exception {
                        for (int i : ints) {
                            LogUtil.e("取到的Array缓存：" + i);
                        }
                    }
                });

        List<String> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testList.add("Hello World!!!" + i);
        }
        RxCache.getInstance()
                .put("testList", testList, 10 * 1000)
                .compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtil.e("缓存List数据成功！");
                    }
                });

        Type type = new TypeToken<List<String>>() {
        }.getType();
        RxCache.getInstance()
                .<List<String>>get("testList", false, type)
                .compose(RxUtil.<List<String>>io_main())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        for (String s : strings) {
                            LogUtil.e("取到的List缓存：" + s);
                        }
                    }
                });
        //test Bean
        final DemoBean demoBean = new DemoBean();
        demoBean.setName("Hello");
        demoBean.setPass("World");
        RxCache.getInstance()
                .put("testBean", demoBean, 100 * 1000)
                .compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtil.e("缓存Bean数据成功！");
                    }
                });

        RxCache.getInstance()
                .get("testBean", false, DemoBean.class)
                .subscribe(new Consumer<DemoBean>() {
                    @Override
                    public void accept(DemoBean demoBean) throws Exception {
                        LogUtil.e("取到的Bean缓存：" + demoBean.toString());
                    }
                });

        //test BeanList
        List<DemoBean> beanList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoBean demoBean1 = new DemoBean();
            demoBean1.setName("Hello");
            demoBean1.setPass("World");
//            demoBean1.setArgs(testArray);
//            demoBean1.setWords(testList);
            beanList.add(demoBean1);
        }

        RxCache.getInstance()
                .put("testBeans", beanList, 10 * 1000)
                .compose(RxUtil.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtil.e("缓存Beans数据成功！");
                    }
                });
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Type type1 = new TypeToken<List<DemoBean>>() {
        }.getType();
        RxCache.getInstance()
                .<List<DemoBean>>get("testBeans", false, type1)
                .compose(RxUtil.<List<DemoBean>>io_main())
                .subscribe(new Consumer<List<DemoBean>>() {
                    @Override
                    public void accept(List<DemoBean> demoBeans) throws Exception {
                        for (DemoBean d:demoBeans) {
                            LogUtil.e("取到的Beans缓存为："+d.toString());
                        }
                    }
                });
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        })
                .compose(RxUtil.<Void>io_main())
                .subscribe(new Consumer<Void>() {
            @Override
            public void accept(Void aVoid) throws Exception {
                LogUtil.e("收到");

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(throwable.getLocalizedMessage());
            }
        });*/
        RxCache.getInstance()
                .<List<DemoBean>>get("testBeans", false, type1)
                .compose(RxUtil.<List<DemoBean>>io_main())
                .subscribe(new Observer<List<DemoBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<DemoBean> value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class DemoBean implements Serializable {
        String name;
        String pass;
        int[] args;
        List<String> words;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public int[] getArgs() {
            return args;
        }

        public void setArgs(int[] args) {
            this.args = args;
        }

        public List<String> getWords() {
            return words;
        }

        public void setWords(List<String> words) {
            this.words = words;
        }

        @Override
        public String toString() {
            return "DemoBean{" +
                    "name='" + name + '\'' +
                    ", pass='" + pass + '\'' +
                    ", args=" + Arrays.toString(args) +
                    ", words=" + words +
                    '}';
        }
    }
}
