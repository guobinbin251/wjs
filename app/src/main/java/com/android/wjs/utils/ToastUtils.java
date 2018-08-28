package com.android.wjs.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.wjs.ui.MyApplication;


/**
 * Created by Andy Guo on 2017/3/10.
 */

public class ToastUtils {

    public static boolean isShow = true;

    public static Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(final @Nullable CharSequence message) {
        if (isShow /*&& Looper.myLooper() == Looper.getMainLooper()*/ && message != null && !TextUtils.isEmpty(message)) {
            try {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(final int message) {
        if (isShow /*&& Looper.myLooper() == Looper.getMainLooper()*/) {
            try {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showDataError() {
        if (isShow /*&& Looper.myLooper() == Looper.getMainLooper()*/) {
            try {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getAppContext(), "data error", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, @Nullable CharSequence message) {
        if (isShow && message != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, @Nullable CharSequence message, int duration) {
        if (isShow && message != null)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }


}
