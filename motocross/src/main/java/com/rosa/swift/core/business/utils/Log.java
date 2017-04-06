package com.rosa.swift.core.business.utils;

import android.text.TextUtils;

/**
 * android.util.Log Wrapper
 * Based on code from http://habrahabr.ru/post/116376/
 */
public final class Log {

    private static String TAG = "Motocross";

    /**
     * VERBOSE = 2, DEBUG = 3, INFO = 4, WARN = 5, ERROR = 6, ASSERT = 7;
     * See android.util.Log class
     */
    private static int logLevel = 0;


    public static void v(String msg) {
        if (logLevel <= android.util.Log.VERBOSE)
            android.util.Log.v(TAG, getLocation() + msg);
    }

    public static void v(String msg, Throwable tr) {
        if (logLevel <= android.util.Log.VERBOSE)
            android.util.Log.v(TAG, getLocation() + msg, tr);
    }

    public static void i(String msg) {
        if (logLevel <= android.util.Log.INFO)
            android.util.Log.i(TAG, getLocation() + msg);
    }

    public static void i(String msg, Throwable tr) {
        if (logLevel <= android.util.Log.INFO)
            android.util.Log.i(TAG, getLocation() + msg, tr);
    }

    public static void d(String msg) {
        if (logLevel <= android.util.Log.DEBUG)
            android.util.Log.d(TAG, getLocation() + msg);
    }

    public static void d(String msg, Throwable tr) {
        if (logLevel <= android.util.Log.DEBUG)
            android.util.Log.d(TAG, getLocation() + msg, tr);
    }

    public static void w(String msg) {
        if (logLevel <= android.util.Log.WARN)
            android.util.Log.w(TAG, getLocation() + msg);
    }

    public static void w(String msg, Throwable tr) {
        if (logLevel <= android.util.Log.DEBUG)
            android.util.Log.w(TAG, getLocation() + msg, tr);
    }

    public static void e(String msg) {
        if (logLevel <= android.util.Log.ERROR)
            android.util.Log.e(TAG, getLocation() + msg);
    }

    public static void e(Throwable tr) {
        e(tr.getMessage(), tr);
    }

    public static void e(String msg, Throwable tr) {
        if (logLevel <= android.util.Log.ERROR)
            android.util.Log.e(TAG, getLocation() + msg, tr);
    }

    public static void wtf(String msg) {
        if (logLevel <= android.util.Log.ASSERT)
            android.util.Log.e(TAG, getLocation() + msg);
    }

//    public static void wtf(String msg, Throwable tr) {
//        if (logLevel <= android.util.Log.ASSERT)
//            android.util.Log.wtf(TAG, getLocation() + msg, tr);
//    }

    private static String getLocation() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        Log.TAG = TAG;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        Log.logLevel = logLevel;
    }

}