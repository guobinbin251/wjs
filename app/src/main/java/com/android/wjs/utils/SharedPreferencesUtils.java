package com.android.wjs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.wjs.ui.MyApplication;

/**
 * Created by Andy Guo on 2018/7/22.
 */
public class SharedPreferencesUtils {

    public static final String NAME = "Siloam_Sp";

    private SharedPreferences sp;

    private SharedPreferences.Editor editor;

    private static SharedPreferencesUtils instance = new SharedPreferencesUtils(MyApplication.getAppContext());

    public static SharedPreferencesUtils getInstance() {
        return instance;
    }

    private SharedPreferencesUtils(@NonNull Context context) {
        sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    /**
     * save cookie
     *
     * @param cookie
     */
    public void setCookie(String cookie) {
        editor.putString("cookie", cookie);
        editor.commit();
    }

    /**
     * get patient_user_number
     *
     * @return
     */
    public String getBirthdy() {
        return sp.getString("cookie", "");
    }
}
