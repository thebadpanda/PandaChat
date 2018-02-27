package com.example.arsenko.chatonfirebase;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.squareup.leakcanary.RefWatcher;

public class ChatUI extends MultiDexApplication {
    private RefWatcher mRefWatcher;

//    static {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
//    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((ChatUI) context.getApplicationContext()).mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}