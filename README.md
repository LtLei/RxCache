# RxCache 轻量的网络缓存库，配合RxJava链式使用

RxCache使用LruCache和DiskLruCache对网络请求数据进行二级缓存，主要适配于接口API返回数据，不用于图片等的缓存。可以设置缓存模式、缓存大小，设置数据过期时间，并提供了根据key删除缓存和清空所有缓存的功能。提供了Gson方式和Serialize方式进行数据存储转换与还原。

# 使用方法：

## 在你的Application中进行初始化：

``` 
RxCache.init(this);//为RxCache提供Context
```

## 也可以使用Builder进行高级初始化：

```
new RxCache.Builder()
    .setDebug(true)   //开启debug，开启后会打印缓存相关日志，默认为true
    .setConverter(new GsonConverter())  //设置转换方式，默认为Gson转换
    .setCacheMode(CacheMode.BOTH)   //设置缓存模式，默认为二级缓存
    .setMemoryCacheSizeByMB(50)   //设置内存缓存的大小，单位是MB
    .setDiskCacheSizeByMB(100)    //设置磁盘缓存的大小，单位是MB
    .setDiskDirName("RxCache")    //设置磁盘缓存的文件夹名称
    .build();
```

## 写入缓存

```
RxCache.getInstance()
    .put("test", "This is data to cache.", 10 * 1000)   //key:缓存的key data:具体的数据 time:缓存的有效时间
    .compose(RxUtil.<Boolean>io_main()) //线程调度
    .subscribe(new Consumer<Boolean>() {
        @Override
        public void accept(Boolean aBoolean) throws Exception {
            if (aBoolean) Log.d("Cache", "cache successful!");
        }
    },new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    });
```

## 读取缓存

读取缓存时，分为以下几种情况：

### 若为Gson转换时：
#### 读取基本类型数据，或自定义的javabean数据，或数组数据等一切可以获取.class的数据

```
RxCache.getInstance()
    .get("test",false,String.class)   //key:缓存的key update:表示从缓存获取数据强行返回NULL
    .compose(RxUtil.<CacheResponse<String>>io_main())
    .subscribe(new Consumer<CacheResponse<String>>() {
        @Override
        public void accept(CacheResponse<String> stringCacheResponse) throws Exception {
            if(stringCacheResponse.getData()!=null)
                Log.d("data from cache : "+stringCacheResponse.getData());
        }
    },new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    });
```

#### 读取List等无法获取.class的数据，以上基本数据也可以使用此方式

```
Type type = new TypeToken<List<String>>(){}.getType();
RxCache.getInstance()
    .<List<String>>get("test",false,type)   //由于Type不是类，需要指定泛型
    .compose(RxUtil.<CacheResponse<List<String>>>io_main())
    .subscribe(new Consumer<CacheResponse<List<String>>>() {
        @Override
        public void accept(CacheResponse<List<String>> listCacheResponse) throws Exception {
            if(listCacheResponse.getData()!=null)
                Log.d("data from cache : "+listCacheResponse.getData().toString());
        }
    },new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    });
```

### 若为Serialize方式时，则统一使用以下方法即可：

```
RxCache.getInstance()
    .<List<String>>get("test",false)   //指定泛型，不再需要传.class或Type
    .compose(RxUtil.<CacheResponse<List<String>>>io_main())
    .subscribe(new Consumer<CacheResponse<List<String>>>() {
        @Override
        public void accept(CacheResponse<List<String>> listCacheResponse) throws Exception {
            if(listCacheResponse.getData()!=null)
                Log.d("data from cache : "+listCacheResponse.getData().toString());
        }
    },new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    });
```

## 清除指定缓存

```
RxCache.getInstance()
    .remove("testList")
    .compose(RxUtil.<Boolean>io_main())
    .subscribe(new Consumer<Boolean>() {
        @Override
        public void accept(Boolean aBoolean) throws Exception {
            if (aBoolean) Log.d("cache data has been deleted.");
        }
    }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    });
```

## 清除全部缓存

```
RxCache.getInstance()
    .clear()
    .compose(RxUtil.<Boolean>io_main())
    .subscribe(new Consumer<Boolean>() {
        @Override
        public void accept(Boolean aBoolean) throws Exception {
            if (aBoolean) Log.d("All datas has been deleted.");
        }
    }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    });
```
