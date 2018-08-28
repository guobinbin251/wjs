package com.android.wjs.utils;

/**
 * author：will on 2016/12/12 15:00
 * email：will.wang@ixdigit.com
 */

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * activity管理类
 * @author Administrator
 *
 */
public class AppActivities {
    @Nullable
    private Activity mCurrentActivity = null;// 当前活动 Activity
    @NonNull
    private static Stack<Activity> activityStack = new Stack<Activity>();
    @NonNull
    private static AppActivities singleton  = new AppActivities();

    private AppActivities() {
    }

    /**
     * 获取管理类对象
     * @return
     */
    @NonNull
    public static AppActivities getSingleton() {
        return singleton;
    }
    /**
     * 删除顶部activity
     */
    public synchronized void popActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }
    /**
     * 删除指定activity
     * @param activity
     */
    public synchronized void popActivity(@Nullable Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    public synchronized void removeActivity(@Nullable Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 设置当前活动的 Activity
     *
     * @param act 当前被激活的 Activity
     */
    public void setCurrentActivity(Activity act) {
        mCurrentActivity = act;
    }

    /**
     * 获取当前栈顶activity
     * @return
     */
    @Nullable
    public Activity currentActivity() {
        if(null != mCurrentActivity){
            return mCurrentActivity;
        }
        if (!activityStack.empty())
            mCurrentActivity = activityStack.lastElement();
        return mCurrentActivity;
    }

    /**
     * 获取栈中 Activity 的数量
     *
     */
    public static int getActivityStackSize() {
        int stackSize = 0;
        synchronized (AppActivities.class) {
            stackSize = activityStack.size();
        }
        return stackSize;
    }

    /**
     * 向栈中加入activity
     * @param activity
     */
    public synchronized void pushActivity(Activity activity) {
        if(activityStack.contains(activity)){
            activityStack.remove(activity);
        }

        activityStack.add(activity);
        System.out.println("当前栈里有："+activityStack.size()+" 个activity");
    }

    public synchronized boolean isStackActivity(Activity activity){
        return activityStack.contains(activity);
    }

    /**
     * 退出栈中所有 Activity到指定activity
     * @param act
     */
    public synchronized void popAllActivityExceptOne(Activity act) {
        Activity activity;
        int actCount = activityStack.size();
        for (int i = 0; i < actCount; i++) {
            activity = activityStack.get(actCount - i - 1);
            if (activity != null && activity != act)
                activity.finish();
        }
    }

    /**
     * 退出栈中所有 Activity
     */
    public synchronized static void finishAllActivities() {
        while (true) {
            if (activityStack.size() < 1) {
                return;
            }

            Activity curActivity = activityStack.lastElement();

            if (curActivity == null) {
                break;
            }

            curActivity.finish();
            activityStack.remove(curActivity);
        }
    }

    /**
     * 通知栈里面所有 activity 调用 method 方法
     *
     * @param method 字符串方法名
     * @param isLocalMethod true 表示获取本类所有成员方法; false 表示取该类中包括父类所有 public 方法
     */
    public static void noticeActivity(final String method, final boolean isLocalMethod) {
        noticeActivity(method, isLocalMethod, null, null);
    }

    /**
     * 通知栈里面所有 activity 调用 method 方法；带参数
     *
     * @param method 字符串方法名
     * @param isLocalMethod true 表示获取本类所有成员方法; false 表示取该类中包括父类所有 public 方法
     * @param parType 参数类型数组
     * @param obj 参数列表
     */
    public static void noticeActivity(final String method, final boolean isLocalMethod, final Class<?>[] parType, final Object[] obj) {
        new Thread() { // 启用新线程的目的是不影响当前业务的进行
            @Override
            public void run() {
                synchronized (AppActivities.class) {
                    for (final Activity activity : activityStack) {
                        try {
                            if (activity.isFinishing()) {
                                continue;
                            }

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (activity.isFinishing()) {
                                            return;
                                        }

                                        Class<? extends Activity> activityClass = activity.getClass();
                                        Method activityMethod;

                                        if (isLocalMethod) {
                                            // getDeclaredMethod得到所有权限的方法，getMethod只能获取到public类型的方法
                                            activityMethod = activityClass.getDeclaredMethod(method, parType);
                                        } else {
                                            // getMethod可以获取到包括父类中的public方法 替代以前的使用getSuperClass，这样避免不确定性因素
                                            activityMethod = activityClass.getMethod(method, parType);
                                        }

                                        // 设置该方法的访问权限
                                        activityMethod.setAccessible(true);

                                        // 调用该方法
                                        activityMethod.invoke(activity, obj);
                                    } catch (Exception e) {
                                        // 如果出错，不需要做处理，也不需要打印
                                    }
                                }
                            });
                        } catch (Exception e) {
                            // 如果出错，不需要做处理，也不需要打印
                        }
                    }
                }
            }
        }.start();
    }
}

