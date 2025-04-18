/**
 * Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.chromium.chrome.browser.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.chromium.base.ContextUtils;
import org.chromium.base.Log;
import org.chromium.chrome.browser.BraveAdsNativeHelper;
import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.app.BraveActivity.BraveActivityNotFoundException;
import org.chromium.chrome.browser.notifications.BraveOnboardingNotification;
import org.chromium.chrome.browser.notifications.retention.RetentionNotificationUtil;
import org.chromium.chrome.browser.profiles.ProfileManager;
import org.chromium.misc_metrics.mojom.MiscAndroidMetrics;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides information regarding onboarding.
 */
public class OnboardingPrefManager {
    private static final String TAG = "OnboardingPrefMgr";

    private static final String PREF_ONBOARDING = "onboarding";
    private static final String PREF_P3A_ONBOARDING = "p3a_onboarding";
    private static final String PREF_ONBOARDING_V2 = "onboarding_v2";
    public static final String PREF_BRAVE_STATS = "brave_stats";
    public static final String PREF_BRAVE_STATS_NOTIFICATION = "brave_stats_notification";
    public static final String FROM_NOTIFICATION = "from_notification";
    public static final String FROM_STATS = "from_stats";
    public static final String ONE_TIME_NOTIFICATION = "one_time_notification";
    public static final String DORMANT_USERS_NOTIFICATION = "dormant_users_notification";
    public static final String SHOW_BADGE_ANIMATION = "show_badge_animation";
    public static final String PREF_DORMANT_USERS_ENGAGEMENT = "dormant_users_engagement";
    private static final String PREF_P3A_CRASH_REPORTING_MESSAGE_SHOWN =
            "p3a_crash_reporting_message_shown";
    private static final String PREF_NOTIFICATION_PERMISSION_ENABLING_DIALOG =
            "notification_permission_enabling_dialog";

    private static final String PREF_NOTIFICATION_PERMISSION_ENABLING_DIALOG_FROM_SETTING =
            "notification_permission_enabling_dialog_from_setting";

    public static final String SHOULD_SHOW_SEARCH_WIDGET_PROMO = "should_show_search_widget_promo";

    private static final String PREF_APP_LAUNCH_COUNT = "APP_LAUNCH_COUNT";

    private static OnboardingPrefManager sInstance;

    private final SharedPreferences mSharedPreferences;

    public static final int NEW_USER_ONBOARDING = 0;
    public static final int EXISTING_USER_REWARDS_OFF_ONBOARDING = 1;
    public static final int EXISTING_USER_REWARDS_ON_ONBOARDING = 2;

    private static boolean sIsOnboardingNotificationShown;

    public static boolean isNotification;

    public static final String BRAVE = "Brave";
    public static final String YANDEX = "Yandex";

    private OnboardingPrefManager() {
        mSharedPreferences = ContextUtils.getAppSharedPreferences();
    }

    /**
     * Returns the singleton instance of OnboardingPrefManager, creating it if needed.
     */
    public static OnboardingPrefManager getInstance() {
        if (sInstance == null) {
            sInstance = new OnboardingPrefManager();
        }
        return sInstance;
    }

    /**
     * Returns the user preference for whether the onboarding is shown.
     */
    public boolean isOnboardingShown() {
        return mSharedPreferences.getBoolean(PREF_ONBOARDING, false);
    }

    /**
     * Sets the user preference for whether the onboarding is shown.
     */
    public void setOnboardingShown(boolean isShown) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_ONBOARDING, isShown);
        sharedPreferencesEditor.apply();
    }

    public boolean isP3aCrashReportingMessageShown() {
        return mSharedPreferences.getBoolean(PREF_P3A_CRASH_REPORTING_MESSAGE_SHOWN, false);
    }

    public void setP3aCrashReportingMessageShown(boolean isShown) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_P3A_CRASH_REPORTING_MESSAGE_SHOWN, isShown);
        sharedPreferencesEditor.apply();
    }

    /**
     * Returns the user preference for whether the onboarding is shown.
     */
    public boolean isP3aOnboardingShown() {
        return mSharedPreferences.getBoolean(PREF_P3A_ONBOARDING, false);
    }

    /**
     * Sets the user preference for whether the onboarding is shown.
     */
    public void setP3aOnboardingShown(boolean isShown) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_P3A_ONBOARDING, isShown);
        sharedPreferencesEditor.apply();
    }

    /**
     * Returns the user preference for whether the onboarding is shown.
     */
    public boolean isNewOnboardingShown() {
        return mSharedPreferences.getBoolean(PREF_ONBOARDING_V2, false);
    }

    /**
     * Sets the user preference for whether the onboarding is shown.
     */
    public void setNewOnboardingShown(boolean isShown) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_ONBOARDING_V2, isShown);
        sharedPreferencesEditor.apply();
    }

    public boolean isBraveStatsEnabled() {
        return mSharedPreferences.getBoolean(PREF_BRAVE_STATS, false);
    }

    public void setBraveStatsEnabled(boolean enabled) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_BRAVE_STATS, enabled);
        sharedPreferencesEditor.apply();
        try {
            BraveActivity activity = BraveActivity.getBraveActivity();
            MiscAndroidMetrics miscAndroidMetrics = activity.getMiscAndroidMetrics();
            if (miscAndroidMetrics != null) {
                miscAndroidMetrics.recordPrivacyHubEnabledStatus(enabled);
            }
        } catch (BraveActivityNotFoundException e) {
            Log.e(TAG, "Could not report privacy hub enabled change to P3A: " + e);
        }
    }

    public boolean isBraveStatsNotificationEnabled() {
        return mSharedPreferences.getBoolean(PREF_BRAVE_STATS_NOTIFICATION, true);
    }

    public void setBraveStatsNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_BRAVE_STATS_NOTIFICATION, enabled);
        sharedPreferencesEditor.apply();
    }

    public boolean isAdsAvailable() {
        return BraveAdsNativeHelper.nativeIsSupportedRegion(
                ProfileManager.getLastUsedRegularProfile());
    }

    public void showOnboarding(Context context) {
        Intent intent = new Intent(context, OnboardingActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
    }

    public boolean isOnboardingNotificationShown() {
        return sIsOnboardingNotificationShown;
    }

    public void setOnboardingNotificationShown(boolean isShown) {
        sIsOnboardingNotificationShown = isShown;
    }

    public void onboardingNotification() {
        if (!isOnboardingNotificationShown()) {
            BraveOnboardingNotification.showOnboardingNotification();
            setOnboardingNotificationShown(true);
        }
    }

    public boolean isFromNotification() {
        return mSharedPreferences.getBoolean(FROM_NOTIFICATION, false);
    }

    public void setFromNotification(boolean isFromNotification) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(FROM_NOTIFICATION, isFromNotification);
        sharedPreferencesEditor.apply();
    }

    public boolean isOneTimeNotificationStarted() {
        return mSharedPreferences.getBoolean(ONE_TIME_NOTIFICATION, false);
    }

    public void setOneTimeNotificationStarted(boolean isOneTimeNotificationStarted) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(ONE_TIME_NOTIFICATION, isOneTimeNotificationStarted);
        sharedPreferencesEditor.apply();
    }

    public boolean isDormantUsersEngagementEnabled() {
        return mSharedPreferences.getBoolean(PREF_DORMANT_USERS_ENGAGEMENT, false);
    }

    public boolean isDormantUsersNotificationsStarted() {
        return mSharedPreferences.getBoolean(DORMANT_USERS_NOTIFICATION, false);
    }

    public void setDormantUsersNotificationsStarted(boolean isDormantUsersNotificationsStarted) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(
                DORMANT_USERS_NOTIFICATION, isDormantUsersNotificationsStarted);
        sharedPreferencesEditor.apply();
    }

    public long getDormantUsersNotificationTime(String notificationType) {
        return mSharedPreferences.getLong(notificationType, 0);
    }

    public void setDormantUsersNotificationTime(String notificationType, long timeInMilliseconds) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putLong(notificationType, timeInMilliseconds);
        sharedPreferencesEditor.apply();
    }

    public boolean shouldShowBadgeAnimation() {
        return mSharedPreferences.getBoolean(SHOW_BADGE_ANIMATION, true);
    }

    public void setShowBadgeAnimation(boolean shouldShowBadgeAnimation) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(SHOW_BADGE_ANIMATION, shouldShowBadgeAnimation);
        sharedPreferencesEditor.apply();
    }

    public void setDormantUsersPrefs() {
        setDormantUsersNotificationTime(
                RetentionNotificationUtil.DORMANT_USERS_DAY_14, setTimeInMillis(14 * 24 * 60));
        setDormantUsersNotificationTime(
                RetentionNotificationUtil.DORMANT_USERS_DAY_25, setTimeInMillis(25 * 24 * 60));
        setDormantUsersNotificationTime(
                RetentionNotificationUtil.DORMANT_USERS_DAY_40, setTimeInMillis(40 * 24 * 60));
    }

    private long setTimeInMillis(int timeInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, timeInMinutes);

        Date date = calendar.getTime();
        return date.getTime();
    }

    /**
     * Returns the user preference for whether the Notification Permission Enabling dialog is shown.
     */
    public boolean isNotificationPermissionEnablingDialogShown() {
        return mSharedPreferences.getBoolean(PREF_NOTIFICATION_PERMISSION_ENABLING_DIALOG, false);
    }

    /**
     * Sets the user preference for whether the Notification Permission Enabling dialog is shown.
     */
    public void setNotificationPermissionEnablingDialogShown(boolean isShown) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(PREF_NOTIFICATION_PERMISSION_ENABLING_DIALOG, isShown);
        sharedPreferencesEditor.apply();
    }

    /**
     * Returns the user preference for whether the Notification Permission Enabling dialog is shown
     * From setting.
     */
    public boolean isNotificationPermissionEnablingDialogShownFromSetting() {
        return mSharedPreferences.getBoolean(
                PREF_NOTIFICATION_PERMISSION_ENABLING_DIALOG_FROM_SETTING, false);
    }

    /**
     * Sets the user preference for whether the Notification Permission Enabling dialog is shown
     * From setting.
     */
    public void setNotificationPermissionEnablingDialogShownFromSetting(boolean isShown) {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(
                PREF_NOTIFICATION_PERMISSION_ENABLING_DIALOG_FROM_SETTING, isShown);
        sharedPreferencesEditor.apply();
    }

    /**
     * Returns the user preference for application launch count
     */
    public int launchCount() {
        return mSharedPreferences.getInt(PREF_APP_LAUNCH_COUNT, 0);
    }

    /**
     * Sets the user preference for application launch count
     */
    public void updateLaunchCount() {
        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putInt(PREF_APP_LAUNCH_COUNT, launchCount() + 1);
        sharedPreferencesEditor.apply();
    }
}
