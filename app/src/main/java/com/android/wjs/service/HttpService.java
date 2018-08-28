package com.android.wjs.service;

import android.text.TextUtils;

import com.android.wjs.common.Constant;
import com.android.wjs.http.HttpUtils;
import com.android.wjs.http.callback.HttpCallBack;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Andy Guo on 2018/6/7.
 * 跳转页面
 */
public class HttpService {

    private static final String LOGIN_URL = "/member/login";
    private static final String REGISTER_URL = "/member/reg";

    public static void login(String account, String passwd, HttpCallBack<Object> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", passwd);
        HttpUtils.post(Constant.API_DOMAIN + LOGIN_URL, params, listener);
    }

    public static void register(String account, String passwd, String invite, HttpCallBack<Object> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", passwd);
        if (!TextUtils.isEmpty(invite)) {
            params.put("inviteCode", invite);
        }

        HttpUtils.post(Constant.API_DOMAIN + REGISTER_URL, params, listener);
    }

}
