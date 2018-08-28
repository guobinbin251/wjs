package com.android.wjs.http.callback;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Andy Guo on 2016/12/22.
 *
 * 网络回调接口
 */
public abstract class HttpCallBack<T> {

    /**
     * type用于方便JSON的解析
     */
    public Type mType;    /**
     * 把type转换成对应的类，这里不用看明白也行。
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }    /**
     * 构造的时候获得type的class
     */
    public HttpCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    public abstract void onSuccess(T data);

    /**
     * 请求错误message,为空说明网络错误
     * @param errorMsg
     */
    public abstract void onFailure(String errorMsg);
}
