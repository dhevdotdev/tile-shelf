package com.tileshelf.app.ui

import com.tileshelf.app.catalog.CustomSlot
import com.tileshelf.app.catalog.Detail
import com.tileshelf.app.catalog.RowSwitch
import com.tileshelf.app.catalog.ShelfSection
import com.tileshelf.app.catalog.Tile
import com.tileshelf.app.catalog.shelfEntries

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
    val rows = shelfEntries(sdk, values)
        .map { entry -> entry.toRow(titleOf, iconOf, customLabel, turnOffNote, noAppNote) }
    val groups = ShelfSection.entries.mapNotNull { section ->
        val named = sectionTitle(section)
        val sectionMatches = needle.isNotEmpty() && named.contains(needle, ignoreCase = true)
        val matching = rows
            .filter { it.tile.section == section }
            .filter { row ->
                needle.isEmpty() || sectionMatches ||
                    row.title.contains(needle, ignoreCase = true) ||
                    row.supporting?.contains(needle, ignoreCase = true) == true
            }
        if (matching.isEmpty()) null else ShelfGroupModel(named, matching)
    }
    return ShelfModel.Ready(
        query = query,
        yours = if (needle.isEmpty()) rows.filter { it.isOn } else emptyList(),
        groups = groups,
    )
}

private fun com.tileshelf.app.catalog.ShelfEntry.toRow(
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
