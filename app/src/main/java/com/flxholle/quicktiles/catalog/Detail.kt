package com.flxholle.quicktiles.catalog

import com.flxholle.quicktiles.utils.SharedPreferencesUtil

enum class CustomSlot(val packageKey: String, val labelKey: String) {
    One(SharedPreferencesUtil.CUSTOM_PACKAGE_ONE, SharedPreferencesUtil.CUSTOM_PACKAGE_ONE_LABEL),
    Two(SharedPreferencesUtil.CUSTOM_PACKAGE_TWO, SharedPreferencesUtil.CUSTOM_PACKAGE_TWO_LABEL),
    Three(SharedPreferencesUtil.CUSTOM_PACKAGE_THREE, SharedPreferencesUtil.CUSTOM_PACKAGE_THREE_LABEL),
    Four(SharedPreferencesUtil.CUSTOM_PACKAGE_FOUR, SharedPreferencesUtil.CUSTOM_PACKAGE_FOUR_LABEL),
    Five(SharedPreferencesUtil.CUSTOM_PACKAGE_FIVE, SharedPreferencesUtil.CUSTOM_PACKAGE_FIVE_LABEL),
}

sealed interface Detail {
    data object None : Detail
    data object Counter : Detail
    data object KeepScreen : Detail
    data class CustomApp(val slot: CustomSlot) : Detail
}

enum class KeepScreenMode(val stored: String) {
    Ac("0"),
    Usb("1"),
    Wireless("2"),
    Any("3"),
    ;

    companion object {
        fun parse(stored: String?): KeepScreenMode? = entries.firstOrNull { it.stored == stored }
    }
}
