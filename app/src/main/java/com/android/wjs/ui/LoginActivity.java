package com.android.wjs.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wjs.R;
import com.android.wjs.common.Constant;
import com.android.wjs.http.callback.HttpCallBack;
import com.android.wjs.service.HttpService;
import com.android.wjs.ui.statusbar.SkinStatusBarUtil;
import com.android.wjs.utils.ACache;
import com.android.wjs.utils.AppActivities;
import com.android.wjs.utils.ToastUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private LoginVideo myVideo;
    private Button btnLogin;
    private EditText etAccount;
    private EditText etPasswd;
    private LinearLayout llRegister;
    private LinearLayout llLogin;

    private EditText etRegisterAccount;
    private EditText etRegisterPasswd;
    private EditText etRegisterPasswdRe;
    private EditText etRegisterInvite;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        llRegister = findViewById(R.id.ll_register);
        llLogin = findViewById(R.id.ll_login);
        myVideo = findViewById(R.id.loginvideo);

        etAccount = findViewById(R.id.et_account);
        etPasswd = findViewById(R.id.et_passwd);


        etRegisterAccount = findViewById(R.id.et_register_account);
        etRegisterPasswd = findViewById(R.id.et_register_passwd);
        etRegisterPasswdRe = findViewById(R.id.et_register_passwd_re);
        etRegisterInvite = findViewById(R.id.et_register_invite);

        findViewById(R.id.tv_forget_password).setOnClickListener(this);
        findViewById(R.id.tv_go_register).setOnClickListener(this);
        findViewById(R.id.tv_go_login).setOnClickListener(this);
        findViewById(R.id.tv_visiter).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

        initView();
        String cacheAccount = ACache.get(MyApplication.getAppContext()).getAsString(Constant.ACACHE_ACCOUNT);
        if (!TextUtils.isEmpty(cacheAccount)) {
            etAccount.setText(cacheAccount);
            llRegister.setVisibility(View.GONE);
            llLogin.setVisibility(View.VISIBLE);
        }else{
            llRegister.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SkinStatusBarUtil.updateStatusBar(LoginActivity.this);
        }

    }

    public void initView() {
        //播放路径
        myVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_video));
        //播放
        myVideo.start();
        //循环播放
        myVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                myVideo.start();
            }
        });
    }

    @Override
    protected void onRestart() {
        //返回重新加载
        initView();
        super.onRestart();
    }


    @Override
    protected void onStop() {
        //防止锁屏或者弹出的时候，音乐在播放
        myVideo.stopPlayback();
        super.onStop();
    }

    private void login(final String account, final String passwd) {
        showLoadingDialog();
        HttpService.login(account, passwd, new HttpCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                dismissLoadingDialog();
                AppActivities.finishAllActivities();
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                String url = Constant.API_DOMAIN + "/front/home?platform=android&username=" + account + "&password=" + passwd;
                intent.putExtra(Constant.EXTRA_URL, url);
                startActivity(intent);
                LoginActivity.this.finish();
                ACache.get(MyApplication.getAppContext()).put(Constant.ACACHE_ACCOUNT, account);
            }

            @Override
            public void onFailure(String errorMsg) {
                dismissLoadingDialog();
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    private void register(final String account, final String passwd, String inviteCode) {
        showLoadingDialog();
        HttpService.register(account, passwd, inviteCode, new HttpCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                HttpService.login(account, passwd, new HttpCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        dismissLoadingDialog();
                        AppActivities.finishAllActivities();
                        Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                        String url = Constant.API_DOMAIN + "/front/home?platform=android&username=" + account + "&password=" + passwd;
                        intent.putExtra(Constant.EXTRA_URL, url);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        ACache.get(MyApplication.getAppContext()).put(Constant.ACACHE_ACCOUNT, account);
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        dismissLoadingDialog();
                        ToastUtils.showShort(errorMsg);
                    }
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                dismissLoadingDialog();
                ToastUtils.showShort(errorMsg);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String account = etAccount.getText().toString();
                String passwd = etPasswd.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    ToastUtils.showShort(getString(R.string.plz_input_account));
                    return;
                } else if (TextUtils.isEmpty(passwd)) {
                    ToastUtils.showShort(getString(R.string.plz_input_passwd));
                    return;
                } else {
                    login(account, passwd);
                }
                break;
            case R.id.btn_register:
                String registerAccount = etRegisterAccount.getText().toString();
                String registerPasswd = etRegisterPasswd.getText().toString();
                String registerPasswdRe = etRegisterPasswdRe.getText().toString();

                if (TextUtils.isEmpty(registerAccount)) {
                    ToastUtils.showShort(getString(R.string.plz_input_account));
                    return;
                } else if (TextUtils.isEmpty(registerPasswd)) {
                    ToastUtils.showShort(getString(R.string.plz_input_passwd));
                    return;
                } else if(!registerPasswd.equals(registerPasswdRe)){
                    ToastUtils.showShort(getString(R.string.password_not_same));
                }else {
                    register(registerAccount, registerPasswd, etRegisterInvite.getText().toString());
                }
                break;
            case R.id.tv_forget_password:
                Intent intentForget = new Intent(LoginActivity.this, WebActivity.class);
                String url = Constant.API_DOMAIN + "/front/forgotPassword?platform=android";
                intentForget.putExtra(Constant.EXTRA_URL, url);
                intentForget.putExtra(Constant.EXTRA_TYPE, "find_password");
                startActivity(intentForget);
                break;
            case R.id.tv_go_register:
                llRegister.setVisibility(View.VISIBLE);
                llLogin.setVisibility(View.GONE);
                break;
            case R.id.tv_go_login:
                llRegister.setVisibility(View.GONE);
                llLogin.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_visiter:
                AppActivities.finishAllActivities();
                Intent intenVisit = new Intent(LoginActivity.this, WebActivity.class);
                String urlVisit = Constant.API_DOMAIN + "/front/home?platform=android";
                intenVisit.putExtra(Constant.EXTRA_URL, urlVisit);
                startActivity(intenVisit);
                LoginActivity.this.finish();
                break;
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:

                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    ToastUtils.showShort(getString(R.string.double_click_quite));
                    firstTime = secondTime;
                    return true;
                } else {
                    //System.exit(0);
//                        logout();
                    LoginActivity.this.finish();
                }

                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    public void showLoadingDialog() {
        if (alertDialog != null && !alertDialog.isShowing()) {
            return;
        }
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).create();
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(true);
        /*alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });*/
        alertDialog.show();
        alertDialog.setContentView(R.layout.dialog_loading);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
