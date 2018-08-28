package com.android.wjs.ui.statusbar;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.android.wjs.R;
import com.android.wjs.ui.statusbar.impl.AndroidMHelper;
import com.android.wjs.ui.statusbar.impl.FlymeHelper;
import com.android.wjs.ui.statusbar.impl.MIUIHelper;
import com.android.wjs.ui.statusbar.impl.StatusBarFontHelper;
import com.android.wjs.ui.statusbar.impl.StatusBarUtil;


/************************************************
 * Created by Beck on 17/6/19 14:03
 * mailto:beck.zhang@ixdigit.com
 ************************************************/

public class SkinStatusBarUtil {

    /**
     * 基类修改状态栏
     *
     * @param activity
     */
    public static void updateStatusBar(Activity activity) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (new MIUIHelper().setStatusBarLightMode(activity, false)) {
                    StatusBarUtil.setColor(activity, ContextCompat.getColor(activity, R.color.color_status_bar), 0);
                    StatusBarFontHelper.setStatusBarMode(activity, false);
                } else if (new FlymeHelper().setStatusBarLightMode(activity, false)) {
                    StatusBarUtil.setColor(activity, ContextCompat.getColor(activity, R.color.color_status_bar), 0);
                    StatusBarFontHelper.setStatusBarMode(activity, false);
                } else if (new AndroidMHelper().setStatusBarLightMode(activity, false)) {
                    StatusBarUtil.setColor(activity, ContextCompat.getColor(activity, R.color.color_status_bar), 0);
                    StatusBarFontHelper.setStatusBarMode(activity, false);
                } else {
                    StatusBarUtil.setColor(activity, ContextCompat.getColor(activity, R.color.color_status_bar), 0);
                    StatusBarFontHelper.setStatusBarMode(activity, false);
                }
            }
        } catch (Resources.NotFoundException e) {

        }
    }
}
