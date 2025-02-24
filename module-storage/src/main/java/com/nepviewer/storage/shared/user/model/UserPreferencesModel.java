package com.nepviewer.storage.shared.user.model;

import android.content.Context;

import com.nepviewer.storage.shared.base.BasePreference;
import com.nepviewer.storage.shared.user.impl.IUserPreferences;

import java.util.TimeZone;

/**
 * FileName: UserPreferencesModel
 *
 * @author: mosaic
 * Date: 2023/4/6 08:25
 * Description:
 * Version:
 */
public class UserPreferencesModel extends BasePreference implements IUserPreferences {

    public UserPreferencesModel(Context context, String name) {
        super(context, name);
    }

    @Override
    public String getUserAccount() {
        return getString(USER_ACCOUNT, "");
    }

    @Override
    public void sutUserAccount(String value) {
        putString(USER_ACCOUNT, value).apply();
    }

    @Override
    public String getUserToken() {
        return getString(USER_TOKEN, "");
    }

    @Override
    public void sutUserToken(String value) {
        putString(USER_TOKEN, value).apply();
    }

    @Override
    public String getUserAppLanguage() {
        return getString(USER_APP_LANGUAGE, "auto");
    }

    @Override
    public void setUserAppLanguage(String value) {
        putString(USER_APP_LANGUAGE, value).apply();
    }

    @Override
    public String getSystemLanguage() {
        return getString(USER_SYSTEM_LANGUAGE, "en");
    }

    @Override
    public void setSystemLanguage(String value) {
        putString(USER_SYSTEM_LANGUAGE, value).apply();
    }

    @Override
    public String getTimezone() {
        return getString(USER_TIMEZONE, TimeZone.getDefault().getID());
    }

    @Override
    public void setTimezone(String value) {
        putString(USER_TIMEZONE, value).apply();
    }

    @Override
    public String getCurrency() {
        return getString(USER_CURRENCY, "USD");
    }

    @Override
    public void setCurrency(String value) {
        putString(USER_CURRENCY, value).apply();
    }

    @Override
    public String getTemperatureUnit() {
        return getString(USER_TEMPERATURE_UNIT, "1");
    }

    @Override
    public void setTemperatureUnit(String value) {
        putString(USER_TEMPERATURE_UNIT, value).apply();
    }

    @Override
    public String getUserInfo() {
        return getString(USER_INFO, "");
    }

    @Override
    public void setUserInfo(String value) {
        putString(USER_INFO, value).apply();
    }

    @Override
    public int getUserType() {
        return getInt(USER_TYPE, 6);
    }

    @Override
    public void setUserType(int value) {
        putInt(USER_TYPE, value).apply();
    }

    @Override
    public int getConfigHz() {
        return getInt(CONFIG_HZ, 0);
    }

    @Override
    public void setConfigHz(int value) {
        putInt(CONFIG_HZ, value).apply();
    }

    @Override
    public String getLoginEmail() {
        return getString(LOGIN_EMAIL, "");
    }

    @Override
    public void setLoginEmail(String value) {
        putString(LOGIN_EMAIL, value).apply();
    }

    @Override
    public String getLoginPassword() {
        return getString(LOGIN_PASSWORD, "");
    }

    @Override
    public void setLoginPassword(String value) {
        putString(LOGIN_PASSWORD, value).apply();
    }

    @Override
    public String getUseDate() {
        return getString(USE_DATE, "");
    }

    @Override
    public void setUseDate(String value) {
        putString(USE_DATE, value).apply();
    }

    @Override
    public String getUploadSettings() {
        return getString(USE_UPLOAD_SETTINGS, "");
    }

    @Override
    public void setUploadSettings(String value) {
        putString(USE_UPLOAD_SETTINGS, value).apply();
    }

    @Override
    public String getWifiPassword() {
        return getString(WIFI_PASSWORD, "");
    }

    @Override
    public void setWifiPassword(String value) {
        putString(WIFI_PASSWORD, value).apply();
    }

    @Override
    public String getWifiName() {
        return getString(WIFI_NAME, "");
    }

    @Override
    public void setWifiName(String value) {
        putString(WIFI_NAME, value).apply();
    }

    @Override
    public int getGuide() {
        return getInt(GUIDE, 1);
    }

    @Override
    public void setGuide(int value) {
        putInt(GUIDE, value).apply();
    }

    /**
     * 获取AP wifi列表
     * @return
     */
    @Override
    public String getWifiList() {
        return getString(AP_WIFI_LIST, "");
    }

    /**
     * 缓存AP wifi列表
     * @param value
     */
    @Override
    public void setWifiList(String value) {
        putString(AP_WIFI_LIST, value).apply();
    }
}
