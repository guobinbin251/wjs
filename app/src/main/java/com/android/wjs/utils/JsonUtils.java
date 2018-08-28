package com.android.wjs.utils;

import com.android.wjs.common.Constant;
import com.android.wjs.exception.JsonErrorException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by Andy Guo on 2017/5/15.
 * Json 解析工具
 */

public class JsonUtils {
    /**
     * Json字符串转实体
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T fromJson(String json, Class<T> classOfT)  throws JsonErrorException {
        try{
            Gson gson = new Gson();
            return gson.fromJson(json,classOfT);
        }catch (JsonSyntaxException ex){
            throw new JsonErrorException(ex.getMessage(), Constant.RET_DATA_CODE);
        }

    }

    public static <T> T fromJson(String json, Type typeOfT)  throws JsonErrorException {
        try{
            Gson gson = new Gson();
            return gson.fromJson(json,typeOfT);
        }catch (JsonSyntaxException ex){
            throw new JsonErrorException(ex.getMessage(), Constant.RET_DATA_CODE);
        }

    }


    /**
     * 实体转json
     * @param src
     * @return
     */
    public static String toJson(Object src){
        Gson gson = new Gson();
        return gson.toJson(src);
    }
}
