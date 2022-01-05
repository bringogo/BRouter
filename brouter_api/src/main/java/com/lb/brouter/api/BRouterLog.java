package com.lb.brouter.api;

import android.util.Log;

/**
 * <p>
 * Created by lb on 2022/01/01
 */
public class BRouterLog {
    private static final String TAG = "(BRouter)";

    public static void d(String tag, String log) {
        Log.d(TAG, tag + "  " + log);
    }

}
