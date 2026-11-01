package com.flxholle.quicktiles.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.preference.PreferenceManager
import com.flxholle.quicktiles.R
import com.flxholle.quicktiles.catalog.CustomApps
import com.flxholle.quicktiles.catalog.CustomSlot
import com.flxholle.quicktiles.catalog.Detail
import com.flxholle.quicktiles.catalog.DeviceGrants
import com.flxholle.quicktiles.catalog.GrantNeed
import com.flxholle.quicktiles.catalog.KeepScreenMode
import com.flxholle.quicktiles.catalog.Publication
import com.flxholle.quicktiles.catalog.PublishResult
import com.flxholle.quicktiles.catalog.StoredApp
import com.flxholle.quicktiles.catalog.Tile
import com.flxholle.quicktiles.catalog.missingNeeds
import com.flxholle.quicktiles.catalog.shelfAllowsSet
import com.flxholle.quicktiles.utils.SharedPreferencesUtil

sealed interface ToggleOutcome {
    data object Updated : ToggleOutcome
    data object Refused : ToggleOutcome
    data class NeedsGrant(val tile: Tile) : ToggleOutcome
    data class NeedsApp(val tile: Tile) : ToggleOutcome
}

class DeviceActions(private val context: Context) {
    fun loadShelf(query: String): ShelfModel = try {
        assembleShelf(
            values = PreferenceManager.getDefaultSharedPreferences(context).all,
            query = query,
            sdk = Build.VERSION.SDK_INT,
            titleOf = { context.getString(tileTitleRes(it)) },
            sectionTitle = { context.getString(sectionTitleRes(it)) },
            iconOf = ::tileIconRes,
            customLabel = { slot -> CustomApps.read(context, slot)?.label },
            turnOffNote = context.getString(R.string.turn_off_only),
            noAppNote = context.getString(R.string.no_app_chosen),
        )
    } catch (_: Exception) {
        ShelfModel.Failed
    }

    fun loadDetail(tile: Tile): DetailModel? {
        val title = context.getString(tileTitleRes(tile))
        return when (val detail = tile.detail) {
            Detail.None -> null
            Detail.Counter -> DetailModel.Counter(title, SharedPreferencesUtil.getCounter(context))
            Detail.KeepScreen -> DetailModel.KeepScreen(
                title = title,
                mode = keepMode(),
                choices = keepChoices(),
            )
            is Detail.CustomApp -> customDetail(title, detail.slot, tile)
        }
    }

    fun loadGrants(tileName: String?): GrantsModel {
        if (tileName.isNullOrBlank()) return GrantsModel.Unknown
        val tile = Tile.fromRoute(tileName) ?: return GrantsModel.Unknown
        val title = context.getString(tileTitleRes(tile))
        val steps = missingNeeds(tile.gate, DeviceGrants.held(context)).map { need ->
            GrantStepModel(need, context.getString(stepTitle(need)), stepBody(need))
        }
        return if (steps.isEmpty()) GrantsModel.Clear(title) else GrantsModel.Missing(title, steps)
    }

    fun loadAbout(): AboutModel = try {
        val info = if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        val version = info.versionName?.takeIf { it.isNotBlank() } ?: return AboutModel.Failed
        AboutModel.Ready(version)
    } catch (_: Exception) {
        AboutModel.Failed
    }

    fun loadPicker(): PickerModel = try {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        }
        // System apps that expose a launcher stay in the list.
        val apps = resolved.mapNotNull { info ->
            val packageName = info.activityInfo?.packageName ?: return@mapNotNull null
            val label = info.loadLabel(context.packageManager)?.toString()?.trim().orEmpty()
            LauncherApp(packageName, label.ifEmpty { packageName })
        }.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }
        if (apps.isEmpty()) PickerModel.Empty else PickerModel.Ready(apps)
    } catch (_: Exception) {
        PickerModel.Failed
    }

    fun isOn(tile: Tile): Boolean = Publication.isOn(context, tile)

    fun toggle(tile: Tile, on: Boolean): ToggleOutcome {
        val published = Publication.isOn(context, tile)
        if (!shelfAllowsSet(tile, on, Build.VERSION.SDK_INT, published)) return ToggleOutcome.Refused
        return when (val result = Publication.set(context, tile, on)) {
            PublishResult.Published, PublishResult.Unpublished -> ToggleOutcome.Updated
            is PublishResult.NeedsGrant -> ToggleOutcome.NeedsGrant(result.tile)
            is PublishResult.NeedsApp -> ToggleOutcome.NeedsApp(result.tile)
        }
    }

    fun resetCounter() {
        SharedPreferencesUtil.resetCounter(context)
    }

    fun setKeepMode(mode: KeepScreenMode) {
        SharedPreferencesUtil.setCustomizeKeepScreenOn(context, mode.stored)
    }

    fun writeApp(tile: Tile, app: LauncherApp) {
        val slot = (tile.detail as? Detail.CustomApp)?.slot ?: return
        CustomApps.write(context, slot, StoredApp(app.packageName, app.label))
    }

    fun grantCommand(need: GrantNeed): String? = when (need) {
        GrantNeed.WriteSecure -> adb(Manifest.permission.WRITE_SECURE_SETTINGS)
        GrantNeed.Dump -> adb(Manifest.permission.DUMP)
        GrantNeed.ModifySystem, GrantNeed.NotificationPolicy, GrantNeed.Accessibility -> null
    }

    fun copy(text: String) {
        val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
        clipboard.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.copy_command), text))
    }

    fun openGrant(need: GrantNeed) {
        val intent = when (need) {
            GrantNeed.ModifySystem -> Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                .setData(Uri.parse("package:${context.packageName}"))
            GrantNeed.NotificationPolicy -> Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            GrantNeed.Accessibility -> Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            GrantNeed.WriteSecure, GrantNeed.Dump -> return
        }
        context.startActivity(intent)
    }

    private fun customDetail(title: String, slot: CustomSlot, tile: Tile): DetailModel {
        val stored = CustomApps.read(context, slot)
        return if (stored == null) {
            DetailModel.CustomEmpty(title)
        } else {
            DetailModel.CustomReady(title, stored.label, stored.packageName, Publication.isOn(context, tile))
        }
    }

    private fun keepMode(): KeepScreenMode {
        val raw = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SharedPreferencesUtil.CUSTOMIZE_KEEP_SCREEN_ON, KeepScreenMode.Any.stored)
        return KeepScreenMode.parse(raw) ?: KeepScreenMode.Any
    }

    private fun keepChoices(): List<ModeChoice> = listOf(
        ModeChoice(KeepScreenMode.Ac, context.getString(R.string.pref_keep_screen_on_mode_ac_adapter)),
        ModeChoice(KeepScreenMode.Usb, context.getString(R.string.pref_keep_screen_on_mode_usb_port)),
        ModeChoice(KeepScreenMode.Wireless, context.getString(R.string.pref_keep_screen_on_mode_wireless)),
        ModeChoice(KeepScreenMode.Any, context.getString(R.string.pref_keep_screen_on_mode_any)),
    )

    private fun stepTitle(need: GrantNeed): Int = when (need) {
        GrantNeed.WriteSecure -> R.string.grant_secure
        GrantNeed.Dump -> R.string.grant_dump
        GrantNeed.ModifySystem -> R.string.grant_modify
        GrantNeed.NotificationPolicy -> R.string.grant_notification_policy
        GrantNeed.Accessibility -> R.string.grant_accessibility
    }

    private fun stepBody(need: GrantNeed): String = when (need) {
        GrantNeed.WriteSecure -> context.getString(
            R.string.grant_secure_body,
            adb(Manifest.permission.WRITE_SECURE_SETTINGS),
        )
        GrantNeed.Dump -> context.getString(
            R.string.grant_dump_body,
            adb(Manifest.permission.DUMP),
        )
        GrantNeed.ModifySystem -> context.getString(R.string.permission_modify_system_settings_description)
        GrantNeed.NotificationPolicy -> context.getString(R.string.permission_notification_policy_description)
        GrantNeed.Accessibility -> context.getString(
            R.string.permission_accessibility_service_description,
            context.getString(R.string.app_name),
        )
    }

    private fun adb(permission: String): String =
        "adb shell pm grant ${context.packageName} $permission"
}
