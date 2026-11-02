package com.tileshelf.app

import com.tileshelf.app.catalog.Tile
import com.tileshelf.app.ui.ShelfModel
import com.tileshelf.app.ui.assembleShelf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssembleShelfTest {
    private fun shelf(values: Map<String, *>, query: String = ""): ShelfModel.Ready = assembleShelf(
        values = values,
        query = query,
        sdk = 36,
        titleOf = { it.name },
        sectionTitle = { it.name },
        iconOf = { 0 },
        customLabel = { null },
        turnOffNote = "turn off",
        noAppNote = "no app",
    )

    @Test
    fun nothing_on_means_no_tiles_of_your_own() {
        assertTrue(shelf(emptyMap<String, Any>()).yours.isEmpty())
    }

    @Test
    fun turned_on_tiles_are_listed_in_catalog_order_and_stay_in_their_sections() {
        val model = shelf(mapOf(Tile.ClearClipboard.key to true, Tile.PlayPause.key to true))
        assertEquals(listOf(Tile.PlayPause, Tile.ClearClipboard), model.yours.map { it.tile })
        val inSections = model.groups.flatMap { it.rows }.map { it.tile }
        assertTrue(inSections.containsAll(listOf(Tile.PlayPause, Tile.ClearClipboard)))
    }

    @Test
    fun a_search_hides_your_tiles_so_results_show_once() {
        val model = shelf(mapOf(Tile.PlayPause.key to true), query = "PlayPause")
        assertTrue(model.yours.isEmpty())
        assertEquals(listOf(Tile.PlayPause), model.groups.flatMap { it.rows }.map { it.tile })
    }
}
