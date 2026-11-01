package com.flxholle.quicktiles.ui

import com.flxholle.quicktiles.catalog.CustomSlot
import com.flxholle.quicktiles.catalog.Detail
import com.flxholle.quicktiles.catalog.RowSwitch
import com.flxholle.quicktiles.catalog.ShelfSection
import com.flxholle.quicktiles.catalog.Tile
import com.flxholle.quicktiles.catalog.shelfEntries
import com.flxholle.quicktiles.catalog.storedTrueTiles

fun assembleShelf(
    values: Map<String, *>,
    query: String,
    sdk: Int,
    titleOf: (Tile) -> String,
    sectionTitle: (ShelfSection) -> String,
    iconOf: (Tile) -> Int,
    customLabel: (CustomSlot) -> String?,
    turnOffNote: String,
    noAppNote: String,
): ShelfModel.Ready {
    val needle = query.trim()
    val entries = shelfEntries(sdk, values)
    val groups = ShelfSection.entries.mapNotNull { section ->
        val named = sectionTitle(section)
        val sectionMatches = needle.isNotEmpty() && named.contains(needle, ignoreCase = true)
        val rows = entries
            .filter { it.tile.section == section }
            .map { entry -> entry.toRow(titleOf, iconOf, customLabel, turnOffNote, noAppNote) }
            .filter { row ->
                needle.isEmpty() || sectionMatches ||
                    row.title.contains(needle, ignoreCase = true) ||
                    row.supporting?.contains(needle, ignoreCase = true) == true
            }
        if (rows.isEmpty()) null else ShelfGroupModel(named, rows)
    }
    return ShelfModel.Ready(
        publishedCount = storedTrueTiles(values).size,
        catalogSize = Tile.entries.size,
        query = query,
        groups = groups,
    )
}

private fun com.flxholle.quicktiles.catalog.ShelfEntry.toRow(
    titleOf: (Tile) -> String,
    iconOf: (Tile) -> Int,
    customLabel: (CustomSlot) -> String?,
    turnOffNote: String,
    noAppNote: String,
): ShelfRowModel {
    val custom = (tile.detail as? Detail.CustomApp)?.let { customLabel(it.slot) }
    val supporting = when {
        switch is RowSwitch.TurnOffOnly && !custom.isNullOrBlank() -> "$custom. $turnOffNote"
        switch is RowSwitch.TurnOffOnly -> turnOffNote
        tile.detail is Detail.CustomApp -> custom?.takeIf { it.isNotBlank() } ?: noAppNote
        else -> null
    }
    return ShelfRowModel(
        tile = tile,
        title = titleOf(tile),
        supporting = supporting,
        iconRes = iconOf(tile),
        switch = switch,
    )
}
