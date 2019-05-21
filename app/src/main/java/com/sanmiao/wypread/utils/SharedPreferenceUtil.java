package com.sanmiao.wypread.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/2 0002.
 * 类说明{}
 */
public class SharedPreferenceUtil {
    public static SharedPreferences preference;
    public static void init(Context context , String name){
        preference = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
    public static void SaveData(String name, Object data){
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(name, String.valueOf(data));
        editor.commit();
    }
    public static boolean getBooleanData(String name){
       return Boolean.valueOf(preference.getString(name,"false"));
    }
    public static String getStringData(String name){
        return preference.getString(name,"");
    }
    public static int getIntData(String name){
        return Integer.valueOf(preference.getString(name,"0"));
    }
    public static double getDoubleData(String name){
        return Double.valueOf(preference.getString(name,"0"));
    }
    public static long getLongData(String name){
        return Long.valueOf(preference.getString(name,"0"));
    }
}
