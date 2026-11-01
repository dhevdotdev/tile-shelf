package com.flxholle.quicktiles

import com.flxholle.quicktiles.catalog.ApplyScope
import com.flxholle.quicktiles.catalog.ComponentGoal
import com.flxholle.quicktiles.catalog.Gate
import com.flxholle.quicktiles.catalog.GrantNeed
import com.flxholle.quicktiles.catalog.HeldGrants
import com.flxholle.quicktiles.catalog.Offer
import com.flxholle.quicktiles.catalog.RowSwitch
import com.flxholle.quicktiles.catalog.SetDecision
import com.flxholle.quicktiles.catalog.Tile
import com.flxholle.quicktiles.catalog.allows
import com.flxholle.quicktiles.catalog.componentPlan
import com.flxholle.quicktiles.catalog.decideSet
import com.flxholle.quicktiles.catalog.missingNeeds
import com.flxholle.quicktiles.catalog.shelfAllowsSet
import com.flxholle.quicktiles.catalog.shelfEntries
import com.flxholle.quicktiles.catalog.shelfSwitch
import com.flxholle.quicktiles.catalog.storedTrueTiles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicationPlanTest {
    @Test
    fun republish_true_only_enables_stored_true() {
        val stored = storedTrueTiles(
            mapOf(
                Tile.Counter.key to java.lang.Boolean.TRUE,
                Tile.Battery.key to java.lang.Boolean.FALSE,
                Tile.Vpn.key to "true",
                Tile.HeadsUp.key to 1,
            ),
        )
        assertEquals(setOf(Tile.Counter), stored)
        assertEquals(
            mapOf(Tile.Counter to ComponentGoal.Enable),
            componentPlan(Tile.entries, stored, ApplyScope.RepublishTrue),
        )
    }

    @Test
    fun full_reconcile_disables_the_rest() {
        val tiles = listOf(Tile.Counter, Tile.Battery, Tile.ShowTaps)
        assertEquals(
            mapOf(
                Tile.Counter to ComponentGoal.Enable,
                Tile.Battery to ComponentGoal.Disable,
                Tile.ShowTaps to ComponentGoal.Disable,
            ),
            componentPlan(tiles, setOf(Tile.Counter), ApplyScope.FullReconcile),
        )
    }

    @Test
    fun set_on_without_grant_writes_nothing() {
        assertEquals(
            SetDecision.NeedsGrant,
            decideSet(Tile.UsbDebugging, on = true, held = HeldGrants(), packageStored = false),
        )
    }

    @Test
    fun set_off_does_not_need_a_grant() {
        assertEquals(
            SetDecision.TurnOff,
            decideSet(Tile.UsbDebugging, on = false, held = HeldGrants(), packageStored = false),
        )
        assertEquals(
            SetDecision.TurnOff,
            decideSet(Tile.CustomOne, on = false, held = HeldGrants(), packageStored = false),
        )
    }

    @Test
    fun custom_app_on_without_package_is_needs_app() {
        assertEquals(
            SetDecision.NeedsApp,
            decideSet(Tile.CustomOne, on = true, held = HeldGrants(), packageStored = false),
        )
        assertEquals(
            SetDecision.TurnOn,
            decideSet(Tile.CustomOne, on = true, held = HeldGrants(), packageStored = true),
        )
    }

    @Test
    fun same_plan_twice_is_the_same_map() {
        val stored = setOf(Tile.PlayPause, Tile.ShowTaps)
        assertEquals(
            componentPlan(Tile.entries, stored, ApplyScope.FullReconcile),
            componentPlan(Tile.entries, stored, ApplyScope.FullReconcile),
        )
        assertEquals(
            componentPlan(Tile.entries, stored, ApplyScope.RepublishTrue),
            componentPlan(Tile.entries, stored, ApplyScope.RepublishTrue),
        )
    }

    @Test
    fun show_taps_is_never_offered() {
        assertEquals(Offer.Never, Tile.ShowTaps.offer)
        assertNull(Tile.ShowTaps.shelfSwitch(36, published = false))
        assertEquals(RowSwitch.TurnOffOnly, Tile.ShowTaps.shelfSwitch(36, published = true))
    }

    @Test
    fun grayscale_is_hidden_from_api_30() {
        assertEquals(Offer.Until(30), Tile.Grayscale.offer)
        assertNull(Tile.Grayscale.shelfSwitch(30, published = false))
        assertEquals(RowSwitch.TurnOffOnly, Tile.Grayscale.shelfSwitch(30, published = true))
        assertTrue(Tile.Grayscale.shelfSwitch(29, published = false) is RowSwitch.Offered)
    }

    @Test
    fun switch_silence_loud_is_notification_policy() {
        assertEquals(Gate.NotificationPolicy, Tile.SwitchSilenceLoud.gate)
        assertFalse(Gate.NotificationPolicy.allows(HeldGrants()))
        assertTrue(Gate.NotificationPolicy.allows(HeldGrants(notificationPolicy = true)))
        assertEquals(
            SetDecision.NeedsGrant,
            decideSet(Tile.SwitchSilenceLoud, on = true, held = HeldGrants(), packageStored = false),
        )
        assertEquals(Gate.SecureAndDump, Tile.DemoMode.gate)
        assertFalse(Gate.SecureAndDump.allows(HeldGrants(writeSecure = true)))
        assertTrue(Gate.SecureAndDump.allows(HeldGrants(writeSecure = true, dump = true)))
        assertEquals(Gate.SecureAndModifySystem, Tile.KeepScreenOn.gate)
    }

    @Test
    fun shelf_hides_a_never_offered_tile_until_it_is_published() {
        assertTrue(shelfEntries(36, emptyMap<String, Any>()).none { it.tile == Tile.ShowTaps })
        val shown = shelfEntries(36, mapOf(Tile.ShowTaps.key to true))
        assertEquals(RowSwitch.TurnOffOnly, shown.first { it.tile == Tile.ShowTaps }.switch)
    }

    @Test
    fun shelf_refuses_to_turn_a_turn_off_only_row_back_on() {
        assertFalse(shelfAllowsSet(Tile.ShowTaps, on = true, sdk = 36, published = true))
        assertTrue(shelfAllowsSet(Tile.ShowTaps, on = false, sdk = 36, published = true))
        assertFalse(shelfAllowsSet(Tile.ShowTaps, on = true, sdk = 36, published = false))
        assertFalse(shelfAllowsSet(Tile.Grayscale, on = true, sdk = 30, published = true))
        assertTrue(shelfAllowsSet(Tile.Grayscale, on = true, sdk = 29, published = false))
        assertEquals(
            SetDecision.TurnOn,
            decideSet(
                Tile.ShowTaps,
                on = true,
                held = HeldGrants(writeSecure = true),
                packageStored = false,
            ),
        )
    }

    @Test
    fun demo_mode_lists_each_missing_permission() {
        assertEquals(
            listOf(GrantNeed.WriteSecure, GrantNeed.Dump),
            missingNeeds(Gate.SecureAndDump, HeldGrants()),
        )
        assertEquals(
            emptyList<GrantNeed>(),
            missingNeeds(Gate.SecureAndDump, HeldGrants(writeSecure = true, dump = true)),
        )
        assertEquals(
            listOf(GrantNeed.ModifySystem),
            missingNeeds(Gate.SecureAndModifySystem, HeldGrants(writeSecure = true)),
        )
    }
}
