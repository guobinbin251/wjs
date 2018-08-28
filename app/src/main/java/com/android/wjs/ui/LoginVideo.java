package com.android.wjs.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Andy Guo on 2018/8/22.
 */

public class LoginVideo extends VideoView {
    public LoginVideo(Context context) {
        super(context);
    }

    public LoginVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //从新写入高度
        int width = getDefaultSize(0,widthMeasureSpec);
        int height = getDefaultSize(0,heightMeasureSpec);
        //设置测量尺寸,将高和宽放进去
        setMeasuredDimension(width, height);
    }
}
