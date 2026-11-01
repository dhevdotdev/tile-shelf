package com.flxholle.quicktiles.catalog

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager

enum class ApplyScope { RepublishTrue, FullReconcile }

enum class ComponentGoal { Enable, Disable }

internal fun storedTrueTiles(values: Map<String, *>): Set<Tile> =
    Tile.entries.filter { values[it.key] == true }.toSet()

internal fun componentPlan(
    tiles: List<Tile>,
    storedTrue: Set<Tile>,
    scope: ApplyScope,
): Map<Tile, ComponentGoal> = when (scope) {
    ApplyScope.RepublishTrue ->
        tiles.filter { it in storedTrue }.associateWith { ComponentGoal.Enable }
    ApplyScope.FullReconcile ->
        tiles.associateWith { tile ->
            if (tile in storedTrue) ComponentGoal.Enable else ComponentGoal.Disable
        }
}

sealed interface SetDecision {
    data object TurnOff : SetDecision
    data object TurnOn : SetDecision
    data object NeedsGrant : SetDecision
    data object NeedsApp : SetDecision
}

internal fun decideSet(
    tile: Tile,
    on: Boolean,
    held: HeldGrants,
    packageStored: Boolean,
): SetDecision {
    if (!on) return SetDecision.TurnOff
    if (tile.detail is Detail.CustomApp && !packageStored) return SetDecision.NeedsApp
    if (!tile.gate.allows(held)) return SetDecision.NeedsGrant
    return SetDecision.TurnOn
}

sealed interface PublishResult {
    data object Published : PublishResult
    data object Unpublished : PublishResult
    data class NeedsGrant(val tile: Tile) : PublishResult
    data class NeedsApp(val tile: Tile, val slot: CustomSlot) : PublishResult
}

object Publication {
    @JvmStatic
    fun republishTrue(context: Context) {
        val stored = storedTrueTiles(preferences(context))
        apply(context, componentPlan(Tile.entries, stored, ApplyScope.RepublishTrue))
    }

    @JvmStatic
    fun reconcileShelf(context: Context) {
        val stored = storedTrueTiles(preferences(context))
        apply(context, componentPlan(Tile.entries, stored, ApplyScope.FullReconcile))
    }

    fun isOn(context: Context, tile: Tile): Boolean =
        preferences(context)[tile.key] == true

    fun set(context: Context, tile: Tile, on: Boolean): PublishResult {
        val packageStored = when (val detail = tile.detail) {
            is Detail.CustomApp -> CustomApps.read(context, detail.slot) != null
            else -> false
        }
        return when (decideSet(tile, on, DeviceGrants.held(context), packageStored)) {
            SetDecision.TurnOff -> {
                writeBoolean(context, tile, false)
                apply(context, mapOf(tile to ComponentGoal.Disable))
                PublishResult.Unpublished
            }
            SetDecision.TurnOn -> {
                writeBoolean(context, tile, true)
                apply(context, mapOf(tile to ComponentGoal.Enable))
                PublishResult.Published
            }
            SetDecision.NeedsGrant -> PublishResult.NeedsGrant(tile)
            SetDecision.NeedsApp -> {
                val slot = (tile.detail as Detail.CustomApp).slot
                PublishResult.NeedsApp(tile, slot)
            }
        }
    }

    private fun preferences(context: Context): Map<String, *> =
        PreferenceManager.getDefaultSharedPreferences(context).all

    private fun writeBoolean(context: Context, tile: Tile, on: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(tile.key, on)
            .apply()
    }

    private fun apply(context: Context, plan: Map<Tile, ComponentGoal>) {
        val pm = context.packageManager
        for ((tile, goal) in plan) {
            val component = ComponentName(context, tile.service.java)
            val current = pm.getComponentEnabledSetting(component)
            if (!already(current, goal)) {
                val desired = when (goal) {
                    ComponentGoal.Enable -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    ComponentGoal.Disable -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                pm.setComponentEnabledSetting(component, desired, PackageManager.DONT_KILL_APP)
            }
            if (goal == ComponentGoal.Enable) {
                TileService.requestListeningState(context, component)
            }
        }
    }

    // Services ship android:enabled="false", so the default state is already off.
    private fun already(current: Int, goal: ComponentGoal): Boolean = when (goal) {
        ComponentGoal.Enable -> current == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        ComponentGoal.Disable ->
            current == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                current == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
    }
}
