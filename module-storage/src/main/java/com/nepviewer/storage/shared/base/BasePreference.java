package com.nepviewer.storage.shared.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nepviewer.storage.shared.base.impl.ISharePreferences;

import java.util.Set;

/**
 * FileName: BasePreference
 *
 * @author: mosaic
 * Date: 2023/4/6 08:19
 * Description:
 * Version:
 */
public class BasePreference implements ISharePreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public BasePreference(Context context, String name) {
        this.sharedPreferences = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        return editor.putString(key, value);
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        return editor.putStringSet(key, values);
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        return editor.putInt(key, value);
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        return editor.putLong(key, value);
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        return editor.putFloat(key, value);
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        return editor.putBoolean(key, value);
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        return editor.remove(key);
    }

    @Override
    public SharedPreferences.Editor clear() {
        return editor.clear();
    }

    @Override
    public boolean commit() {
        return editor.commit();
    }

    @Override
    public void apply() {
        editor.apply();
    }

    @Override
    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue == null ? "" : defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        // 暂未使用
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        // 暂未使用
    }
}
