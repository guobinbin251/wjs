package com.android.wjs.http;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.android.wjs.common.Constant;
import com.android.wjs.common.Logger;
import com.android.wjs.http.callback.HttpCallBack;
import com.android.wjs.http.response.HttpResponse;
import com.android.wjs.ui.MyApplication;
import com.android.wjs.utils.ACache;
import com.android.wjs.utils.JsonUtils;
import com.android.wjs.utils.SharedPreferencesUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Andy Guo on 2016/12/22.
 * <p>
 * 网络连接封装工具类
 * <p>
 * OkHttp
 * 增加了Https的支持  Andy Guo on 2017/6/23.
 * Http优化调用方式，兼容后台的三种方式
 * 将callback以前单一返回字符串修改为底层剥离，直接返回上层用到实体类。在UI线程中  Andy Guo on 2017/7/13.
 */

public class HttpUtils {

    private static HttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    private HttpUtils() {
        /**
         * 构建OkHttpClient
         */


        mOkHttpClient = new OkHttpClient.Builder()
                /**
                 * 设置连接的超时时间
                 */
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                /**
                 * 请求的超时时间
                 */
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                /**
                 * 设置响应的超时时间
                 */
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                //.Hande (new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER))


                /*.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String url, SSLSession session) {
                        return url != null && (url.contains("192.168.35") || url.contains("gwtrader.net") || url.contains("phgsa.cn"));
                    }
                })*/

                .build();


        /**
         * 获取主线程的handler
         */
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 通过单例模式 构造对象
     *
     * @return OkHttpUtils
     */
    private synchronized static HttpUtils getInstance() {

        if (mInstance == null) {
            mInstance = new HttpUtils();
        }
        return mInstance;
    }

    /**
     * get请求
     * 暂时不封装get参数，业务层放入url中
     *
     * @param url      请求url
     * @param listener 监听回调
     */
    public static void get(@NonNull String url, HttpCallBack listener) {
        getInstance().getRequest(url, listener);
    }

    /**
     * post请求
     *
     * @param url      请求url
     * @param map      参数
     * @param callBack 监听回调
     */
    public static void post(@NonNull String url,  Map<String, String> map, HttpCallBack callBack) {
        getInstance().postRequest(url, map, callBack);
    }


    /**
     * put请求
     *
     * @param url      请求url
     * @param obj      参数
     * @param callBack 监听回调
     */
    public static void put(@NonNull String url, Object obj, HttpCallBack callBack) {
        getInstance().putRequest(url, obj, callBack);
    }

    /**
     * 构造Get请求
     *
     * @param url      请求的url
     * @param callBack 结果回调的方法
     */
    private void getRequest(@NonNull String url, final HttpCallBack callBack) {
        Logger.d("http url = " + url);
        final Request request = new Request.Builder().url(url).build();
        deliveryResult(request, callBack);
    }

    private void postRequest(@NonNull String url, Map<String, String> map, HttpCallBack callBack) {


        //form表单的提交方式
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                builder.add(key, map.get(key));
            }
        }

        Logger.d("http url = " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        deliveryResult(request, callBack);
    }


    private void putRequest(@NonNull String url, @Nullable Object obj, HttpCallBack callBack) {
        String json = JsonUtils.toJson(obj);
        Logger.d("http json = " + json);
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Logger.d("http url = " + url);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        deliveryResult(request, callBack);
    }

    /**
     * 处理请求、返回结果
     *
     * @param request
     * @param callBack
     */
    private void deliveryResult(final Request request, @Nullable final HttpCallBack callBack) {

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                Logger.d("网络异常");
                                callBack.onFailure("net error");
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, @NonNull final Response response) throws IOException {
                final String resp = response.body().string();

                Logger.d("http resp = " + resp);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            try {
                                HttpResponse<?> ret = JsonUtils.fromJson(resp, new TypeToken<HttpResponse<?>>() {
                                }.getType());
                                if (ret.getStatus() == 1) {
                                    String jsonStr = JsonUtils.toJson(ret.getResult());
                                    Object Obj = JsonUtils.fromJson(jsonStr, callBack.mType);
                                    /*List<String> cookies= response.headers().values("Set-Cookie");
                                    ArrayList<String> newCk = new ArrayList<String>();
                                    for(String ck:cookies){
                                        newCk.add(ck);
                                    }
                                    ACache.get(MyApplication.getAppContext()).put(Constant.ACACHE_COOKIE,newCk);*/
                                    callBack.onSuccess(Obj);
                                } else {
                                    callBack.onFailure( ret.getMsg());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                callBack.onFailure("data error");
                            }
                        }
                    }
                });

            }
        });
    }

    private X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }

    private SSLSocketFactory systemDefaultSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }
}
