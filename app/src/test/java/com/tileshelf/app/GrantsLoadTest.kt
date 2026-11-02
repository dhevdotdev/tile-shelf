package com.tileshelf.app

import com.tileshelf.app.catalog.GrantNeed
import com.tileshelf.app.ui.DeviceActions
import com.tileshelf.app.ui.GrantsModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GrantsLoadTest {
    private val device = DeviceActions(RuntimeEnvironment.getApplication())

    @Test
    fun no_tile_lists_every_grant_with_its_status() {
        val model = device.loadGrants(null)
        assertTrue(model is GrantsModel.Overview)
        val rows = (model as GrantsModel.Overview).rows
        assertEquals(GrantNeed.entries.toList(), rows.map { it.need })
        assertTrue(rows.all { it.title.isNotBlank() && it.body.isNotBlank() })
        assertFalse(rows.single { it.need == GrantNeed.Dump }.held)
    }

    @Test
    fun blank_tile_is_the_overview() {
        assertTrue(device.loadGrants("  ") is GrantsModel.Overview)
    }

    @Test
    fun unknown_tile_name_stays_unknown() {
        assertEquals(GrantsModel.Unknown, device.loadGrants("not_a_tile"))
    }

    @Test
    fun known_tile_still_lists_only_its_missing_steps() {
        val model = device.loadGrants("DemoMode")
        assertTrue(model is GrantsModel.Missing)
        assertEquals(
            listOf(GrantNeed.WriteSecure, GrantNeed.Dump),
            (model as GrantsModel.Missing).steps.map { it.need },
        )
    }
}
