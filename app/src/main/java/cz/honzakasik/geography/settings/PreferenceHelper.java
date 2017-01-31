package cz.honzakasik.geography.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import cz.honzakasik.geography.common.users.User;
import cz.honzakasik.geography.common.users.PreferencesConstants;

/**
 * Helper for obtaining set values from default SharedPreferences. This class should be always used
 * when accessing those preferences
 */
public class PreferenceHelper {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceHelper.class);

    private static final String DEFAULT_USER_KEY = PreferencesConstants.DEFAULT_USER_KEY;

    /**
     * Set source context for obtaining preferences
     */
    public static PreferenceHelperWithContext with(Context context) {
        return new PreferenceHelperWithContext(context);
    }

    public static class PreferenceHelperWithContext {

        private SharedPreferences preferenceManager;

        private PreferenceHelperWithContext(Context context) {
            this.preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        }

        public boolean isUserManagementEnabled() {
            return preferenceManager.getBoolean(PreferencesConstants.USERS_ENABLED_KEY, false);
        }

        /**
         * Whether the default user is selected.
         * @return true if default user is selected
         */
        public boolean isDefaultUserSelected() {
            return preferenceManager.getString(PreferencesConstants.DEFAULT_USER_KEY, null) != null;
        }

        public void setDefaultUser(@NonNull User user) {
            preferenceManager.edit()
                    .putString(DEFAULT_USER_KEY, String.valueOf(user.getUserId()))
                    .apply();
            logger.info("Default user is now user with id '{}'.", user.getUserId());
        }

        public Integer getDefaultUserId() {
            return Integer.valueOf(preferenceManager.getString(DEFAULT_USER_KEY, "-1"));
        }

        public boolean isOverridingLanguageSelected() {
            return preferenceManager.getString(PreferencesConstants.OVERRIDING_LANGUAGE_KEY, null) != null;
        }

        public Locale getOverridingLanguage() {
            String languageTag = preferenceManager.getString(PreferencesConstants.OVERRIDING_LANGUAGE_KEY, null);
            return new Locale(languageTag);
        }
    }

}
