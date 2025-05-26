package club.ccit.storage.shared.user;

import android.content.Context;

import club.ccit.storage.shared.base.ApplicationContext;
import club.ccit.storage.shared.user.model.UserPreferencesModel;

/**
 * FileName: UserPreferenceManager

 * author:  mosaic
 * Date: 2023/4/6 08:23
 * Description:
 * Version:
 */
public class UserPreferenceManager implements ApplicationContext {
    private static volatile UserPreferenceManager instance;
    private static UserPreferencesModel userPreferences;
    /**
     * SharedPreferences 存储的名称
     */
    private static final String SHARED_PREFERENCES_NAME = "user";

    /**
     * 单例获取 UserPreferenceManager
     *
     * @return UserPreferenceManager
     */
    public static UserPreferenceManager getInstance() {
        if (instance == null) {
            synchronized (UserPreferenceManager.class) {
                if (instance == null) {
                    instance = new UserPreferenceManager();
                    userPreferences = UserPreferenceManager.getInstance().setUserPreferences(new UserPreferencesModel(ApplicationContext.getContext(), SHARED_PREFERENCES_NAME));
                }
            }
        }
        return instance;
    }

    public static UserPreferenceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserPreferenceManager.class) {
                if (instance == null) {
                    instance = new UserPreferenceManager();
                    userPreferences = UserPreferenceManager.getInstance().setUserPreferences(new UserPreferencesModel(context, SHARED_PREFERENCES_NAME));
                }
            }
        }
        return instance;
    }


    /**
     * 获取 UserPreferencesModel 数据实体类
     *
     * @return
     */
    public UserPreferencesModel getUserPreferencesModel() {
        if (instance == null) {
            getInstance();
        }
        return userPreferences;
    }

    private UserPreferencesModel setUserPreferences(UserPreferencesModel user) {
        return userPreferences = user;
    }

}
