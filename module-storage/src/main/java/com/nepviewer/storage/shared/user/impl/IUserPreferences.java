package com.nepviewer.storage.shared.user.impl;

/**
 * FileName: IUserPreferences
 *
 * @author: mosaic
 * Date: 2023/4/6 08:24
 * Description:
 * Version:
 */
public interface IUserPreferences {
    /**
     * 获取用户登录账号
     *
     * @return 字符串
     */
    String getUserAccount();

    /**
     * 保存用户登录账号
     *
     * @param value 账号
     */
    void sutUserAccount(String value);

    /**
     * 获取用户登录成功的Token
     *
     * @return 字符串
     */
    String getUserToken();

    /**
     * 保存用户登录成功的Token
     *
     * @param value token
     */
    void sutUserToken(String value);


    /**
     * 获取用户设置的语言
     *
     * @return 字符串
     */
    String getUserAppLanguage();

    /**
     * 保存用户设置的语言
     *
     * @param value 语言类型，zh
     */
    void setUserAppLanguage(String value);

    /**
     * 获取系统语言
     *
     * @return 字符串
     */
    String getSystemLanguage();

    /**
     * 保存系统语言
     *
     * @param value 语言类型，zh
     */
    void setSystemLanguage(String value);

    /**
     * 获取时区数据
     *
     * @return 字符串
     */
    String getTimezone();

    /**
     * 设置时区数据
     *
     * @param value
     */
    void setTimezone(String value);

    /**
     * 获取币种数据
     *
     * @return 字符串
     */
    String getCurrency();

    /**
     * 设置币种数据
     *
     * @param value
     */
    void setCurrency(String value);

    /**
     * 获取温度单位数据
     *
     * @return 字符串
     */
    String getTemperatureUnit();

    /**
     * 设置温度单位数据
     *
     * @param value
     */
    void setTemperatureUnit(String value);

    /**
     * 获取用户信息数据
     *
     * @return 字符串
     */
    String getUserInfo();

    /**
     * 设置用户信息数据
     *
     * @param value
     */
    void setUserInfo(String value);

    /**
     * 获取用户类型数据
     *
     * @return 字符串
     */
    int getUserType();

    /**
     * 设置用户类型数据
     *
     * @param value
     */
    void setUserType(int value);

    /**
     * 获取配置的HZ
     *
     * @return 字符串
     */
    int getConfigHz();

    /**
     * 设置配置的HZ
     *
     * @param value
     */
    void setConfigHz(int value);

    /**
     * 获取记住的账号
     *
     * @return 字符串
     */
    String getLoginEmail();

    /**
     * 设置记住的账号
     *
     * @param value
     */
    void setLoginEmail(String value);

    /**
     * 获取记住的密码
     *
     * @return 字符串
     */
    String getLoginPassword();

    /**
     * 设置记住的密码
     *
     * @param value
     */
    void setLoginPassword(String value);

    /**
     * 获取打开APP的日期
     *
     * @return 字符串
     */
    String getUseDate();

    /**
     * 设置打开APP的日期
     *
     * @param value
     */
    void setUseDate(String value);

    /**
     * 读取上传硬件参数设置
     *
     * @return 字符串 需要list解析
     */
    String getUploadSettings();

    /**
     * 设置上传硬件参数设置
     *
     * @param value
     */
    void setUploadSettings(String value);

    /**
     * 读取wifi密码
     *
     * @return 字符串
     */
    String getWifiPassword();

    /**
     * 设置wifi密码
     *
     * @param value
     */
    void setWifiPassword(String value);

    /**
     * 当前wifi名称
     *
     * @return 字符串
     */
    String getWifiName();

    /**
     * 设置wifi名称
     *
     * @param value
     */
    void setWifiName(String value);

    /**
     * 获取引导第几步
     *
     * @return 字符串
     */
    int getGuide();

    /**
     * 设置引导第几步
     *
     * @param value
     */
    void setGuide(int value);

    /**
     * 缓存wifi列表
     *
     * @return 字符串
     */
    String getWifiList();

    /**
     * 读取wifi列表缓存
     *
     * @param value
     */
    void setWifiList(String value);


    String USER_ACCOUNT = "userAccount";
    String USER_TOKEN = "userToken";
    String USER_APP_LANGUAGE = "userAppLanguage";
    String USER_SYSTEM_LANGUAGE = "systemAppLanguage";
    String USER_TIMEZONE = "Timezone";
    String USER_CURRENCY = "Currency";
    String USER_TEMPERATURE_UNIT = "temperatureUnit";
    String USER_INFO = "userInfo";
    String USER_TYPE = "userType";
    String CONFIG_HZ = "configHz";
    String LOGIN_EMAIL = "loginEmail";
    String LOGIN_PASSWORD = "loginPassword";
    String USE_DATE = "useDate";
    String USE_UPLOAD_SETTINGS = "uploadSettings";
    String WIFI_NAME = "wifiName";
    String WIFI_PASSWORD = "wifiPassword";
    String GUIDE = "guide"; // 引导字段
    String AP_WIFI_LIST = "wifiList"; // AP 读取wifi列表缓存
}
